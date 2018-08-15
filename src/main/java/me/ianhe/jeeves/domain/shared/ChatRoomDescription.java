package me.ianhe.jeeves.domain.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author iHelin
 * @since 2018/8/15 20:33
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatRoomDescription {
    @JsonProperty
    private String UserName;
    @JsonProperty
    private String ChatRoomId = "";

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getChatRoomId() {
        return ChatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        ChatRoomId = chatRoomId;
    }
}
