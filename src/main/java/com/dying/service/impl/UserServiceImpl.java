package com.dying.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dying.common.ErrorCode;
import com.dying.domain.User;
import com.dying.domain.UserVo;
import com.dying.exception.BusinessException;
import com.dying.service.GeoService;
import com.dying.service.UserService;
import com.dying.mapper.UserMapper;
import com.dying.utils.MD5Utils;
import com.dying.utils.RegexUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.dying.constant.UserConstant.*;

/**
 * @author 666
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2025-05-10 13:59:54
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private GeoService geoService;

    private static final String SALT = "Dying";
    @Override
    public long userRegister(String userAccount, String password, String checkPassword,String email) {
        //账号密码不能为空
        if (StringUtils.isBlank(userAccount) || StringUtils.isBlank(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码为空");
        }
        if (StringUtils.isBlank(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"第二次输入密码为空");
        }
        if(StringUtils.isBlank(email)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"邮箱");
        }
        if(!RegexUtils.isEmailInvalid(email)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"邮箱格式错误");
        }
        //账号大于四位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度小于4");
        }
        //密码不小于八位
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不小于八位");
        }
        //账号不能重复
        User flag = userMapper.findAllByUserAccountBoolean(userAccount);

        if (flag!=null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能重复");
        }
        //不允许出现特殊字符
        String regEx = "\\pP|\\pS|\\s+";
        String str = Pattern.compile(regEx).matcher(userAccount).replaceAll("").trim();
        if (!userAccount.equals(str)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不允许出现特殊字符");
        }

        if(!checkPassword.equals(password)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不相同");
        }

        String  newpassword = MD5Utils.string2MD5(SALT + password + SALT);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(newpassword);
        user.setEmail(email);
        user.setUserStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        userMapper.insert(user);
        log.info("用户创建成功");
        return 0;
    }

    @Override
    public User userLogin(String userAccount, String password, HttpServletRequest request,Double latitude,Double longitude) {
        //账号密码不能为空
        if (StringUtils.isBlank(userAccount) || StringUtils.isBlank(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码为空");
        }
        //账号大于四位z
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号大于四位");
        }
        //密码不小于八位
        if (password.length() < 8) {
          throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码小于八位");
        }
        //不允许出现特殊字符
        String regEx = "\\pP|\\pS|\\s+";
        String str = Pattern.compile(regEx).matcher(userAccount).replaceAll("").trim();
        if (!userAccount.equals(str)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不允许出现特殊字符");
        }
        //检验用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount).eq("user_password",MD5Utils.string2MD5(SALT+password+SALT));
        User user = userMapper.selectOne(queryWrapper);
        if(user==null){
            log.info("用户或密码错误");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户或密码错误");
        };
        userMapper.updateById(user);
        //用户脱敏
        User saftyUser=getSaftyUser(user);
        //记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,saftyUser);

        return saftyUser;
    }

    @Override
    public User getSaftyUser(User originUser) {
        if(originUser==null){
            return null;
        }
        User saftyUser = new User();
        saftyUser.setId(originUser.getId());
        saftyUser.setUserName(originUser.getUserName());
        saftyUser.setUserAccount(originUser.getUserAccount());
        saftyUser.setAvatarUrl(originUser.getAvatarUrl());
        saftyUser.setGender(originUser.getGender());
        saftyUser.setPhone("");
        saftyUser.setEmail("");
        saftyUser.setUserStatus(originUser.getUserStatus());
        saftyUser.setCreateTime(originUser.getCreateTime());
        saftyUser.setUserRole(originUser.getUserRole());
        saftyUser.setTags(originUser.getTags());
        saftyUser.setProfile(originUser.getProfile());
        return saftyUser;
    }

    @Override
    public boolean userUpdate(User user) {
        if(user==null) {
            return false;
        }
        userMapper.updateById(user);
        return true;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        if(request == null){
            return 0;
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     *
     * @param tagsList
     * sql 查询
     */
    @Override
    public List<User> searchAllByTags(List<String> tagsList)    {
        if(CollectionUtils.isEmpty(tagsList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"标签列表为空");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(stringRedisTemplate.opsForValue().get(USER_LIKE_STATE+tagsList)!=null){
            return JSONUtil.toList(stringRedisTemplate.opsForValue().get(USER_LIKE_STATE+tagsList),User.class);
        }
        for (String tag : tagsList) {
           queryWrapper=queryWrapper.like("tags",tag);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        List<User> list= userList.stream().map(this::getSaftyUser).collect(Collectors.toList());
        stringRedisTemplate.opsForValue().set(USER_LIKE_STATE+tagsList,JSONUtil.toJsonStr(list),USER_REDIS_EXPIRE, TimeUnit.MINUTES);
        return list;    }

    @Override
    public boolean checkEmail(String email){
        if(StringUtils.isBlank(email)){
            return false;
        }
        if(!RegexUtils.isEmailInvalid(email)){
            return false;
        }
        return true;
    }

    @Override
    public List<User> backLike(User loginUser,Integer count){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.notIn("id",loginUser.getId());
        List<String> tagsList=JSONUtil.toList(JSONUtil.parseArray(USER_DEFAULT_TAGS),String.class);
        if(stringRedisTemplate.opsForValue().get(USER_LIKE_STATE+loginUser.getTags()+count)!=null){
            return JSONUtil.toList(stringRedisTemplate.opsForValue().get(USER_LIKE_STATE+loginUser.getTags()+count),User.class);
        }
        queryWrapper.in("tags",loginUser.getTags());
//        for (String tag : tagsList) {
//            queryWrapper=queryWrapper.like("tags",tag);
//        }
        IPage<User> page=new Page<>(count,USER_PAGE_SIZE);
        userMapper.selectPage(page,queryWrapper);
        List<User> userList = page.getRecords();
        List<User> list= userList.stream().map(this::getSaftyUser).collect(Collectors.toList());
        stringRedisTemplate.opsForValue().set(USER_LIKE_STATE+loginUser.getTags()+count,JSONUtil.toJsonStr(list),USER_REDIS_EXPIRE, TimeUnit.MINUTES);
        return list;
    }

    @Override
    public List<UserVo> getNearUser(Long userId) {
        User loginUser=userMapper.selectById(userId);
        double latitude = loginUser.getLatitude();
        double longitude=loginUser.getLongitude();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("latitude");
        queryWrapper.isNotNull("longitude");
        queryWrapper.ne("id",userId);
        List<User> userList = userMapper.selectList(queryWrapper);
        List<UserVo> list= new ArrayList<>();
        int i=0;
        for(User user : userList){
            if(i>15){
                break;
            }
            double longitude2 = user.getLongitude();
            double latitude2 = user.getLatitude();
            double distance=getDistance(latitude,longitude,latitude2,longitude2);
            if(distance<1000){
                UserVo userVo = new UserVo();
                BeanUtils.copyProperties(user,userVo);
                userVo.setDistance(distance);
                i++;
                list.add(userVo);
            }
        }
        list.sort((o1, o2) -> Double.compare(o1.getDistance(), o2.getDistance()));
        return list;

    }


    public double getDistance(Double latitude1, Double longitude1, Double latitude2, Double longitude2) {
        geoService.addLocation("cities", "Beijing", longitude1, latitude1);
        geoService.addLocation("cities", "Shanghai", longitude2, latitude2);

        // 计算距离
        return geoService.getDistance("cities", "Beijing", "Shanghai");
    }
}




