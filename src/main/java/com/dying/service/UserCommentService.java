package com.dying.service;

import com.dying.domain.CommentVo;
import com.dying.domain.UserComment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 666
* @description 针对表【user_comment(用户评论)】的数据库操作Service
* @createDate 2025-07-18 11:30:24
*/
public interface UserCommentService extends IService<UserComment> {

    UserComment createComment(String  comment,Long blogID, Long userId);

    List<CommentVo> getAllComments(Long blogId);
}
