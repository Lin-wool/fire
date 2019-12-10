package com.bcloud.fire.repository;

import com.bcloud.fire.entity.MonitorInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MonitorInfoRepository extends JpaRepository<MonitorInfo, Long>, JpaSpecificationExecutor<MonitorInfo> {

}
