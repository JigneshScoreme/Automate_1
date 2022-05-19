package com.automate.df.controller;

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
import com.automate.df.model.df.dashboard.DashBoardReqV3;
import com.automate.df.service.DashBoardServiceV3;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
@RestController
@Slf4j
@Api(value = "/dashboard", tags = "dashboard V2", description = "dashboard V2")
@RequestMapping(value="/dashboard")
public class DashBoardControllerV3 {
	@Autowired
	Environment env;
	

	@Autowired
	DashBoardServiceV3 dashBoardService;
	
	@CrossOrigin
	@PostMapping(value = "v2/get_target_params_scheduler")
	public ResponseEntity<?> getTargetAchivementParams(@RequestBody DashBoardReqV3 req)
			throws DynamicFormsServiceException {
		 String  response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getTargetAchivementParams(req.getEmpId());
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping(value = "v2/get_target_params_for_emp_scheduler")
	public ResponseEntity<?> getTargetParamsForEmp(@RequestBody DashBoardReqV3 req)
			throws DynamicFormsServiceException {
		 String  response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getTargetParamsForEmp(req.getEmpId());
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping(value = "v2/get_target_params_for_all_emps_scheduler")
	public ResponseEntity<?> getTargetParamsForAllEmp(@RequestBody DashBoardReqV3 req)
			throws DynamicFormsServiceException {
		 String response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getTargetParamsForAllEmp(req.getEmpId());
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
