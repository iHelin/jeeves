package me.ianhe.jeeves.domain.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author iHelin
 * @since 2018/8/13 19:48
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncKeyPair {
    @JsonProperty
    private int Key;
    @JsonProperty
    private String Val;

    public int getKey() {
        return Key;
    }

    public void setKey(int key) {
        Key = key;
    }

    public String getVal() {
        return Val;
    }

    public void setVal(String val) {
        Val = val;
    }
}
