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

        // 缓存预热：预先生成第一页推荐
        backLike(user, 1);

        return 0;
    }

    @Override
    public User userLogin(String userAccount, String password, HttpServletRequest request, Double latitude, Double longitude) {
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

        // 缓存预热：预先生成第一页推荐
        backLike(saftyUser, 1);

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
        // 缓存预热：预先生成第一页推荐
        backLike(user, 1);
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
    public List<User> backLike(User loginUser, Integer count) {
        final List<String> myTags;
        if (StringUtils.isBlank(loginUser.getTags())) {
            myTags = new ArrayList<>();
            myTags.add("Java");
        } else {
            List<String> tempTags;
            try {
                tempTags = JSONUtil.toList(JSONUtil.parseArray(loginUser.getTags()), String.class);
            } catch (Exception e) {
                tempTags = new ArrayList<>();
            }
            if (tempTags.isEmpty()) {
                tempTags.add("Java");
            }
            myTags = tempTags;
        }

        // 1. Redis缓存优先
        String cacheKey = USER_LIKE_STATE + ":" + loginUser.getId() + ":" + String.join(",", myTags) + ":" + count;
        String cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNotBlank(cacheValue)) {
            return JSONUtil.toList(cacheValue, User.class);
        }

        // 2. 查询所有其他用户，且tags不为空
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("id", loginUser.getId());
        queryWrapper.isNotNull("tags");
        queryWrapper.ne("tags", "");
        List<User> allUsers = userMapper.selectList(queryWrapper);

        // 3. 计算相似度
        List<User> sortedUsers = allUsers.stream()
            .peek(u -> {
                List<String> tags;
                try {
                    tags = JSONUtil.toList(JSONUtil.parseArray(u.getTags()), String.class);
                } catch (Exception e) {
                    tags = new ArrayList<>();
                }
                int similarity = 0;
                for (String tag : myTags) {
                    if (tags.contains(tag)) {
                        similarity++;
                    }
                }
                u.setUserStatus(similarity); // 临时存储相似度
            })
            .sorted((u1, u2) -> Integer.compare(u2.getUserStatus(), u1.getUserStatus()))
            .collect(Collectors.toList());

        // 4. 分页
        int pageSize = USER_PAGE_SIZE;
        int start = (count - 1) * pageSize;
        int end = Math.min(start + pageSize, sortedUsers.size());
        if (start >= end) {
            return new ArrayList<>();
        }
        List<User> result = sortedUsers.subList(start, end);

        // 5. 返回脱敏用户
        List<User> safeList = result.stream().map(this::getSaftyUser).collect(Collectors.toList());

        // 6. 写入缓存
        stringRedisTemplate.opsForValue().set(cacheKey, JSONUtil.toJsonStr(safeList), USER_REDIS_EXPIRE, TimeUnit.MINUTES);

        return safeList;
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




