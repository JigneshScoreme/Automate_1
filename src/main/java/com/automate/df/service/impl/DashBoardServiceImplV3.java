package com.automate.df.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.automate.df.dao.TmpEmpHierarchyDao;
import com.automate.df.dao.dashboard.DashBoardV3Dao;
import com.automate.df.dao.dashboard.DmsEmpTargetRankingBranchDao;
import com.automate.df.dao.dashboard.DmsEmpTargetRankingOrgDao;
import com.automate.df.dao.dashboard.DmsTargetParamAllEmployeeSchedularDao;
import com.automate.df.dao.dashboard.DmsTargetParamEmployeeSchedularDao;
import com.automate.df.entity.DmsEmployeeTargetRankingBranch;
import com.automate.df.entity.DmsEmployeeTargetRankingOrg;
import com.automate.df.entity.DmsTargetParamAllEmployeeSchedular;
import com.automate.df.entity.DmsTargetParamEmployeeSchedular;
import com.automate.df.entity.DmsTargetParamSchedular;
import com.automate.df.entity.TmpEmpHierarchy;
import com.automate.df.exception.DynamicFormsServiceException;
import com.automate.df.service.DashBoardServiceV3;
import com.google.common.base.Optional;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class DashBoardServiceImplV3  implements DashBoardServiceV3{
	@Autowired
	DashBoardV3Dao boardV3Dao;
	@Autowired
	DmsTargetParamEmployeeSchedularDao dmsTargetParamEmployeeSchedularDao;
	@Autowired
	DmsTargetParamAllEmployeeSchedularDao dmsTargetParamAllEmployeeSchedularDao;
	@Autowired
	DmsEmpTargetRankingOrgDao dmsEmpTargetRankingOrgDao;
	@Autowired
	DmsEmpTargetRankingBranchDao dmsEmpTargetRankingBranchDao;
	
	@Autowired
	TmpEmpHierarchyDao tmpEmpHierarchyDao;
	
	@Override
	public  String getEmpHierararchyDataSchedular(Integer empId,Integer orgId)
			 {
		String str = null;
		try {
		java.util.Optional<TmpEmpHierarchy> opt = tmpEmpHierarchyDao.getDataByEmp(orgId,empId);
	
		TmpEmpHierarchy auto = null;
		if (opt.isPresent()) {
			auto = opt.get();
			str = new Gson().toJson(auto.getData());
			str = str.replace("\\", "");
			str = str.replaceAll("^\"|\"$", "");
			////System.out.println("str " + str);
			auto.setData(str);
			// convertedObject = new Gson().fromJson(auto.getData(), JsonObject.class);
		} else {
			throw new DynamicFormsServiceException("Data Not found in sysem for given empId",
					HttpStatus.BAD_REQUEST);
		}
		}catch(Exception e) {
			e.printStackTrace();
			log.error("exception in getEmpHierararchyDataSchedular ",e);
		}
		return str;	
	}
	
	@Override
	public  String getTargetAchivementParams(String empId)
			throws DynamicFormsServiceException {
		Optional<DmsTargetParamSchedular> opt = boardV3Dao.findByEmpId(empId);
		String str = null;
		DmsTargetParamSchedular auto = null;
		if (opt.isPresent()) {
			auto = opt.get();
			str = new Gson().toJson(auto.getData());
			str = str.replace("\\", "");
			str = str.replaceAll("^\"|\"$", "");
			////System.out.println("str " + str);
			auto.setData(str);
			// convertedObject = new Gson().fromJson(auto.getData(), JsonObject.class);
		} else {
			throw new DynamicFormsServiceException("Data Not found in sysem for given universalId",
					HttpStatus.BAD_REQUEST);
		}
		return str;	
	}
	
	@Override
	public  String getTargetParamsForEmp(String empId)
			throws DynamicFormsServiceException {
		Optional<DmsTargetParamEmployeeSchedular> opt = dmsTargetParamEmployeeSchedularDao.findByEmpId(empId);
		String str = null;
		DmsTargetParamEmployeeSchedular auto = null;
		if (opt.isPresent()) {
			auto = opt.get();
			str = new Gson().toJson(auto.getData());
			str = str.replace("\\", "");
			str = str.replaceAll("^\"|\"$", "");
			////System.out.println("str " + str);
			auto.setData(str);
			// convertedObject = new Gson().fromJson(auto.getData(), JsonObject.class);
		} else {
			throw new DynamicFormsServiceException("Data Not found in sysem for given universalId",
					HttpStatus.BAD_REQUEST);
		}
		return str;	
	}
	@Override
	public String getTargetParamsForAllEmp(String empId)
			throws DynamicFormsServiceException {
		Optional<DmsTargetParamAllEmployeeSchedular> opt = dmsTargetParamAllEmployeeSchedularDao.findByEmpId(empId);
		String str = null;
		DmsTargetParamAllEmployeeSchedular auto = null;
		if (opt.isPresent()) {
			auto = opt.get();
			str = new Gson().toJson(auto.getData());
			str = str.replace("\\", "");
			str = str.replaceAll("^\"|\"$", "");
			////System.out.println("str " + str);
			auto.setData(str);
			// convertedObject = new Gson().fromJson(auto.getData(), JsonObject.class);
		} else {
			throw new DynamicFormsServiceException("Data Not found in sysem for given universalId",
					HttpStatus.BAD_REQUEST);
		}
		return str;	
	}
	
	@Override
	public String getEmpRankOrg(Integer empId, Integer orgId)
			throws DynamicFormsServiceException {
		Optional<DmsEmployeeTargetRankingOrg> opt = dmsEmpTargetRankingOrgDao.findByEmpId(empId,orgId);
		String str = null;
		DmsEmployeeTargetRankingOrg auto = null;
		if (opt.isPresent()) {
			auto = opt.get();
			str = new Gson().toJson(auto.getData());
			str = str.replace("\\", "");
			str = str.replaceAll("^\"|\"$", "");
			////System.out.println("str " + str);
			auto.setData(str);
			// convertedObject = new Gson().fromJson(auto.getData(), JsonObject.class);
		} else {
			throw new DynamicFormsServiceException("Data Not found in sysem for given universalId",
					HttpStatus.BAD_REQUEST);
		}
		return str;	
	}
	
	@Override
	public String getEmpRankBranch(Integer empId,Integer orgId, Integer branchId)
			throws DynamicFormsServiceException {
		Optional<DmsEmployeeTargetRankingBranch> opt = dmsEmpTargetRankingBranchDao.findByEmpIdAndBranchId(empId,orgId,branchId);
		String str = null;
		DmsEmployeeTargetRankingBranch auto = null;
		if (opt.isPresent()) {
			auto = opt.get();
			str = new Gson().toJson(auto.getData());
			str = str.replace("\\", "");
			str = str.replaceAll("^\"|\"$", "");
			////System.out.println("str " + str);
			auto.setData(str);
			// convertedObject = new Gson().fromJson(auto.getData(), JsonObject.class);
		} else {
			throw new DynamicFormsServiceException("Data Not found in sysem for given universalId",
					HttpStatus.BAD_REQUEST);
		}
		return str;	
	}
	

}
