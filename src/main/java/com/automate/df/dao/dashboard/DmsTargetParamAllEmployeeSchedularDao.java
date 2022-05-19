package com.automate.df.dao.dashboard;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.automate.df.entity.DmsTargetParamAllEmployeeSchedular;
@Repository
public interface DmsTargetParamAllEmployeeSchedularDao  extends CrudRepository<DmsTargetParamAllEmployeeSchedular, Integer> {
	  @Query(value = "SELECT * FROM dms_target_param_all_employees_schedular WHERE emp_id=?1", nativeQuery = true)
			List<DmsTargetParamAllEmployeeSchedular> findByEmpId(String empId);

}
