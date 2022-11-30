package com.automate.df.model.df.dashboard;

import java.util.List;

import javax.annotation.Nonnull;

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
	String empName;
	@Nonnull
	Integer orgId; 
	@Nonnull
	Integer loggedInEmpId;
	
	List<String> stage;
	List<String> status;
	String category;
	
	
}
