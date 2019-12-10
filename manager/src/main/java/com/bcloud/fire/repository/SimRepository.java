package com.bcloud.fire.repository;

import com.bcloud.fire.entity.Sim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SimRepository extends JpaRepository<Sim, Long>, JpaSpecificationExecutor<Sim> {

}
