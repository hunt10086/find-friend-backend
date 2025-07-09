package com.dying.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dying.domain.UserTeam;
import com.dying.mapper.UserTeamMapper;
import com.dying.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author 666
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2025-07-09 14:55:05
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




