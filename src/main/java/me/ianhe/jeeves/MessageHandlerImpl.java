package me.ianhe.jeeves;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import me.ianhe.jeeves.config.Constants;
import me.ianhe.jeeves.dao.WechatMsgMapper;
import me.ianhe.jeeves.domain.shared.*;
import me.ianhe.jeeves.entity.WechatMsg;
import me.ianhe.jeeves.service.CacheService;
import me.ianhe.jeeves.service.MessageHandler;
import me.ianhe.jeeves.service.QiniuStoreService;
import me.ianhe.jeeves.service.WeChatHttpService;
import me.ianhe.jeeves.utils.MessageUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author iHelin
 * @since 2018/8/13 18:56
 */
@Component
public class MessageHandlerImpl implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MessageHandlerImpl.class);

    @Autowired
    private WeChatHttpService wechatHttpService;
    @Autowired
    private QiniuStoreService qiniuStoreService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private WechatMsgMapper wechatMsgMapper;

    @Override
    public void onReceivingChatRoomTextMessage(Message message) {
        logger.debug("ChatRoom text msg...");
        WechatMsg msg = new WechatMsg();
        msg.setDisplayFromName(cacheService.getDisplayChatRoomName(message.getFromUserName(),
                MessageUtils.getSenderOfChatRoomTextMessage(message.getContent())));
        msg.setMsgType(message.getMsgType());
        msg.setContent(MessageUtils.getChatRoomTextMessageContent(message.getContent()));
        msg.setCreateTime(new Date(message.getCreateTime() * 1000L));
        msg.setPrivateMsg(Boolean.FALSE);
        wechatMsgMapper.insert(msg);
    }

    @Override
    public void onReceivingPrivateTextMessage(Message message) {
        logger.info("private text msg...");
        WechatMsg msg = new WechatMsg();
        msg.setDisplayFromName(cacheService.getDisplayUserName(message.getFromUserName()));
        msg.setMsgType(message.getMsgType());
        msg.setContent(message.getContent());
        msg.setCreateTime(new Date(message.getCreateTime() * 1000L));
        msg.setPrivateMsg(Boolean.TRUE);
        wechatMsgMapper.insert(msg);
    }

    @Override
    public void onReceivingChatRoomImageMessage(Message message, String thumbImageUrl, String fullImageUrl) {
        logger.info("onReceivingChatRoomImageMessage");
        logger.info("thumbImageUrl:" + thumbImageUrl);
        logger.info("fullImageUrl:" + fullImageUrl);
        byte[] data = wechatHttpService.downloadImage(fullImageUrl);
        String imgUrl = qiniuStoreService.uploadFile("jeeves/chatroom/" + UUID.randomUUID().toString(), data);
        WechatMsg msg = new WechatMsg();
        msg.setDisplayFromName(cacheService.getDisplayChatRoomName(message.getFromUserName(),
                MessageUtils.getSenderOfChatRoomTextMessage(message.getContent())));
        msg.setMsgType(message.getMsgType());
        msg.setContent(imgUrl);
        msg.setCreateTime(new Date(message.getCreateTime() * 1000L));
        msg.setPrivateMsg(Boolean.FALSE);
        wechatMsgMapper.insert(msg);
    }

    @Override
    public void onReceivingPrivateImageMessage(Message message, String thumbImageUrl, String fullImageUrl) throws IOException {
        logger.info("onReceivingPrivateImageMessage");
        byte[] data = wechatHttpService.downloadImage(fullImageUrl);
        String imgUrl = qiniuStoreService.uploadFile("jeeves/private/" + UUID.randomUUID().toString(), data);
        WechatMsg msg = new WechatMsg();
        msg.setDisplayFromName(cacheService.getDisplayUserName(message.getFromUserName()));
        msg.setMsgType(message.getMsgType());
        msg.setContent(imgUrl);
        msg.setCreateTime(new Date(message.getCreateTime() * 1000L));
        msg.setPrivateMsg(Boolean.TRUE);
        wechatMsgMapper.insert(msg);
    }

    @Override
    public boolean onReceivingFriendInvitation(RecommendInfo info) {
        logger.info("onReceivingFriendInvitation");
        logger.info("recommendinfo content:" + info.getContent());
//        默认接收所有的邀请
        return true;
    }

    @Override
    public void postAcceptFriendInvitation(Message message) throws IOException {
        logger.info("postAcceptFriendInvitation");
//        将该用户的微信号设置成他的昵称
        String content = StringEscapeUtils.unescapeXml(message.getContent());
        ObjectMapper xmlMapper = new XmlMapper();
        FriendInvitationContent friendInvitationContent = xmlMapper.readValue(content, FriendInvitationContent.class);
        wechatHttpService.setAlias(message.getRecommendInfo().getUserName(), friendInvitationContent.getFromusername());
    }

    @Override
    public void onChatRoomMembersChanged(Contact chatRoom, Set<ChatRoomMember> membersJoined, Set<ChatRoomMember> membersLeft) {
        logger.info("onChatRoomMembersChanged");
        logger.info("chatRoom:" + chatRoom.getUserName());
        if (membersJoined != null && membersJoined.size() > 0) {
            logger.info("membersJoined:" + String.join(",", membersJoined.stream().map(ChatRoomMember::getNickName).collect(Collectors.toList())));
        }
        if (membersLeft != null && membersLeft.size() > 0) {
            logger.info("membersLeft:" + String.join(",", membersLeft.stream().map(ChatRoomMember::getNickName).collect(Collectors.toList())));
        }
    }

    @Override
    public void onNewChatRoomsFound(Set<Contact> chatRooms) {
        logger.info("onNewChatRoomsFound");
        chatRooms.forEach(x -> logger.info(x.getUserName()));
    }

    @Override
    public void onChatRoomsDeleted(Set<Contact> chatRooms) {
        logger.info("onChatRoomsDeleted");
        chatRooms.forEach(x -> logger.info(x.getUserName()));
    }

    @Override
    public void onNewFriendsFound(Set<Contact> contacts) {
        logger.info("onNewFriendsFound");
        contacts.forEach(x -> {
            logger.info(x.getUserName());
            logger.info(x.getNickName());
        });
    }

    @Override
    public void onFriendsDeleted(Set<Contact> contacts) {
        logger.info("onFriendsDeleted");
        contacts.forEach(x -> {
            logger.info(x.getUserName());
            logger.info(x.getNickName());
        });
    }

    @Override
    public void onNewMediaPlatformsFound(Set<Contact> mps) {
        logger.info("onNewMediaPlatformsFound");
    }

    @Override
    public void onMediaPlatformsDeleted(Set<Contact> mps) {
        logger.info("onMediaPlatformsDeleted");
    }

    @Override
    public void onRedPacketReceived(Contact contact) {
        logger.info("onRedPacketReceived");
        if (contact != null) {
            logger.info("the red packet is from " + contact.getNickName());
        }
    }

    @Override
    public void onReceiveAppMsg(Message message) {
        logger.info(message.getFileName());
        Matcher matcher = Pattern.compile(Constants.BAI_CI_ZHAN).matcher(message.getFileName());
        if (matcher.find()) {
            logger.info("{},{}", matcher.group(1), matcher.group(2));
            String userName = cacheService.getUserNameByNickName(Constants.DEST_CHATROOM_NAME);
            wechatHttpService.sendText(userName, "骚年，加油哦！");
        }
        logger.info(cacheService.getDisplayChatRoomName(message.getFromUserName(), MessageUtils.getSenderOfChatRoomTextMessage(message.getContent())));
        logger.info(StringEscapeUtils.unescapeXml(message.getContent()));
        logger.info(message.getUrl());
    }
}
