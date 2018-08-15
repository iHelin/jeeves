package me.ianhe.jeeves.domain.response.component;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author iHelin
 * @since 2018/8/15 09:53
 */
public class BaseResponse {
    @JsonProperty
    private int Ret;
    @JsonProperty
    private String ErrMsg;

    public int getRet() {
        return Ret;
    }

    public void setRet(int ret) {
        Ret = ret;
    }

    public String getErrMsg() {
        return ErrMsg;
    }

    public void setErrMsg(String errMsg) {
        ErrMsg = errMsg;
    }
}
