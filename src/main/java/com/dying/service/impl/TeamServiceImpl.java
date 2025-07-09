package com.dying.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dying.common.ErrorCode;
import com.dying.domain.Team;
import com.dying.domain.User;
import com.dying.domain.UserTeam;
import com.dying.domain.request.TeamDTO;
import com.dying.exception.BusinessException;
import com.dying.mapper.TeamMapper;
import com.dying.mapper.UserMapper;
import com.dying.mapper.UserTeamMapper;
import com.dying.service.TeamService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.dying.constant.TeamConstant.*;

@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private UserTeamMapper userTeamMapper;

    @Override
    public boolean createTeam(TeamDTO teamDto, User loginUser){
        // 1. 检验请求参数是否为空
        if(teamDto==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        // 2. 检验用户登录态
        if(loginUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        // 3. 检验队伍名长度是否<=32
        if(StringUtils.isBlank(teamDto.getTeamName()) ||teamDto.getTeamName().length()>TEAM_MAX_NAME_LENGTH){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
        // 4. status状态的确认，若加密则要设置密码<=64
        if(teamDto.getStatus()==1){
            if(StringUtils.isBlank(teamDto.getPassword())||teamDto.getPassword().length()>TEAM_MAX_PASSWORD_LENGTH){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码为空或长度过长");
            }
        }
        //5. 检验人数是否大于15
        if(teamDto.getMaxNum()>TEAM_MAX_USER_LENGTH){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"人数不能大于15");
        }
        //6 检验队伍描述是否合理
        if(teamDto.getDescription().length()>TEAM_MAX_DESCRIPTION_LENGTH){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述过长");
        }
        // 7. 一个用户最多创建5个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",loginUser.getId());
        long count=teamMapper.selectCount(queryWrapper);
        if(count>=TEAM_MAX_NUM_LENGTH){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍数超过5队,要创建更多队伍请成为VIP");
        }
        // 8.往队伍和用户队伍关系表中插入数据
        //往team表插入数据
        System.out.println(222);
        Team team=new Team();
        BeanUtils.copyProperties(teamDto,team);
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
    public List<TeamDTO> getTeamList(User loginUser,Integer count) {
        // 1. 检验用户登录态
        if(loginUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        //2. 分页返回所有队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        IPage<Team> ipage=new Page<>(count,TEAM_SIZE);
        List<Team> teamList= teamMapper.selectList(ipage, queryWrapper);
        List<TeamDTO> teamDTOList=new ArrayList<>();
        for(Team team:teamList){
            TeamDTO teamDTO=new TeamDTO();
            BeanUtils.copyProperties(team,teamDTO);
            teamDTOList.add(teamDTO);
        }
        return teamDTOList;
    }
}
