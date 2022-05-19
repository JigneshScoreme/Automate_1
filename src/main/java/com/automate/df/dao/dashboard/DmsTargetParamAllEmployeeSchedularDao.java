package com.automate.df.dao.dashboard;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.automate.df.entity.DmsTargetParamAllEmployeeSchedular;
import com.google.common.base.Optional;
@Repository
public interface DmsTargetParamAllEmployeeSchedularDao  extends CrudRepository<DmsTargetParamAllEmployeeSchedular, Integer> {
	  @Query(value = "SELECT * FROM dms_target_param_all_employees_schedular WHERE emp_id=?1", nativeQuery = true)
			Optional<DmsTargetParamAllEmployeeSchedular> findByEmpId(String empId);

}
