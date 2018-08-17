package me.ianhe.jeeves.entity;

import java.util.Date;

public class WechatMsg {
    private Integer id;

    private String displayFromName;

    private Integer msgType;

    private String content;

    private Date createTime;

    private String url;

    private String fileName;

    private Boolean privateMsg;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDisplayFromName() {
        return displayFromName;
    }

    public void setDisplayFromName(String displayFromName) {
        this.displayFromName = displayFromName;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getPrivateMsg() {
        return privateMsg;
    }

    public void setPrivateMsg(Boolean privateMsg) {
        this.privateMsg = privateMsg;
    }
}