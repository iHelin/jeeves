package me.ianhe.jeeves.domain.request.component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author iHelin
 * @since 2018/8/13 19:47
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseRequest {

    @JsonProperty("Uin")
    private String uin;
    @JsonProperty("Sid")
    private String sid;
    @JsonProperty("Skey")
    private String sKey;
    @JsonProperty("DeviceID")
    private String deviceID;

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSkey() {
        return sKey;
    }

    public void setSkey(String sKey) {
        this.sKey = sKey;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
