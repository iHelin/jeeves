package me.ianhe.jeeves.service;

import me.ianhe.jeeves.domain.shared.ChatRoomMember;
import me.ianhe.jeeves.domain.shared.Contact;
import me.ianhe.jeeves.domain.shared.Message;
import me.ianhe.jeeves.domain.shared.RecommendInfo;

import java.io.IOException;
import java.util.Set;

public interface MessageHandler {
    /**
     * 事件：收到群聊天文本消息
     *
     * @param message 消息体
     */
    default void onReceivingChatRoomTextMessage(Message message) {
    }

    /**
     * 事件：收到群聊天图片消息
     *
     * @param message       消息体
     * @param thumbImageUrl 图片缩略图链接
     * @param fullImageUrl  图片完整图链接
     */
    default void onReceivingChatRoomImageMessage(Message message, String thumbImageUrl, String fullImageUrl) {
    }

    /**
     * 事件：收到个人聊天文本消息
     *
     * @param message 消息体
     */
    default void onReceivingPrivateTextMessage(Message message) throws IOException {
    }

    /**
     * 事件：收到个人聊天图片消息
     *
     * @param message       消息体
     * @param thumbImageUrl 图片缩略图链接
     * @param fullImageUrl  图片完整图链接
     */
    default void onReceivingPrivateImageMessage(Message message, String thumbImageUrl, String fullImageUrl) throws IOException {
    }

    /**
     * 事件：收到加好友邀请
     *
     * @param info 邀请信息
     * @return {@code true} 如果接受请求, 否则 {@code false}
     */
    default boolean onReceivingFriendInvitation(RecommendInfo info) {
        return true;
    }

    /**
     * 事件：接受好友邀请成功
     *
     * @param message 消息体
     */
    default void postAcceptFriendInvitation(Message message) throws IOException {
    }

    /**
     * 事件：群成员发生变化
     *
     * @param chatRoom      群
     * @param membersJoined 新加入的群成员
     * @param membersLeft   离开的群成员
     */
    default void onChatRoomMembersChanged(Contact chatRoom, Set<ChatRoomMember> membersJoined, Set<ChatRoomMember> membersLeft) {
    }

    /**
     * 事件：发现新增群（例如加入了新群）
     *
     * @param chatRooms 新增的群
     */
    default void onNewChatRoomsFound(Set<Contact> chatRooms) {
    }

    /**
     * 事件：发现群减少（例如被踢出了群）
     *
     * @param chatRooms 减少的群
     */
    default void onChatRoomsDeleted(Set<Contact> chatRooms) {
    }

    /**
     * 事件：发现新的好友
     *
     * @param contacts 新的好友
     */
    default void onNewFriendsFound(Set<Contact> contacts) {
    }

    /**
     * 事件：发现好友减少
     *
     * @param contacts 减少的好友
     */
    default void onFriendsDeleted(Set<Contact> contacts) {
    }

    /**
     * 事件：发现新的公众号
     *
     * @param mps 新的公众号
     */
    default void onNewMediaPlatformsFound(Set<Contact> mps) {
    }

    /**
     * 事件：删除公众号
     *
     * @param mps 被删除的公众号
     */
    default void onMediaPlatformsDeleted(Set<Contact> mps) {
    }

    /**
     * 事件：收到红包（个人的或者群里的）
     *
     * @param contact 发红包的个人或者群
     */
    default void onRedPacketReceived(Contact contact) {
    }

    /**
     * 收到链接消息等
     *
     * @param message 消息内容
     */
    default void onReceiveAppMsg(Message message) {
    }

}
