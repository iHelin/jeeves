package me.ianhe.jeeves.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.ianhe.jeeves.domain.response.component.AbstractWeChatHttpResponseBase;
import me.ianhe.jeeves.domain.shared.Contact;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetContactResponse extends AbstractWeChatHttpResponseBase {
    @JsonProperty
    private int MemberCount;
    @JsonProperty
    private Set<Contact> MemberList;
    @JsonProperty
    private long Seq;

    public int getMemberCount() {
        return MemberCount;
    }

    public void setMemberCount(int memberCount) {
        MemberCount = memberCount;
    }

    public Set<Contact> getMemberList() {
        return MemberList;
    }

    public void setMemberList(Set<Contact> memberList) {
        MemberList = memberList;
    }

    public long getSeq() {
        return Seq;
    }

    public void setSeq(long seq) {
        Seq = seq;
    }
}