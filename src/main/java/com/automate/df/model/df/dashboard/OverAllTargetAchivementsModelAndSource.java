package com.automate.df.model.df.dashboard;

import java.util.List;

import com.automate.df.dao.dashboard.TargetAchivementModelandSource;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OverAllTargetAchivementsModelAndSource {
	
	private List<TargetAchivementModelandSource> overallTargetAchivements;
	private List<EmployeeTargetAchievementModelAndView> employeeTargetAchievements;

}
