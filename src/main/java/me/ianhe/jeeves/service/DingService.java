package me.ianhe.jeeves.service;

import me.ianhe.jeeves.config.SystemProperties;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author iHelin
 * @since 2017/11/14 17:04
 */
@Service
public class DingService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SystemProperties systemProperties;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 发送文本消息
     *
     * @author iHelin
     * @since 2017/5/17 11:16
     */
    public String sendTextMsg(String content) {
        Map<String, Object> body = new HashMap<>();
        body.put("msgtype", "text");
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("content", content);
        body.put("text", contentMap);
        return doSend(body);
    }

    public String doSend(Object data) {
        String token = systemProperties.getDing().getDingToken();
        Long timestamp = System.currentTimeMillis();
        String sign = sign(timestamp);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<Object> request = new HttpEntity<>(data, headers);

        String result = restTemplate.postForObject(systemProperties.getDing().getDingUrl(),
            request,
            String.class,
            token, sign, timestamp);

        logger.info("Robot return {}", result);
        return result;
    }

    private String sign(Long timestamp) {
        String stringToSign = timestamp + "\n" + systemProperties.getDing().getDingSign();
        byte[] bytesToSign = stringToSign.getBytes(StandardCharsets.UTF_8);
        String sign = "";
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(systemProperties.getDing().getDingSign().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(bytesToSign);
            sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        } catch (Exception e) {
            logger.error("签名失败", e);
        }
        return sign;
    }

}
