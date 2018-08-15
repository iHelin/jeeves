package me.ianhe.jeeves.domain.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author linhe2
 * @since 2018/8/15 20:40
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyUser {
    @JsonProperty
    private String Value;
    @JsonProperty
    private String VerifyUserTicket;

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getVerifyUserTicket() {
        return VerifyUserTicket;
    }

    public void setVerifyUserTicket(String verifyUserTicket) {
        VerifyUserTicket = verifyUserTicket;
    }
}
