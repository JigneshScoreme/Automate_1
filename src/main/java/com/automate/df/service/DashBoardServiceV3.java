package com.automate.df.service;

import java.util.List;

import com.automate.df.entity.DmsTargetParamSchedular;
import com.automate.df.exception.DynamicFormsServiceException;

public interface DashBoardServiceV3 {
	 List<DmsTargetParamSchedular>  getTargetAchivementParams(String empId) throws DynamicFormsServiceException;

}
