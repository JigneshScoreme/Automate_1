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
import com.automate.df.model.df.dashboard.ReceptionistDashBoardReq;
import com.automate.df.model.df.dashboard.SourceRes;
import com.automate.df.model.df.dashboard.VehicleModelRes;
import com.automate.df.service.ReceptionistService;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Api(value = "/dashboard", tags = "dashboard", description = "dashboard")
@RequestMapping(value="/dashboard")
public class ReceptionistController {
	
	@Autowired
	Environment env;

	@Autowired
	ReceptionistService dashBoardService;
	
	
	@CrossOrigin
	@PostMapping(value = "/receptionist")
	public ResponseEntity<Map> getReceptionistData(@RequestBody ReceptionistDashBoardReq req)
			throws DynamicFormsServiceException {
			Map response = dashBoardService.getReceptionistData(req);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@CrossOrigin
	@PostMapping(value = "/receptionist/model")
	public ResponseEntity<List<VehicleModelRes>> getReceptionistModelData(@RequestBody ReceptionistDashBoardReq req)
			throws DynamicFormsServiceException {
		List<VehicleModelRes> response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getReceptionistModelData(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping(value = "/receptionist/source")
	public ResponseEntity<List<SourceRes>> getReceptionistSourceData(@RequestBody ReceptionistDashBoardReq req)
			throws DynamicFormsServiceException {
		List<SourceRes> response = null;
		if (Optional.of(req).isPresent()) {
			response = dashBoardService.getReceptionistSourceData(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	}
