package me.ianhe.jeeves.service;

import me.ianhe.jeeves.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author iHelin
 * @since 2018/8/15 12:48
 */
@Service
public class TaskService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CacheService cacheService;

    @Autowired
    private WeChatHttpService weChatHttpService;

    /**
     * 工作日11点执行
     */
    @Scheduled(cron = "0 0 11 ? * MON-FRI")
    public void runEveryDay11() throws IOException {
        sendMenu();
    }

    /**
     * 工作日18点执行
     */
    @Scheduled(cron = "0 0 18 ? * MON-FRI")
    public void runEveryDay18() throws IOException {
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
        sendMenu();
    }

    /**
     * 打卡提醒
     */
    private void daka() {
        if (cacheService.isAlive()) {
            logger.debug("打卡提醒");
            String userName = cacheService.getUserNameByNickName(Constants.DEST_CHATROOM_NAME);
            weChatHttpService.sendText(userName, "美丽的小姐姐，到打卡时间了，别忘记打卡呦！");
        }
    }

    /**
     * 吃饭提醒
     */
    private void sendMenu() {
        HashMap map = new RestTemplate().getForObject("https://dev.fluttercn.com/now-eat/menu-0620.json", HashMap.class);
        List<String> workDate = (List<String>) map.get("workDate");
        String currentDateStr = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        for (String aWorkDate : workDate) {
            if (currentDateStr.equals(aWorkDate)) {
                List<Map> timeplan = (List<Map>) map.get("timeplan");
                String mealTime = "";
                for (Map tp : timeplan) {
                    if (((String) tp.get("key")).contains("B")) {
                        mealTime = (String) tp.get("value");
                    }
                }
                String userName = cacheService.getUserNameByNickName(Constants.DEST_CHATROOM_NAME);
                weChatHttpService.sendText(userName, "本周吃饭时间：" + mealTime);
            }
        }
    }

}
