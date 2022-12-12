package com.automate.df.entity.sales;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TargetUpdateBasedOnEmplyeeDto {
	
	private String employeeId;
	
	private List<TargetsDto> targets;
		
	String branch;
	
	String department;
	
	String designation;

	Integer updatedByUserId;
	
	
	
	

}
