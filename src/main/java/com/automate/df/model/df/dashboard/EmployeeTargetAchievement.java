package com.automate.df.model.df.dashboard;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeTargetAchievement {
	private String empName;
	private Integer empId;
	private String orgId;
	private String branchId;
	private List<TargetAchivement> targetAchievements;
	Map<String, Integer> targetAchievementsMap;
}
