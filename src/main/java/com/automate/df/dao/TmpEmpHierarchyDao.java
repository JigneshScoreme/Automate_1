package com.automate.df.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.automate.df.entity.TmpEmpHierarchy;

public interface TmpEmpHierarchyDao extends JpaRepository<TmpEmpHierarchy, Integer> {

	
	
	@Query(value="select * from emp_hierarchy_tmp where org_id=:orgId and emp_id=:empId",nativeQuery = true)
	Optional<TmpEmpHierarchy> getDataByEmp(@Param(value="orgId") int orgId,@Param(value="empId") int empId);


}
