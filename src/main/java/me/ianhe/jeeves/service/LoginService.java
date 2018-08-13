package me.ianhe.jeeves.service;

import com.google.zxing.WriterException;
import me.ianhe.jeeves.domain.request.component.BaseRequest;
import me.ianhe.jeeves.domain.response.*;
import me.ianhe.jeeves.domain.shared.ChatRoomDescription;
import me.ianhe.jeeves.domain.shared.Token;
import me.ianhe.jeeves.enums.LoginCode;
import me.ianhe.jeeves.enums.StatusNotifyCode;
import me.ianhe.jeeves.exception.WechatException;
import me.ianhe.jeeves.exception.WechatQRExpiredException;
import me.ianhe.jeeves.utils.QRCodeUtils;
import me.ianhe.jeeves.utils.WechatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

@Component
public class LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private CacheService cacheService;
    @Autowired
    private SyncServie syncServie;
    @Autowired
    private WeChatHttpServiceInternal wechatHttpServiceInternal;

    @Value("${jeeves.auto-relogin-when-qrcode-expired}")
    private boolean AUTO_RELOGIN_WHEN_QRCODE_EXPIRED;

    @Value("${jeeves.max-qr-refresh-times}")
    private int MAX_QR_REFRESH_TIMES;

    private int qrRefreshTimes = 0;

    public void login() {
        cacheService.reset();
        try {
            //0 入口
            wechatHttpServiceInternal.open(qrRefreshTimes);
            logger.info("[0] entry completed");
            //1 获取uuid
            String uuid = wechatHttpServiceInternal.getUUID();
            cacheService.setUuid(uuid);
            logger.info("[1] uuid completed,is {}", uuid);
            //2 获取二维码
            byte[] qrData = wechatHttpServiceInternal.getQR(uuid);
            ByteArrayInputStream stream = new ByteArrayInputStream(qrData);
            String qrUrl = QRCodeUtils.decode(stream, uuid);
            stream.close();
            String qr = QRCodeUtils.generateQR(qrUrl, 10, 10);
            logger.info("\r\n" + qr);
            logger.info("[2] qrcode completed");
            //3 statreport
            wechatHttpServiceInternal.statReport();
            logger.info("[3] statReport completed");
            //4 login
            LoginResult loginResponse;
            while (true) {
                loginResponse = wechatHttpServiceInternal.login(uuid);
                if (LoginCode.SUCCESS.getCode().equals(loginResponse.getCode())) {
                    if (loginResponse.getHostUrl() == null) {
                        throw new WechatException("hostUrl can't be found");
                    }
                    if (loginResponse.getRedirectUrl() == null) {
                        throw new WechatException("redirectUrl can't be found");
                    }
                    cacheService.setHostUrl(loginResponse.getHostUrl());
                    if ("https://wechat.com".equals(loginResponse.getHostUrl())) {
                        cacheService.setSyncUrl("https://webpush.web.wechat.com");
                        cacheService.setFileUrl("https://file.web.wechat.com");
                    } else {
                        cacheService.setSyncUrl(loginResponse.getHostUrl().replaceFirst("^https://", "https://webpush."));
                        cacheService.setFileUrl(loginResponse.getHostUrl().replaceFirst("^https://", "https://file."));
                    }
                    break;
                } else if (LoginCode.AWAIT_CONFIRMATION.getCode().equals(loginResponse.getCode())) {
                    logger.info("[*] login status = AWAIT_CONFIRMATION");
                } else if (LoginCode.AWAIT_SCANNING.getCode().equals(loginResponse.getCode())) {
                    logger.info("[*] login status = AWAIT_SCANNING");
                } else if (LoginCode.EXPIRED.getCode().equals(loginResponse.getCode())) {
                    logger.info("[*] login status = EXPIRED");
                    throw new WechatQRExpiredException();
                } else {
                    logger.info("[*] login status = " + loginResponse.getCode());
                }
            }
            logger.info("[4] login completed");
            //5 redirect login
            Token token = wechatHttpServiceInternal.openNewloginpage(loginResponse.getRedirectUrl());
            if (token.getRet() == 0) {
                cacheService.setPassTicket(token.getPass_ticket());
                cacheService.setsKey(token.getSkey());
                cacheService.setSid(token.getWxsid());
                cacheService.setUin(token.getWxuin());
                BaseRequest baseRequest = new BaseRequest();
                baseRequest.setUin(cacheService.getUin());
                baseRequest.setSid(cacheService.getSid());
                baseRequest.setSkey(cacheService.getsKey());
                cacheService.setBaseRequest(baseRequest);
            } else {
                throw new WechatException("token ret = " + token.getRet());
            }
            logger.info("[5] redirect login completed");
            //6 redirect
            wechatHttpServiceInternal.redirect(cacheService.getHostUrl());
            logger.info("[6] redirect completed");
            //7 init
            InitResponse initResponse = wechatHttpServiceInternal.init(cacheService.getHostUrl(), cacheService.getBaseRequest());
            WechatUtils.checkBaseResponse(initResponse);
            cacheService.setSyncKey(initResponse.getSyncKey());
            cacheService.setOwner(initResponse.getUser());
            logger.info("[7] init completed");
            //8 status notify
            StatusNotifyResponse statusNotifyResponse =
                    wechatHttpServiceInternal.statusNotify(cacheService.getHostUrl(),
                            cacheService.getBaseRequest(),
                            cacheService.getOwner().getUserName(), StatusNotifyCode.INITED.getCode());
            WechatUtils.checkBaseResponse(statusNotifyResponse);
            logger.info("[8] status notify completed");
            //9 获取联系人
            long seq = 0;
            do {
                GetContactResponse getContactResponse = wechatHttpServiceInternal.getContact(cacheService.getHostUrl(), cacheService.getBaseRequest().getSkey(), seq);
                WechatUtils.checkBaseResponse(getContactResponse);
                logger.info("[*] getContactResponse seq = " + getContactResponse.getSeq());
                logger.info("[*] getContactResponse memberCount = " + getContactResponse.getMemberCount());
                seq = getContactResponse.getSeq();
                cacheService.getIndividuals().addAll(getContactResponse.getMemberList().stream().filter(WechatUtils::isIndividual).collect(Collectors.toSet()));
                cacheService.getMediaPlatforms().addAll(getContactResponse.getMemberList().stream().filter(WechatUtils::isMediaPlatform).collect(Collectors.toSet()));
            } while (seq > 0);
            logger.info("[9] get contact completed");
            //10 batch get contact
            ChatRoomDescription[] chatRoomDescriptions = initResponse.getContactList().stream()
                    .filter(x -> x != null && WechatUtils.isChatRoom(x))
                    .map(x -> {
                        ChatRoomDescription description = new ChatRoomDescription();
                        description.setUserName(x.getUserName());
                        return description;
                    })
                    .toArray(ChatRoomDescription[]::new);
            if (chatRoomDescriptions.length > 0) {
                BatchGetContactResponse batchGetContactResponse = wechatHttpServiceInternal.batchGetContact(
                        cacheService.getHostUrl(),
                        cacheService.getBaseRequest(),
                        chatRoomDescriptions);
                WechatUtils.checkBaseResponse(batchGetContactResponse);
                logger.info("[*] batchGetContactResponse count = " + batchGetContactResponse.getCount());
                cacheService.getChatRooms().addAll(batchGetContactResponse.getContactList());
            }
            logger.info("[10] batch get contact completed");
            cacheService.setAlive(true);
            logger.info("[*] login process completed");
            logger.info("[*] start listening");
            while (true) {
                syncServie.listen();
            }
        } catch (IOException | WriterException | URISyntaxException ex) {
            throw new WechatException(ex);
        } catch (WechatQRExpiredException ex) {
            if (AUTO_RELOGIN_WHEN_QRCODE_EXPIRED && qrRefreshTimes <= MAX_QR_REFRESH_TIMES) {
                login();
            } else {
                throw new WechatException(ex);
            }
        }
    }
}
