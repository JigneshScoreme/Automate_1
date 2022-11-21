package com.automate.df.model.df.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceptionistDashBoardReq {
	
	String startDate;
	String endDate;
	String dealerCode;
	int orgId; 
	
}
