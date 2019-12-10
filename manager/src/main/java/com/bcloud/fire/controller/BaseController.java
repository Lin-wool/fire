package com.bcloud.fire.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.bcloud.fire.Constants;
import com.bcloud.fire.entity.Account;
import com.bcloud.fire.entity.SysLog;
import com.bcloud.fire.entity.SysUser;
import com.bcloud.fire.repository.SysLogRepository;

public class BaseController {

    @Autowired
    private SysLogRepository sysLogRepository;

    public void saveSysUserLog(HttpServletRequest request, String content, long type) {
        String operName = null;
        SysUser sysUser = (SysUser) request.getSession().getAttribute(Constants.SESSION_SYSUSER_KEY);
        if (sysUser != null) {
            operName = sysUser.getName();
        } else {
            operName = "SYSTEM";
        }
        String ip = getIp(request);
        long loginType = Constants.LOGIN_TYPE_SYS_USER;
        saveLog(operName, content, ip, loginType, type);
    }

    public void saveClientLog(HttpServletRequest request, String content, long type) {
        String operName = null;
        Account client = (Account) request.getSession().getAttribute(Constants.SESSION_ACCOUNT_KEY);
        if (client != null) {
            operName = client.getName();
        } else {
            operName = "ACCOUNT";
        }
        String ip = getIp(request);
        long loginType = Constants.LOGIN_TYPE_ACCOUNT;
        saveLog(operName, content, ip, loginType, type);
    }

    public void saveMaintainLog(HttpServletRequest request, String content, long type) {
        String operName = null;
        Account maintain = (Account) request.getSession().getAttribute(Constants.SESSION_ACCOUNT_KEY);
        if (maintain != null) {
            operName = maintain.getName();
        } else {
            operName = "MAINTAIN";
        }
        String ip = getIp(request);
        long loginType = Constants.LOGIN_TYPE_MAINTAIN_ADMIN;
        saveLog(operName, content, ip, loginType, type);
    }

    private void saveLog(String operName, String operContent, String ip, long loginType, long type) {
        SysLog log = new SysLog();
        log.setName(operName);
        log.setContent(operContent);
        log.setIp(ip);
        log.setType(type);
        log.setLoginType(loginType);
        log.setCreateTime(new Date());
        sysLogRepository.save(log);
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String proxyIp = System.getProperty("proxy.ip");
        if (proxyIp != null) {
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && proxyIp.contains("," + ip + ",")) {
                return forwarded;
            }
        }
        return ip;
    }
}
