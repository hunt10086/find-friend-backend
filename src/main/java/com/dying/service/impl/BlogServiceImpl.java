package com.dying.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.dying.common.ErrorCode;
import com.dying.domain.Blog;
import com.dying.domain.User;
import com.dying.domain.request.BlogRequest;
import com.dying.exception.BusinessException;
import com.dying.service.BlogService;
import com.dying.mapper.BlogMapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.dying.constant.BlogConstant.*;
import static com.dying.constant.RedisConstant.BLOG_LIKE;

/**
* @author 666
* @description 针对表【blog(博客文章)】的数据库操作Service实现
* @createDate 2025-07-17 15:21:54
*/
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog>
    implements BlogService{

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean createBlog(BlogRequest blog, User loginUser){
        // 1.检验返回值为空  是否登录
        if(loginUser == null||loginUser.getId()==null||blog==null||loginUser.getId()<0){
            return false;
        }
        // 2. 标题，文章不能为空
        if(StringUtils.isBlank(blog.getTitle())||StringUtils.isBlank(blog.getPassage())){
            return false;
        }
        // 3.文章 标题 种类 符合规范
        if(!StringUtils.isBlank(blog.getKind())&&blog.getKind().length()>MAX_KIND_LENGTH){
            return false;
        }
        if(blog.getTitle().length()>MAX_TITLE_LENGTH||blog.getPassage().length()>MAX_PASSAGE_LENGTH){
            return false;
        }
        Blog blog1 = new Blog();
        blog1.setTitle(blog.getTitle());
        blog1.setKind(blog.getKind());
        blog1.setPassage(blog.getPassage());
        blog1.setUserId(loginUser.getId());
        blog1.setCreateTime(new Date());
        blog1.setUpdateTime(new Date());
        blog1.setPraise(0);
        blogMapper.insert(blog1);
        return true;
    }

    @Override
    public boolean updateBlog(BlogRequest blog, User loginUser, Long id){
        Blog blog1 = blogMapper.selectById(id);
        if(blog1==null){
            return false;
        }
        // 1.检验返回值为空  是否登录
        if(loginUser == null||loginUser.getId()==null||blog==null||loginUser.getId()<0){
            return false;
        }
        // 2. 标题，文章不能为空
        if(StringUtils.isBlank(blog.getTitle())||StringUtils.isBlank(blog.getPassage())){
            return false;
        }
        // 3.文章 标题 种类 符合规范
        if(!StringUtils.isBlank(blog.getKind())&&blog.getKind().length()>MAX_KIND_LENGTH){
            return false;
        }
        blog1.setTitle(blog.getTitle());
        blog1.setKind(blog.getKind());
        blog1.setPassage(blog.getPassage());
        blog1.setUpdateTime(new Date());
        blogMapper.updateById(blog1);
        return true;
    }

    @Override
    public List<Blog> getBlogList(User loginUser){
        if(loginUser == null||loginUser.getId()==null||loginUser.getId()<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未登录");
        }
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("praise");
        return blogMapper.selectList(queryWrapper);
    }

    @Override
    public boolean deleteBlog(User loginUser, Long id){
        if(loginUser == null||loginUser.getId()==null||loginUser.getId()<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未登录");
        }
        if(id==null||id<0){
            return false;
        }
        Blog blog = blogMapper.selectById(id);
        if(!Objects.equals(blog.getUserId(), loginUser.getId())){
            return false;
        }
        blogMapper.deleteById(id);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean like(Long blogId, Long userId){
        if(blogId==null||userId==null||userId<0||blogId<0) {
            return false;
        }
        Blog blog = blogMapper.selectById(blogId);
        String key=BLOG_LIKE+blogId+":";
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, userId + "");
        if(BooleanUtil.isTrue(isMember)){
            stringRedisTemplate.opsForSet().remove(key, userId + "");
            blog.setPraise(blog.getPraise()-1);
        }else{
            stringRedisTemplate.opsForSet().add(key, userId + "");
            blog.setPraise(blog.getPraise()+1);
        }
        return blogMapper.updateById(blog) > 0;
    }

    @Override
    public Blog getBlog(Long blogId, Long userId){
        if(blogId==null||userId==null||userId<0||blogId<0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求失败");
        }
        return blogMapper.selectById(blogId);
    }


}




