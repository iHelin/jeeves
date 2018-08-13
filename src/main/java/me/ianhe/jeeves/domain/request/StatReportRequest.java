package me.ianhe.jeeves.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.ianhe.jeeves.domain.request.component.BaseRequest;
import me.ianhe.jeeves.domain.shared.StatReport;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatReportRequest {
    private me.ianhe.jeeves.domain.request.component.BaseRequest BaseRequest;
    private int Count;
    private StatReport[] List;

    public BaseRequest getBaseRequest() {
        return BaseRequest;
    }

    public void setBaseRequest(BaseRequest baseRequest) {
        BaseRequest = baseRequest;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public StatReport[] getList() {
        return List;
    }

    public void setList(StatReport[] list) {
        List = list;
    }
}
