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

    @Scheduled(cron = "0/10 * * * * ?")
    public void runEveryDay0() {
//        sendMenu();
    }

    /**
     * 打卡提醒
     */
    private void daka() {
        if (cacheService.isAlive()) {
            logger.debug("打卡提醒");
            String userName = cacheService.getUserNameByNickName(Constants.CHATROOM_NAME_SEVEN);
            weChatHttpService.sendText(userName, "美丽的小姐姐，到打卡时间了，别忘记打卡呦！");
        }
    }

    /**
     * 吃饭提醒
     */
    public void sendMenu() {
        HashMap map = new RestTemplate().getForObject("https://dev.fluttercn.com/now-eat/menu-0620.json", HashMap.class);
        List<String> workDate = (List<String>) map.get("workDate");
        String currentDateStr = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
//        String currentDateStr = "2018/08/20";
        for (int currentIndex = 0; currentIndex < workDate.size(); currentIndex++) {
            if (currentDateStr.equals(workDate.get(currentIndex))) {
                List<Map> timeplan = (List<Map>) map.get("timeplan");
                String mealTime = "";
                for (Map tp : timeplan) {
                    if (((String) tp.get("key")).contains("B")) {
                        mealTime = (String) tp.get("value");
                    }
                }
                String userName = cacheService.getUserNameByNickName(Constants.CHATROOM_NAME_SEVEN);
                weChatHttpService.sendText(userName, "本周吃饭时间：" + mealTime);
                Map<String, Object> nooning = (Map<String, Object>) map.get("nooning");
                List<Map> a22fs = (List<Map>) nooning.get("a22f");
                StringBuilder a22fbuilder = new StringBuilder("A2 2楼\n");
                for (Map a22f : a22fs) {
                    String key = (String) a22f.get("key");
                    String k = key.replaceAll("&ensp;", "").replaceAll("&emsp;", "");
                    List<String> values = (List<String>) a22f.get("value");
                    String v = values.get(currentIndex) + (values.size() / 2 <= 5 ? "" : "&" + values.get(currentIndex + values.size() / 2));
                    a22fbuilder.append(k + "：" + v + "\n");
                }
                weChatHttpService.sendText(userName, a22fbuilder.toString());
                List<Map> a23fs = (List<Map>) nooning.get("a23f");
                StringBuilder a23fbuilder = new StringBuilder("A2 3楼\n");
                for (Map a23f : a23fs) {
                    String key = (String) a23f.get("key");
                    String k = key.replaceAll("&ensp;", "").replaceAll("&emsp;", "");
                    List<String> values = (List<String>) a23f.get("value");
                    String v = values.get(currentIndex) + (values.size() / 2 <= 5 ? "" : "&" + values.get(currentIndex + values.size() / 2));
                    a23fbuilder.append(k + "：" + v + "\n");
                }
                weChatHttpService.sendText(userName, a23fbuilder.toString());
                List<Map> a55fs = (List<Map>) nooning.get("a55f");
                StringBuilder a55fbuilder = new StringBuilder("A5 5楼\n");
                for (Map a55f : a55fs) {
                    String key = (String) a55f.get("key");
                    String k = key.replaceAll("&ensp;", "").replaceAll("&emsp;", "");
                    List<String> values = (List<String>) a55f.get("value");
                    String v = values.get(currentIndex) + (values.size() / 2 <= 5 ? "" : "&" + values.get(currentIndex + values.size() / 2));
                    a55fbuilder.append(k + "：" + v + "\n");
                }
                weChatHttpService.sendText(userName, a55fbuilder.toString());
            }
        }
    }

}
