package com.dying;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;  
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;  
  
@Configuration  
public class CorsConfig implements WebMvcConfigurer {  
  
    @Override  
    public void addCorsMappings(CorsRegistry registry) {  
        // 添加映射路径  
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:9090","http://123.249.124.78:9090","http://www.seestars.top:9090","http://www.seestars.top:80") // 允许哪些域的请求，星号代表允许所有
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE") // 允许的方法
                .allowedHeaders("*") // 允许哪些请求头
                .exposedHeaders("*")
                .allowCredentials(true) // 是否发送cookie  
                .maxAge(168000); // 预检间隔时间  
    }  
}