package me.ianhe.jeeves;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author iHelin
 * @since 2018/8/13 22:42
 */
@EnableScheduling
@SpringBootApplication
@MapperScan("me.ianhe.jeeves.dao")
public class JeevesApplication {

    public static void main(String[] args) {
        SpringApplication.run(JeevesApplication.class, args);
    }

}
