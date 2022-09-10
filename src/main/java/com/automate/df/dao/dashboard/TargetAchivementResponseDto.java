package com.automate.df.dao.dashboard;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.automate.df.model.df.dashboard.TargetAchivement;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TargetAchivementResponseDto {

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
