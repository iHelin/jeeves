package me.ianhe.jeeves.domain.request.component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author iHelin
 * @since 2018/8/13 19:47
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseRequest {
    @JsonProperty
    private String Uin;
    @JsonProperty
    private String Sid;
    @JsonProperty
    private String Skey;
    @JsonProperty
    private String DeviceID;

    public String getUin() {
        return Uin;
    }

    public void setUin(String uin) {
        Uin = uin;
    }

    public String getSid() {
        return Sid;
    }

    public void setSid(String sid) {
        Sid = sid;
    }

    public String getSkey() {
        return Skey;
    }

    public void setSkey(String skey) {
        Skey = skey;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }
}
