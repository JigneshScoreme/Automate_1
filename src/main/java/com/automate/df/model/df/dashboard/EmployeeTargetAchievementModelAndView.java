package com.automate.df.model.df.dashboard;

import java.util.List;
import java.util.Map;

import com.automate.df.dao.dashboard.TargetAchivementModelandSource;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeTargetAchievementModelAndView {
	
	private String empName;
	private Integer empId;
	private String orgId;
	private String branchId;
	private List<TargetAchivementModelandSource> targetAchievements;
	Map<String, Integer> targetAchievementsMap;
	String model;
	String source;

}
