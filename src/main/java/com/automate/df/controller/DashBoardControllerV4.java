package com.automate.df.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.automate.df.exception.DynamicFormsServiceException;
import com.automate.df.model.df.dashboard.DashBoardReqV2;
import com.automate.df.model.df.dashboard.OverAllTargetAchivements;
import com.automate.df.model.df.dashboard.OverAllTargetAchivementsModelAndSource;
import com.automate.df.model.df.dashboard.TargetAchivement;
import com.automate.df.service.DashBoardServiceV4;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author srujan
 *
 */
@RestController
@Slf4j
@Api(value = "/dashboard", tags = "dashboard V4", description = "dashboard 4")
@RequestMapping(value="/dashboard")
public class DashBoardControllerV4 {
	
	

	@Autowired
	DashBoardServiceV4 dashBoardService;
	
	
	@CrossOrigin
	@PostMapping(value = "v4/get_target_params")
	public ResponseEntity<List<TargetAchivement>> getTargetAchivementParams(@RequestBody DashBoardReqV2 req)
			throws DynamicFormsServiceException {
		List<TargetAchivement> response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getTargetAchivementParams(req);
		} else {
			throw new DynamicFormsServiceException("Invalid Request", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping(value = "v4/get_target_params_for_all_emps")
	public ResponseEntity<OverAllTargetAchivements> getTargetAchivementParamsByEmps(@RequestBody DashBoardReqV2 req)
			throws DynamicFormsServiceException {
		OverAllTargetAchivements response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getTargetAchivementParamsWithEmps(req);
		} else {
			throw new DynamicFormsServiceException("Invalid Request", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	
	@PostMapping(value = "v4/get_target_params_for_all_emps_model_source")
	public ResponseEntity<OverAllTargetAchivementsModelAndSource> getTargetAchivementParamsByEmpsModelAndSource(@RequestBody DashBoardReqV2 req)
			throws DynamicFormsServiceException {
		OverAllTargetAchivementsModelAndSource response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getTargetAchivementParamsWithEmpsModelAndSource(req);
		} else {
			throw new DynamicFormsServiceException("Invalid Request", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	

}
