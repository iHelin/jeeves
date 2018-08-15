package me.ianhe.jeeves.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author iHelin
 * @since 2018/8/15 10:09
 */
@Component
public class CleanupService implements DisposableBean {

    @Autowired
    private WeChatHttpServiceInternal weChatHttpService;
    @Autowired
    private CacheService cacheService;

    private static final Logger logger = LoggerFactory.getLogger(CleanupService.class);

    @Override
    public void destroy() throws Exception {
        logger.warn("[*] system is being destroyed");
        if (cacheService.isAlive()) {
            try {
                logger.warn("[*] logging out");
                weChatHttpService.logout(cacheService.getHostUrl(), cacheService.getsKey());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }
}
