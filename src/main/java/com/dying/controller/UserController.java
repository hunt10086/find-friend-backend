package com.dying.controller;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dying.common.BaseResponse;
import com.dying.common.ErrorCode;
import com.dying.common.ResultUtils;
import com.dying.constant.UserConstant;
import com.dying.domain.User;
import com.dying.domain.UserVo;
import com.dying.domain.request.UserLoginRequest;
import com.dying.domain.request.UserRegisterRequest;
import com.dying.exception.BusinessException;
import com.dying.mapper.UserMapper;
import com.dying.service.UserService;
import com.dying.service.impl.emailServiceImpl;
import com.dying.utils.RegexUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.dying.constant.UserConstant.USER_CHECK_CODE;
import static com.dying.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户接口
 *
 * @author 666
 */
@RestController()
@RequestMapping("/user")
@Tag(name = "用户接口")
@CrossOrigin(origins = {"http://www.seestars.top:9090", "http://localhost:9090"}, allowCredentials = "true")
@Slf4j
public class  UserController {

    @Resource
    private UserService userService;

    @Resource
    private emailServiceImpl emailServiceImpl;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserMapper userMapper;


    @Operation(summary = "发送验证码")
    @GetMapping("/sendCode")
    public BaseResponse<Long>sendCode(@RequestParam String email) throws MessagingException, UnsupportedEncodingException {
        boolean flag = userService.checkEmail(email);
        if(!flag) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"验证码发送失败");
        }
        String code=emailServiceImpl.sendEmailBackCode(email);
        stringRedisTemplate.opsForValue().set(USER_CHECK_CODE+email,code,5, TimeUnit.MINUTES);
        return ResultUtils.success(1L);
    }

    @Operation(summary = "注册请求")
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String email=userRegisterRequest.getEmail();
        String code=userRegisterRequest.getCode();
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");
        }
        if (StringUtils.isBlank(userAccount) || StringUtils.isBlank(userPassword) || StringUtils.isBlank(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码为空");
        }
        if(StringUtils.isBlank(email)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"邮箱为空");
        }
        if(!code.equals(stringRedisTemplate.opsForValue().get(USER_CHECK_CODE+email))){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"验证码错误");
        }
        long l = userService.userRegister(userAccount, userPassword, checkPassword,email);
        return ResultUtils.success(l);
    }

    @Operation(summary = "展示当前用户信息")
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if(currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        long userId = currentUser.getId();
        User user = userService.getById(userId);
        return ResultUtils.success(user);
    }

    @Operation(summary = "登录请求")
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"清输入账号，密码");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        Double latitude = userLoginRequest.getLatitude();
        Double longitude = userLoginRequest.getLongitude();
        if (StringUtils.isBlank(userAccount) || StringUtils.isBlank(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户或密码为空");
        }
        log.info("登陆成功");
        User user = userService.userLogin(userAccount, userPassword, request, latitude, longitude);
        return ResultUtils.success(user);
    }

    @Operation(summary = "查询所有用户")
    @GetMapping("/search")
    public BaseResponse<List<User>> getUser(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTO,"权限不足");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("user_name", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSaftyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @Operation(summary = "删除用户")
    @PostMapping("/delete")
    public BaseResponse<Boolean> userDelete(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTO,"权限不够");
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    @Operation(summary = "查询单个用户")
    @GetMapping("/searchOne")
    public BaseResponse<List<User>> updateMassage(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) attribute;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        long userId = user.getId();
        User user1 = userService.getById(userId);
        List<User> list = new ArrayList<>();
        list.add(user1);
        return ResultUtils.success(list);
    }

    @Operation(summary = "根据标签名查询用户")
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam (required = false) List<String> tagsList){
        if(CollectionUtils.isEmpty(tagsList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> list=userService.searchAllByTags(tagsList);
        return ResultUtils.success(list);
    }

    @Operation(summary = "用户更新")
    @PostMapping("/update")
    public BaseResponse<Boolean> userUpdate(@RequestBody User user, HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user1 = (User) attribute;
        user.setId(user1.getId());
        if (user1 == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        if(!userService.userUpdate(user)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更改失败");
        }
        return ResultUtils.success(true);
    }

    @Operation(summary = "用户注销")
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result > 0);
    }

    @Operation(summary = "主页用户推荐")
    @GetMapping("/listLike")
    public BaseResponse<List<User>> userListLike(Integer count,HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if(currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        List<User> list=userService.backLike(currentUser,count);
        return ResultUtils.success(list,list.size());
    }

    @Operation(summary = "附近用户")
    @GetMapping("/nearUser")
    public BaseResponse<List<UserVo>> nearUser(HttpServletRequest request) {
        Long id=checkLogin(request);
        if(id==null||id<=0){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        List<UserVo> list=userService.getNearUser(id);
        return ResultUtils.success(list);
    }

    @Operation(summary = "根据id查询")
    @GetMapping("/search/one")
    public BaseResponse<User> searchUserById(Long id, HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) attribute;
        if(currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        User user = userMapper.selectById(id);
        User saftyUser = userService.getSaftyUser(user);
        if(user==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        return ResultUtils.success(saftyUser);
    }


    public boolean isAdmin(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) attribute;
        if (user == null || user.getUserRole() != UserConstant.ADMIN_ROLE) {
            return false;
        }
        return true;
    }

    private Long checkLogin(HttpServletRequest request){
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user1 = (User) attribute;
        if (user1 == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        return user1.getId();
    }

}
