package me.ianhe.jeeves.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.ianhe.jeeves.domain.response.component.AbstractWeChatHttpResponseBase;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteChatRoomMemberResponse extends AbstractWeChatHttpResponseBase {
}
