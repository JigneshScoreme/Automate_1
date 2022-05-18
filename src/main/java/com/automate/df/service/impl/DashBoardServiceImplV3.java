package com.automate.df.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.automate.df.dao.dashboard.DashBoardV3Dao;
import com.automate.df.entity.DmsTargetParamSchedular;
import com.automate.df.exception.DynamicFormsServiceException;
import com.automate.df.service.DashBoardServiceV3;
@Service
public class DashBoardServiceImplV3  implements DashBoardServiceV3{
	@Autowired
	DashBoardV3Dao boardV3Dao;
	@Override
	public  List<DmsTargetParamSchedular>  getTargetAchivementParams(String empId)
			throws DynamicFormsServiceException {
		return boardV3Dao.findByEmpId(empId);
	}

}
