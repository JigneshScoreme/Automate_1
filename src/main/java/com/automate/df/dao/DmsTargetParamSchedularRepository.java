package com.automate.df.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.automate.df.entity.DmsTargetParamSchedular;

public interface DmsTargetParamSchedularRepository extends JpaRepository<DmsTargetParamSchedular, Integer>{
	@Query(value = "select * from dms_target_param_schedular where org_id=?1 and emp_id=?2", nativeQuery = true)
	 List<DmsTargetParamSchedular> getbyOrgIdAndEmpId(String orgId,String empId);

}
