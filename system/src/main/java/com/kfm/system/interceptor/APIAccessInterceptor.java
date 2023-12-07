package com.kfm.system.interceptor;

import com.kfm.system.bean.AccessInfo;
import com.kfm.system.bean.IpAccessList;
import com.kfm.system.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
public class APIAccessInterceptor implements HandlerInterceptor {
    @Autowired
    private IpAccessList accessList;
    @Autowired
    private IpInterceptor ipInterceptor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取 ip
        String ip = IpUtil.getIpAddress(request);

        AccessInfo accessInfo = accessList.getIpMap().get(ip);

        long current = System.currentTimeMillis();
        System.out.println(ip + " : " + current);

        if (accessInfo == null) {
            // 第一次访问
            accessInfo = new AccessInfo();
            accessInfo.setCount(1);
            accessInfo.setMillis(current);
            accessList.getIpMap().put(ip, accessInfo);
        } else if ((current - accessInfo.getMillis()) > 1000) {
            // 超过 1 秒
            accessInfo.setCount(1);
            accessInfo.setMillis(current);
        } else {
            // 1 秒内， 判断次数
            if (accessInfo.getCount() > 10) {
                ipInterceptor.blackList.add(ip);
                response.sendRedirect("/error");
                return false;
            } else {
                accessInfo.setCount(accessInfo.getCount() + 1);
            }
        }

        return true;
    }
}
