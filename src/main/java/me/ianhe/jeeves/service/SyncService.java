package me.ianhe.jeeves.service;

import me.ianhe.jeeves.config.Constants;
import me.ianhe.jeeves.domain.response.SyncCheckResponse;
import me.ianhe.jeeves.domain.response.SyncResponse;
import me.ianhe.jeeves.domain.response.VerifyUserResponse;
import me.ianhe.jeeves.domain.shared.*;
import me.ianhe.jeeves.enums.MessageType;
import me.ianhe.jeeves.enums.RetCode;
import me.ianhe.jeeves.enums.Selector;
import me.ianhe.jeeves.exception.WeChatException;
import me.ianhe.jeeves.utils.WeChatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author iHelin
 * @since 2018/8/14 22:40
 */
@Component
public class SyncService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CacheService cacheService;
    @Autowired
    private WeChatHttpServiceInternal weChatHttpServiceInternal;
    @Autowired
    private MessageHandler messageHandler;

    @Value("${wechat.url.get_msg_img}")
    private String WECHAT_URL_GET_MSG_IMG;

    public void listen() throws IOException, URISyntaxException {
        SyncCheckResponse syncCheckResponse = weChatHttpServiceInternal.syncCheck(
                cacheService.getHostUrl(),
                cacheService.getBaseRequest().getUin(),
                cacheService.getBaseRequest().getSid(),
                cacheService.getBaseRequest().getSkey(),
                cacheService.getSyncKey());
        int retCode = syncCheckResponse.getRetcode();
        int selector = syncCheckResponse.getSelector();
        logger.info("[SYNCCHECK] retCode = {}, selector = {}", retCode, selector);
        if (retCode == RetCode.NORMAL.getCode()) {
            //有新消息
            if (selector == Selector.NEW_MESSAGE.getCode()) {
                onNewMessage();
            } else if (selector == Selector.ENTER_LEAVE_CHAT.getCode()) {
                sync();
            } else if (selector == Selector.CONTACT_UPDATED.getCode()) {
                sync();
            } else if (selector == Selector.UNKNOWN1.getCode()) {
                sync();
            } else if (selector == Selector.UNKNOWN6.getCode()) {
                sync();
            } else if (selector == Selector.NORMAL.getCode()) {
                //do nothing
            } else {
                logger.error("unknown selector:{}", selector);
                throw new WeChatException("syncCheckResponse selector = " + selector);
            }
        } else {
            throw new WeChatException("syncCheckResponse ret = " + retCode);
        }
    }

    private SyncResponse sync() throws IOException {
        SyncResponse syncResponse = weChatHttpServiceInternal.sync(cacheService.getHostUrl(), cacheService.getSyncKey(), cacheService.getBaseRequest());
        WeChatUtils.checkBaseResponse(syncResponse);
        cacheService.setSyncKey(syncResponse.getSyncKey());
        cacheService.setSyncCheckKey(syncResponse.getSyncCheckKey());
        //mod包含新增和修改
        if (syncResponse.getModContactCount() > 0) {
            onContactsModified(syncResponse.getModContactList());
        }
        //del->联系人移除
        if (syncResponse.getDelContactCount() > 0) {
            onContactsDeleted(syncResponse.getDelContactList());
        }
        return syncResponse;
    }

    private void acceptFriendInvitation(RecommendInfo info) throws IOException, URISyntaxException {
        VerifyUser user = new VerifyUser();
        user.setValue(info.getUserName());
        user.setVerifyUserTicket(info.getTicket());
        VerifyUserResponse verifyUserResponse = weChatHttpServiceInternal.acceptFriend(
                cacheService.getHostUrl(),
                cacheService.getBaseRequest(),
                cacheService.getPassTicket(),
                new VerifyUser[]{user}
        );
        WeChatUtils.checkBaseResponse(verifyUserResponse);
    }

    private void onNewMessage() throws IOException, URISyntaxException {
        SyncResponse syncResponse = sync();
        if (syncResponse.getAddMsgCount() <= 0) {
            WeChatUtils.sleep();
        } else {
            for (Message message : syncResponse.getAddMsgList()) {
                //文本消息
                if (message.getMsgType() == MessageType.TEXT.getCode()) {
                    cacheService.getContactNamesWithUnreadMessage().add(message.getFromUserName());
                    //个人
                    if (WeChatUtils.isMessageFromIndividual(message)) {
                        messageHandler.onReceivingPrivateTextMessage(message);
                    }
                    //群
                    else if (WeChatUtils.isMessageFromChatRoom(message)) {
                        messageHandler.onReceivingChatRoomTextMessage(message);
                    }
                    //图片
                } else if (message.getMsgType() == MessageType.IMAGE.getCode()) {
                    cacheService.getContactNamesWithUnreadMessage().add(message.getFromUserName());
                    String fullImageUrl = String.format(WECHAT_URL_GET_MSG_IMG, cacheService.getHostUrl(), message.getMsgId(), cacheService.getsKey());
                    String thumbImageUrl = fullImageUrl + "&type=slave";
                    //个人
                    if (WeChatUtils.isMessageFromIndividual(message)) {
                        messageHandler.onReceivingPrivateImageMessage(message, thumbImageUrl, fullImageUrl);
                    }
                    //群
                    else if (WeChatUtils.isMessageFromChatRoom(message)) {
                        messageHandler.onReceivingChatRoomImageMessage(message, thumbImageUrl, fullImageUrl);
                    }
                }
                //系统消息
                else if (message.getMsgType() == MessageType.SYS.getCode()) {
                    //红包
                    if (Constants.RED_PACKET_CONTENT.equals(message.getContent())) {
                        logger.info("[*] you've received a red packet");
                        String from = message.getFromUserName();
                        Set<Contact> contacts = null;
                        //个人
                        if (WeChatUtils.isMessageFromIndividual(message)) {
                            contacts = cacheService.getIndividuals();
                        }
                        //群
                        else if (WeChatUtils.isMessageFromChatRoom(message)) {
                            contacts = cacheService.getChatRooms();
                        }
                        if (contacts != null) {
                            Contact contact = contacts.stream().filter(x -> Objects.equals(x.getUserName(), from)).findAny().orElse(null);
                            messageHandler.onRedPacketReceived(contact);
                        }
                    }
                }
                //好友邀请
                else if (message.getMsgType() == MessageType.VERIFYMSG.getCode() && cacheService.getOwner().getUserName().equals(message.getToUserName())) {
                    if (messageHandler.onReceivingFriendInvitation(message.getRecommendInfo())) {
                        acceptFriendInvitation(message.getRecommendInfo());
                        logger.info("[*] you've accepted the invitation");
                        messageHandler.postAcceptFriendInvitation(message);
                    } else {
                        logger.info("[*] you've declined the invitation");
                    }
                } else if (message.getMsgType() == MessageType.APP.getCode()) {
                    logger.info("app || link 消息");
                    messageHandler.onReceiveAppMsg(message);
                } else if (message.getMsgType() == MessageType.TAPEVENT.getCode()) {
                    logger.info("tap msg");
                } else if (message.getMsgType() == MessageType.VOICE.getCode()) {
                    logger.info("voice msg");
                } else if (message.getMsgType() == MessageType.RECALLED.getCode()) {
                    logger.info("recall msg");
                } else {
                    logger.info("msg type:{}", message.getMsgType());
                }
            }
        }
    }

    private void onContactsModified(Set<Contact> contacts) {
        Set<Contact> individuals = new HashSet<>();
        Set<Contact> chatRooms = new HashSet<>();
        Set<Contact> mediaPlatforms = new HashSet<>();

        for (Contact contact : contacts) {
            if (WeChatUtils.isIndividual(contact)) {
                individuals.add(contact);
            } else if (WeChatUtils.isMediaPlatform(contact)) {
                mediaPlatforms.add(contact);
            } else if (WeChatUtils.isChatRoom(contact)) {
                chatRooms.add(contact);
            }
        }

        //individual
        if (individuals.size() > 0) {
            Set<Contact> existingIndividuals = cacheService.getIndividuals();
            Set<Contact> newIndividuals = individuals.stream().filter(x -> !existingIndividuals.contains(x)).collect(Collectors.toSet());
            individuals.forEach(x -> {
                existingIndividuals.remove(x);
                existingIndividuals.add(x);
            });
            if (messageHandler != null && newIndividuals.size() > 0) {
                messageHandler.onNewFriendsFound(newIndividuals);
            }
        }
        //chatroom
        if (chatRooms.size() > 0) {
            Set<Contact> existingChatRooms = cacheService.getChatRooms();
            Set<Contact> newChatRooms = new HashSet<>();
            Set<Contact> modifiedChatRooms = new HashSet<>();
            for (Contact chatRoom : chatRooms) {
                if (existingChatRooms.contains(chatRoom)) {
                    modifiedChatRooms.add(chatRoom);
                } else {
                    newChatRooms.add(chatRoom);
                }
            }
            existingChatRooms.addAll(newChatRooms);
            if (messageHandler != null && newChatRooms.size() > 0) {
                messageHandler.onNewChatRoomsFound(newChatRooms);
            }
            for (Contact chatRoom : modifiedChatRooms) {
                Contact existingChatRoom = existingChatRooms.stream().filter(x -> x.getUserName().equals(chatRoom.getUserName())).findFirst().orElse(null);
                if (existingChatRoom == null) {
                    continue;
                }
                existingChatRooms.remove(existingChatRoom);
                existingChatRooms.add(chatRoom);
                if (messageHandler != null) {
                    Set<ChatRoomMember> oldMembers = existingChatRoom.getMemberList();
                    Set<ChatRoomMember> newMembers = chatRoom.getMemberList();
                    Set<ChatRoomMember> joined = newMembers.stream().filter(x -> !oldMembers.contains(x)).collect(Collectors.toSet());
                    Set<ChatRoomMember> left = oldMembers.stream().filter(x -> !newMembers.contains(x)).collect(Collectors.toSet());
                    if (joined.size() > 0 || left.size() > 0) {
                        messageHandler.onChatRoomMembersChanged(chatRoom, joined, left);
                    }
                }
            }
        }
        if (mediaPlatforms.size() > 0) {
            //media platform
            Set<Contact> existingPlatforms = cacheService.getMediaPlatforms();
            Set<Contact> newMediaPlatforms = existingPlatforms.stream().filter(x -> !existingPlatforms.contains(x)).collect(Collectors.toSet());
            mediaPlatforms.forEach(x -> {
                existingPlatforms.remove(x);
                existingPlatforms.add(x);
            });
            if (messageHandler != null && newMediaPlatforms.size() > 0) {
                messageHandler.onNewMediaPlatformsFound(newMediaPlatforms);
            }
        }
    }

    private void onContactsDeleted(Set<Contact> contacts) {
        Set<Contact> individuals = new HashSet<>();
        Set<Contact> chatRooms = new HashSet<>();
        Set<Contact> mediaPlatforms = new HashSet<>();
        for (Contact contact : contacts) {
            if (WeChatUtils.isIndividual(contact)) {
                individuals.add(contact);
                cacheService.getIndividuals().remove(contact);
            } else if (WeChatUtils.isChatRoom(contact)) {
                chatRooms.add(contact);
                cacheService.getChatRooms().remove(contact);
            } else if (WeChatUtils.isMediaPlatform(contact)) {
                mediaPlatforms.add(contact);
                cacheService.getMediaPlatforms().remove(contact);
            }
        }
        if (messageHandler != null) {
            if (individuals.size() > 0) {
                messageHandler.onFriendsDeleted(individuals);
            }
            if (chatRooms.size() > 0) {
                messageHandler.onChatRoomsDeleted(chatRooms);
            }
            if (mediaPlatforms.size() > 0) {
                messageHandler.onMediaPlatformsDeleted(mediaPlatforms);
            }
        }
    }
}
