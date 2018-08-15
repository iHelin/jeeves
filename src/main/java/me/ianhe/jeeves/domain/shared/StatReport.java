package me.ianhe.jeeves.domain.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author iHelin
 * @since 2018/8/14 20:34
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatReport {
    private String Text;
    private int Type;

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }
}
