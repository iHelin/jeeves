package me.ianhe.jeeves.exception;

/**
 * @author linhe2
 * @since 2018/8/15 23:38
 */
public class WeChatException extends RuntimeException {

    public WeChatException() {

    }

    public WeChatException(String message) {
        super(message);
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
