package com.bcloud.fire.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import com.bcloud.fire.Constants;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class LoginFilter extends ZuulFilter {

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpSession session = request.getSession();

        if (session.getAttribute(Constants.SESSION_SYSUSER_KEY) == null
                && session.getAttribute(Constants.SESSION_ACCOUNT_KEY) == null) {
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(200);
            // 为使得中文字符不乱码
            ctx.getResponse().setCharacterEncoding("UTF-8");

            JSONObject json = new JSONObject();
            json.put("success", false);
            json.put("error", "nologin");

            ctx.setResponseBody(json.toString());

        }

        return null;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

}
