package com.automate.df.dao.salesgap;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.automate.df.entity.salesgap.DmsEmployee;
@Repository
public interface DmsRoleRepo extends JpaRepository<DmsEmployee, Integer> {
	
	@Query(value = "SELECT rolemap.organization_id, rolemap.branch_id, rolemap.emp_id, role.role_name, role.role_id, role.precedence FROM dms_role role INNER JOIN dms_employee_role_mapping rolemap ON rolemap.role_id=role.role_id AND rolemap.emp_id in (:empId) ORDER BY role.precedence", nativeQuery = true)
	List<Integer> roleMapQueryimmediate(@Param(value = "empId") List<Integer> empId);

}
