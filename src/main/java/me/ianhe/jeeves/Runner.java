package me.ianhe.jeeves;

import io.github.biezhi.wechat.api.constant.Config;
import me.ianhe.jeeves.config.SystemProperties;
import me.ianhe.jeeves.exception.WeChatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author Ian He
 * @since 2018/9/8 12:56
 */
@Component
public class Runner implements CommandLineRunner {

    @Autowired
    private SystemProperties systemProperties;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(String... strings) throws Exception {
        logger.debug("Jeeves starts");
        logger.debug("app id = {}", systemProperties.getInstanceId());
        System.setProperty("https.protocols", "TLSv1");
        System.setProperty("jsse.enableSNIExtension", "false");
        try {
            new Jeeves(Config.me().autoLogin(true).showTerminal(true)).start();
        } catch (WeChatException e) {
            logger.error("运行异常，{}", e.getMessage());
        }
    }
}
