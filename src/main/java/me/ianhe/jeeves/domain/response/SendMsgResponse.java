package me.ianhe.jeeves.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.ianhe.jeeves.domain.response.component.AbstractWeChatHttpResponseBase;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SendMsgResponse extends AbstractWeChatHttpResponseBase {
    @JsonProperty
    private String MsgID;
    @JsonProperty
    private String LocalID;

    public String getMsgID() {
        return MsgID;
    }

    public void setMsgID(String msgID) {
        MsgID = msgID;
    }

    public String getLocalID() {
        return LocalID;
    }

    public void setLocalID(String localID) {
        LocalID = localID;
    }
}