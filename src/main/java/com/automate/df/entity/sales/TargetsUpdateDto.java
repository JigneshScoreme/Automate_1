package com.automate.df.entity.sales;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TargetsUpdateDto {
	
	List<TargetsDto> targets;
	
	String employeeId;
	
	String branch;
	
	String department;
	
	String designation;
	
	String orgId;
	
	String start_date;
	
	String end_date;

}
