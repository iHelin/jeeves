package me.ianhe.jeeves.domain.response;

/**
 * @author iHelin
 * @since 2018/8/15 09:46
 */
public class LoginResult {
    private String code;
    private String redirectUrl;
    private String hostUrl;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }
}