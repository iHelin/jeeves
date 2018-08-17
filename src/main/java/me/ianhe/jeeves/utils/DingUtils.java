package me.ianhe.jeeves.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ian He
 * @since 2018/8/17 14:54
 */
public class DingUtils {

    private static final String URL = "https://oapi.dingtalk.com/robot/send?access_token=0822db7059b63a7f73a12e0b665574310108c73649e256c87c646394e63fc6a2";

    public static void send(String content) {
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("content", content);
        Map<String, Object> data = new HashMap<>();
        data.put("msgtype", "text");
        data.put("text", contentMap);
        ObjectMapper objectMapper = new ObjectMapper();
        RestTemplate restTemplate = new RestTemplate();
        Object data1 = null;
        try {
            data1 = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpHeaders customHeader = new HttpHeaders();
        customHeader.setContentType(MediaType.APPLICATION_JSON_UTF8);
        restTemplate.postForObject(URL, new HttpEntity<>(data, customHeader), HashMap.class);
    }

    public static void main(String[] args) {
        send("你才是");
    }
}
