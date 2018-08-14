package me.ianhe.jeeves.enums;

/**
 * @author linhe2
 * @since 2018/8/14 21:55
 */
public enum RetCode {
    /**
     * 正常
     */
    NORMAL(0),
    LOGOUT1(1100),
    /**
     * 其它地方登录
     */
    LOGOUT2(1101),
    /**
     * 移动端退出
     */
    LOGOUT3(1102);

    private final int code;

    RetCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
