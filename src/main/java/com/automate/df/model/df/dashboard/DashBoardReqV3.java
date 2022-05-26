package com.automate.df.model.df.dashboard;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DashBoardReqV3 {
	String orgId;
	String empId;
	String startDate;
	String endDate;
	int pageNo;
	int size;
	Integer loggedInEmpId;

}
