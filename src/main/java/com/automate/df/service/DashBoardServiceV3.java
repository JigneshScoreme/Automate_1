package com.automate.df.service;

import java.util.List;

import com.automate.df.entity.DmsTargetParamAllEmployeeSchedular;
import com.automate.df.entity.DmsTargetParamEmployeeSchedular;
import com.automate.df.entity.DmsTargetParamSchedular;
import com.automate.df.exception.DynamicFormsServiceException;

public interface DashBoardServiceV3 {
	
	String getEmpHierararchyDataSchedular(Integer empId,Integer orgId);
	String getTargetAchivementParams(String empId) throws DynamicFormsServiceException;
	String getTargetParamsForEmp(String empId) throws DynamicFormsServiceException;
	String getTargetParamsForAllEmp(String empId) throws DynamicFormsServiceException;
	String getEmpRankOrg(Integer empId, Integer orgId) throws DynamicFormsServiceException;
	String getEmpRankBranch(Integer empId,Integer orgId,Integer branchId) throws DynamicFormsServiceException;
	

}
