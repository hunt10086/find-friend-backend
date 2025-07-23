package com.dying;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.dying.mapper")
@EnableRedisHttpSession
@EnableScheduling
public class findFriendApplication {

    public static void main(String[] args) {
        SpringApplication.run(findFriendApplication.class, args);
    }

}
