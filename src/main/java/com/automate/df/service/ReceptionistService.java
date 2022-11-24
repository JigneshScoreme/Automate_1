package com.automate.df.service;

import java.util.List;
import java.util.Map;

import com.automate.df.model.df.dashboard.ReceptionistDashBoardReq;
import com.automate.df.model.df.dashboard.SourceRes;
import com.automate.df.model.df.dashboard.VehicleModelRes;

public interface ReceptionistService {
	
	Map getReceptionistData(ReceptionistDashBoardReq req);

	List<VehicleModelRes> getReceptionistModelData(ReceptionistDashBoardReq req);
	
	List<SourceRes> getReceptionistSourceData(ReceptionistDashBoardReq req);
	

}
