package com.dying.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dying.common.ErrorCode;
import com.dying.domain.Team;
import com.dying.domain.User;
import com.dying.domain.UserTeam;
import com.dying.domain.request.CreateTeamRequest;
import com.dying.domain.request.TeamDTO;
import com.dying.exception.BusinessException;
import com.dying.mapper.TeamMapper;
import com.dying.mapper.UserTeamMapper;
import com.dying.service.TeamService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.dying.constant.TeamConstant.*;
import static com.dying.constant.UserConstant.ADMIN_ROLE;

@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private UserTeamMapper userTeamMapper;

    @Override
    public boolean createTeam(CreateTeamRequest createTeamRequest, User loginUser) {
        // 1. 检验请求参数是否为空
        if (createTeamRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 2. 检验用户登录态
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 3. 检验队伍名长度是否<=32
        if (StringUtils.isBlank(createTeamRequest.getTeamName()) || createTeamRequest.getTeamName().length() > TEAM_MAX_NAME_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        // 4. status状态的确认，若加密则要设置密码<=64
        if (createTeamRequest.getStatus() == 1) {
            if (StringUtils.isBlank(createTeamRequest.getPassword()) || createTeamRequest.getPassword().length() > TEAM_MAX_PASSWORD_LENGTH) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码为空或长度过长");
            }
        }
        //5. 检验人数是否大于15
        if (createTeamRequest.getMaxNum() > TEAM_MAX_USER_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "人数不能大于15");
        }
        //6 检验队伍描述是否合理
        if (createTeamRequest.getDescription().length() > TEAM_MAX_DESCRIPTION_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        // 7. 一个用户最多创建5个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginUser.getId());
        long count = teamMapper.selectCount(queryWrapper);
        if (count >= TEAM_MAX_NUM_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍数超过5队,要创建更多队伍请成为VIP");
        }
        // 8.往队伍和用户队伍关系表中插入数据

        //往team表插入数据
        Team team = new Team();
        BeanUtils.copyProperties(createTeamRequest, team);
        team.setCreateTime(new Date());
        team.setUpdateTime(new Date());
        teamMapper.insert(team);

        System.out.println(team);
        //往user_team表插入数据
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(loginUser.getId());
        userTeam.setCreateTime(new Date());
        userTeam.setUpdateTime(new Date());
        userTeam.setJoinTime(new Date());
        userTeam.setTeamId(team.getId());
        userTeamMapper.insert(userTeam);
        System.out.println(userTeam);
        return true;
    }

    @Override
    public List<TeamDTO> getTeamList(User loginUser) {
        // 1. 检验用户登录态
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        //2. 返回所有队伍 不包含创建的和加入的
        Long[] Ids = userTeamMapper.selectTeam(loginUser.getId());
        System.out.println(Ids.length);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", (Object[]) Ids);
        List<Team> teamList = teamMapper.selectList(queryWrapper);
        List<TeamDTO> teamDTOList = new ArrayList<>();
        for (Team team : teamList) {
            TeamDTO teamDTO = new TeamDTO();
            BeanUtils.copyProperties(team, teamDTO);
            teamDTO.setPassword("");
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("team_id", teamDTO.getId());
            Long l = userTeamMapper.selectCount(userTeamQueryWrapper);
            teamDTO.setNowNum(l);
            teamDTOList.add(teamDTO);
        }
        return teamDTOList;
    }

    @Override
    public boolean updateTeam(Long id, User loginUser, TeamDTO teamDto) {
        // 1. 检验请求参数是否为空
        if (teamDto == null ||id<=0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 2. 检验用户登录态
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Team team = teamMapper.selectById(id);
         Long uId=team.getUserId();
        // 3.检验身份为队长或管理员
        if (loginUser.getUserRole() != ADMIN_ROLE && !Objects.equals(loginUser.getId(), uId)) {
            throw new BusinessException(ErrorCode.NO_AUTO, "无权限");
        }
        // 4. 检验队伍名长度是否<=32
        String teamName = teamDto.getTeamName();
        String description = teamDto.getDescription();
        String password = teamDto.getPassword();
        int status = teamDto.getStatus();
        int maxNum = teamDto.getMaxNum();
        String icon = teamDto.getIcon();
        if (StringUtils.isBlank(teamName) || teamName.length() > TEAM_MAX_NAME_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        // 5. status状态的确认，若加密则要设置密码<=64
        if (status == 1) {
            if (StringUtils.isBlank(password) || password.length() > TEAM_MAX_PASSWORD_LENGTH) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码为空或长度过长");
            }
        }
        // 6. 检验人数是否大于15
        if (maxNum > TEAM_MAX_USER_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "人数不能大于15");
        }
        // 7 检验队伍描述是否合理
        if (description.length() > TEAM_MAX_DESCRIPTION_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        if(icon.length()>TEAM_MAX_ICON_LENGTH){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "头像链接过长");
        }
        // 8.若信息相同不触发更新
        if (teamName.equals(team.getTeamName()) && description.equals(team.getDescription())
                && status == team.getStatus() && maxNum == team.getMaxNum()&&icon.equals(team.getIcon())) {
            if (status == 1) {
                if (password.equals(teamDto.getPassword())) {
                    return true;
                }
            }
            return true;
        }
        // 9.更新
        Team team1 = new Team();
        BeanUtils.copyProperties(teamDto, team1);
        team.setUpdateTime(new Date());
        int i = teamMapper.updateById(team1);
        return i == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean joinTeam(TeamDTO teamDTO, User loginUser, String password) {
        // 1. 检验请求参数是否为空
        if (teamDTO == null || teamDTO.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 2. 检验用户登录态
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Team team = teamMapper.selectById(teamDTO.getId());
        // 3.不能加入自己的队伍
        if (Objects.equals(loginUser.getId(), team.getUserId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能加入自己的队伍");
        }
        // 4.也不能重复加入同一个队伍
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginUser.getId());
        List<UserTeam> userTeams = userTeamMapper.selectList(queryWrapper);
        for (UserTeam userTeam : userTeams) {
            if (Objects.equals(userTeam.getTeamId(), teamDTO.getId())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能重复加入该队伍");
            }
        }
        // 5. 若有密码需输入密码
        if (StringUtils.isNotBlank(team.getPassword()) && !team.getPassword().equals(password)) {
            throw new BusinessException(ErrorCode.NO_AUTO, "密码错误");
        }
        // 5.人数满了也不能加入
        QueryWrapper<UserTeam> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("team_id", teamDTO.getId());
        long count = userTeamMapper.selectCount(queryWrapper1);
        if (count >= teamDTO.getMaxNum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数已满");
        }
        // 6.插入数据
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamDTO.getId());
        userTeam.setUserId(loginUser.getId());
        userTeam.setJoinTime(new Date());
        userTeam.setUpdateTime(teamDTO.getUpdateTime());
        userTeam.setCreateTime(teamDTO.getCreateTime());
        int i = userTeamMapper.insert(userTeam);
        return i == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean quitTeam(TeamDTO teamDTO, User loginUser) {
        // 1. 检查请求参数是否为空
        if (teamDTO == null || teamDTO.getId() <= 0) {
            return false;
        }
        // 2. 检验用户登录态
        if (loginUser == null) {
            return false;
        }
        // 3. 检验是否入队
        Team team = teamMapper.selectById(teamDTO.getId());
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        Long id = loginUser.getId();
        queryWrapper.eq("team_id", teamDTO.getId());
        long count = userTeamMapper.selectCount(queryWrapper);//队伍人数
        queryWrapper.eq("user_id", id);
        UserTeam userTeamI = userTeamMapper.selectOne(queryWrapper);
        long count1 = userTeamMapper.selectCount(queryWrapper);
        if (count1 < 1) {
            return false;
        }
        // 4. 若队伍只剩一人，解散队伍
        QueryWrapper<UserTeam> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("team_id", teamDTO.getId());
        if (count == 1) {
            teamMapper.deleteById(teamDTO.getId());
            userTeamMapper.deleteById(userTeamI);
            return true;
        }
        // 5. 大于一人，队长退出，队长权转移给剩下最早加入队伍的用户
        if (Objects.equals(id, team.getUserId())) {
            Long[] ids = userTeamMapper.selectByJoinTime(userTeamI.getTeamId());
            userTeamMapper.deleteById(userTeamI);
            team.setUserId(ids[1]);
            userTeamMapper.deleteById(ids[0]);
            team.setUpdateTime(new Date());
            teamMapper.updateById(team);
            return true;
        }
        // 6. 队员则直接退出
        userTeamMapper.deleteById(userTeamI);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteTeam(TeamDTO teamDTO, User loginUser) {
        // 1.检验请求参数是否为空
        if (teamDTO == null || teamDTO.getId() <= 0) {
            return false;
        }
        // 2.检验身份为队长或管理员
        if (loginUser.getUserRole() != ADMIN_ROLE && !Objects.equals(loginUser.getId(), teamDTO.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTO, "无权限");
        }
        // 3.删除相关成员信息
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("team_id", teamDTO.getId());
        userTeamMapper.delete(queryWrapper);
        teamMapper.deleteById(teamDTO.getId());
        return true;
    }


    @Override
    public List<TeamDTO> getMyTeam(User loginUser) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未登录");
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginUser.getId());
        List<Team> teams = teamMapper.selectList(queryWrapper);
        List<TeamDTO> teamDTOs = new ArrayList<>();
        for (Team team : teams) {
            TeamDTO teamDTO = new TeamDTO();
            BeanUtils.copyProperties(team, teamDTO);
            teamDTO.setPassword("");
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("team_id", teamDTO.getId());
            Long l = userTeamMapper.selectCount(userTeamQueryWrapper);
            teamDTO.setNowNum(l);
            teamDTOs.add(teamDTO);
        }
        return teamDTOs;
    }

    @Override
    public List<TeamDTO> getJoinTeam(User loginUser) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未登录");
        }
        Long id = loginUser.getId();
        //我创建的队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginUser.getId());
        List<Team> teams = teamMapper.selectList(queryWrapper);
        //我加入的队伍
        QueryWrapper<UserTeam> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("user_id", loginUser.getId());
        List<UserTeam> userTeams = userTeamMapper.selectList(queryWrapper1);
        List<TeamDTO> teamDTOs = new ArrayList<>();
        for (UserTeam userTeam : userTeams) {
            Long userId = userTeam.getTeamId();
            Team team = teamMapper.selectById(userId);
            if (!team.getUserId().equals(id)) {
                TeamDTO teamDTO = new TeamDTO();
                BeanUtils.copyProperties(team, teamDTO);
                teamDTO.setPassword("");
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("team_id", teamDTO.getId());
                Long l = userTeamMapper.selectCount(userTeamQueryWrapper);
                teamDTO.setNowNum(l);
                teamDTOs.add(teamDTO);
            }
        }
        return teamDTOs;
    }

    @Override
    public List<TeamDTO> getTeams(User loginUser){
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未登录");
        }
        Long id = loginUser.getId();
        //我加入的队伍
        QueryWrapper<UserTeam> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("user_id", loginUser.getId());
        List<UserTeam> userTeams = userTeamMapper.selectList(queryWrapper1);
        List<TeamDTO> teamDTOs = new ArrayList<>();
        for(UserTeam userTeam : userTeams) {
            Long teamId = userTeam.getTeamId();
            Team team = teamMapper.selectById(teamId);
            TeamDTO teamDTO = new TeamDTO();
            BeanUtils.copyProperties(team, teamDTO);
            teamDTO.setPassword("");
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("team_id", teamDTO.getId());
            Long l = userTeamMapper.selectCount(userTeamQueryWrapper);
            teamDTO.setNowNum(l);
            teamDTOs.add(teamDTO);
        }
        return teamDTOs;
    }

    @Override
    public List<TeamDTO> searchTeam(String teamName, User loginUser){
        if(loginUser==null||teamName==null){
            return null;
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("team_name", teamName);
        List<Team> teams = teamMapper.selectList(queryWrapper);
        List<TeamDTO> teamDTOs = new ArrayList<>();
        for (Team team : teams) {
            TeamDTO teamDTO = new TeamDTO();
            BeanUtils.copyProperties(team, teamDTO);
            teamDTO.setPassword("");
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("team_id", teamDTO.getId());
            Long l = userTeamMapper.selectCount(userTeamQueryWrapper);
            teamDTO.setNowNum(l);
            teamDTOs.add(teamDTO);
        }
        return teamDTOs;
    }

    @Override
    public List<TeamDTO> getOneTeam(Long teamId, User loginUser){
        if(teamId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        if(loginUser==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未登录");
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",teamId);
        List<Team> teamList = teamMapper.selectList(queryWrapper);
        List<TeamDTO> teamDTOs = new ArrayList<>();
        for (Team team : teamList) {
            TeamDTO teamDTO = new TeamDTO();
            BeanUtils.copyProperties(team, teamDTO);
            teamDTO.setPassword("");
            teamDTOs.add(teamDTO);
        }
        return teamDTOs;
    }
}
