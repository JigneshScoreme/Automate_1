package com.automate.df.dao.dashboard;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TargetAchivementModelandSource {
	
	String paramName;
	String paramShortName;
	String target;
	String achievment;
	String achivementPerc;
	String shortfall;
	String shortFallPerc;
	List<Object> data;
	String model;
	String source;

}
