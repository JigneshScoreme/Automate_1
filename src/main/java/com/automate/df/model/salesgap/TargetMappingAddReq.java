package com.automate.df.model.salesgap;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@NoArgsConstructor
public class TargetMappingAddReq {

	String recordId;
	String loggedInEmpId;
	String branch=null;
	String location;
	String branchmangerId;
	String startDate;
	String endDate;
	String employeeId;
	String retailTarget;
	String managerId;
	String teamLeadId;
	String generalManagerId;
	
	String targetType;
	String targetName;
	Integer updatedById;
}
