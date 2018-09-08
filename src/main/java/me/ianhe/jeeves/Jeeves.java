package me.ianhe.jeeves;

import io.github.biezhi.wechat.WeChatBot;
import io.github.biezhi.wechat.api.annotation.Bind;
import io.github.biezhi.wechat.api.constant.Config;
import io.github.biezhi.wechat.api.enums.MsgType;
import io.github.biezhi.wechat.api.model.WeChatMessage;
import io.github.biezhi.wechat.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author iHelin
 * @since 2018/8/15 12:32
 */
public class Jeeves extends WeChatBot {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public Jeeves(Config config) {
        super(config);
    }

    @Bind(msgType = MsgType.TEXT)
    public void handleText(WeChatMessage message) {
        if (StringUtils.isNotEmpty(message.getName())) {
            logger.info("接收到 [{}] 的消息: {}", message.getName(), message.getText());
            if (!message.isGroup()) {
                this.sendMsg(message.getFromUserName(), "自动回复: " + message.getText());
            }
        }
    }

}