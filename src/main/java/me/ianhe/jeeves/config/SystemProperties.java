package me.ianhe.jeeves.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author iHelin
 * @since 2018/8/13 22:41
 */
@Component
@ConfigurationProperties(prefix = "jeeves")
public class SystemProperties {

    private String instanceId;
    private Boolean autoReLogin;
    private Integer maxQrRefreshTimes;
    private String qnAccessKey;
    private String qnSecretKey;
    private String qnPrefix;
    private String qnBucket;
    private Boolean ide;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Boolean getAutoReLogin() {
        return autoReLogin;
    }

    public void setAutoReLogin(Boolean autoReLogin) {
        this.autoReLogin = autoReLogin;
    }

    public Integer getMaxQrRefreshTimes() {
        return maxQrRefreshTimes;
    }

    public void setMaxQrRefreshTimes(Integer maxQrRefreshTimes) {
        this.maxQrRefreshTimes = maxQrRefreshTimes;
    }

    public String getQnAccessKey() {
        return qnAccessKey;
    }

    public void setQnAccessKey(String qnAccessKey) {
        this.qnAccessKey = qnAccessKey;
    }

    public String getQnSecretKey() {
        return qnSecretKey;
    }

    public void setQnSecretKey(String qnSecretKey) {
        this.qnSecretKey = qnSecretKey;
    }

    public String getQnPrefix() {
        return qnPrefix;
    }

    public void setQnPrefix(String qnPrefix) {
        this.qnPrefix = qnPrefix;
    }

    public String getQnBucket() {
        return qnBucket;
    }

    public void setQnBucket(String qnBucket) {
        this.qnBucket = qnBucket;
    }

    public Boolean getIde() {
        return ide;
    }

    public void setIde(Boolean ide) {
        this.ide = ide;
    }
}
