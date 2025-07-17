package com.dying.controller;

import com.dying.common.BaseResponse;
import com.dying.common.ErrorCode;
import com.dying.common.ResultUtils;
import com.dying.domain.Blog;
import com.dying.domain.User;
import com.dying.domain.request.BlogRequest;
import com.dying.exception.BusinessException;
import com.dying.service.BlogService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dying.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @Author daylight
 * @Date 2025/7/17 15:34
 */

@Slf4j
@RequestMapping("/blog")
@RestController()
@CrossOrigin(origins = {"http://123.249.124.78:8080", "http://localhost:5173"}, allowCredentials = "true")
public class BlogController {
    @Resource
    private BlogService blogService;

    @PostMapping("/create")
    public BaseResponse<String> createBlog(@RequestBody BlogRequest blog, HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        if (blog == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "发表内容为空");
        }
        if (blogService.createBlog(blog, currentUser)) {
            return ResultUtils.success("success");
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建失败");
        }
    }

    @PostMapping("/update")
    public BaseResponse<String> updateBlog(@RequestBody BlogRequest blog, HttpServletRequest request, Long id) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        if (blog == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新不合法");
        }
        if (blogService.updateBlog(blog, currentUser, id)) {
            return ResultUtils.success("success");
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新失败");
        }
    }

    @GetMapping("/list")
    public BaseResponse<List<Blog>> getBlogList(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        return ResultUtils.success(blogService.getBlogList(currentUser));
    }

    @GetMapping("/delete")
    public BaseResponse<Boolean> deleteBlog(HttpServletRequest request, Long id) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        if (blogService.deleteBlog(currentUser, id)) {
            return ResultUtils.success(true);
        } else {
            return ResultUtils.error(ErrorCode.NO_AUTO);
        }
    }

    @GetMapping("like")
    public BaseResponse<Boolean> getBlogLike(Long blogId, HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        Long userId = currentUser.getId();
        if (blogId == null || userId == null || userId < 0 || blogId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不正确");
        }
        return ResultUtils.success(blogService.like(blogId, userId));
    }

    @GetMapping("/getOne")
    public BaseResponse<Blog> getBlog(Long blogId, HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        Long userId = currentUser.getId();
        if (blogId == null || userId == null || userId < 0 || blogId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不正确");
        }
        return ResultUtils.success(blogService.getBlog(blogId, userId));
    }
}
