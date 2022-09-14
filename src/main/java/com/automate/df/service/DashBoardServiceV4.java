package com.automate.df.service;

import java.util.List;

import com.automate.df.exception.DynamicFormsServiceException;
import com.automate.df.model.df.dashboard.DashBoardReqV2;
import com.automate.df.model.df.dashboard.OverAllTargetAchivements;
import com.automate.df.model.df.dashboard.OverAllTargetAchivementsModelAndSource;
import com.automate.df.model.df.dashboard.TargetAchivement;

public interface DashBoardServiceV4 {

	List<TargetAchivement> getTargetAchivementParams(DashBoardReqV2 req) throws DynamicFormsServiceException;

	OverAllTargetAchivements getTargetAchivementParamsWithEmps(DashBoardReqV2 req) throws DynamicFormsServiceException;
	
	OverAllTargetAchivementsModelAndSource getTargetAchivementParamsWithEmpsModelAndSource(DashBoardReqV2 req) throws DynamicFormsServiceException;

}
