package me.ianhe.jeeves.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.ianhe.jeeves.domain.request.component.BaseRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InitRequest {
    @JsonProperty
    private me.ianhe.jeeves.domain.request.component.BaseRequest BaseRequest;

    public BaseRequest getBaseRequest() {
        return BaseRequest;
    }

    public void setBaseRequest(BaseRequest baseRequest) {
        BaseRequest = baseRequest;
    }
}