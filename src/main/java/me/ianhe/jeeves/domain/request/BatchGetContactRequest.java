package me.ianhe.jeeves.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.ianhe.jeeves.domain.request.component.BaseRequest;
import me.ianhe.jeeves.domain.shared.ChatRoomDescription;

/**
 * @author iHelin
 * @since 2018/8/15 20:23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchGetContactRequest {
    @JsonProperty
    private BaseRequest BaseRequest;
    @JsonProperty
    private int Count;
    @JsonProperty
    private ChatRoomDescription[] List;

    public BaseRequest getBaseRequest() {
        return BaseRequest;
    }

    public void setBaseRequest(BaseRequest baseRequest) {
        BaseRequest = baseRequest;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public ChatRoomDescription[] getList() {
        return List;
    }

    public void setList(ChatRoomDescription[] list) {
        List = list;
    }
}
