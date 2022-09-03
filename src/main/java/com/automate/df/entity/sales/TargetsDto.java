package com.automate.df.entity.sales;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TargetsDto {
	private String unit;
	private String target;
	private String parameter;
	@Override
	public String toString() {
		return "TargetsDto [unit=" + unit + ", target=" + target + ", parameter=" + parameter + "]";
	} 
	
	
}
