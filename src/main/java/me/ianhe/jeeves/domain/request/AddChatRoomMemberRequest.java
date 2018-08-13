package me.ianhe.jeeves.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.ianhe.jeeves.domain.request.component.BaseRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddChatRoomMemberRequest {

    @JsonProperty
    private me.ianhe.jeeves.domain.request.component.BaseRequest BaseRequest;
    @JsonProperty
    private String ChatRoomName;
    @JsonProperty
    private String AddMemberList;

    public BaseRequest getBaseRequest() {
        return BaseRequest;
    }

    public void setBaseRequest(BaseRequest baseRequest) {
        BaseRequest = baseRequest;
    }

    public String getChatRoomName() {
        return ChatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        ChatRoomName = chatRoomName;
    }

    public String getAddMemberList() {
        return AddMemberList;
    }

    public void setAddMemberList(String addMemberList) {
        AddMemberList = addMemberList;
    }
}
