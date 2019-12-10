package com.bcloud.fire.repository;

import com.bcloud.fire.entity.Park;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ParkRepository extends JpaRepository<Park, Long>, JpaSpecificationExecutor<Park> {

}
