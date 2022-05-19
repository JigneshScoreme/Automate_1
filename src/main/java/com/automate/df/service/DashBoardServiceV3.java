package com.automate.df.service;

import java.util.List;

import com.automate.df.entity.DmsTargetParamAllEmployeeSchedular;
import com.automate.df.entity.DmsTargetParamEmployeeSchedular;
import com.automate.df.entity.DmsTargetParamSchedular;
import com.automate.df.exception.DynamicFormsServiceException;

public interface DashBoardServiceV3 {
	 List<DmsTargetParamSchedular>  getTargetAchivementParams(String empId) throws DynamicFormsServiceException;
	 List<DmsTargetParamEmployeeSchedular>  getTargetParamsForEmp(String empId) throws DynamicFormsServiceException;
	 List<DmsTargetParamAllEmployeeSchedular>  getTargetParamsForAllEmp(String empId) throws DynamicFormsServiceException;

}
