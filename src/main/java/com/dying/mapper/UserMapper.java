package com.dying.mapper;

import com.dying.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author 666
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2025-05-10 13:59:54
* @Entity com.dying.domain.User
*/
public interface UserMapper extends BaseMapper<User> {

    public User findAllByUserAccountBoolean(String userAccount);


}




