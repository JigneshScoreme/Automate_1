package com.automate.df.model.df.dashboard;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OverAllTargetAchivements {
	private List<TargetAchivement> overallTargetAchivements;
	private List<EmployeeTargetAchievement> employeeTargetAchievements;
}
