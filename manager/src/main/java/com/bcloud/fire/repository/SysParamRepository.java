package com.bcloud.fire.repository;

import com.bcloud.fire.entity.SysParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SysParamRepository extends JpaRepository<SysParam, Long>, JpaSpecificationExecutor<SysParam> {

}
