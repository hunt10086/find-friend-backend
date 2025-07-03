package com.dying.service;

import com.dying.domain.User;
import com.dying.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * @Author daylight
 * @Date 2025/7/3 19:58
 */
@SpringBootTest
@MapperScan("com.dying.mapper")
public class insertUsers {

    @Autowired
    private UserService userService;


    @Resource
    private UserMapper userMapper;



}
