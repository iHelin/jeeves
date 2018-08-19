package me.ianhe.jeeves;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import me.ianhe.jeeves.config.Constants;
import me.ianhe.jeeves.domain.shared.*;
import me.ianhe.jeeves.service.CacheService;
import me.ianhe.jeeves.service.MessageHandler;
import me.ianhe.jeeves.service.QiniuStoreService;
import me.ianhe.jeeves.service.WeChatHttpService;
import me.ianhe.jeeves.utils.DingUtils;
import me.ianhe.jeeves.utils.MessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    private StringRedisTemplate redisTemplate;

    @Override
    public void onReceivingChatRoomTextMessage(Message message) {
        try {
            logger.info("onReceivingChatRoomTextMessage");
            logger.info("{}:{}", cacheService.getDisplayChatRoomMemberName(message.getFromUserName(),
                    MessageUtils.getSenderOfChatRoomTextMessage(message.getContent())),
                    MessageUtils.getChatRoomTextMessageContent(message.getContent()));
        } catch (Exception e) {
            logger.error("onReceivingChatRoomTextMessage error.", e);
        }
    }

    @Override
    public void onReceivingPrivateTextMessage(Message message) {
        try {
            logger.info("onReceivingPrivateTextMessage");
            logger.info("{}:{}", cacheService.getDisplayUserName(message.getFromUserName()), message.getContent());
        } catch (Exception e) {
            logger.error("onReceivingPrivateTextMessage error.", e);
        }
    }

    @Override
    public void onReceivingChatRoomImageMessage(Message message, String thumbImageUrl, String fullImageUrl) {
        try {
            logger.info("onReceivingChatRoomImageMessage");
            logger.info("thumbImageUrl:" + thumbImageUrl);
            logger.info("fullImageUrl:" + fullImageUrl);
            byte[] data = wechatHttpService.downloadImage(fullImageUrl);
            logger.info("chatroom image:{}", cacheService.getDisplayChatRoomMemberName(message.getFromUserName(),
                    MessageUtils.getSenderOfChatRoomTextMessage(message.getContent())) + "/");
            qiniuStoreService.uploadFile("jeeves/chatroom/" + UUID.randomUUID().toString(), data);
        } catch (Exception e) {
            logger.error("onReceivingChatRoomImageMessage error.", e);
        }

    }

    @Override
    public void onReceivingPrivateImageMessage(Message message, String thumbImageUrl, String fullImageUrl) throws IOException {
        try {
            logger.info("onReceivingPrivateImageMessage");
            logger.info("thumbImageUrl:" + thumbImageUrl);
            logger.info("fullImageUrl:" + fullImageUrl);
            byte[] data = wechatHttpService.downloadImage(fullImageUrl);
            qiniuStoreService.uploadFile("jeeves/private/" + UUID.randomUUID().toString(), data);
        } catch (Exception e) {
            logger.error("onReceivingPrivateImageMessage error.", e);
        }
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
            DingUtils.send("收到红包啦，红包来自" + contact.getUserName());
        } else {
            DingUtils.send("收到红包啦，快去抢啊。");
        }
    }

    @Override
    public void onReceiveAppMsg(Message message) {
        try {
            logger.info(message.getFileName());
            Matcher matcher = Pattern.compile(Constants.BAI_CI_ZHAN).matcher(message.getFileName());
            if (matcher.find()) {
                Long userMaxDays = Long.valueOf(matcher.group(1));
                String maxDaysStr = redisTemplate.opsForValue().get("jeeves:maxDays");
                Long maxDays = 0L;
                if (StringUtils.isNotEmpty(maxDaysStr)) {
                    maxDays = Long.valueOf(maxDaysStr);
                }
                String destUserName = cacheService.getUserNameByNickName(Constants.CHATROOM_NAME_STUDY);
                if (userMaxDays > maxDays) {
                    redisTemplate.opsForValue().set("jeeves:maxDays", String.valueOf(userMaxDays));
                    redisTemplate.opsForValue().set("jeeves:maxDaysUserName", cacheService.getDisplayUserName(MessageUtils.getSenderOfChatRoomTextMessage(message.getContent())));
                    wechatHttpService.sendText(destUserName, "恭喜你，目前你是第一名，继续加油。");
                } else {
                    String maxDaysUserName = redisTemplate.opsForValue().get("jeeves:maxDaysUserName");
                    wechatHttpService.sendText(destUserName, "目前第一名是" + maxDaysUserName + "，一共坚持了" + maxDaysStr + "天，骚年，要加油哦！");
                }
            }
        } catch (Exception e) {
            logger.error("onReceiveAppMsg error.", e);
        }
    }
}
