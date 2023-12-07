package com.kfm.system.interceptor;

import com.kfm.system.bean.IPInfo;
import com.kfm.system.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
@Component
public class LoginErrorInterceptor implements HandlerInterceptor {
    @Autowired
    private IPInfo ipInfo;

    @Value("${my.ip.maxCount}")
    private Integer maxCount;

    @Autowired
    private IpInterceptor ipInterceptor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, Integer> ipMap = ipInfo.getIpMap();

        // 获取请求的 ip
        String ip = IpUtil.getIpAddress(request);

        Integer integer = ipMap.get(ip);

        if (integer == null || integer <= maxCount){
            return true;
        } else {
            System.out.println(ip + "错误次数过多，已被拉黑");
            ipInterceptor.blackList.add(ip);
            response.sendRedirect("/error");
            return false;
        }
    }
}
