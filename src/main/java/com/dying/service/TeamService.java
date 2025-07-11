package com.dying.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dying.domain.Team;
import com.dying.domain.User;
import com.dying.domain.request.CreateTeamRequest;
import com.dying.domain.request.TeamDTO;

import java.util.List;


/**
* @author 666
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2025-07-09 14:30:23
*/
public interface TeamService extends IService<Team> {

    boolean createTeam(CreateTeamRequest createTeamRequest, User loginUser);

    List<TeamDTO> getTeamList(User loginUser,Integer count);

    boolean updateTeam(Long id, User loginUser, TeamDTO teamDto);

    boolean joinTeam(TeamDTO teamDTO, User loginUser, String password);

    boolean quitTeam(TeamDTO teamDTO, User loginUser);

    boolean deleteTeam(TeamDTO teamDTO, User loginUser);

    List<TeamDTO> getMyTeam(User loginUser);

    List<TeamDTO> getJoinTeam(User loginUser);

    List<TeamDTO> searchTeam(String teamName, User loginUser);

    List<TeamDTO> getOneTeam(Long id, User loginUser);
}
