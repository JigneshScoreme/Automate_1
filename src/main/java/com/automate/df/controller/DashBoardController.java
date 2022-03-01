package com.automate.df.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.automate.df.exception.DynamicFormsServiceException;
import com.automate.df.model.df.dashboard.DashBoardReq;
import com.automate.df.model.df.dashboard.EventDataRes;
import com.automate.df.model.df.dashboard.LeadSourceRes;
import com.automate.df.model.df.dashboard.TargetAchivement;
import com.automate.df.model.df.dashboard.TodaysRes;
import com.automate.df.model.df.dashboard.VehicleModelRes;
import com.automate.df.service.DashBoardService;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Api(value = "/dashboard", tags = "dashboard", description = "dashboard")
@RequestMapping(value="/dashboard")
public class DashBoardController {
	
	@Autowired
	Environment env;
	

	@Autowired
	DashBoardService dashBoardService;
	
	
	
	@CrossOrigin
	@PostMapping(value = "get_target_params")
	public ResponseEntity<List<TargetAchivement>> getTargetAchivementParams(@RequestBody DashBoardReq req)
			throws DynamicFormsServiceException {
		List<TargetAchivement> response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getTargetAchivementParams(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	@CrossOrigin
	@PostMapping(value = "get_vehicle_model_data")
	public ResponseEntity<List<VehicleModelRes>> getVehicleModelData(@RequestBody DashBoardReq req)
			throws DynamicFormsServiceException {
		List<VehicleModelRes> response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getVehicleModelData(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	
	@CrossOrigin
	@PostMapping(value = "get_vehicle_model_data_branch")
	public ResponseEntity<List<VehicleModelRes>> getVehicleModelDataByBranch(@RequestBody DashBoardReq req)
			throws DynamicFormsServiceException {
		List<VehicleModelRes> response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getVehicleModelDataByBranch(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	@CrossOrigin
	@PostMapping(value = "get_leadsource_data")
	public ResponseEntity<List<LeadSourceRes>> getLeadSourceData(@RequestBody DashBoardReq req)
			throws DynamicFormsServiceException {
		List<LeadSourceRes> response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getLeadSourceData(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@CrossOrigin
	@PostMapping(value = "get_leadsource_data_branch")
	public ResponseEntity<List<LeadSourceRes>> getLeadSourceDataByBranch(@RequestBody DashBoardReq req)
			throws DynamicFormsServiceException {
		List<LeadSourceRes> response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getLeadSourceDataByBranch(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping(value = "get_events_data")
	public ResponseEntity<List<EventDataRes>> getEventsData(@RequestBody DashBoardReq req)
			throws DynamicFormsServiceException {
		List<EventDataRes> response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getEventSourceData(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@CrossOrigin
	@PostMapping(value = "get_events_data_branch")
	public ResponseEntity<List<EventDataRes>> getEventsDataByBranch(@RequestBody DashBoardReq req)
			throws DynamicFormsServiceException {
		List<EventDataRes> response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getEventSourceDataByBranch(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	

	@CrossOrigin
	@PostMapping(value = "get_lostdrop_data")
	public ResponseEntity<Map<String,Object>> getLostDropData(@RequestBody DashBoardReq req)
			throws DynamicFormsServiceException {
		Map<String,Object> response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getLostDropData(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	

	@CrossOrigin
	@PostMapping(value = "get_todays_data")
	public ResponseEntity<Map<String,Object>> getTodaysData(@RequestBody DashBoardReq req)
			throws DynamicFormsServiceException {
		Map<String, Object> response = null;
		if (req.getLoggedInEmpId() != null && req.getPageNo() >= 0 && req.getSize() > 0) {
			response = dashBoardService.getTodaysPendingUpcomingData(req);
		} else {
			throw new DynamicFormsServiceException("LoggedInEmpId,PageNo and Size are mandatory params",
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
}
