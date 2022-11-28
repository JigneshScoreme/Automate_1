package com.automate.df.entity.salesgap;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class TargetPlanningReq {

	int pageNo;
	int size;
	//int role;
	Set<Integer> childEmpId;
	int loggedInEmpId;
	String targetType;

	String startDate;
	String endDate;
	List<Integer> branchNumber;
}
