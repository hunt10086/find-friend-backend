package com.dying.service;

import com.dying.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author 666
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2025-05-10 13:59:54
*/
public interface UserService extends IService<User> {

    public long userRegister(String userAccount, String password,String checkPassword);

    public User userLogin(String userAccount, String password, HttpServletRequest request);


    User getSaftyUser(User originUser);

    int userLogout(HttpServletRequest request);

    boolean userUpdate(User user);

    List<User> searchAllByTags(List<String> tagsList);
}
