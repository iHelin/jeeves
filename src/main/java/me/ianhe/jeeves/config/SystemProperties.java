package me.ianhe.jeeves.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author linhe2
 * @since 2018/8/13 22:41
 */
@Component
@ConfigurationProperties(prefix = "my.system")
public class SystemProperties {

    /**
     * 七牛存储
     */
    private String qnAccessKey;
    private String qnSecretKey;
    private String qnPrefix;
    private String qnBucket;

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
}
