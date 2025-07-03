package com.dying.service;


import com.dying.domain.User;
import com.dying.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
@MapperScan("com.dying.mapper")
public class serviceImplTest {

    @Autowired
    private UserService userService;

@Autowired
private UserMapper userMapper;

    @Test
    public void Registertest() {
        long result = userService.userRegister("", "", "");
        System.out.println(result);

        long result2 = userService.userRegister("Dyi", "123456", "123456");
        System.out.println(result2);


        long result3 = userService.userRegister("Dying", "123456", "123456");
        System.out.println(result3);

        long result4 = userService.userRegister("Dyin*&$^g", "12345678", "12345678");
        System.out.println(result4);

        long result5 = userService.userRegister("dayligh", "12345678", "12345678");
        System.out.println(result5);

    }
    @Test
    public void update(){
        User user = new User();
        user.setId(1L);
        user.setUserName("Dying");
        boolean result = userService.updateById(user);
    }


    /**
     * 测试插入条数据
     * 1000条3秒
     *
     * 100000条2分38秒
     *
     */

    @Test
    public void insertUsersTest2(){
        final int NUM=100000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < NUM; i++) {
            User user=new User();
            user.setUserName("black");
            user.setUserAccount("black54"+i);
            user.setAvatarUrl("https://tse2-mm.cn.bing.net/th/id/OIP-C.7GLMYPqMlt2LgkbPsOnDIAAAAA?cb=iwp2&rs=1&pid=ImgDetMain");
            user.setGender(0);
            user.setTags("[]");
            user.setUserPassword("12345678");
            user.setProfile("阿巴");
            user.setUserStatus(0);
            user.setUpdateTime(new Date());
            user.setCreateTime(new Date());
            userMapper.insert(user);
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时："+(end-start));
    }

    /**
     * mybatisPlus 分组插入
     * 1000 条  一次100条  1.4秒
     *
     * 100000条  1次5000  15秒
     * 1000000 条   一次 10000  2分13秒
     */
    @Test
    public void insertUsersTest(){
        final int NUM=100000;
        long start = System.currentTimeMillis();
        List<User> list = new ArrayList<>();
        for (int i = 0; i < NUM; i++) {
            User user=new User();
            user.setUserName("black");
            user.setUserAccount("bl"+i);
            user.setAvatarUrl("https://tse2-mm.cn.bing.net/th/id/OIP-C.7GLMYPqMlt2LgkbPsOnDIAAAAA?cb=iwp2&rs=1&pid=ImgDetMain");
            user.setGender(0);
            user.setTags("[]");
            user.setUserPassword("12345678");
            user.setProfile("阿巴");
            user.setUserStatus(0);
            user.setUpdateTime(new Date());
            user.setCreateTime(new Date());
            list.add(user);
        }
        userService.saveBatch(list,5000);
        long end = System.currentTimeMillis();
        System.out.println("耗时："+(end-start));
    }

    @Test
    public void insertUsersTest3(){
        final int NUM=100000;
        long start = System.currentTimeMillis();
    }


}
