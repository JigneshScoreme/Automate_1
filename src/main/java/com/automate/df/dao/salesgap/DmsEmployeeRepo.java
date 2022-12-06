package com.automate.df.dao.salesgap;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.automate.df.entity.salesgap.DmsEmployee;
import com.automate.df.entity.salesgap.TargetEntity;

public interface DmsEmployeeRepo extends JpaRepository<DmsEmployee, Integer> {
	
	@Query(value = "SELECT emp_name FROM dms_employee where emp_id in (:eidList)", nativeQuery = true)
	List<String> findEmpNamesById(@Param(value = "eidList") List<Integer> eidList);
	
	@Query(value = "SELECT * FROM dms_employee where emp_id in (:empId)", nativeQuery = true)
	List<DmsEmployee> findByImmediateId(@Param(value = "empId") List<Integer> empId);

	/*@Query(value = "SELECT emp_name FROM dms_employee E, dms_role R where E.status = 'Active' and E.org = :org"
			+ " AND R.role_name = 'Sales Consultant' AND E.org = R.org_id and E.hrms_role = R.role_id ", nativeQuery = true) */
	
	@Query(value = "SELECT emp_name FROM dms_employee where status = 'Active' and org = :org", nativeQuery = true)
	List<String> findEmpNames(int org);

	
/*	@Query(value = "SELECT * FROM dms_employee where org=:orgId and branch=:branchId", nativeQuery = true)
	List<DmsEmployee> getEmployeesByOrgBranch(@Param(value = "orgId") Integer orgId,
			@Param(value = "branchId") Integer branchId);
*/	
	@Query(value = "SELECT * FROM dms_employee where org=:orgId and branch=:branchId  and hrms_role =:roleId and status = 'Active'", nativeQuery = true)
	List<DmsEmployee> getEmployeesByOrgBranch(@Param(value = "orgId") Integer orgId,
			@Param(value = "branchId") Integer branchId,@Param(value = "roleId") Integer roleId);
	
	
	@Query(value = "SELECT * FROM dms_employee where org=:orgId", nativeQuery = true)
	List<DmsEmployee> getEmployeesByOrg(@Param(value = "orgId") Integer orgId);
	
	
	@Query(value = "SELECT * FROM dms_employee where emp_id = :id", nativeQuery = true)
	Optional<DmsEmployee> findEmpById(@Param(value = "id") Integer id);
	
	@Query(value = "SELECT * FROM dms_employee where status='Active' and org=:orgId "
			+ "and emp_id = :empId and primary_department in (SELECT dms_department_id FROM dms_department where hrms_department_id='Sales')", nativeQuery = true)
	Optional<DmsEmployee> findEmpByDeptwithActive(@Param(value = "orgId") String orgId,@Param(value = "empId") Integer empId);
	

	@Query(value = "SELECT emp_id FROM dms_employee where emp_id in (:empNamelist)", nativeQuery = true)
	List<Integer> findEmpIdsByNames(@Param(value = "empNamelist") List<String> empNamelist);


	@Query(value = "SELECT emp_id FROM dms_employee where emp_name =:empName", nativeQuery = true)
	String findEmpIdByName(@Param(value = "empName") String empName);

	@Query(value = "SELECT * FROM dms_employee where reporting_to =:to", nativeQuery = true)
	Optional<DmsEmployee> findByReportingId(@Param(value = "to") String reportingTo);
	
	@Query(value = "SELECT * FROM dms_employee where org =:orgId and hrms_role =:roleId and status ='Active'", nativeQuery = true)
	List<DmsEmployee> findAllByOrgId(@Param(value = "orgId") Integer orgId,@Param(value = "roleId") Integer roleId);

	@Query(value = "SELECT emp_name FROM dms_employee where emp_id=:id ", nativeQuery = true)
	String getEmpName(@Param(value = "id") String id);
	
	@Query(value = "SELECT hrms_role FROM dms_employee where emp_id=:id ", nativeQuery = true)
	Integer getEmpHrmsRole(@Param(value = "id") Integer id);
	
	@Query(value = "SELECT * FROM dms_employee where emp_id in (:empId)", nativeQuery = true)
	List<Integer> dmsEmpimmediateByidQuery(@Param(value = "empId") List<Integer> empId);

}
