package me.ianhe.jeeves.domain.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author iHelin
 * @since 2018/8/13 19:48
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncKey {
    @JsonProperty
    private int Count;
    @JsonProperty
    private SyncKeyPair[] List;

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public SyncKeyPair[] getList() {
        return List;
    }

    public void setList(SyncKeyPair[] list) {
        List = list;
    }

    @Override
    public String toString() {
        if (this.List != null) {
            return String.join("|", Arrays.stream(this.List)
                    .map(x -> String.format("%s_%S", x.getKey(), x.getVal())).collect(Collectors.toList()));
        }
        return null;
    }
}
