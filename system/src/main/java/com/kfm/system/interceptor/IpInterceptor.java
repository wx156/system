package com.kfm.system.interceptor;

import com.kfm.system.util.IpUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
@Component
public class IpInterceptor implements HandlerInterceptor {
    public final List<String> blackList = new CopyOnWriteArrayList<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取 ip
        String ip = IpUtil.getIpAddress(request);

        if (blackList.contains(ip)){
            System.out.println(ip + "已被拉黑");
            // TODO 可以写一个页面，提示用户被拉黑
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write("您已经被限制访问，请稍后再试");
            return false;
        }

        return true;
    }
}
