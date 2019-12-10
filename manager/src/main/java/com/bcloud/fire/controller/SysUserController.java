package com.bcloud.fire.controller;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bcloud.fire.Constants;
import com.bcloud.fire.annotation.Authority;
import com.bcloud.fire.entity.SysUser;
import com.bcloud.fire.repository.SysUserRepository;
import com.bcloud.fire.util.EncryptUtil;
import com.google.common.collect.Lists;

@RestController
@RequestMapping("/api")
public class SysUserController extends BaseController {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Authority(role = "ROLE_SYSUSER")
    @RequestMapping(value = "/sysUser/list", method = { RequestMethod.POST })
    public Resp list(final @RequestParam(defaultValue = "") String searchName,
            @RequestParam(defaultValue = "1") Integer pageIndex,
            @RequestParam(defaultValue = "15") Integer pageSize) {
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, Direction.DESC, "createTime");

        Specification<SysUser> spec = new Specification<SysUser>() {

            @Override
            public Predicate toPredicate(Root<SysUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = Lists.newArrayList();
                if (StringUtils.isNotBlank(searchName)) {
                    list.add(cb.like(root.get("fullName").as(String.class), "%" + searchName + "%"));
                }
                return cb.and(list.toArray(new Predicate[list.size()]));
            }

        };

        Page<SysUser> sysUserList = sysUserRepository.findAll(spec, pageRequest);
        return Resp.success(sysUserList);
    }

    @Authority(role = "ROLE_SYSUSER")
    @RequestMapping(value = "/sysUser/save", method = { RequestMethod.POST })
    public Resp save(HttpServletRequest request,
            HttpSession session,
            @RequestParam(required = false) Long id,
            @RequestParam String name,
            @RequestParam String fullName,
            @RequestParam String mobile,
            @RequestParam String role) {

        SysUser model = null;
        SysUser original = new SysUser();
        if (id != null) {
            if (id == 0) {
                return Resp.fail("不允许修改admin账号");
            }
            model = sysUserRepository.findOne(id);
            if (model == null) {
                return Resp.fail("未找到记录");
            }
            BeanUtils.copyProperties(model, original);
        } else {
            model = sysUserRepository.findByName(name);
            if (model != null) {
                return Resp.fail("用户名重复");
            }
            model = new SysUser();
            model.setName(name);
            model.setCreateTime(new Date());
            model.setLocked(0L);
            setPassword(model, Constants.DEFAULT_PASSWORD);
        }
        model.setFullName(fullName);
        model.setMobile(mobile);
        model.setRole(role);
        sysUserRepository.save(model);
        String operation = null;
        if (id != null) {
            operation = String.format("新建管理账号:[用户名：%s, 全名：%s, 手机号码：%s, 角色：%s]", model.getName(), model.getFullName(),
                    model.getMobile(), model.getRole());
        } else {
            operation = String.format("修改管理账号：%s, 原信息:[全名：%s, 手机号码：%s, 角色：%s], 新信息:[全名：%s, 手机号码：%s, 角色：%s]",
                    model.getName(), original.getFullName(), original.getMobile(), original.getRole(),
                    model.getFullName(), model.getMobile(), model.getRole());
        }
        saveSysUserLog(request, operation, Constants.LOG_TYPE_ADD);
        return Resp.success();
    }

    private void setPassword(SysUser user, String password) {
        String salt = String.valueOf(System.currentTimeMillis());
        String md5Password = EncryptUtil.md5(password, salt);
        user.setPassword(md5Password);
        user.setSalt(salt);
    }

    @Authority(role = "ROLE_SYSUSER")
    @RequestMapping(value = "/sysUser/delete", method = { RequestMethod.POST })
    public Resp delete(HttpServletRequest request, @RequestParam Long id) {

        if (id == null || id == 0) {
            return Resp.fail("找不到记录");
        }
        SysUser model = sysUserRepository.findOne(id);
        if (model == null) {
            return Resp.fail("找不到记录");
        }
        sysUserRepository.delete(model);

        String operation = String.format("删除管理账号：%s", model.getName());
        saveSysUserLog(request, operation, Constants.LOG_TYPE_DELETE);

        return Resp.success();
    }

    @Authority(role = "ROLE_SYSUSER")
    @RequestMapping(value = "/sysUser/changeStatus", method = { RequestMethod.POST })
    public Resp lock(HttpServletRequest request,
            @RequestParam Long id,
            @RequestParam Long locked) {
        if (id == null || id == 0) {
            return Resp.fail("找不到记录");
        }
        SysUser model = sysUserRepository.findOne(id);
        if (model == null) {
            return Resp.fail("找不到记录");
        }
        model.setLocked(locked);
        sysUserRepository.save(model);
        String operation;
        if (locked == 1) {
            operation = String.format("锁定管理账号：%s", model.getName());
        } else {
            operation = String.format("解锁管理账号：%s", model.getName());
        }
        saveSysUserLog(request, operation, Constants.LOG_TYPE_UPDATE);
        return Resp.success();
    }

    @Authority(role = "ROLE_SYSUSER")
    @RequestMapping(value = "/sysUser/resetPass", method = { RequestMethod.POST })
    public Resp resetPass(HttpServletRequest request, @RequestParam Long id) {

        if (id == null || id == 0) {
            return Resp.fail("找不到记录");
        }
        SysUser model = sysUserRepository.findOne(id);
        if (model == null) {
            return Resp.fail("找不到记录");
        }
        setPassword(model, Constants.DEFAULT_PASSWORD);

        sysUserRepository.save(model);
        String operation = String.format("重置管理账号密码：%s", model.getName());
        saveSysUserLog(request, operation, Constants.LOG_TYPE_UPDATE);
        return Resp.success();
    }

    @RequestMapping(value = "/sysUser/changePass", method = { RequestMethod.POST })
    public Resp changePass(HttpServletRequest request, HttpSession session, @RequestParam String newPass) {

        // 只能修改自己的密码
        SysUser user = (SysUser) session.getAttribute("user");
        Long id = user.getId();
        SysUser model = sysUserRepository.findOne(id);
        if (model == null) {
            return Resp.fail("找不到记录");
        }
        setPassword(model, newPass);

        sysUserRepository.save(model);

        saveSysUserLog(request, "修改密码", Constants.LOG_TYPE_UPDATE);
        return Resp.success();
    }
}
