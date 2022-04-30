package com.automate.df.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.automate.df.exception.DynamicFormsServiceException;
import com.automate.df.model.df.dashboard.DashBoardReqV2;
import com.automate.df.model.df.dashboard.EventDataRes;
import com.automate.df.model.df.dashboard.LeadSourceRes;
import com.automate.df.model.df.dashboard.OverAllTargetAchivements;
import com.automate.df.model.df.dashboard.SalesDataRes;
import com.automate.df.model.df.dashboard.TargetAchivement;
import com.automate.df.model.df.dashboard.TargetRankingRes;
import com.automate.df.model.df.dashboard.VehicleModelRes;
import com.automate.df.service.DashBoardServiceV2;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Api(value = "/dashboard", tags = "dashboard V2", description = "dashboard V2")
@RequestMapping(value="/dashboard")
public class DashBoardControllerV2 {
	
	@Autowired
	Environment env;
	

	@Autowired
	DashBoardServiceV2 dashBoardService;
	
	
	
	@CrossOrigin
	@PostMapping(value = "v2/get_target_params")
	public ResponseEntity<OverAllTargetAchivements> getTargetAchivementParams(@RequestBody DashBoardReqV2 req)
			throws DynamicFormsServiceException {
		OverAllTargetAchivements response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getTargetAchivementParams(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping(value = "v2/get_target_params_for_emp")
	public ResponseEntity<List<TargetAchivement>> getTargetAchivementParamsForEmp(@RequestBody DashBoardReqV2 req)
			throws DynamicFormsServiceException {
		List<TargetAchivement> response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getTargetAchivementParamsForSingleEmp(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin
	@GetMapping(value = "v2/get_emp_target_ranking/org/{orgId}")
	public ResponseEntity<List<TargetRankingRes>> getEmployeeTargetRankingsByOrg(@PathVariable(name="orgId") Integer orgId)
			throws DynamicFormsServiceException {
		List<TargetRankingRes> response = null;
		if (Optional.of(orgId).isPresent()) {
			response = dashBoardService.getEmployeeTargetRankingByOrg(orgId);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin
	@GetMapping(value = "v2/get_emp_target_ranking/org/{orgId}/branch/{branchId}")
	public ResponseEntity<List<TargetRankingRes>> getEmployeeTargetRankingsByOrgAndBranch(@PathVariable(name="orgId") Integer orgId,@PathVariable(name="branchId") Integer branchId)
			throws DynamicFormsServiceException {
		List<TargetRankingRes> response = null;
		if (Optional.of(orgId).isPresent() && Optional.of(branchId).isPresent()) {
			response = dashBoardService.getEmployeeTargetRankingByOrgAndBranch(orgId, branchId);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	@CrossOrigin
	@PostMapping(value = "v2/get_vehicle_model_data")
	public ResponseEntity<List<VehicleModelRes>> getVehicleModelData(@RequestBody DashBoardReqV2 req)
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
	@PostMapping(value = "v2/get_vehicle_model_data_branch")
	public ResponseEntity<List<VehicleModelRes>> getVehicleModelDataByBranch(@RequestBody DashBoardReqV2 req)
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
	@PostMapping(value = "v2/get_leadsource_data")
	public ResponseEntity<List<LeadSourceRes>> getLeadSourceData(@RequestBody DashBoardReqV2 req)
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
	@PostMapping(value = "v2/get_leadsource_data_branch")
	public ResponseEntity<List<LeadSourceRes>> getLeadSourceDataByBranch(@RequestBody DashBoardReqV2 req)
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
	@PostMapping(value = "v2/get_events_data")
	public ResponseEntity<List<EventDataRes>> getEventsData(@RequestBody DashBoardReqV2 req)
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
	@PostMapping(value = "v2/get_events_data_branch")
	public ResponseEntity<List<EventDataRes>> getEventsDataByBranch(@RequestBody DashBoardReqV2 req)
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
	@PostMapping(value = "v2/get_lostdrop_data")
	public ResponseEntity<Map<String,Object>> getLostDropData(@RequestBody DashBoardReqV2 req)
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
	@PostMapping(value = "v2/get_todays_data")
	public ResponseEntity<Map<String,Object>> getTodaysData(@RequestBody DashBoardReqV2 req)
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
	
	@CrossOrigin
	@PostMapping(value = "v2/get_sales_data")
	public ResponseEntity<?> getSalesData(@RequestBody DashBoardReqV2 req)
			throws DynamicFormsServiceException {
		SalesDataRes response = null;
		if (req.getLoggedInEmpId() != null) {
			response = dashBoardService.getSalesData(req);
		} else {
			throw new DynamicFormsServiceException("LoggedInEmpId is mandatory params",
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping(value = "v2/get_sales_comparsion_data")
	public ResponseEntity<?> getSalesComparsionData(@RequestBody DashBoardReqV2 req)
			throws DynamicFormsServiceException {
		List<Map<String, Long>> response = null;
		if (req.getLoggedInEmpId() != null) {
			response = dashBoardService.getSalesComparsionData(req);
		} else {
			throw new DynamicFormsServiceException("LoggedInEmpId is mandatory params",
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
