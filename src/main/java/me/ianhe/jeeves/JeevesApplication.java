package me.ianhe.jeeves;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author iHelin
 * @since 2018/8/13 22:42
 */
@EnableScheduling
@SpringBootApplication
public class JeevesApplication {

    public static void main(String[] args) {
        SpringApplication.run(JeevesApplication.class, args);
    }

}
