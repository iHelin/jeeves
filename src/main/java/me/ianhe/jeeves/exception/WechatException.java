package me.ianhe.jeeves.exception;

/**
 * @author iHelin
 * @since 2018/8/15 15:08
 */
public class WeChatException extends RuntimeException {

    private Integer errCode;

    public WeChatException() {
    }

    public WeChatException(String message) {
        super(message);
    }

    public WeChatException(String message, Integer errCode) {
        super(message);
        this.errCode = errCode;
    }

    public WeChatException(Integer errCode) {
        this.errCode = errCode;
    }

    public WeChatException(String message, Throwable cause) {
        super(message, cause);
    }

    public WeChatException(Throwable cause) {
        super(cause);
    }

    public WeChatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
