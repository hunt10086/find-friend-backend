package com.dying.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dying.domain.Blog;
import com.dying.domain.BlogVo;
import com.dying.domain.User;
import com.dying.domain.request.BlogRequest;

import java.util.List;

/**
* @author 666
* @description 针对表【blog(博客文章)】的数据库操作Service
* @createDate 2025-07-17 15:21:54
*/
public interface BlogService extends IService<Blog> {

    boolean createBlog(BlogRequest blog, User login);

    boolean updateBlog(BlogRequest blog, User loginUser, Long id);

    List<BlogVo> getBlogList(User loginUser);

    boolean deleteBlog(User loginUser, Long id);

    boolean like(Long blogId, Long userId);

    Blog getBlog(Long blogId, Long userId);

    List<BlogVo> getMyBlog(Long userId);
}
