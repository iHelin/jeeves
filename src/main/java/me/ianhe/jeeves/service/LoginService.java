package me.ianhe.jeeves.service;

import com.google.zxing.WriterException;
import me.ianhe.jeeves.config.SystemProperties;
import me.ianhe.jeeves.domain.request.component.BaseRequest;
import me.ianhe.jeeves.domain.response.*;
import me.ianhe.jeeves.domain.shared.ChatRoomDescription;
import me.ianhe.jeeves.domain.shared.Contact;
import me.ianhe.jeeves.domain.shared.Token;
import me.ianhe.jeeves.enums.LoginCode;
import me.ianhe.jeeves.enums.StatusNotifyCode;
import me.ianhe.jeeves.exception.WeChatException;
import me.ianhe.jeeves.exception.WechatQRExpiredException;
import me.ianhe.jeeves.utils.QRCodeUtils;
import me.ianhe.jeeves.utils.WeChatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * @author iHelin
 * @since 2018/8/15 10:11
 */
@Component
public class LoginService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CacheService cacheService;
    @Autowired
    private SyncService syncService;
    @Autowired
    private SystemProperties systemProperties;
    @Autowired
    private WeChatHttpServiceInternal wechatHttpServiceInternal;

    private int qrRefreshTimes = 0;

    public void login() {
        wechatHttpServiceInternal.open(qrRefreshTimes);
        logger.debug("[0] entry completed");
        cacheService.reset();
        loginLoop();
    }

    private void loginLoop() {
        try {
            String uuid = wechatHttpServiceInternal.getUUID();
            cacheService.setUuid(uuid);
            logger.debug("[1] uuid: {}", uuid);
            byte[] qrData = wechatHttpServiceInternal.getQR(uuid);
            ByteArrayInputStream stream = new ByteArrayInputStream(qrData);
            String qrUrl = QRCodeUtils.decode(stream, uuid);
            stream.close();
            String qr = QRCodeUtils.generateQR(qrUrl, systemProperties.getIde(), 10, 10);
            logger.debug("\r\n" + qr);
            logger.debug("[2] qrcode completed");
            wechatHttpServiceInternal.statReport();
            logger.debug("[3] statReport completed");
            LoginResult loginResponse;
            while (true) {
                loginResponse = wechatHttpServiceInternal.login(uuid);
                if (LoginCode.SUCCESS.getCode().equals(loginResponse.getCode())) {
                    if (loginResponse.getHostUrl() == null) {
                        throw new WeChatException("hostUrl can't be found");
                    }
                    if (loginResponse.getRedirectUrl() == null) {
                        throw new WeChatException("redirectUrl can't be found");
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
                    logger.debug("[*] login status = AWAIT_CONFIRMATION");
                } else if (LoginCode.AWAIT_SCANNING.getCode().equals(loginResponse.getCode())) {
                    logger.debug("[*] login status = AWAIT_SCANNING");
                } else if (LoginCode.EXPIRED.getCode().equals(loginResponse.getCode())) {
                    logger.debug("[*] login status = EXPIRED");
                    throw new WechatQRExpiredException();
                } else {
                    logger.debug("[*] login status = " + loginResponse.getCode());
                }
            }
            logger.debug("[4] login completed");
            //5 redirect login
            Token token = wechatHttpServiceInternal.openNewLoginPage(loginResponse.getRedirectUrl());
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
                throw new WeChatException("token ret = " + token.getRet());
            }
            logger.debug("[5] redirect login completed");
            //6 redirect
            wechatHttpServiceInternal.redirect(cacheService.getHostUrl());
            logger.debug("[6] redirect completed");
            //7 init
            InitResponse initResponse = wechatHttpServiceInternal.init(cacheService.getHostUrl(), cacheService.getBaseRequest());
            WeChatUtils.checkBaseResponse(initResponse);
            cacheService.setSyncKey(initResponse.getSyncKey());
            cacheService.setOwner(initResponse.getUser());
            logger.debug("[7] init completed");
            //8 status notify
            StatusNotifyResponse statusNotifyResponse =
                    wechatHttpServiceInternal.statusNotify(cacheService.getHostUrl(),
                            cacheService.getBaseRequest(),
                            cacheService.getOwner().getUserName(), StatusNotifyCode.INITED.getCode());
            WeChatUtils.checkBaseResponse(statusNotifyResponse);
            logger.debug("[8] status notify completed");
            //9 获取联系人
            long seq = 0;
            do {
                GetContactResponse getContactResponse = wechatHttpServiceInternal.getContact(cacheService.getHostUrl(), cacheService.getBaseRequest().getSkey(), seq);
                WeChatUtils.checkBaseResponse(getContactResponse);
                logger.debug("[*] getContactResponse seq = {},memberCount:{}", getContactResponse.getSeq(), getContactResponse.getMemberCount());
                seq = getContactResponse.getSeq();
                Set<Contact> members = getContactResponse.getMemberList();
                for (Contact member : members) {
                    cacheService.getAllMembers().add(member);
                    if (WeChatUtils.isIndividual(member)) {
                        cacheService.getIndividuals().add(member);
                    }
                    if (WeChatUtils.isChatRoom(member)) {
                        cacheService.getChatRooms().add(member);
                    }
                    if (WeChatUtils.isMediaPlatform(member)) {
                        cacheService.getMediaPlatforms().add(member);
                    }
                }
            } while (seq > 0);
            logger.debug("[9] get contact completed");
            //10 batch get contact
            ChatRoomDescription[] chatRoomDescriptions = initResponse.getContactList().stream()
                    .filter(x -> x != null && WeChatUtils.isChatRoom(x))
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
                WeChatUtils.checkBaseResponse(batchGetContactResponse);
                logger.debug("[*] batchGetContactResponse count = " + batchGetContactResponse.getCount());
                Set<Contact> contactList = batchGetContactResponse.getContactList();
                for (Contact contact : contactList) {
                    cacheService.getChatRooms().add(contact);
                    cacheService.getAllMembers().add(contact);
                }
            }
            logger.debug("[10] batch get contact completed");
            cacheService.setAlive(true);
            logger.debug("[*] login process completed");
            logger.debug("[*] start listening");
            while (true) {
                syncService.listen();
            }
        } catch (IOException | WriterException | URISyntaxException ex) {
            throw new WeChatException(ex);
        } catch (WechatQRExpiredException ex) {
            if (systemProperties.getAutoReLogin() && qrRefreshTimes <= systemProperties.getMaxQrRefreshTimes()) {
                qrRefreshTimes++;
                cacheService.reset();
                login();
            } else {
                throw new WeChatException(ex);
            }
        }
    }
}
