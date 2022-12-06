package com.automate.df.service;

import java.util.List;
import java.util.Map;

import com.automate.df.exception.DynamicFormsServiceException;
import com.automate.df.model.df.dashboard.DigitalManagerDashBoardReq;
import com.automate.df.model.df.dashboard.ReceptionistDashBoardReq;
import com.automate.df.model.df.dashboard.ReceptionistLeadRes;
import com.automate.df.model.df.dashboard.SourceRes;
import com.automate.df.model.df.dashboard.VehicleModelRes;

public interface ReceptionistService {
	
	Map getReceptionistData(ReceptionistDashBoardReq req, String roleName);

	List<VehicleModelRes> getReceptionistModelData(ReceptionistDashBoardReq req, String roleName);
	
	List<SourceRes> getReceptionistSourceData(ReceptionistDashBoardReq req, String roleName);

	List<ReceptionistLeadRes> getReceptionistLeadData(ReceptionistDashBoardReq req, String roleName);
	
	public List<ReceptionistLeadRes> getReceptionistDroppedLeadData(ReceptionistDashBoardReq req, String roleName);
	
	public List<ReceptionistLeadRes> getReceptionistDroppedLeadDataByStage(ReceptionistDashBoardReq req, String roleName) ;

	Map getDigitalManagerDahboardData(DigitalManagerDashBoardReq req) throws DynamicFormsServiceException;
	
	List getDigitalManagerDahboardTeamData(DigitalManagerDashBoardReq req) throws DynamicFormsServiceException;
}
