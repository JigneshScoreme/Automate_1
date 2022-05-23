package com.automate.df.entity.salesgap;



import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

import com.automate.df.dao.salesgap.EmployeeAddress;
import com.automate.df.model.DmsAddress;

import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="dms_employee")
@Entity
@Data
@NoArgsConstructor
public class DmsEmployee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="emp_id")
	private int emp_id;
	
	@Column(name="hrms_emp_id")
	private String hrmsEmpId;
	
	@Column(name="hrms_emp_code")
	private String hrmsEmpCode;
	
	@Column(name="emp_name")
	private String empName;
	
	@Column(name="email")
	private String email;
	
	@Column(name="mobile")
	private String mobile;
	
    @Column(name = "new_password")
    private String password;
	
	@Column(name="basic_salary")
	private String basicSal;
	
	@Column(name="reporting_to")
	private String reportingTo;
	
	@Column(name="approver_id")
	private String approver;
	
	@Column(name="primary_department")
	private String deptId;
	
	@Column(name="primary_designation")
	private String designationId;
	
	
	@Column(name="employee_status_id")
	private String employeeStatusId;
	
	@Column(name="status_id")
	private String statusId;	
	
	@Column(name="sponsor_id")
	private String sponserId;
	
	@Column(name="workshift_id")
	private String workshift_id;
	
	
	@Column(name="hrms_role")
	private String hrmsRole;
	
	//grade_id
	//hrms_role
	//hmrs_holiday_schedule_id
	@Column(name="joining_date")
	private String joiningDate;
	
	@Column(name="created_time")
	private String createdTime;
	
	@Column(name="updated_time")
	private String updatedTime;

	@Column(name="prev_experience")
	private String prevExperience;
	
	//workshift_id
	//stores_list
	//image_url
	//social_id
	@Column(name="location")
	private String location;
	
	@Column(name="location_id")
	private String locationId;
	
	@Column(name="is_allow_overtime")
	private String isAllowOvertime;
	
	@Column(name="is_approval_authorities")
	private String isApprovalAuthorities;
	
	@Column(name="is_reporting_authorities")
	private String isReportingAuthorities;
	
	
	@Column(name="emp_personal_ifo")
	private String empPersonalInfo;
	
	@Column(name="emp_travel")
	private String empTravel;
	
	//profession
//	status_id
	//is_allow_overtime
	//is_approval_authorities
	//is_reporting_authorities
	//org
	//branch
	//created_time
	//updated_time
	//created_by
	//updated_by
	
	@Column(name="branch")
	private String branch;
	
	@Column(name="org")
	private String org;
	
	@Column(name="religion_id")
	private String religionId;
	
	@Column(name="gender_id")
	private String genderId;
	
	@Column(name="marital_status_id")
	private String maritalStatusId;
	
	@Column(name="engaged_as_id")
	private String engagedAsId;
	
	@Column(name="nationality_id")
	private String nationalityId;
	
	
	@Column(name="emp_payroll")
	private String empPayrollId;
	
	@Column(name="cognito_name")
	private String cogintoName;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="updated_by")
	private String updatedBy;
	
	
	@Column(name = "address", columnDefinition = "json")
	@Type(type = "json")
	private EmployeeAddress address;
	

	
	/*emp_personal_ifo
	emp_travel
	approver_id
	cognito_name
	dashboard_url
	status
	s3_name
	address*/
}
