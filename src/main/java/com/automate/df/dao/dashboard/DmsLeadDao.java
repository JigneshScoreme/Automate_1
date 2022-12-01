package com.automate.df.dao.dashboard;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.automate.df.entity.dashboard.DmsLead;

public interface DmsLeadDao extends JpaRepository<DmsLead, Integer> {

	
	@Query(value = "SELECT * FROM dms_lead where sales_consultant = :empName and createddatetime>=:startDate\r\n"
			+ "and createddatetime<=:endDate and lead_stage=:leadType", nativeQuery = true)
	List<DmsLead> getLeads(@Param(value = "empName") String empName,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "leadType") String leadType);
	
	@Query(value = "SELECT * FROM dms_lead where sales_consultant in(:empNamesList) and createddatetime>=:startDate\r\n"
			+ "and createddatetime<=:endDate and lead_stage=:leadType", nativeQuery = true)
	List<DmsLead> getAllEmployeeLeads(@Param(value = "empNamesList") List<String> empNamesList,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "leadType") String leadType);
	
	@Query(value = "SELECT count(*) FROM dms_lead where sales_consultant = :empName and createddatetime>=:startDate\r\n"
			+ "and createddatetime<=:endDate and lead_stage=:leadType", nativeQuery = true)
	Integer getLeadsCount(@Param(value = "empName") String empName,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "leadType") String leadType);
	
	//-----------
	
	@Query(value = "SELECT count(*) FROM dms_lead A, dms_employee E , dms_role R where A.createddatetime>=:startDate "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName"
			+ " and A.createddatetime<=:endDate and A.lead_stage=:leadType and A.organization_id=:orgId", nativeQuery = true)
	Integer getAllLeadsCount(
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "leadType") String leadType,
			@Param(value = "orgId") int orgId,
			@Param(value = "loginEmpName") String loginEmpName,
			@Param(value = "roleName") String roleName);
	
	@Query(value = "SELECT count(*) FROM dms_lead A , dms_branch B, dms_employee E , dms_role R "
			+ " where A.branch_id = B.branch_id and B.dealer_code = :dealerCode AND A.createddatetime>=:startDate "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName "
			+ " and A.createddatetime<=:endDate and A.lead_stage=:leadType and A.organization_id=:orgId", nativeQuery = true)
	Integer getAllLeadsCount(
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "leadType") String leadType,
			@Param(value = "orgId") int orgId,
			@Param(value = "dealerCode") String dealerCode,
			@Param(value = "loginEmpName") String loginEmpName,
			@Param(value = "roleName") String roleName);
	
	
	@Query(value = "SELECT count(*) FROM dms_lead A, dms_employee E , dms_role R where A.sales_consultant = :empName and A.createddatetime>=:startDate  "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName "
			+ " and A.createddatetime<=:endDate and A.lead_stage not in ('DROPPED') and A.organization_id=:orgId", nativeQuery = true)
	Integer getAllocatedLeadsCountByEmp(@Param(value = "empName") String empName,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "orgId") int orgId,
			@Param(value = "loginEmpName") String loginEmpName,
			@Param(value = "roleName") String roleName );
	
	@Query(value = "SELECT count(*) FROM dms_lead  A , dms_branch B, dms_employee E , dms_role R  where "
			+ " A.branch_id = B.branch_id and B.dealer_code = :dealerCode"
			+ " and A.sales_consultant = :empName and A.createddatetime>=:startDate  "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName "
			+ " and A.createddatetime<=:endDate and A.lead_stage not in ('DROPPED') and A.organization_id=:orgId", nativeQuery = true)
	Integer getAllocatedLeadsCountByEmp(@Param(value = "empName") String empName,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "orgId") int orgId,
			@Param(value = "dealerCode") String dealerCode,
			@Param(value = "loginEmpName") String loginEmpName ,
			@Param(value = "roleName") String roleName
			);
	
	@Query(value = "SELECT count(*) FROM dms_lead A, dms_employee E , dms_role R where A.sales_consultant = :empName and A.createddatetime>=:startDate  "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName"
			+ "	 and A.createddatetime<=:endDate and A.lead_stage in ('DROPPED') and A.organization_id=:orgId", nativeQuery = true)
	Integer getDropeedLeadsCountByEmp(@Param(value = "empName") String empName,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "orgId") int orgId,
			@Param(value = "loginEmpName") String loginEmpName,
			@Param(value = "roleName") String roleName );
	
	@Query(value = "SELECT count(*) FROM dms_lead  A , dms_branch B, dms_employee E , dms_role R where "
			+ "	 A.branch_id = B.branch_id and B.dealer_code = :dealerCode AND  A.sales_consultant = :empName and A.createddatetime>=:startDate  "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName"
			+ "	 and A.createddatetime<=:endDate and A.lead_stage in ('DROPPED') and A.organization_id=:orgId", nativeQuery = true)
	Integer getDropeedLeadsCountByEmp(@Param(value = "empName") String empName,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "orgId") int orgId,
			@Param(value = "dealerCode") String dealerCode,
			@Param(value = "loginEmpName") String loginEmpName,
			@Param(value = "roleName") String roleName );
	
	@Query(value = "SELECT count(*) FROM dms_lead A, dms_employee E , dms_role R where A.createddatetime>=:startDate "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName"
			+ " and A.createddatetime<=:endDate and A.lead_stage not in ('DROPPED') and A.organization_id=:orgId", nativeQuery = true)
	Integer getAllocatedLeadsCount(@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate, @Param(value = "orgId") int orgId,
			@Param(value = "loginEmpName") String loginEmpName,
			@Param(value = "roleName") String roleName );
	
	@Query(value = "SELECT count(*) FROM dms_lead A , dms_branch B , dms_employee E , dms_role R  where A.branch_id = B.branch_id and B.dealer_code = :dealerCode AND A.createddatetime>=:startDate "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName"
			+ " and A.createddatetime<=:endDate and A.lead_stage not in ('DROPPED') and A.organization_id=:orgId", nativeQuery = true)
	Integer getAllocatedLeadsCount(@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate, @Param(value = "orgId") int orgId,
			@Param(value = "dealerCode") String dealerCode,
			@Param(value = "loginEmpName") String loginEmpName,
			@Param(value = "roleName") String roleName );
	
	@Query(value = "SELECT count(*) FROM dms_lead A, dms_employee E , dms_role R where A.createddatetime>=:startDate "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName"
			+ " and A.createddatetime<=:endDate and A.lead_stage in ('DROPPED') and A.organization_id=:orgId", nativeQuery = true)
	Integer getDroppedLeadsCount(@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate, @Param(value = "orgId") int orgId,
			@Param(value = "loginEmpName") String loginEmpName,
			@Param(value = "roleName") String roleName );
	
	@Query(value = "SELECT count(*) FROM dms_lead A , dms_branch B , dms_employee E , dms_role R where A.branch_id = B.branch_id and B.dealer_code = :dealerCode AND A.createddatetime>=:startDate  "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName"
			+ " and A.createddatetime<=:endDate and A.lead_stage in ('DROPPED') and A.organization_id=:orgId", nativeQuery = true)
	Integer getDroppedLeadsCount(@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate, @Param(value = "orgId") int orgId,
			@Param(value = "dealerCode") String dealerCode,
			@Param(value = "loginEmpName") String loginEmpName,
			@Param(value = "roleName") String roleName );
	
	// ----------------

	@Query(value = "SELECT id FROM dms_lead where sales_consultant in(:empNamesList) and lead_stage not in ('DROPPED') ", nativeQuery = true)
	List<Integer> getLeadIdsByEmpNamesWithOutDrop(@Param(value = "empNamesList") List<String> empNamesList);
	
	@Query(value = "SELECT id FROM dms_lead where sales_consultant in(:empNamesList) and lead_stage not in ('DROPPED') and model in (:model) and  createddatetime>=:startDate and createddatetime<=:endDate", nativeQuery = true)
	List<Integer> getLeadIdsByEmpNamesWithOutDrop1(@Param(value = "empNamesList") List<String> empNamesList, @Param(value = "model") List<String> model, @Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate);
	
	@Query(value = "SELECT id FROM dms_lead where sales_consultant in(:empNamesList) and lead_stage in ('DROPPED') ", nativeQuery = true)
	List<Integer> getLeadIdsByEmpNamesWithDrop(@Param(value = "empNamesList") List<String> empNamesList);
	
	@Query(value = "SELECT id FROM dms_lead where sales_consultant in(:empNamesList) and lead_stage in ('DROPPED') and model in (:model) AND createddatetime>=:startDate and createddatetime<=:endDate", nativeQuery = true)
	List<Integer> getLeadIdsByEmpNamesWithDrop1(@Param(value = "empNamesList") List<String> empNamesList, @Param(value = "model") List<String> model, @Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate);
	
	
	// Vehicle model query starts here 
	@Query(value = "select distinct model from dms_lead", nativeQuery = true)
	List<String> getModelNames();
	
	
	
	@Query(value = "SELECT * FROM dms_lead where sales_consultant in(:empNamesList) and createddatetime>=:startDate\r\n"
			+ "and createddatetime<=:endDate and model=:model and organization_id=:orgId ", nativeQuery = true)
	List<DmsLead> getAllEmployeeLeadsWithModel(
			@Param(value = "orgId") String orgId,
			
			@Param(value = "empNamesList") List<String> empNamesList,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "model") String model);
	
	
	@Query(value = "SELECT * FROM dms_lead where sales_consultant in(:empNamesList) and createddatetime>=:startDate\r\n"
			+ "and createddatetime<=:endDate and model=:model and organization_id=:orgId and lead_stage not in ('DROPPED')", nativeQuery = true)
	List<Integer> getAllEmployeeLeadsWithModel1(
			@Param(value = "orgId") String orgId,
			
			@Param(value = "empNamesList") List<String> empNamesList,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "model") String model);
	
	
	@Query(value = "SELECT * FROM dms_lead where sales_consultant in(:empNamesList) and createddatetime>=:startDate\r\n"
			+ "and createddatetime<=:endDate and model=:model and organization_id=:orgId and lead_stage in ('DROPPED')", nativeQuery = true)
	List<Integer> getAllEmployeeLeadsWithModel11(
			@Param(value = "orgId") String orgId,
			
			@Param(value = "empNamesList") List<String> empNamesList,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "model") String model);
	
	// Vehicle model query ends here
	
	
	// Lead Source and  EventSource query starts here
	
	@Query(value = "SELECT * FROM dms_lead where sales_consultant in(:empNamesList) and createddatetime>=:startDate\r\n"
			+ "and createddatetime<=:endDate and source_of_enquiry=:enqId and organization_id=:orgId", nativeQuery = true)
	List<DmsLead> getAllEmployeeLeadsBasedOnEnquiry(
			@Param(value = "orgId") String orgId,
		
			@Param(value = "empNamesList") List<String> empNamesList,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "enqId") Integer enqId);
	
	@Query(value = "SELECT * FROM dms_lead where sales_consultant in(:empNamesList) and createddatetime>=:startDate\r\n"
			+ "and createddatetime<=:endDate and source_of_enquiry=:enqId and model in(:vehicleModelList) and organization_id=:orgId and lead_stage not in ('DROPPED')", nativeQuery = true)
	List<Integer> getAllEmployeeLeadsBasedOnEnquiry1(
			@Param(value = "orgId") String orgId,
		
			@Param(value = "empNamesList") List<String> empNamesList,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "enqId") Integer enqId,
			@Param(value = "vehicleModelList") List<String> vehicleModelList);
	
	
	@Query(value = "SELECT * FROM dms_lead where sales_consultant in(:empNamesList) and createddatetime>=:startDate\r\n"
			+ "and createddatetime<=:endDate and source_of_enquiry=:enqId and model in(:vehicleModelList) and organization_id=:orgId and lead_stage in ('DROPPED')", nativeQuery = true)
	List<Integer> getAllEmployeeLeadsBasedOnEnquiry11(
			@Param(value = "orgId") String orgId,
		
			@Param(value = "empNamesList") List<String> empNamesList,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "enqId") Integer enqId,
			@Param(value = "vehicleModelList") List<String> vehicleModelList);
	
	
	// Lead Source and  EventSource query ends here
	
	
	//Lost Drop query starts
	
	@Query(value = "SELECT * FROM dms_lead where sales_consultant in(:empNamesList) and createddatetime>=:startDate\r\n"
			+ "and createddatetime<=:endDate and model in (:model) and lead_stage=:leadType and organization_id=:orgId", nativeQuery = true)
	List<DmsLead> getAllEmployeeLeadsWithModelandStage(
			@Param(value = "orgId") String orgId,
			
			@Param(value = "empNamesList") List<String> empNamesList,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "model") List<String> model,
			@Param(value = "leadType") String leadType);
	
	

	@Query(value="SELECT * FROM dms_lead WHERE first_name=:firstName and last_name=:lastName", nativeQuery = true)
	List<DmsLead> verifyFirstName(@Param(value = "firstName") String firstName,@Param(value = "lastName") String lastName);

	
	@Query(value="SELECT * FROM dms_lead WHERE crm_universal_id=:unversalId", nativeQuery = true)
	List<DmsLead> getLeadByUniversalId(@Param(value = "unversalId") String unversalId);
	
	@Query(value = "SELECT * FROM dms_lead where sales_consultant in(:empNamesList) and createddatetime>=:startDate\r\n"
			+ "and createddatetime<=:endDate and lead_stage in (:leadStages) and organization_id=:orgId", nativeQuery = true)
	List<DmsLead> getLeadsBasedonStage(@Param(value = "empNamesList") List<String> empNamesList,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "orgId") String orgId,
			@Param(value = "leadStages") List<String> leadStages);

	

	@Query(value = "SELECT * FROM dms_lead where sales_consultant in(:empNamesList) and createddatetime>=:startDate\r\n"
			+ "and createddatetime<=:endDate and lead_status=:leadType", nativeQuery = true)
	List<DmsLead> getAllEmployeeLeadsByLeadStatus(@Param(value = "empNamesList") List<String> empNamesList,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "leadType") String leadType);

	@Query(value = "SELECT * FROM dms_lead where sales_consultant in(:empNamesList) and createddatetime>=:startDate\r\n"
			+ "and createddatetime<=:endDate", nativeQuery = true)
	List<DmsLead> getAllEmployeeLeasForDate(@Param(value = "empNamesList") List<String> empNamesList,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate);

	@Query(value = "SELECT * FROM dms_lead where id in(:idList)", nativeQuery = true)
	List<DmsLead> getLeadsBasedonId(@Param(value = "idList") List<Integer> idList);

	@Query(value = "SELECT id FROM dms_lead where sales_consultant in(:empNamesList) and createddatetime>=:startDate\r\n"
			+ "and createddatetime<=:endDate and organization_id=:orgId", nativeQuery = true)
	List<Integer> getLeadsBasedonEmpNames(@Param(value = "empNamesList") List<String> empNamesList,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "orgId") String orgId);
	//Lost Ddrop query ends


	@Query(value="SELECT model FROM dms_lead WHERE crm_universal_id=:unversalId", nativeQuery = true)
	String getModelWithUniversalId(@Param(value = "unversalId") String unversalId);

	@Query(value="SELECT * FROM dms_lead WHERE crm_universal_id=:unversalId", nativeQuery = true)
	DmsLead getDMSLead(@Param(value = "unversalId") String unversalId);
	
	@Query(value="SELECT * FROM dms_lead WHERE organization_id=:orgId and id IN (:leadIdList) AND createddatetime>=:startDate and createddatetime<=:endDate and lead_stage in ('DROPPED')",nativeQuery = true)
	List<DmsLead> getLeadsByStageandDate(
			@Param(value="orgId") String orgId,
			@Param(value="leadIdList") List<Integer> leadIdList,
			@Param(value="startDate") String startDate,@Param(value="endDate") String endDate
			);
	// ---------------------------------------
	
	@Query(value = "SELECT count(*) FROM dms_lead A, dms_employee E , dms_role R where A.model = :model and A.createddatetime>=:startDate  "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName "
			+ " and A.createddatetime<=:endDate and A.lead_stage not in ('DROPPED') and A.organization_id=:orgId", nativeQuery = true)
	Integer getAllocatedLeadsCountByModel(@Param(value = "model") String model,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "orgId") int orgId,
			@Param(value = "loginEmpName") String loginEmpName,
			@Param(value = "roleName") String roleName );
	
	@Query(value = "SELECT * FROM dms_lead A where A.allocated = 'Yes' and A.createddatetime>=:startDate and A.created_by = :loggedEmpName"
			+ " and A.createddatetime<=:endDate and A.model=:model and A.organization_id=:orgId ", nativeQuery = true)
	List<DmsLead> getAllEmployeeLeadsByModel(
			@Param(value = "orgId") String orgId,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "model") String model,
			@Param(value = "loggedEmpName") String loggedEmpName );

	@Query(value = "SELECT * FROM dms_lead A, dms_branch B, dms_employee E , dms_role R where A.allocated = 'Yes' and A.createddatetime>=:startDate and A.created_by :loggedEmpName"
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " A.branch_id = B.branch_id and B.dealer_code = :dealerCode "
			+ " and A.createddatetime<=:endDate and A.model=:model and A,organization_id=:orgId ", nativeQuery = true)
	List<DmsLead> getAllEmployeeLeadsByModel(
			@Param(value = "orgId") String orgId,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "model") String model,
			@Param(value = "loggedEmpName") String loggedEmpName ,
			@Param(value = "dealerCode") String dealerCode,
			@Param(value = "roleName") String roleName );
	
	@Query(value = "SELECT A.* FROM dms_lead A where A.allocated = 'Yes' and A.createddatetime>=:startDate and A.created_by = :loggedEmpName"
			+ " and A.createddatetime<=:endDate and A.source_of_enquiry=:source and A.organization_id=:orgId ", nativeQuery = true)
	List<DmsLead> getAllEmployeeLeadsBySource(
			@Param(value = "orgId") String orgId,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "source") int source,
			@Param(value = "loggedEmpName") String loggedEmpName );

	@Query(value = "SELECT A.* FROM dms_lead A, dms_branch B, dms_employee E , dms_role R where A.allocated = 'Yes' and A.createddatetime>=:startDate and A.created_by :loggedEmpName"
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " A.branch_id = B.branch_id and B.dealer_code = :dealerCode "
			+ " and A.createddatetime<=:endDate and A.source_of_enquiry=:source and A,organization_id=:orgId ", nativeQuery = true)
	List<DmsLead> getAllEmployeeLeadsBySource(
			@Param(value = "orgId") String orgId,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "source") int source,
			@Param(value = "loggedEmpName") String loggedEmpName ,
			@Param(value = "dealerCode") String dealerCode,
			@Param(value = "roleName") String roleName );
	
	@Query(value = "SELECT S.name, A.model, A.createddatetime, A.first_name as firstName, A.last_name as lastName, A.lead_stage as leadStage, A.sales_consultant as salesConsultant, A.phone " 
			+" FROM dms_lead A, dms_employee E , dms_role R, dms_source_of_enquiries S where A.sales_consultant = :empName and A.createddatetime>=:startDate  "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName "
			+ " and A.source_of_enquiry = S.id "
			+ " and A.createddatetime<=:endDate and A.lead_stage not in ('DROPPED') and A.organization_id=:orgId", nativeQuery = true)
	List<Object[]> getAllocatedLeadsByEmp(@Param(value = "empName") String empName,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "orgId") int orgId,
			@Param(value = "loginEmpName") String loginEmpName,
			@Param(value = "roleName") String roleName  );
	
	@Query(value = "SELECT S.name, A.model, A.createddatetime, A.first_name , A.last_name , A.lead_stage , A.sales_consultant , A.phone " 
			+ " FROM dms_lead  A , dms_branch B, dms_employee E , dms_role R, dms_source_of_enquiries S  where "
			+ " A.branch_id = B.branch_id and B.dealer_code = :dealerCode"
			+ " and A.sales_consultant = :empName and A.createddatetime>=:startDate  "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName "
			+ " and A.source_of_enquiry = S.id "
			+ " and A.createddatetime<=:endDate and A.lead_stage not in ('DROPPED') and A.organization_id=:orgId", nativeQuery = true)
	List<Object[]> getAllocatedLeadsByEmp(@Param(value = "empName") String empName,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "orgId") int orgId,
			@Param(value = "dealerCode") String dealerCode,
			@Param(value = "loginEmpName") String loginEmpName,
			@Param(value = "roleName") String roleName  
			);
	
	
	@Query(value = "SELECT S.name, A.model, A.createddatetime, A.first_name as firstName, A.last_name as lastName, A.lead_stage as leadStage, A.sales_consultant as salesConsultant, A.phone "
			+ " , D.dropped_date "
			+" FROM dms_lead A, dms_employee E , dms_role R, dms_source_of_enquiries S, dms_lead_drop D where A.sales_consultant = :empName and A.createddatetime>=:startDate  "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and D.lead_id = A.id "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName "
			+ " and A.source_of_enquiry = S.id "
			+ " and A.createddatetime<=:endDate and A.lead_stage in ('DROPPED') and A.organization_id=:orgId", nativeQuery = true)
	List<Object[]> getDroppedLeadsByEmp(@Param(value = "empName") String empName,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "orgId") int orgId,
			@Param(value = "loginEmpName") String loginEmpName,
			@Param(value = "roleName") String roleName  );
	
	@Query(value = "SELECT S.name, A.model, A.createddatetime, A.first_name , A.last_name , A.lead_stage , A.sales_consultant , A.phone " 
			+" , D.dropped_date "
			+ " FROM dms_lead  A , dms_branch B, dms_employee E , dms_role R, dms_source_of_enquiries S , dms_lead_drop D where "
			+ " A.branch_id = B.branch_id and B.dealer_code = :dealerCode "
			+ " and D.lead_id = A.id "
			+ " and A.sales_consultant = :empName and A.createddatetime>=:startDate  "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName "
			+ " and A.source_of_enquiry = S.id "
			+ " and A.createddatetime<=:endDate and A.lead_stage in ('DROPPED') and A.organization_id=:orgId", nativeQuery = true)
	List<Object[]> getDroppedLeadsByEmp(@Param(value = "empName") String empName,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "orgId") int orgId,
			@Param(value = "dealerCode") String dealerCode,
			@Param(value = "loginEmpName") String loginEmpName ,
			@Param(value = "roleName") String roleName 
			);
	
	@Query(value = "SELECT S.name, A.model, A.createddatetime, A.first_name as firstName, A.last_name as lastName, A.lead_stage as leadStage, A.sales_consultant as salesConsultant, A.phone "
			+ " , D.dropped_date, A.enquiry_category "
			+" FROM dms_lead A, dms_employee E , dms_role R, dms_source_of_enquiries S, dms_lead_drop D where A.createddatetime>=:startDate  "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and D.lead_id = A.id "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName "
			+ " and A.source_of_enquiry = S.id "
			+ " and enquiry_category = coalesce(:category, enquiry_category)"
			+ " and A.createddatetime<=:endDate and A.lead_stage in (:stage) and A.organization_id=:orgId", nativeQuery = true)
	List<Object[]> getDroppedLeadsByStage(
			@Param(value = "stage") List<String> stage,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "orgId") int orgId,
			@Param(value = "loginEmpName") String loginEmpName,
			@Param(value = "roleName") String roleName,
			@Param(value = "category") String category
			); 
	
	@Query(value = "SELECT S.name, A.model, A.createddatetime, A.first_name as firstName, A.last_name as lastName, A.lead_stage as leadStage, A.sales_consultant as salesConsultant, A.phone "
			+ " , D.dropped_date, A.enquiry_category "
			+" FROM dms_lead A, dms_employee E , dms_role R, dms_source_of_enquiries S, dms_lead_drop D where A.createddatetime>=:startDate  "
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " and D.lead_id = A.id "
			+ " and A.allocated = 'Yes' and A.created_by = :loginEmpName "
			+ " and A.source_of_enquiry = S.id "
			+ " and enquiry_category = coalesce(:category, enquiry_category)"
			+ " and A.createddatetime<=:endDate and A.lead_stage in (:stage) and A.lead_status in (:status) and A.organization_id=:orgId", nativeQuery = true)
	List<Object[]> getDroppedLeadsByStageAndStatus(
			@Param(value = "stage") List<String> stage,
			@Param(value = "status") List<String> status,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "orgId") int orgId,
			@Param(value = "loginEmpName") String loginEmpName,
			@Param(value = "roleName") String roleName,
			@Param(value = "category") String category ); 
	
	@Query(value = "SELECT A.* FROM dms_lead A where A.allocated = 'Yes' and A.createddatetime>=:startDate and A.created_by = :loggedEmpName"
			+ " and A.createddatetime<=:endDate and A.source_of_enquiry=:subSource and A.organization_id=:orgId ", nativeQuery = true)
	List<DmsLead> getAllEmployeeLeadsBySubSource(
			@Param(value = "orgId") String orgId,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "subSource") String subSource,
			@Param(value = "loggedEmpName") String loggedEmpName );

	@Query(value = "SELECT A.* FROM dms_lead A, dms_branch B, dms_employee E , dms_role R where A.allocated = 'Yes' and A.createddatetime>=:startDate and A.created_by :loggedEmpName"
			+ " and R.role_name = :roleName  and R.org_id = A.organization_id and E.org = R.org_id and E.hrms_role = R.role_id and A.created_by = E.emp_name "
			+ " A.branch_id = B.branch_id and B.dealer_code = :dealerCode "
			+ " and A.createddatetime<=:endDate and A.source_of_enquiry=:subSource and A,organization_id=:orgId ", nativeQuery = true)
	List<DmsLead> getAllEmployeeLeadsBySubSource(
			@Param(value = "orgId") String orgId,
			@Param(value = "startDate") String startDate,
			@Param(value = "endDate") String endDate,
			@Param(value = "subSource") String subSource,
			@Param(value = "loggedEmpName") String loggedEmpName ,
			@Param(value = "dealerCode") String dealerCode,
			@Param(value = "roleName") String roleName );

}
