package com.dying.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户评论
 * @TableName user_comment
 */
@TableName(value ="user_comment")
@Data
public class UserComment {
    /**
     * 发表用户Id
     */
    private Long userId;

    /**
     * 博客id
     */
    private Long blogId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 评论
     */
    private String content;
}