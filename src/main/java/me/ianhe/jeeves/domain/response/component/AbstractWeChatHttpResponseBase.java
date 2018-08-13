package me.ianhe.jeeves.domain.response.component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * @author linhe2
 * @since 2018/8/13 22:19
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractWeChatHttpResponseBase {

    @JsonProperty
    private BaseResponse BaseResponse;

    public BaseResponse getBaseResponse() {
        return BaseResponse;
    }

    public void setBaseResponse(BaseResponse baseResponse) {
        BaseResponse = baseResponse;
    }
}
