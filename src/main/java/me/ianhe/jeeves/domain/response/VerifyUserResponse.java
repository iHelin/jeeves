package me.ianhe.jeeves.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.ianhe.jeeves.domain.response.component.AbstractWeChatHttpResponseBase;

/**
 * @author iHelin
 * @since 2018/8/15 09:45
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyUserResponse extends AbstractWeChatHttpResponseBase {

    @JsonProperty
    private String MsgID;

    public String getMsgID() {
        return MsgID;
    }

    public void setMsgID(String msgID) {
        MsgID = msgID;
    }
}
