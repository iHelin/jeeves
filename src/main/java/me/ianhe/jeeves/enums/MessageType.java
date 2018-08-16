package me.ianhe.jeeves.enums;

/**
 * @author iHelin
 * @since 2018/8/14 22:51
 */
public enum MessageType {

    /**
     * 文本
     */
    TEXT(1),
    /**
     * 图片
     */
    IMAGE(3),
    /**
     * 语音
     */
    VOICE(34),
    /**
     * 视频
     */
    VIDEO(43),
    /**
     * 短视屏
     */
    MICROVIDEO(62),
    /**
     *
     */
    EMOTICON(47),
    /**
     * 链接、app消息
     */
    APP(49),
    /**
     *
     */
    VOIPMSG(50),
    /**
     * 点击
     */
    TAPEVENT(51),
    /**
     *
     */
    VOIPNOTIFY(52),
    /**
     *
     */
    VOIPINVITE(53),
    /**
     * 地理位置消息
     */
    LOCATION(48),
    /**
     *
     */
    STATUSNOTIFY(51),
    /**
     *
     */
    SYSNOTICE(9999),
    /**
     *
     */
    POSSIBLEFRIEND_MSG(40),
    /**
     * 好友邀请
     */
    VERIFYMSG(37),
    /**
     *
     */
    SHARECARD(42),
    /**
     * 系统消息
     */
    SYS(10000),
    /**
     * RECALLED
     */
    RECALLED(10002);

    private final int code;

    MessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
