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
import com.bcloud.fire.entity.Sim;
import com.bcloud.fire.repository.SimRepository;
import com.google.common.collect.Lists;

@RestController
@RequestMapping("/api")
public class SimController extends BaseController {

    @Autowired
    private SimRepository simRepository;

    @Authority(role = "ROLE_SIM")
    @RequestMapping(value = "/sim/list", method = {RequestMethod.POST})
    public Resp list(final @RequestParam(defaultValue = "") Long searchGateway,
                     final @RequestParam(defaultValue = "") Long searchType,
                     final @RequestParam(defaultValue = "") String searchMsisdn,
                     @RequestParam(defaultValue = "1") Integer pageIndex,
                     @RequestParam(defaultValue = "15") Integer pageSize) {
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, Direction.DESC, "createTime");

        Specification<Sim> spec = new Specification<Sim>() {

            @Override
            public Predicate toPredicate(Root<Sim> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = Lists.newArrayList();

                if (searchGateway != null) {
                    list.add(cb.equal(root.get("gateway").as(String.class), searchGateway));
                }
                if (searchType != null) {
                    list.add(cb.equal(root.get("type").as(String.class), searchType));
                }
                if (searchMsisdn != null) {
                    list.add(cb.like(root.get("msisdn").as(String.class), "%"+searchMsisdn+"%"));
                }
                return cb.and(list.toArray(new Predicate[list.size()]));
            }

        };
        Page<Sim> simList = simRepository.findAll(spec, pageRequest);
        return Resp.success(simList);
    }

    @Authority(role = "ROLE_SIM")
    @RequestMapping(value = "/sim/save", method = {RequestMethod.POST})
    public Resp save(HttpServletRequest request,
                     HttpSession session,
                     @RequestParam(required = false) Long id,
                     @RequestParam String msisdn,
                     @RequestParam Long gateway,
                     @RequestParam Long type) {

        Sim model = null;
        Sim original = new Sim();
        if (id != null) {
            model = simRepository.findOne(id);
            if (model == null) {
                return Resp.fail("未找到记录");
            }
            BeanUtils.copyProperties(model, original);
        } else {
            model = new Sim();
            model.setCreateTime(new Date());
            model.setGateway(gateway);
            model.setMsisdn(msisdn);
            model.setType(type);
            // 调用OneLink单卡基本信息查询接口，查询新增卡的信息，并更新卡信息数据


        }
        simRepository.save(model);
        String operation = null;
        if (id != null) {
            // operation = String.format("修改XXX, id: %s，原XX[内容: %s，账号: %s]，新XX[内容: %s，账号:
            // %s]",
            // original.getId(), original.getContent(), original.getClientId(),
            // model.getContent(), model.getClientId());
        } else {
            // operation = String.format("新增XX, [id: %s, 内容: %s，账号: %s]", model.getId(),
            // model.getContent(), model.getClientId());
            operation = String.format("新增SIM卡,[id: %s, msisdn: %s, gatewang: %s, type: %s]", model.getId(), model.getMsisdn(), model.getGateway(), model.getType());
        }
        saveSysUserLog(request, operation, Constants.LOG_TYPE_ADD);
        return Resp.success();

    }

    @Authority(role = "ROLE_SIM")
    @RequestMapping(value = "/sim/delete", method = {RequestMethod.POST})
    public Resp delete(HttpServletRequest request, @RequestParam String ids) {

        if (StringUtils.isNotBlank(ids)) {
            String[] idArray = ids.split("@");
            for (String id : idArray) {
                simRepository.delete(Long.valueOf(id));
            }

            String operation = String.format("删除XX%s条：[%s]", idArray.length, StringUtils.join(idArray, ","));
            saveSysUserLog(request, operation, Constants.LOG_TYPE_DELETE);
        }

        return Resp.success();

    }

}
