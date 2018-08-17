package me.ianhe.jeeves.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import me.ianhe.jeeves.config.Constants;
import me.ianhe.jeeves.domain.request.*;
import me.ianhe.jeeves.domain.request.component.BaseRequest;
import me.ianhe.jeeves.domain.response.*;
import me.ianhe.jeeves.domain.shared.*;
import me.ianhe.jeeves.enums.*;
import me.ianhe.jeeves.exception.WeChatException;
import me.ianhe.jeeves.utils.HeaderUtils;
import me.ianhe.jeeves.utils.WeChatUtils;
import me.ianhe.jeeves.utils.rest.StatefulRestTemplate;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author iHelin
 * @since 2018/8/13 19:57
 */
@Component
class WeChatHttpServiceInternal {

    @Value("${wechat.url.entry}")
    private String WECHAT_URL_ENTRY;
    @Value("${wechat.url.uuid}")
    private String WECHAT_URL_UUID;
    @Value("${wechat.url.qrcode}")
    private String WECHAT_URL_QRCODE;
    @Value("${wechat.url.status_notify}")
    private String WECHAT_URL_STATUS_NOTIFY;
    @Value("${wechat.url.statreport}")
    private String WECHAT_URL_STATREPORT;
    @Value("${wechat.url.login}")
    private String WECHAT_URL_LOGIN;
    @Value("${wechat.url.init}")
    private String WECHAT_URL_INIT;
    @Value("${wechat.url.sync_check}")
    private String WECHAT_URL_SYNC_CHECK;
    @Value("${wechat.url.sync}")
    private String WECHAT_URL_SYNC;
    @Value("${wechat.url.get_contact}")
    private String WECHAT_URL_GET_CONTACT;
    @Value("${wechat.url.send_msg}")
    private String WECHAT_URL_SEND_MSG;
    @Value("${wechat.url.upload_media}")
    private String WECHAT_URL_UPLOAD_MEDIA;
    @Value("${wechat.url.get_msg_img}")
    private String WECHAT_URL_GET_MSG_IMG;
    @Value("${wechat.url.get_voice}")
    private String WECHAT_URL_GET_VOICE;
    @Value("${wechat.url.get_video}")
    private String WECHAT_URL_GET_VIDEO;
    @Value("${wechat.url.push_login}")
    private String WECHAT_URL_PUSH_LOGIN;
    @Value("${wechat.url.logout}")
    private String WECHAT_URL_LOGOUT;
    @Value("${wechat.url.batch_get_contact}")
    private String WECHAT_URL_BATCH_GET_CONTACT;
    @Value("${wechat.url.op_log}")
    private String WECHAT_URL_OP_LOG;
    @Value("${wechat.url.verify_user}")
    private String WECHAT_URL_VERIFY_USER;
    @Value("${wechat.url.get_media}")
    private String WECHAT_URL_GET_MEDIA;
    @Value("${wechat.url.create_chatroom}")
    private String WECHAT_URL_CREATE_CHATROOM;
    @Value("${wechat.url.delete_chatroom_member}")
    private String WECHAT_URL_DELETE_CHATROOM_MEMBER;
    @Value("${wechat.url.add_chatroom_member}")
    private String WECHAT_URL_ADD_CHATROOM_MEMBER;


    private final HttpHeaders postHeader = new HttpHeaders();
    private final HttpHeaders baseHeader = new HttpHeaders();
    private String originValue = null;
    private String refererValue = null;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper jsonMapper;
    @Autowired
    private CacheService cacheService;

    private static final Pattern PATTERN_CODE = Pattern.compile("window.code=(\\d+)");
    private static final Pattern PATTERN_HOST_URL = Pattern.compile("window.redirect_uri=\\\"(.*)\\/cgi-bin");
    private static final Pattern PATTERN_REDIRECT_URI = Pattern.compile("window.redirect_uri=\\\"(.*)\\\";");
    private static final Pattern PATTERN_SYNC_CHECK = Pattern.compile("window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"\\}");
    private static final Pattern PATTERN_UUID = Pattern.compile("window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";");


    {
        baseHeader.set(HttpHeaders.USER_AGENT, Constants.USER_AGENT);
        baseHeader.set(HttpHeaders.ACCEPT_LANGUAGE, Constants.BROWSER_DEFAULT_ACCEPT_LANGUAGE);
        baseHeader.set(HttpHeaders.ACCEPT_ENCODING, Constants.BROWSER_DEFAULT_ACCEPT_ENCODING);
        postHeader.set(HttpHeaders.USER_AGENT, Constants.USER_AGENT);
        postHeader.set(HttpHeaders.ACCEPT_LANGUAGE, Constants.BROWSER_DEFAULT_ACCEPT_LANGUAGE);
        postHeader.set(HttpHeaders.ACCEPT_ENCODING, Constants.BROWSER_DEFAULT_ACCEPT_ENCODING);
        postHeader.setContentType(MediaType.APPLICATION_JSON_UTF8);
        postHeader.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.ALL));
    }

    void logout(String hostUrl, String skey) throws IOException {
        final String url = String.format(WECHAT_URL_LOGOUT, hostUrl, WeChatUtils.escape(skey));
        restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(postHeader), Object.class);
    }

    /**
     * 打开首页
     *
     * @param retryTimes retry times of qr scan
     */
    public void open(int retryTimes) {
        final String url = WECHAT_URL_ENTRY;
        HttpHeaders customHeader = new HttpHeaders();
        customHeader.setPragma("no-cache");
        customHeader.setCacheControl("no-cache");
        customHeader.set("Upgrade-Insecure-Requests", "1");
        customHeader.setAccept(MediaType.parseMediaTypes(Constants.HEADER_ACCEPT));
        HeaderUtils.assign(customHeader, baseHeader);
        restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(customHeader), String.class);
        CookieStore store = (CookieStore) ((StatefulRestTemplate) restTemplate).getHttpContext().getAttribute(HttpClientContext.COOKIE_STORE);
        Date maxDate = new Date(Long.MAX_VALUE);
        String domain = WECHAT_URL_ENTRY.replaceAll("https://", "").replaceAll("/", "");
        Map<String, String> cookies = new HashMap<>(3);
        cookies.put("MM_WX_NOTIFY_STATE", "1");
        cookies.put("MM_WX_SOUND_STATE", "1");
        if (retryTimes > 0) {
            cookies.put("refreshTimes", String.valueOf(retryTimes));
        }
        appendAdditionalCookies(store, cookies, domain, "/", maxDate);
        this.originValue = WECHAT_URL_ENTRY;
        this.refererValue = WECHAT_URL_ENTRY.replaceAll("/$", "");
    }

    /**
     * 获取UUID
     *
     * @return UUID
     */
    public String getUUID() {
        final String url = String.format(WECHAT_URL_UUID, System.currentTimeMillis());
        HttpHeaders customHeader = new HttpHeaders();
        customHeader.setPragma("no-cache");
        customHeader.setCacheControl("no-cache");
        customHeader.setAccept(Collections.singletonList(MediaType.ALL));
        customHeader.set(HttpHeaders.REFERER, WECHAT_URL_ENTRY);
        HeaderUtils.assign(customHeader, baseHeader);
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(customHeader), String.class);
        String body = responseEntity.getBody();
        Matcher matcher = PATTERN_UUID.matcher(body);
        if (matcher.find()) {
            if (Constants.SUCCESS_CODE.equals(matcher.group(1))) {
                return matcher.group(2);
            }
        }
        throw new WeChatException("uuid can't be found");
    }

    /**
     * 获取二维码数据
     *
     * @param uuid UUID
     * @return QR code in binary
     */
    public byte[] getQR(String uuid) {
        final String url = WECHAT_URL_QRCODE + "/" + uuid;
        HttpHeaders customHeader = new HttpHeaders();
        customHeader.setAccept(MediaType.parseMediaTypes("image/webp,image/apng,image/*,*/*;q=0.8"));
        customHeader.set(HttpHeaders.REFERER, WECHAT_URL_ENTRY);
        HeaderUtils.assign(customHeader, baseHeader);
        ResponseEntity<byte[]> responseEntity
                = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(customHeader), new ParameterizedTypeReference<byte[]>() {
        });
        return responseEntity.getBody();
    }

    /**
     * report stats to server
     */
    public void statReport() {
        final String url = String.format(WECHAT_URL_STATREPORT, cacheService.getPassTicket());
        StatReportRequest request = new StatReportRequest();
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setUin("");
        baseRequest.setSid("");
        baseRequest.setDeviceID(WeChatUtils.generateDeviceId());
        request.setBaseRequest(baseRequest);
        request.setCount(0);
        request.setList(new StatReport[0]);
        HttpHeaders customHeader = new HttpHeaders();
        customHeader.set(HttpHeaders.REFERER, WECHAT_URL_ENTRY);
        customHeader.setOrigin(WECHAT_URL_ENTRY.replaceAll("/$", ""));
        HeaderUtils.assign(customHeader, postHeader);
        restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request, customHeader), String.class);
    }

    /**
     * Get hostUrl and redirectUrl
     *
     * @param uuid
     * @return hostUrl and redirectUrl
     * @throws WeChatException if the response doesn't contain code
     */
    public LoginResult login(String uuid) {
        long time = System.currentTimeMillis();
        final String url = String.format(WECHAT_URL_LOGIN, uuid, WeChatUtils.generateDateWithBitwiseNot(time), time);
        HttpHeaders customHeader = new HttpHeaders();
        customHeader.setAccept(Collections.singletonList(MediaType.ALL));
        customHeader.set(HttpHeaders.REFERER, WECHAT_URL_ENTRY);
        HeaderUtils.assign(customHeader, baseHeader);
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(customHeader), String.class);
        String body = responseEntity.getBody();
        Matcher matcher = PATTERN_CODE.matcher(body);
        LoginResult response = new LoginResult();
        if (matcher.find()) {
            response.setCode(matcher.group(1));
        } else {
            throw new WeChatException("code can't be found");
        }
        Matcher hostUrlMatcher = PATTERN_HOST_URL.matcher(body);
        if (hostUrlMatcher.find()) {
            response.setHostUrl(hostUrlMatcher.group(1));
        }
        Matcher redirectUrlMatcher = PATTERN_REDIRECT_URI.matcher(body);
        if (redirectUrlMatcher.find()) {
            response.setRedirectUrl(redirectUrlMatcher.group(1));
        }
        return response;
    }

    /**
     * Get basic parameters for this session
     *
     * @param redirectUrl
     * @return session token
     * @throws IOException if the http response body can't be convert to {@link Token}
     */
    public Token openNewLoginPage(String redirectUrl) {
        HttpHeaders customHeader = new HttpHeaders();
        customHeader.set(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        customHeader.set(HttpHeaders.REFERER, WECHAT_URL_ENTRY);
        customHeader.set("Upgrade-Insecure-Requests", "1");
        HeaderUtils.assign(customHeader, baseHeader);
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(redirectUrl, HttpMethod.GET, new HttpEntity<>(customHeader), String.class);
        String xmlString = responseEntity.getBody();
        ObjectMapper xmlMapper = new XmlMapper();
        Token token;
        try {
            token = xmlMapper.readValue(xmlString, Token.class);
            return token;
        } catch (IOException e) {
            throw new WeChatException("openNewLoginPage,解析xml失败", e);
        }
    }

    /**
     * Redirect to main page of wechat
     *
     * @param hostUrl hostUrl
     */
    public void redirect(String hostUrl) {
        final String url = hostUrl + "/";
        HttpHeaders customHeader = new HttpHeaders();
        customHeader.set(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        customHeader.set(HttpHeaders.REFERER, WECHAT_URL_ENTRY);
        customHeader.set("Upgrade-Insecure-Requests", "1");
        HeaderUtils.assign(customHeader, baseHeader);
        restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(customHeader), String.class);
        //It's now at main page.
        this.originValue = hostUrl;
        this.refererValue = hostUrl + "/";
    }

    /**
     * Initialization
     *
     * @param hostUrl     hostUrl
     * @param baseRequest baseRequest
     * @return current user's information and contact information
     * @throws IOException if the http response body can't be convert to {@link InitResponse}
     */
    public InitResponse init(String hostUrl, BaseRequest baseRequest) {
        String url = String.format(WECHAT_URL_INIT, hostUrl, WeChatUtils.generateDateWithBitwiseNot(), cacheService.getPassTicket());
        CookieStore store = (CookieStore) ((StatefulRestTemplate) restTemplate).getHttpContext().getAttribute(HttpClientContext.COOKIE_STORE);
        Date maxDate = new Date(Long.MAX_VALUE);
        String domain = hostUrl.replaceAll("https://", "").replaceAll("/", "");
        Map<String, String> cookies = new HashMap<>(3);
        cookies.put("MM_WX_NOTIFY_STATE", "1");
        cookies.put("MM_WX_SOUND_STATE", "1");
        appendAdditionalCookies(store, cookies, domain, "/", maxDate);
        InitRequest request = new InitRequest();
        request.setBaseRequest(baseRequest);
        HttpHeaders customHeader = new HttpHeaders();
        customHeader.set(HttpHeaders.REFERER, hostUrl + "/");
        customHeader.setOrigin(hostUrl);
        HeaderUtils.assign(customHeader, postHeader);
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request, customHeader), String.class);
        InitResponse initResponse;
        try {
            initResponse = jsonMapper.readValue(WeChatUtils.textDecode(responseEntity.getBody()), InitResponse.class);
            return initResponse;
        } catch (IOException e) {
            throw new WeChatException("init failed", e);
        }
    }

    /**
     * Notify mobile side once certain actions have been taken on web side.
     *
     * @param hostUrl     hostUrl
     * @param baseRequest baseRequest
     * @param userName    the userName of the user
     * @param code        {@link StatusNotifyCode}
     * @return the http response body
     * @throws IOException if the http response body can't be convert to {@link StatusNotifyResponse}
     */
    public StatusNotifyResponse statusNotify(String hostUrl, BaseRequest baseRequest, String userName, int code) {
        String rnd = String.valueOf(System.currentTimeMillis());
        final String url = String.format(WECHAT_URL_STATUS_NOTIFY, hostUrl, cacheService.getPassTicket());
        StatusNotifyRequest request = new StatusNotifyRequest();
        request.setBaseRequest(baseRequest);
        request.setFromUserName(userName);
        request.setToUserName(userName);
        request.setCode(code);
        request.setClientMsgId(rnd);
        HttpHeaders customHeader = new HttpHeaders();
        customHeader.set(HttpHeaders.REFERER, hostUrl + "/");
        customHeader.setOrigin(hostUrl);
        HeaderUtils.assign(customHeader, postHeader);
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request, customHeader), String.class);
        try {
            return jsonMapper.readValue(WeChatUtils.textDecode(responseEntity.getBody()), StatusNotifyResponse.class);
        } catch (IOException e) {
            throw new WeChatException("statusNotify failed", e);
        }
    }

    /**
     * Get all the contacts. If the Seq it returns is greater than zero, it means at least one more request is required to fetch all contacts.
     *
     * @param hostUrl hostUrl
     * @param skey    skey
     * @param seq     seq
     * @return contact information
     * @throws IOException if the http response body can't be convert to {@link GetContactResponse}
     */
    public GetContactResponse getContact(String hostUrl, String skey, long seq) {
        long rnd = System.currentTimeMillis();
        final String url = String.format(WECHAT_URL_GET_CONTACT, hostUrl, cacheService.getPassTicket(), rnd, seq, WeChatUtils.escape(skey));
        HttpHeaders customHeader = new HttpHeaders();
        customHeader.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.ALL));
        customHeader.set(HttpHeaders.REFERER, hostUrl + "/");
        HeaderUtils.assign(customHeader, baseHeader);
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(customHeader), String.class);
        try {
            return jsonMapper.readValue(WeChatUtils.textDecode(responseEntity.getBody()), GetContactResponse.class);
        } catch (IOException e) {
            throw new WeChatException("getContact failed", e);
        }
    }

    /**
     * Get all the members in the given chatrooms
     *
     * @param hostUrl     hostUrl
     * @param baseRequest baseRequest
     * @param list        chatroom information
     * @return chatroom members information
     * @throws IOException if the http response body can't be convert to {@link BatchGetContactResponse}
     */
    public BatchGetContactResponse batchGetContact(String hostUrl, BaseRequest baseRequest, ChatRoomDescription[] list) {
        long rnd = System.currentTimeMillis();
        String url = String.format(WECHAT_URL_BATCH_GET_CONTACT, hostUrl, rnd, cacheService.getPassTicket());
        BatchGetContactRequest request = new BatchGetContactRequest();
        request.setBaseRequest(baseRequest);
        request.setCount(list.length);
        request.setList(list);
        HttpHeaders customHeader = createPostCustomHeader();
        HeaderUtils.assign(customHeader, postHeader);
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request, customHeader), String.class);
        try {
            return jsonMapper.readValue(WeChatUtils.textDecode(responseEntity.getBody()), BatchGetContactResponse.class);
        } catch (IOException e) {
            throw new WeChatException("batchGetContact failed", e);
        }
    }

    /**
     * Periodically request to server
     *
     * @param hostUrl hostUrl
     * @param uin     uin
     * @param sid     sid
     * @param skey    skey
     * @param syncKey syncKey
     * @return synccheck response
     * @throws IOException        if the http response body can't be convert to {@link SyncCheckResponse}
     * @throws URISyntaxException if url is invalid
     */
    public SyncCheckResponse syncCheck(String hostUrl, String uin, String sid, String skey, SyncKey syncKey) {
        final String path = String.format(WECHAT_URL_SYNC_CHECK, hostUrl);
        final URI uri;
        try {
            URIBuilder builder = new URIBuilder(path);
            builder.addParameter("uin", uin);
            builder.addParameter("sid", sid);
            builder.addParameter("skey", skey);
            builder.addParameter("deviceid", WeChatUtils.generateDeviceId());
            builder.addParameter("synckey", syncKey.toString());
            builder.addParameter("r", String.valueOf(System.currentTimeMillis()));
            builder.addParameter("_", String.valueOf(System.currentTimeMillis()));
            uri = builder.build().toURL().toURI();
        } catch (Exception e) {
            throw new WeChatException("syncCheck failed", e);
        }
        HttpHeaders customHeader = new HttpHeaders();
        customHeader.setAccept(Arrays.asList(MediaType.ALL));
        customHeader.set(HttpHeaders.REFERER, hostUrl + "/");
        HeaderUtils.assign(customHeader, baseHeader);
        //长连接
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(customHeader), String.class);
        String body = responseEntity.getBody();
        Matcher matcher = PATTERN_SYNC_CHECK.matcher(body);
        if (!matcher.find()) {
            return null;
        } else {
            SyncCheckResponse result = new SyncCheckResponse();
            result.setRetcode(Integer.valueOf(matcher.group(1)));
            result.setSelector(Integer.valueOf(matcher.group(2)));
            return result;
        }
    }

    /**
     * Sync with server to get new messages and contacts
     *
     * @param hostUrl     hostUrl
     * @param syncKey     syncKey
     * @param baseRequest baseRequest
     * @return new messages and contacts
     * @throws IOException if the http response body can't be convert to {@link SyncResponse}
     */
    public SyncResponse sync(String hostUrl, SyncKey syncKey, BaseRequest baseRequest) {
        final String url = String.format(WECHAT_URL_SYNC, hostUrl, baseRequest.getSid(), WeChatUtils.escape(baseRequest.getSkey()), cacheService.getPassTicket());
        SyncRequest request = new SyncRequest();
        request.setBaseRequest(baseRequest);
        request.setRr(-System.currentTimeMillis() / 1000);
        request.setSyncKey(syncKey);
        HttpHeaders customHeader = createPostCustomHeader();
        HeaderUtils.assign(customHeader, postHeader);
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request, customHeader), String.class);
        try {
            return jsonMapper.readValue(WeChatUtils.textDecode(responseEntity.getBody()), SyncResponse.class);
        } catch (IOException e) {
            throw new WeChatException("sync failed", e);
        }
    }

    public VerifyUserResponse acceptFriend(String hostUrl, BaseRequest baseRequest, String passTicket, VerifyUser[] verifyUsers) {
        final int opCode = VerifyUserOPCode.VERIFYOK.getCode();
        final int[] sceneList = new int[]{AddScene.WEB.getCode()};
        final String path = String.format(WECHAT_URL_VERIFY_USER, hostUrl);
        VerifyUserRequest request = new VerifyUserRequest();
        request.setBaseRequest(baseRequest);
        request.setOpcode(opCode);
        request.setSceneList(sceneList);
        request.setSceneListCount(sceneList.length);
        request.setSkey(baseRequest.getSkey());
        request.setVerifyContent("");
        request.setVerifyUserList(verifyUsers);
        request.setVerifyUserListSize(verifyUsers.length);
        final URI uri;
        try {
            URIBuilder builder = new URIBuilder(path);
            builder.addParameter("r", String.valueOf(System.currentTimeMillis()));
            builder.addParameter("pass_ticket", passTicket);
            uri = builder.build().toURL().toURI();
        } catch (Exception e) {
            throw new WeChatException("acceptFriend failed", e);
        }
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(request, this.postHeader), String.class);
        try {
            return jsonMapper.readValue(WeChatUtils.textDecode(responseEntity.getBody()), VerifyUserResponse.class);
        } catch (IOException e) {
            throw new WeChatException("acceptFriend failed", e);
        }
    }

    public SendMsgResponse sendText(String hostUrl, BaseRequest baseRequest, String content, String fromUserName, String toUserName) {
        final int scene = 0;
        final String rnd = String.valueOf(System.currentTimeMillis() * 10);
        String passTicket = cacheService.getPassTicket();
        final String url = String.format(WECHAT_URL_SEND_MSG, hostUrl, passTicket);
        SendMsgRequest request = new SendMsgRequest();
        request.setBaseRequest(baseRequest);
        request.setScene(scene);
        BaseMsg msg = new BaseMsg();
        msg.setType(MessageType.TEXT.getCode());
        msg.setClientMsgId(rnd);
        msg.setContent(content);
        msg.setFromUserName(fromUserName);
        msg.setToUserName(toUserName);
        msg.setLocalID(rnd);
        request.setMsg(msg);
        HttpHeaders customHeader = createPostCustomHeader();
        HeaderUtils.assign(customHeader, postHeader);
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request, customHeader), String.class);
        SendMsgResponse sendMsgResponse = null;
        try {
            sendMsgResponse = jsonMapper.readValue(WeChatUtils.textDecode(responseEntity.getBody()), SendMsgResponse.class);
        } catch (IOException e) {
            logger.error("发送消息失败。", e);
        }
        return sendMsgResponse;
    }

    public OpLogResponse setAlias(String hostUrl, BaseRequest baseRequest, String newAlias, String userName) throws IOException {
        final int cmdId = OpLogCmdId.MODREMARKNAME.getCode();
        final String url = String.format(WECHAT_URL_OP_LOG, hostUrl);
        OpLogRequest request = new OpLogRequest();
        request.setBaseRequest(baseRequest);
        request.setCmdId(cmdId);
        request.setRemarkName(newAlias);
        request.setUserName(userName);
        HttpHeaders customHeader = createPostCustomHeader();
        HeaderUtils.assign(customHeader, postHeader);
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request, customHeader), String.class);
        return jsonMapper.readValue(WeChatUtils.textDecode(responseEntity.getBody()), OpLogResponse.class);
    }

    public CreateChatRoomResponse createChatRoom(String hostUrl, BaseRequest baseRequest, String[] userNames, String topic) throws IOException {
        String rnd = String.valueOf(System.currentTimeMillis());
        final String url = String.format(WECHAT_URL_CREATE_CHATROOM, hostUrl, rnd);
        CreateChatRoomRequest request = new CreateChatRoomRequest();
        request.setBaseRequest(baseRequest);
        request.setMemberCount(userNames.length);
        ChatRoomMember[] members = new ChatRoomMember[userNames.length];
        for (int i = 0; i < userNames.length; i++) {
            members[i] = new ChatRoomMember();
            members[i].setUserName(userNames[i]);
        }
        request.setMemberList(members);
        request.setTopic(topic);
        HttpHeaders customHeader = createPostCustomHeader();
        HeaderUtils.assign(customHeader, postHeader);
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request, customHeader), String.class);
        return jsonMapper.readValue(WeChatUtils.textDecode(responseEntity.getBody()), CreateChatRoomResponse.class);
    }

    public DeleteChatRoomMemberResponse deleteChatRoomMember(String hostUrl, BaseRequest baseRequest, String chatRoomUserName, String userName) throws IOException {
        final String url = String.format(WECHAT_URL_DELETE_CHATROOM_MEMBER, hostUrl);
        DeleteChatRoomMemberRequest request = new DeleteChatRoomMemberRequest();
        request.setBaseRequest(baseRequest);
        request.setChatRoomName(chatRoomUserName);
        request.setDelMemberList(userName);
        HttpHeaders customHeader = createPostCustomHeader();
        HeaderUtils.assign(customHeader, postHeader);
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request, customHeader), String.class);
        return jsonMapper.readValue(WeChatUtils.textDecode(responseEntity.getBody()), DeleteChatRoomMemberResponse.class);
    }

    public AddChatRoomMemberResponse addChatRoomMember(String hostUrl, BaseRequest baseRequest, String chatRoomUserName, String userName) throws IOException {
        final String url = String.format(WECHAT_URL_ADD_CHATROOM_MEMBER, hostUrl);
        AddChatRoomMemberRequest request = new AddChatRoomMemberRequest();
        request.setBaseRequest(baseRequest);
        request.setChatRoomName(chatRoomUserName);
        request.setAddMemberList(userName);
        HttpHeaders customHeader = createPostCustomHeader();
        HeaderUtils.assign(customHeader, postHeader);
        ResponseEntity<String> responseEntity
                = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request, customHeader), String.class);
        return jsonMapper.readValue(WeChatUtils.textDecode(responseEntity.getBody()), AddChatRoomMemberResponse.class);
    }

    public byte[] downloadImage(String url) {
        HttpHeaders customHeader = new HttpHeaders();
        customHeader.set("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
        customHeader.set("Referer", this.refererValue);
        HeaderUtils.assign(customHeader, baseHeader);
        ResponseEntity<byte[]> responseEntity
                = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(customHeader), new ParameterizedTypeReference<byte[]>() {
        });
        return responseEntity.getBody();
    }

    private void appendAdditionalCookies(CookieStore store, Map<String, String> cookies, String domain, String path, Date expiryDate) {
        cookies.forEach((key, value) -> {
            BasicClientCookie cookie = new BasicClientCookie(key, value);
            cookie.setDomain(domain);
            cookie.setPath(path);
            cookie.setExpiryDate(expiryDate);
            store.addCookie(cookie);
        });
    }

    private HttpHeaders createPostCustomHeader() {
        HttpHeaders customHeader = new HttpHeaders();
        customHeader.setOrigin(this.originValue);
        customHeader.set(HttpHeaders.REFERER, this.refererValue);
        return customHeader;
    }
}