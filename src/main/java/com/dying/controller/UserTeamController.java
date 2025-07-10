package com.dying.controller;

import com.dying.common.ErrorCode;
import com.dying.domain.User;
import com.dying.domain.UserTeam;
import com.dying.exception.BusinessException;
import com.dying.service.UserTeamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dying.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @Author daylight
 * @Date 2025/7/10 18:01
 */
@RestController
@RequestMapping("/teamUser")
@Tag(name="展示队伍成员信息")
@CrossOrigin(origins = {"http://123.249.124.78:8080","http://localhost:5173"},allowCredentials = "true")
@Slf4j
public class UserTeamController {
    @Resource
    private UserTeamService userTeamService;

    @GetMapping("/list")
    public List<User> list(Long teamId, HttpServletRequest request) {
        if(teamId == null||teamId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询队伍不存在");
        }
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) attribute;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        return userTeamService.getTeamPeople(teamId,user);
    }
}
