package me.ianhe.jeeves.domain.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author linhe2
 * @since 2018/8/15 20:40
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncCheckKeyPair {
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
