package me.ianhe.jeeves.service;

import me.ianhe.jeeves.domain.response.*;
import me.ianhe.jeeves.domain.shared.ChatRoomDescription;
import me.ianhe.jeeves.domain.shared.Contact;
import me.ianhe.jeeves.enums.StatusNotifyCode;
import me.ianhe.jeeves.exception.WeChatException;
import me.ianhe.jeeves.utils.WeChatUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/**
 * @author iHelin
 * @since 2018/8/13 22:06
 */
@Component
public class WeChatHttpService {

    @Autowired
    private WeChatHttpServiceInternal wechatHttpServiceInternal;
    @Autowired
    private CacheService cacheService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Send plain text to a contact (not chatroom)
     *
     * @param userName the username of the contact
     * @param content  the content of text
     * @throws IOException if sendText fails
     */
    public void sendText(String userName, String content) throws IOException {
        if (StringUtils.isNotEmpty(userName)) {
            if (!userName.equals(cacheService.getOwner().getUserName())) {
                notifyNecessary(userName);
                SendMsgResponse response = wechatHttpServiceInternal.sendText(cacheService.getHostUrl(), cacheService.getBaseRequest(), content, cacheService.getOwner().getUserName(), userName);
                WeChatUtils.checkBaseResponse(response);
            } else {
                logger.warn("不能发送给自己");
            }
        }
    }

    /**
     * Set the alias of a contact
     *
     * @param userName the username of the contact
     * @param newAlias alias
     * @throws IOException if setAlias fails
     */
    public void setAlias(String userName, String newAlias) throws IOException {
        OpLogResponse response = wechatHttpServiceInternal.setAlias(cacheService.getHostUrl(), cacheService.getBaseRequest(), newAlias, userName);
        WeChatUtils.checkBaseResponse(response);
    }

    /**
     * Create a chatroom with a topic.
     * In fact, a topic is usually not provided when creating the chatroom.
     *
     * @param userNames the usernames of the contacts who are invited to the chatroom.
     * @param topic     the topic(or nickname)
     * @throws IOException
     */
    public void createChatRoom(String[] userNames, String topic) throws IOException {
        CreateChatRoomResponse response = wechatHttpServiceInternal.createChatRoom(cacheService.getHostUrl(), cacheService.getBaseRequest(), userNames, topic);
        WeChatUtils.checkBaseResponse(response);
        //invoke BatchGetContact after CreateChatRoom
        ChatRoomDescription description = new ChatRoomDescription();
        description.setUserName(response.getChatRoomName());
        ChatRoomDescription[] descriptions = new ChatRoomDescription[]{description};
        BatchGetContactResponse batchGetContactResponse = wechatHttpServiceInternal.batchGetContact(cacheService.getHostUrl(), cacheService.getBaseRequest(), descriptions);
        WeChatUtils.checkBaseResponse(batchGetContactResponse);
        cacheService.getChatRooms().addAll(batchGetContactResponse.getContactList());
    }

    /**
     * Delete a contact from a certain chatroom (if you're the owner!)
     *
     * @param chatRoomUserName chatroom username
     * @param userName         contact username
     * @throws IOException if remove chatroom member fails
     */
    public void deleteChatRoomMember(String chatRoomUserName, String userName) throws IOException {
        DeleteChatRoomMemberResponse response = wechatHttpServiceInternal.deleteChatRoomMember(cacheService.getHostUrl(), cacheService.getBaseRequest(), chatRoomUserName, userName);
        WeChatUtils.checkBaseResponse(response);
    }

    /**
     * Invite a contact to a certain chatroom
     *
     * @param chatRoomUserName chatroom username
     * @param userName         contact username
     * @throws IOException if add chatroom member fails
     */
    public void addChatRoomMember(String chatRoomUserName, String userName) throws IOException {
        AddChatRoomMemberResponse response = wechatHttpServiceInternal.addChatRoomMember(cacheService.getHostUrl(), cacheService.getBaseRequest(), chatRoomUserName, userName);
        WeChatUtils.checkBaseResponse(response);
        Contact chatRoom = cacheService.getChatRooms().stream().filter(x -> chatRoomUserName.equals(x.getUserName())).findFirst().orElse(null);
        if (chatRoom == null) {
            throw new WeChatException("can't find " + chatRoomUserName);
        }
        chatRoom.getMemberList().addAll(response.getMemberList());
    }

    /**
     * download images in the conversation. Note that it's better not to download image directly. This method has included cookies in the request.
     *
     * @param url image url
     * @return image data
     */
    public byte[] downloadImage(String url) {
        return wechatHttpServiceInternal.downloadImage(url);
    }

    /**
     * notify the server that all the messages in the conversation between {@code userName} and me have been read.
     *
     * @param userName the contact with whom I need to set the messages read.
     * @throws IOException if statusNotify fails.
     */
    private void notifyNecessary(String userName) throws IOException {
        if (userName == null) {
            throw new IllegalArgumentException("userName");
        }
        Set<String> unreadContacts = cacheService.getContactNamesWithUnreadMessage();
        if (unreadContacts.contains(userName)) {
            wechatHttpServiceInternal.statusNotify(cacheService.getHostUrl(), cacheService.getBaseRequest(), userName, StatusNotifyCode.READED.getCode());
            unreadContacts.remove(userName);
        }
    }
}
