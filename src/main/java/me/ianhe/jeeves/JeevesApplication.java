package me.ianhe.jeeves;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author iHelin
 * @since 2018/8/13 22:42
 */
@SpringBootApplication
public class JeevesApplication {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(JeevesApplication.class, args);
    }

}
