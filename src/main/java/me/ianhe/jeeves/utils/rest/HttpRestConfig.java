package me.ianhe.jeeves.utils.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author iHelin
 * @since 2018/8/13 20:01
 */
@Configuration
public class HttpRestConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new StatefulRestTemplate();
    }
}