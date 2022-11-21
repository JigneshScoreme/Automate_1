package com.automate.df.controller;

import java.util.Map;

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
import com.automate.df.service.DashBoardService;

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
	DashBoardService dashBoardService;
	
	
	@CrossOrigin
	@PostMapping(value = "/receptionist")
	public ResponseEntity<Map> getReceptionistData(@RequestBody ReceptionistDashBoardReq req)
			throws DynamicFormsServiceException {
			Map response = dashBoardService.getReceptionistData(req);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	}
