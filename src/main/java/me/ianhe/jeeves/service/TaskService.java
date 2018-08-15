package me.ianhe.jeeves.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author linhe2
 * @since 2018/8/15 12:48
 */
@Service
public class TaskService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CacheService cacheService;

    @Autowired
    private WeChatHttpService weChatHttpService;

    /**
     * 工作日11点执行
     */
    @Scheduled(cron = "0 0 6 ? * MON-FRI")
    public void runEveryDay6() throws IOException {
        daka();
    }

    /**
     * 每天0点执行
     *
     * @author iHelin
     * @since 2017/12/21 10:21
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void runEveryDay0() {
        logger.debug("我每隔10秒出现一次");
//        daka();
    }

    private void daka() {
        if (cacheService.isAlive()) {
            logger.debug("打卡提醒");
            String nickName = "下辈子一定要做只猪";
            String userName = cacheService.getUserNameByNickName(nickName);
            try {
                weChatHttpService.sendText(userName, "下班了，注意别忘了打卡哦！");
            } catch (IOException e) {
                logger.error("提醒打卡失败。", e);
            }
        }
    }

}
