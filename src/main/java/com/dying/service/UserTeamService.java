package com.dying.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dying.domain.User;
import com.dying.domain.UserTeam;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
* @author 666
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service
* @createDate 2025-07-09 14:55:05
*/
public interface UserTeamService extends IService<UserTeam> {

    List<User> getTeamPeople(Long id, User loginUser);
}
