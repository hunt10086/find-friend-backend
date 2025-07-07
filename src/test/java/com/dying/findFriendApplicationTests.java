package com.dying;

import cn.hutool.Hutool;
import cn.hutool.core.lang.Validator;
import com.dying.domain.User;
import com.dying.mapper.UserMapper;
import com.dying.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@MapperScan("com.dying.mapper")
class findFriendApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Test
    void getUserByTags(){
        List<String> tags = new ArrayList<>();
        tags.add("java");
        tags.add("python");
        List<User> list= userService.searchAllByTags(tags);
        for(User user:list){
            System.out.println(user.toString());
        }
    }

    @Test
    void checkEmail(){
//        boolean flag = userService.checkEmail();
        boolean flag= Validator.isEmail("2646130539@qq.com");
        System.out.println("flag:"+flag);
    }


}
