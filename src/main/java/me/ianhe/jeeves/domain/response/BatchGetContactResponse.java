package me.ianhe.jeeves.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.ianhe.jeeves.domain.response.component.AbstractWeChatHttpResponseBase;
import me.ianhe.jeeves.domain.shared.Contact;

import java.util.Set;

/**
 * @author iHelin
 * @since 2018/8/15 09:01
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchGetContactResponse extends AbstractWeChatHttpResponseBase {

    @JsonProperty
    private Set<Contact> ContactList;
    @JsonProperty
    private int Count;

    public Set<Contact> getContactList() {
        return ContactList;
    }

    public void setContactList(Set<Contact> contactList) {
        ContactList = contactList;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }
}
