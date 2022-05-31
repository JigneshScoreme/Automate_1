package com.automate.df.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyTaskReq {
	
	Integer loggedInEmpId;
	String startDate;
	String endDate;
	boolean isOnlyForEmp;
	
	String dataType; // allowed Values todaysData,upcomingData,pendingData,rescheduledData
	
	

}
