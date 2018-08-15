package me.ianhe.jeeves.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.ianhe.jeeves.domain.response.component.AbstractWeChatHttpResponseBase;
import me.ianhe.jeeves.domain.shared.ChatRoomMember;

import java.util.Set;

/**
 * @author iHelin
 * @since 2018/8/15 09:00
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddChatRoomMemberResponse extends AbstractWeChatHttpResponseBase {

    @JsonProperty
    private int MemberCount;
    @JsonProperty
    private Set<ChatRoomMember> MemberList;

    public int getMemberCount() {
        return MemberCount;
    }

    public void setMemberCount(int memberCount) {
        MemberCount = memberCount;
    }

    public Set<ChatRoomMember> getMemberList() {
        return MemberList;
    }

    public void setMemberList(Set<ChatRoomMember> memberList) {
        MemberList = memberList;
    }
}
