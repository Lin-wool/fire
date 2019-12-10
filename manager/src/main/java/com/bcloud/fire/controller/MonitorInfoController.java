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
import com.bcloud.fire.entity.MonitorInfo;
import com.bcloud.fire.repository.MonitorInfoRepository;
import com.google.common.collect.Lists;

@RestController
@RequestMapping("/api")
public class MonitorInfoController extends BaseController {

    @Autowired
    private MonitorInfoRepository monitorInfoRepository;

    @Authority(role = "ROLE_MONITORINFO")
    @RequestMapping(value = "/monitorInfo/list", method = { RequestMethod.POST })
    public Resp list(final @RequestParam(defaultValue = "") String searchName,
            @RequestParam(defaultValue = "1") Integer pageIndex,
            @RequestParam(defaultValue = "15") Integer pageSize) {
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, Direction.DESC, "createTime");

        Specification<MonitorInfo> spec = new Specification<MonitorInfo>() {

            @Override
            public Predicate toPredicate(Root<MonitorInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = Lists.newArrayList();
                if (StringUtils.isNotBlank(searchName)) {
                    list.add(cb.like(root.get("fullName").as(String.class), "%" + searchName + "%"));
                }
                return cb.and(list.toArray(new Predicate[list.size()]));
            }

        };

        Page<MonitorInfo> monitorInfoList = monitorInfoRepository.findAll(spec, pageRequest);
        return Resp.success(monitorInfoList);
    }

    @Authority(role = "ROLE_MONITORINFO")
    @RequestMapping(value = "/monitorInfo/save", method = { RequestMethod.POST })
    public Resp save(HttpServletRequest request,
            HttpSession session,
            @RequestParam(required = false) Long id,
            @RequestParam String content) {

        MonitorInfo model = null;
        MonitorInfo original = new MonitorInfo();
        if (id != null) {
            model = monitorInfoRepository.findOne(id);
            if (model == null) {
                return Resp.fail("未找到记录");
            }
            BeanUtils.copyProperties(model, original);
        } else {
            model = new MonitorInfo();
        }
        model.setCreateTime(new Date());
        monitorInfoRepository.save(model);
        String operation = null;
        if (id != null) {
            // operation = String.format("修改XXX, id: %s，原XX[内容: %s，账号: %s]，新XX[内容: %s，账号:
            // %s]",
            // original.getId(), original.getContent(), original.getClientId(),
            // model.getContent(), model.getClientId());
        } else {
            // operation = String.format("新增XX, [id: %s, 内容: %s，账号: %s]", model.getId(),
            // model.getContent(), model.getClientId());
        }
        saveSysUserLog(request, operation, Constants.LOG_TYPE_ADD);

        return Resp.success();

    }

    @Authority(role = "ROLE_MONITORINFO")
    @RequestMapping(value = "/monitorInfo/delete", method = { RequestMethod.POST })
    public Resp delete(HttpServletRequest request, @RequestParam String ids) {

        if (StringUtils.isNotBlank(ids)) {
            String[] idArray = ids.split("@");
            for (String id : idArray) {
                monitorInfoRepository.delete(Long.valueOf(id));
            }

            String operation = String.format("删除XX%s条：[%s]", idArray.length, StringUtils.join(idArray, ","));
            saveSysUserLog(request, operation, Constants.LOG_TYPE_DELETE);
        }

        return Resp.success();

    }

}
