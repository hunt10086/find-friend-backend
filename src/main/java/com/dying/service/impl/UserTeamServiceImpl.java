package com.dying.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dying.domain.User;
import com.dying.domain.UserTeam;
import com.dying.mapper.UserMapper;
import com.dying.mapper.UserTeamMapper;
import com.dying.service.UserTeamService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author 666
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2025-07-09 14:55:05
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserTeamMapper userTeamMapper;

    @Override
    public List<User> getTeamPeople(Long teamId, User loginUser){
        if(loginUser==null||loginUser.getId()==null||loginUser.getId()<=0||teamId<=0){
            return  null;
        }
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("team_id",teamId);
        List<UserTeam> userTeams = userTeamMapper.selectList(queryWrapper);
        if(userTeams==null||userTeams.size()<=0){
            return null;
        }
        List<User> users = new ArrayList<>();
        for(UserTeam userTeam:userTeams){
            User user = userMapper.selectById(userTeam.getUserId());
            users.add(user);
        }
        return users;
    }
}




