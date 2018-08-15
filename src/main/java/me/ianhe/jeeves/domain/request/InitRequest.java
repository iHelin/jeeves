package me.ianhe.jeeves.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.ianhe.jeeves.domain.request.component.BaseRequest;

/**
 * @author iHelin
 * @since 2018/8/15 10:07
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitRequest {
    @JsonProperty
    private BaseRequest BaseRequest;

    public BaseRequest getBaseRequest() {
        return BaseRequest;
    }

    public void setBaseRequest(BaseRequest baseRequest) {
        BaseRequest = baseRequest;
    }
}