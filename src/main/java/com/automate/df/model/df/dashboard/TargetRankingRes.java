package com.automate.df.model.df.dashboard;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TargetRankingRes {
	Integer empId;
	Integer orgId;
	Integer rank;
	Integer targetAchivements;
	Integer branchId;
	String empName;
}
