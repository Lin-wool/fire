package com.bcloud.fire.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.bcloud.fire.Constants;
import com.bcloud.fire.annotation.Authority;

public class AuthorityInterceptor implements HandlerInterceptor {

    private boolean fail(HttpServletResponse response, String msg) throws Exception {
        response.setCharacterEncoding("UTF-8");

        JSONObject json = new JSONObject();
        json.put("success", false);
        json.put("error", msg);

        response.getOutputStream().write(json.toString().getBytes());
        response.getOutputStream().flush();

        return false;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession();

        if (session.getAttribute(Constants.SESSION_SYSUSER_KEY) == null
                && session.getAttribute(Constants.SESSION_ACCOUNT_KEY) == null) {
            return fail(response, "nologin");
        }

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.getMethod().isAnnotationPresent(Authority.class)) {
                Authority authority = handlerMethod.getMethod().getAnnotation(Authority.class);

                String[] requireRoles = authority.role();
                boolean flag = false;
                for (String r : requireRoles) {
                    Boolean hasRole = request.getSession().getAttribute(r) == null ? false
                            : (Boolean) request.getSession().getAttribute(r);
                    if (hasRole) {
                        flag = true; // 包含其一即可
                        break;
                    }
                }
                if (!flag) {
                    return fail(response, "权限不足");
                }
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

}
