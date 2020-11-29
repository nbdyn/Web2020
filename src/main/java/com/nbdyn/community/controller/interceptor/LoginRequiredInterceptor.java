package com.nbdyn.community.controller.interceptor;

import com.nbdyn.community.annotation.LoginRequired;
import com.nbdyn.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if (loginRequired != null && hostHolder.getUser() == null) {
                response.sendRedirect(request.getContextPath() + "/login");//request.getContextPath() or 通过注入变量的得到(即/community)
                                                                                //    @Value("${server.servlet.context-path}")
                                                                                //    private String contextPath;

                return false;
            }
        }
        return true;
    }
}
