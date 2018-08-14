package me.ianhe.jeeves.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.ianhe.jeeves.domain.request.component.BaseRequest;
import me.ianhe.jeeves.domain.shared.StatReport;

/**
 * @author linhe2
 * @since 2018/8/14 20:31
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatReportRequest {

    @JsonProperty("BaseRequest")
    private BaseRequest baseRequest;
    @JsonProperty("Count")
    private int count;
    @JsonProperty("List")
    private StatReport[] list;

    public BaseRequest getBaseRequest() {
        return baseRequest;
    }

    public void setBaseRequest(BaseRequest baseRequest) {
        this.baseRequest = baseRequest;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public StatReport[] getList() {
        return list;
    }

    public void setList(StatReport[] list) {
        this.list = list;
    }
}
