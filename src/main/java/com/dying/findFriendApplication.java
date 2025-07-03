package com.dying;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dying.mapper")
public class findFriendApplication {

    public static void main(String[] args) {
        SpringApplication.run(findFriendApplication.class, args);
    }

}
