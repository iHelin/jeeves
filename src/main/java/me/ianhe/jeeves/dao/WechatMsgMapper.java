package me.ianhe.jeeves.dao;

import me.ianhe.jeeves.entity.WechatMsg;

public interface WechatMsgMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WechatMsg record);

    int insertSelective(WechatMsg record);

    WechatMsg selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WechatMsg record);

    int updateByPrimaryKey(WechatMsg record);
}