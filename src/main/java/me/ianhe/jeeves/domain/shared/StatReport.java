package me.ianhe.jeeves.domain.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author linhe2
 * @since 2018/8/14 20:34
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatReport {

    @JsonProperty("Text")
    private String text;
    @JsonProperty("Type")
    private int type;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
