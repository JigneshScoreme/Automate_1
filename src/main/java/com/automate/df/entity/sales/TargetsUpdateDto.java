package com.automate.df.entity.sales;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TargetsUpdateDto {
	
	List<TargetUpdateBasedOnEmplyeeDto> targets;
	
    String start_date;
	
	String end_date;
	
	String orgId;
	
	String employeeId;

}
