package com.kfm.system.config;

import com.kfm.system.interceptor.APIAccessInterceptor;
import com.kfm.system.interceptor.IpInterceptor;
import com.kfm.system.interceptor.LoginErrorInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginErrorInterceptor loginErrorInterceptor;
    @Autowired
    private APIAccessInterceptor apiAccessInterceptor;
    @Autowired
    private IpInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
//        registry.addInterceptor(new MyInterceptor()).addPathPatterns("/**").excludePathPatterns("/css/**", "/js/**", "/images/**");
//        registry.addInterceptor(new MyInterceptor2()).addPathPatterns("/**");

        registry.addInterceptor(interceptor).addPathPatterns("/**");
        registry.addInterceptor(loginErrorInterceptor).addPathPatterns("/login", "/").excludePathPatterns("/css/**", "/js/**", "/images/**", "/layui/**");
        registry.addInterceptor(apiAccessInterceptor).addPathPatterns("/**").excludePathPatterns("/css/**", "/js/**", "/images/**", "/layui/**");
    }
}
