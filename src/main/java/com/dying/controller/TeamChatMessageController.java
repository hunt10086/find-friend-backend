package com.dying.controller;

import com.dying.common.BaseResponse;
import com.dying.common.ResultUtils;
import com.dying.domain.User;
import com.dying.domain.request.TeamChatMessageRequest;
import com.dying.domain.vo.TeamChatMessageVo;
import com.dying.exception.BusinessException;
import com.dying.common.ErrorCode;
import com.dying.service.TeamChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import static com.dying.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/api/teamChat")
public class TeamChatMessageController {
    @Autowired
    private TeamChatMessageService teamChatMessageService;

    // 发送消息
    @PostMapping("/send")
    public BaseResponse<Boolean> sendMessage(@RequestBody TeamChatMessageRequest request, jakarta.servlet.http.HttpServletRequest httpServletRequest) {
        Object attribute = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        boolean result = teamChatMessageService.sendTeamChatMessage(currentUser.getId(), request.getTeamId(), request.getContent());
        if (!result) {
            return ResultUtils.error(403, "无权限或发送失败", null);
        }
        return ResultUtils.success(true);
    }

    // 获取队伍消息列表
    @GetMapping("/list")
    public BaseResponse<List<TeamChatMessageVo>> getMessageList(@RequestParam Long teamId, jakarta.servlet.http.HttpServletRequest httpServletRequest) {
        Object attribute = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        // 复用权限校验逻辑
        boolean isMember = teamChatMessageService.sendTeamChatMessage(currentUser.getId(), teamId, "__check_only__");
        if (!isMember) {
            return ResultUtils.error(403, "无权限", null);
        }
        List<TeamChatMessageVo> list = teamChatMessageService.getTeamChatMessages(teamId);
        return ResultUtils.success(list);
    }
} 