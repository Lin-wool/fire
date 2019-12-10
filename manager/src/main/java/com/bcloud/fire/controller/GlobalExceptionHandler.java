package com.bcloud.fire.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Resp defaultErrorHandler(HttpServletRequest request, Exception e) { // 出现异常之后会跳转到此方法
        // TODO API访问异常处理
        // String uri = request.getRequestURI();
        // alarmService.publish(Constants.ALARM_CODE_API + uri, uri + "访问异常:" +
        // e.getMessage());
        e.printStackTrace();
        return Resp.fail(e.getMessage());
    }
}
