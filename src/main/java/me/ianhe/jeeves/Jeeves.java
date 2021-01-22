package me.ianhe.jeeves;

import me.ianhe.jeeves.config.SystemProperties;
import me.ianhe.jeeves.exception.WeChatException;
import me.ianhe.jeeves.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author iHelin
 * @since 2018/8/15 12:32
 */
@Component
public class Jeeves implements CommandLineRunner {

    @Autowired
    private LoginService loginService;

    @Autowired
    private SystemProperties systemProperties;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(String... strings) throws Exception {
        logger.debug("Jeeves starts");
        logger.debug("app id = {}", systemProperties.getInstanceId());
        System.setProperty("https.protocols", "TLSv1");
        System.setProperty("jsse.enableSNIExtension", "false");
        try {
            loginService.login();
        } catch (WeChatException e) {
            logger.error("运行异常，{}", e.getMessage());
        }
    }

}