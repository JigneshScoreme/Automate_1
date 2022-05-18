package com.automate.df.dao.dashboard;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.automate.df.entity.DmsTargetParamSchedular;
@Repository
public interface DashBoardV3Dao extends CrudRepository<DmsTargetParamSchedular, Integer> {
	  @Query(value = "SELECT * FROM dms_target_param_schedular WHERE emp_id=?1", nativeQuery = true)
			List<DmsTargetParamSchedular> findByEmpId(String empId);
}
