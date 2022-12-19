package com.automate.df.dao.salesgap;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.automate.df.entity.salesgap.TargetEntity;
import com.automate.df.entity.salesgap.TargetEntityUser;



public interface TargetUserRepo extends JpaRepository<TargetEntityUser, Integer> {
	
	@Query(value="SELECT * FROM dms_target_setting_user where emp_id=:empId",nativeQuery = true)
	public Optional<TargetEntityUser> findByEmpId(@Param(value="empId") String empId);
	
	@Query(value="SELECT * FROM dms_target_setting_user where emp_id=:empId and is_active='Y' ",nativeQuery = true)
	public List<TargetEntityUser> findAllEmpIds(@Param(value="empId") String empId);
	
	@Query(value="SELECT * FROM dms_target_setting_user where emp_id=:empId and is_active='Y' and type!='default'",nativeQuery = true)
	public List<TargetEntityUser> findAllEmpIdsWithNoDefault(@Param(value="empId") String empId);
	
	
	@Query(value="SELECT * FROM dms_target_setting_user where emp_id=:empId and is_active='Y' and type='default'",nativeQuery = true)
	public List<TargetEntityUser> findAllEmpIdsWithDefault(@Param(value="empId") String empId);
	
	@Query(value = "SELECT * FROM dms_target_setting_user where org_id=:orgId  and department=:department and designation=:designation and branch=:branch and is_active='Y'", nativeQuery = true)
	List<TargetEntityUser> getUserTargetData(@Param(value = "orgId") String orgId,
			@Param(value="department") String deptId,
			@Param(value="designation") String designation,
			@Param(value="branch") String branch
			);
	
	
	@Query(value = "SELECT * FROM dms_target_setting_user where org_id=:orgId and emp_id=:eid and department=:department and designation=:designation and branch=:branch and is_active='Y'", nativeQuery = true)
	List<TargetEntityUser> getUserTargetDataV2(@Param(value = "orgId") String orgId,
			@Param(value="department") String deptId,
			@Param(value="designation") String designation,
			@Param(value="branch") String branch,
			@Param(value="eid") String eid
			);
	
	
	@Query(value="SELECT * FROM dms_target_setting_user where emp_id=:empId and start_date=:startDate and end_date=:endDate and target_type=:targetType and is_active='Y' and target_name=:targetName",nativeQuery = true)
	public List<TargetEntityUser> findByEmpIdWithDate(@Param(value="empId") String empId,
			@Param(value="startDate") String startDate,
			@Param(value="endDate") String endDate,
			@Param(value="targetType") String targetType,
			@Param(value="targetName") String targetName);

	@Query(value="SELECT * FROM dms_target_setting_user where emp_id=:empId and type='default'",nativeQuery = true)
	public Optional<TargetEntityUser> checkDefaultDataInTargetUser(@Param(value="empId") String empId);

	@Query(value="SELECT * FROM dms_target_setting_user where emp_id=:empId and id=:recordId",nativeQuery = true)
	public Optional<TargetEntityUser> findByEmpIdWithRecordId(@Param(value="recordId")  String recordId, @Param(value="empId")  String empId);

	@Query(value="SELECT * FROM dms_target_setting_user where emp_id = :empId and start_date>=:startDate and end_date<=:endDate",nativeQuery = true)
	public List<TargetEntityUser> findAllQ1(@Param(value="empId") String empId, @Param(value="startDate")  String startDate,  @Param(value="endDate") String endDate);

	@Query(value="SELECT * FROM dms_target_setting_user where emp_id = :empId and start_date>=:startDate",nativeQuery = true)
	public List<TargetEntityUser> findAllQ2(@Param(value="empId") String empId, @Param(value="startDate")  String startDate);

	
	@Query(value="SELECT * FROM dms_target_setting_user where emp_id = :empId ",nativeQuery = true)
	public List<TargetEntityUser> findAllQ3(@Param(value="empId") String empId);
	
	

	@Query(value="SELECT * FROM dms_target_setting_user where target_admin_id = :adminID and is_active='Y'",nativeQuery = true)
	public List<TargetEntityUser> findAllByTargetAdminId(@Param(value="adminID") Integer adminID);
	

	@Modifying
	@Transactional
	@Query(value = "UPDATE dms_target_setting_user SET targets = :targets, updated_by_user_id =:updated_by_user_id WHERE emp_id = :employeeId and org_id=:org_id and branch =:branch and department = :department and designation=:designation and start_date>= :start_date and end_date <= :end_date",nativeQuery = true)
	public int updateTargetSetings1(@Param(value = "targets") String targets,
			@Param(value = "employeeId") String employeeId, @Param(value = "org_id") String org_id,
			@Param(value = "branch") String branch, @Param(value = "department") String department,
			@Param(value = "designation") String designation
			,@Param(value = "start_date") String start_date,@Param(value = "end_date") String end_date,
									@Param(value = "updated_by_user_id") Integer updated_by_user_id
			);


	@Modifying
	@Transactional
	@Query(value = "UPDATE dms_target_setting_user SET targets = :targets, updated_by_user_id =:updated_by_user_id WHERE emp_id = :employeeId and org_id=:org_id and branch =:branch and department = :department and designation=:designation and start_date>= :start_date and end_date <= :end_date",nativeQuery = true)
	public int updateTargetSetings(@Param(value = "targets") String targets,
								   @Param(value = "employeeId") String employeeId, @Param(value = "org_id") String org_id,
								   @Param(value = "branch") String branch, @Param(value = "department") String department,
								   @Param(value = "designation") String designation, @Param(value = "start_date") String start_date,@Param(value = "end_date") String end_date,
								   @Param(value = "updated_by_user_id") Integer updated_by_user_id
	);

	@Query(value = "SELECT * FROM dms_target_setting_user WHERE emp_id = :employeeId and org_id=:org_id and branch =:branch and department = :department and designation=:designation and start_date>= :start_date and end_date <= :end_date",nativeQuery = true)
	public List<TargetEntityUser> getTargetSettings(@Param(value = "employeeId") String employeeId, @Param(value = "org_id") String org_id,
									@Param(value = "branch") String branch, @Param(value = "department") String department,
									@Param(value = "designation") String designation,@Param(value = "start_date") String start_date,@Param(value = "end_date") String end_date
	);

}
