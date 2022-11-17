package com.automate.df.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor

public class MyTaskReq {
	
	Integer orgId;
	Integer loggedInEmpId;
	String startDate;
	String endDate;
	boolean isOnlyForEmp;
	
	String dataType; // allowed Values todaysData,upcomingData,pendingData,rescheduledData
	boolean ignoreDateFilter;
	boolean isDetailView;
	String viewForEmp;
	Integer detailedViewEmpId;
	List<String> branchCodes;


	List<Integer> salesConsultantId;

	
	

}
