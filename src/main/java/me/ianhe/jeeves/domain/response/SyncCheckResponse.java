package me.ianhe.jeeves.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author iHelin
 * @since 2018/8/15 09:47
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncCheckResponse {
    @JsonProperty
    private int retcode;
    @JsonProperty
    private int selector;

    public int getRetcode() {
        return retcode;
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    public int getSelector() {
        return selector;
    }

    public void setSelector(int selector) {
        this.selector = selector;
    }
}
