package com.dying.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dying.common.BaseResponse;
import com.dying.common.ErrorCode;
import com.dying.common.ResultUtils;
import com.dying.domain.Team;
import com.dying.domain.User;
import com.dying.domain.request.TeamDTO;
import com.dying.exception.BusinessException;
import com.dying.service.TeamService;
import com.dying.service.UserService;
import com.dying.service.UserTeamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dying.constant.UserConstant.USER_LOGIN_STATE;


/**
 * @Author daylight
 * @Date 2025/7/9 15:02
 */
@RestController
@RequestMapping("/team")
@Tag(name="队伍接口")
@CrossOrigin(origins = {"http://123.249.124.78:8080","http://localhost:5173"},allowCredentials = "true")
@Slf4j
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/create")
    public BaseResponse<Boolean> addTeam(@RequestBody TeamDTO teamDto, HttpServletRequest request) {
        if(teamDto==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍参数为空");
        }
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) attribute;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        boolean flag=teamService.createTeam(teamDto, user);
        return ResultUtils.success(flag);
    }

    @GetMapping("list")
    public BaseResponse<List<TeamDTO>> listTeam(Integer count,HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) attribute;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        List<TeamDTO> res=teamService.getTeamList(user,count);
        return ResultUtils.success(res);
    }

}
