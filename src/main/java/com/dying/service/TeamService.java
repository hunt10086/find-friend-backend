package com.dying.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dying.domain.Team;
import com.dying.domain.User;
import com.dying.domain.request.TeamDTO;

import java.util.List;


/**
* @author 666
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2025-07-09 14:30:23
*/
public interface TeamService extends IService<Team> {

    boolean createTeam(TeamDTO team, User loginUser);

    List<TeamDTO> getTeamList(User loginUser,Integer count);
}
