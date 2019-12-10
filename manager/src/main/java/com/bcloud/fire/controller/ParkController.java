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
import com.bcloud.fire.entity.Park;
import com.bcloud.fire.repository.ParkRepository;
import com.google.common.collect.Lists;

@RestController
@RequestMapping("/api")
public class ParkController extends BaseController {

    @Autowired
    private ParkRepository parkRepository;

    @Authority(role = "ROLE_BUILDING")
    @RequestMapping(value = "/park/list", method = {RequestMethod.POST})
    public Resp list(final @RequestParam(defaultValue = "") String searchName,
                     final @RequestParam(defaultValue = "") String searchMaintain,
                     final @RequestParam(defaultValue = "") String searchAccount,
                     final @RequestParam(defaultValue = "") String searchPosition,
                     final @RequestParam(defaultValue = "") String maintain,
                     @RequestParam(defaultValue = "1") Integer pageIndex,
                     @RequestParam(defaultValue = "15") Integer pageSize) {
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, Direction.DESC, "createTime");

        Specification<Park> spec = new Specification<Park>() {
            @Override
            public Predicate toPredicate(Root<Park> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = Lists.newArrayList();
                // 根据maintain判断用户类型 0 为超级管理员 其他为维保管理人员名称
                if (!"0".equals(maintain)) {
                    // 如果不是超级管理员则添加查询条件maintain
                    if (StringUtils.isNotBlank(maintain)) {
                        list.add(cb.equal(root.get("maintain").as(String.class), searchMaintain));
                    }
                }
                if (StringUtils.isNotBlank(searchName)) {
                    list.add(cb.like(root.get("name").as(String.class), "%" + searchName + "%"));
                }
                if (StringUtils.isNotBlank(searchMaintain)) {
                    list.add(cb.like(root.get("maintain").as(String.class), "%" + searchMaintain + "%"));
                }
                if (StringUtils.isNotBlank(searchAccount)) {
                    list.add(cb.like(root.get("account").as(String.class), "%" + searchAccount + "%"));
                }
                if (StringUtils.isNotBlank(searchPosition)) {
                    list.add(cb.like(root.get("position").as(String.class), "%" + searchPosition + "%"));
                }

                return cb.and(list.toArray(new Predicate[list.size()]));
            }

        };

        Page<Park> parkList = parkRepository.findAll(spec, pageRequest);
        return Resp.success(parkList);
    }

    @Authority(role = "ROLE_BUILDING")
    @RequestMapping(value = "/park/save", method = {RequestMethod.POST})
    public Resp save(HttpServletRequest request,
                     HttpSession session,
                     @RequestParam(required = false) Long id,
                     @RequestParam String content) {

        Park model = null;
        Park original = new Park();
        if (id != null) {
            model = parkRepository.findOne(id);
            if (model == null) {
                return Resp.fail("未找到记录");
            }
            BeanUtils.copyProperties(model, original);
        } else {
            model = new Park();
        }
        model.setCreateTime(new Date());
        parkRepository.save(model);
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

    @Authority(role = "ROLE_BUILDING")
    @RequestMapping(value = "/park/delete", method = {RequestMethod.POST})
    public Resp delete(HttpServletRequest request, @RequestParam String ids) {

        if (StringUtils.isNotBlank(ids)) {
            String[] idArray = ids.split("@");
            for (String id : idArray) {
                parkRepository.delete(Long.valueOf(id));
            }

            String operation = String.format("删除XX%s条：[%s]", idArray.length, StringUtils.join(idArray, ","));
            saveSysUserLog(request, operation, Constants.LOG_TYPE_DELETE);
        }

        return Resp.success();

    }

}
