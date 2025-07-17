package com.dying.service;

import cn.hutool.core.thread.BlockPolicy;
import com.dying.domain.Blog;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * @Author daylight
 * @Date 2025/7/17 15:26
 */
@MapperScan("com.dying.mapper")
@SpringBootTest
public class SqlTest {
    @Autowired
    private BlogService blogService;
    @Test
    public void test() {
        Blog blog = new Blog();
        blog.setTitle("test");
        blog.setKind("爱困");
        blog.setUserId(1L);
        blog.setPassage("afjhhfkajfhakfafafhakf");
        blog.setCreateTime(new Date());
        blog.setUpdateTime(new Date());
        blogService.save(blog);
    }

}
