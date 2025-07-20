package com.dying;

import cn.hutool.Hutool;
import cn.hutool.core.lang.Validator;
import com.dying.domain.User;
import com.dying.mapper.UserMapper;
import com.dying.service.UserService;
import com.dying.utils.MD5Utils;
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

    @Test
    void check(){
        String SALT="Dying";
        String pas=SALT+"22222222"+SALT;
        String pas1= MD5Utils.string2MD5(pas);
        System.out.println("a9981efe9f1d29dad1c2d8960c385388");
        System.out.println(pas1);
    }


}
