package com.automate.df.entity.sales;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TargetsUpdateDto1 {

	String loggedInEmpId;

List<TargetsDto> targets;
	
	String employeeId;
	
	String branch;
	
	String department;
	
	String designation;
	
	String orgId;
	
	String start_date;
	
	String end_date;

}
