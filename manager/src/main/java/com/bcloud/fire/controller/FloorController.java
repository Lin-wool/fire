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
import com.bcloud.fire.entity.Floor;
import com.bcloud.fire.repository.FloorRepository;
import com.google.common.collect.Lists;

@RestController
@RequestMapping("/api")
public class FloorController extends BaseController {

    @Autowired
    private FloorRepository floorRepository;

    @Authority(role = "ROLE_BUILDING")
    @RequestMapping(value = "/floor/list", method = { RequestMethod.POST })
    public Resp list(final @RequestParam(defaultValue = "") String searchName,
            @RequestParam(defaultValue = "1") Integer pageIndex,
            @RequestParam(defaultValue = "15") Integer pageSize) {
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, Direction.DESC, "createTime");

        Specification<Floor> spec = new Specification<Floor>() {

            @Override
            public Predicate toPredicate(Root<Floor> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = Lists.newArrayList();
                if (StringUtils.isNotBlank(searchName)) {
                    list.add(cb.like(root.get("fullName").as(String.class), "%" + searchName + "%"));
                }
                return cb.and(list.toArray(new Predicate[list.size()]));
            }

        };

        Page<Floor> floorList = floorRepository.findAll(spec, pageRequest);
        return Resp.success(floorList);
    }

    @Authority(role = "ROLE_BUILDING")
    @RequestMapping(value = "/floor/save", method = { RequestMethod.POST })
    public Resp save(HttpServletRequest request,
            HttpSession session,
            @RequestParam(required = false) Long id,
            @RequestParam String content) {

        Floor model = null;
        Floor original = new Floor();
        if (id != null) {
            model = floorRepository.findOne(id);
            if (model == null) {
                return Resp.fail("未找到记录");
            }
            BeanUtils.copyProperties(model, original);
        } else {
            model = new Floor();
        }
        model.setCreateTime(new Date());
        floorRepository.save(model);
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
    @RequestMapping(value = "/floor/delete", method = { RequestMethod.POST })
    public Resp delete(HttpServletRequest request, @RequestParam String ids) {

        if (StringUtils.isNotBlank(ids)) {
            String[] idArray = ids.split("@");
            for (String id : idArray) {
                floorRepository.delete(Long.valueOf(id));
            }

            String operation = String.format("删除XX%s条：[%s]", idArray.length, StringUtils.join(idArray, ","));
            saveSysUserLog(request, operation, Constants.LOG_TYPE_DELETE);
        }

        return Resp.success();

    }

}
