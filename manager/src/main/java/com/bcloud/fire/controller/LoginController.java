package com.bcloud.fire.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bcloud.fire.Constants;
import com.bcloud.fire.entity.Account;
import com.bcloud.fire.entity.SysUser;
import com.bcloud.fire.repository.AccountRepository;
import com.bcloud.fire.repository.SysUserRepository;
import com.bcloud.fire.util.CommonUtils;
import com.bcloud.fire.util.EncryptUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RestController
@RequestMapping("/api")
public class LoginController extends BaseController {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping(value = "/sysUser/login", method = RequestMethod.POST)
    public Resp userLogin(HttpSession session, HttpServletRequest request,
            @RequestParam String userName,
            @RequestParam String password) {

        SysUser user = sysUserRepository.findByName(userName);
        if (user == null) {
            return Resp.fail("无此用户");
        }
        if (user.getLocked() == 1) {
            return Resp.fail("用户状态异常");
        }

        String md5Password = EncryptUtil.md5(password, user.getSalt());

        if (!md5Password.equalsIgnoreCase(user.getPassword())) {
            return Resp.fail("密码错误");
        }
        session.setAttribute(Constants.SESSION_SYSUSER_KEY, user);
        session.setAttribute("loginType", 0);

        if ("admin".equals(user.getName())) {
            List<String> allRoles = Lists.newArrayList();
            for (Constants.Role r : Constants.Role.values()) {
                allRoles.add(r.name());
                session.setAttribute(r.name(), true);
            }
            user.setRole(Joiner.on(",").join(allRoles));
        } else if (StringUtils.isNotBlank(user.getRole())) {
            String[] roles = user.getRole().split(",");
            if (roles != null) {
                for (String role : roles) {
                    session.setAttribute(role, true);
                }
            }

        }

        saveSysUserLog(request, "登录", Constants.LOG_TYPE_LOGIN);

        Map<String, Object> map = Maps.newHashMap();
        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("fullName", user.getFullName());
        map.put("loginType", Constants.LOGIN_TYPE_SYS_USER);
        map.put("role", user.getRole());

        return Resp.success(map);
    }

    @RequestMapping(value = "/account/login", method = RequestMethod.POST)
    public Resp accountLogin(HttpSession session, HttpServletRequest request,
            @RequestParam String userName,
            @RequestParam String password) {

        Account account = accountRepository.findByName(userName);
        if (account == null) {
            return Resp.fail("无此用户");
        }

        String md5Password = EncryptUtil.md5(password, account.getSalt());

        if (!md5Password.equalsIgnoreCase(account.getPassword())) {
            return Resp.fail("密码错误");
        }

        session.setAttribute(Constants.SESSION_ACCOUNT_KEY, account);
        session.setAttribute("loginType", account.getType());

        if (account.getType() == Account.TYPE_MAINTAIN_ADMIN || account.getType() == Account.TYPE_MAINTAIN_SUB) {
            saveMaintainLog(request, "登录", Constants.LOG_TYPE_LOGIN);
        } else {
            session.setAttribute(Constants.SESSION_ACCOUNT_KEY, account);
            saveClientLog(request, "登录", Constants.LOG_TYPE_LOGIN);
        }

        Map<String, Object> map = CommonUtils.populateMap(account);
        return Resp.success(map);
    }

    @RequestMapping(value = "/logout", method = { RequestMethod.GET, RequestMethod.POST })
    public Resp login(HttpSession session) {

        session.invalidate();

        return Resp.success();
    }

}
