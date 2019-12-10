package com.bcloud.fire.repository;

import com.bcloud.fire.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FloorRepository extends JpaRepository<Floor, Long>, JpaSpecificationExecutor<Floor> {

}
