package com.dying.controller;

import com.dying.common.BaseResponse;
import com.dying.common.ErrorCode;
import com.dying.common.ResultUtils;
import com.dying.domain.CommentVo;
import com.dying.domain.User;
import com.dying.domain.UserComment;
import com.dying.exception.BusinessException;
import com.dying.service.UserCommentService;
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
 * @Date 2025/7/18 11:32
 */
@Slf4j
@RequestMapping("/comment")
@RestController()
@CrossOrigin(origins = {"http://123.249.124.78:8080", "http://localhost:5173"}, allowCredentials = "true")
public class CommentController {

    @Resource
    private UserCommentService userCommentService;

    @GetMapping("/create")
    public BaseResponse<UserComment> create(String comment,Long blogId, HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        UserComment userComment=userCommentService.createComment(comment,blogId,currentUser.getId());
        if(userComment==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }else{
            return ResultUtils.success(userComment);
        }
    }

    @GetMapping("/list")
    public BaseResponse<List<CommentVo>> list(Long blogId, HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        List<CommentVo> list=userCommentService.getAllComments(blogId);
        if(list==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }else{
            return ResultUtils.success(list);
        }
    }

}
