package com.automate.df.dao.dashboard;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.automate.df.entity.DmsTargetParamEmployeeSchedular;
import com.google.common.base.Optional;
@Repository
public interface DmsTargetParamEmployeeSchedularDao extends CrudRepository<DmsTargetParamEmployeeSchedular, Integer> {
	  @Query(value = "SELECT * FROM dms_target_param_employee_schedular WHERE emp_id=?1", nativeQuery = true)
			Optional<DmsTargetParamEmployeeSchedular> findByEmpId(String empId);
}
