package com.automate.df.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.automate.df.entity.salesgap.TargetPlanningCountRes;
import com.automate.df.entity.salesgap.TargetPlanningReq;
import com.automate.df.model.df.dashboard.DashBoardReq;
import com.automate.df.model.df.dashboard.DashBoardReqV2;
import com.automate.df.model.df.dashboard.TargetAchivement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.automate.df.dao.salesgap.TargetUserRepo;
import com.automate.df.entity.sales.TargetSettingsResponseDto;
import com.automate.df.entity.sales.TargetUpdateBasedOnEmplyeeDto;
import com.automate.df.entity.sales.TargetsDto;
import com.automate.df.entity.sales.TargetsUpdateDto;
import com.automate.df.entity.sales.TargetsUpdateDto1;
import com.automate.df.entity.salesgap.TSAdminUpdateReq;
import com.automate.df.entity.salesgap.TargetRoleReq;
import com.automate.df.exception.DynamicFormsServiceException;
import com.automate.df.model.salesgap.TargetDropDown;
import com.automate.df.model.salesgap.TargetMappingAddReq;
import com.automate.df.model.salesgap.TargetSearch;
import com.automate.df.model.salesgap.TargetSettingReq;
import com.automate.df.model.salesgap.TargetSettingRes;
import com.automate.df.service.SalesGapService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author srujan
 *
 */
@RestController
@Slf4j
@Api(value = "/sales-gap", tags = "sales-gap", description = "sales-gap")
@RequestMapping(value="/sales-gap")
public class SalesGapController {

	
	@Autowired
	SalesGapService salesGapService;
	
	@Autowired
	Environment env;
	
	@Autowired
	private TargetUserRepo targetuserrepo;
	
	
	@CrossOrigin
	@GetMapping(value = "getall_target_mapping_admin")
	public ResponseEntity<?> getTargetSettingData(@RequestParam(defaultValue = "0") int pageNo,
			@RequestParam(defaultValue = "10") int size,@RequestParam int orgId)
			throws DynamicFormsServiceException {
		List<TargetSettingRes> response = null;
		if (Optional.of(pageNo).isPresent()) {
			response = salesGapService.getTargetSettingData(pageNo,size,orgId);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin
	@GetMapping(value = "get_target_mapping_admin/{id}")
	public ResponseEntity<?> getTargetSettingAdminById(@PathVariable(name="id") int id)
			throws DynamicFormsServiceException {
		TSAdminUpdateReq response = null;
		if (Optional.of(id).isPresent()) {
			response = salesGapService.getTargetSettingAdminById(id);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	@CrossOrigin
	@PostMapping(value = "create_target_mapping_admin")
	public ResponseEntity<TargetSettingRes> saveTargetSettingData(@RequestBody TargetSettingReq request)
			throws DynamicFormsServiceException {
		TargetSettingRes response = null;
		if (Optional.of(request).isPresent()) {
			response = salesGapService.saveTargetSettingData(request);
		} 
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	@CrossOrigin
	@PostMapping(value = "verify_target_mapping_admin")
	public ResponseEntity<?> verifyTargetSettingData(@RequestBody TargetSettingReq request)
			throws DynamicFormsServiceException {
		Map<String,String> response = null;
		if (Optional.of(request).isPresent()) {
			response = salesGapService.verifyTargetSettingData(request);
		} 
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin
	@PutMapping(value = "update_target_mapping_admin")
	public ResponseEntity<?> updateTargetSettingData(@RequestBody TargetSettingRes request)
			throws DynamicFormsServiceException {
		String response = null;
		if (Optional.of(request).isPresent()) {
			response = salesGapService.updateTargetSettingData(request);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin
	@PutMapping(value = "update_target_mapping_admin_v2")
	public ResponseEntity<?> updateTargetSettingDataV2(@RequestBody TSAdminUpdateReq request)
			throws DynamicFormsServiceException {
		TargetSettingRes response = null;
		if (Optional.of(request).isPresent()) {
			response = salesGapService.updateTargetSettingDataV2(request);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	/*
	@CrossOrigin
	@PostMapping(value = "target-mapping")
	public ResponseEntity<?> saveTargetMappingData(@RequestBody TargetMappingReq request)
			throws DynamicFormsServiceException {
		String response = null;
			response = salesGapService.saveTargetMappingData(request);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	*/
	/*
	
	@CrossOrigin
	@GetMapping(value = "target-mapping/{id}")
	public ResponseEntity<?> getTargetMappingData(@PathVariable(name="id") Integer id)
			throws DynamicFormsServiceException {
		TargetMappingReq response = null;
		if (Optional.of(id).isPresent()) {
			response = salesGapService.getTargetMappingData(id);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	*/
	@CrossOrigin
	@PostMapping(value = "search_targetmapping_role")
	public ResponseEntity<?> searchTargetMappingData(@RequestBody TargetSearch request)
			throws DynamicFormsServiceException {
		List<TargetSettingRes> response = null;
		if (Optional.of(request).isPresent()) {
			response = salesGapService.searchTargetMappingData(request);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
/*
	@CrossOrigin
	@GetMapping(value = "target-dropdown/{type}")
	public ResponseEntity<?> getTargetDropdown(@PathVariable("type") String type)
			throws DynamicFormsServiceException {
		List<TargetDropDown> response = null;
		if (Optional.of(type).isPresent()) {
			response = salesGapService.getTargetDropdown(type);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	*/
	

	@CrossOrigin
	@PostMapping(value = "get_all_targetmapping_role")
	public ResponseEntity<?> getTargetDataWithRole(@RequestBody TargetRoleReq req)
			throws DynamicFormsServiceException {
		Map<String, Object> response = null;
		if (Optional.of(req).isPresent()) {
			response = salesGapService.getTargetDataWithRole(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	

	@CrossOrigin
	@PostMapping(value = "get_all_target_mapping_search")
	public ResponseEntity<?> getTargetMappingDataSearchByEmpId(@RequestBody TargetPlanningReq req)
			throws DynamicFormsServiceException {
		Map<String, Object> response = null;
		if (Optional.of(req).isPresent()) {
			response = salesGapService.getTargetMappingDataSearchByEmpId(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}


	@CrossOrigin
	@PostMapping(value = "get_target_planning_count")
	public ResponseEntity<?> getTargetPlanningParamsCount(@RequestBody TargetPlanningReq req)
			throws DynamicFormsServiceException {
		List<TargetPlanningCountRes> response = null;
		if (Optional.of(req).isPresent()) {
			response = salesGapService.getTargetPlanningParamsCount(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}



	/*
	 * @CrossOrigin
	 * 
	 * @PutMapping(value = "edit_targetmapping_dse") public ResponseEntity<?>
	 * updateTargetDataWithRole(@RequestBody TargetSettingRes req) throws
	 * DynamicFormsServiceException { TargetSettingRes response = null; if
	 * (Optional.of(req).isPresent()) { response =
	 * salesGapService.updateTargetDataWithRole(req); } else { throw new
	 * DynamicFormsServiceException(env.getProperty("BAD_REQUEST"),
	 * HttpStatus.BAD_REQUEST); } return new ResponseEntity<>(response,
	 * HttpStatus.OK); }
	 */
	@CrossOrigin
	@PostMapping(value = "add_targetmapping_role")
	public ResponseEntity<?> addTargetDataWithRole(@RequestBody TargetMappingAddReq req)
			throws DynamicFormsServiceException {
		TargetSettingRes response = null;
		if (Optional.of(req).isPresent()) {
			response = salesGapService.addTargetDataWithRole(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping(value = "edit_targetmapping_role")
	public ResponseEntity<?> editTargetDataWithRoleV2(@RequestBody TargetMappingAddReq req)
			throws DynamicFormsServiceException {
		TargetSettingRes response = null;
		if (Optional.of(req).isPresent()) {
			response = salesGapService.editTargetDataWithRoleV2(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin
	@GetMapping(value = "target-dropdown")
	public ResponseEntity<?> getTargetDropdownV2(
			@RequestParam(name="orgId",required = true) String orgId,
			@RequestParam(name="branchId",required = false) String branchId,
			@RequestParam(name="parent",required = false) String parent,
			@RequestParam(name="child",required = false) String child,
			@RequestParam(name="parentId",required = true) String parentId
			)
			throws DynamicFormsServiceException {
		List<TargetDropDown> response = null;
		if (Optional.of(orgId).isPresent()) {
			response = salesGapService.getTargetDropdownV2(orgId,branchId,parent,child,parentId);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	
	
	

	

	@CrossOrigin
	@DeleteMapping(value = "delete_target_mapping_role")
	public ResponseEntity<?> deleteTSData(
			@RequestParam(name="recordId",required = true) String recordId,
			@RequestParam(name="empId",required = true) String empId
			)
			throws DynamicFormsServiceException {
		String response = null;
		if (Optional.of(recordId).isPresent()) {
			response = salesGapService.deleteTSData(recordId,empId);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@CrossOrigin
	@GetMapping(value = "get_employee_role/{empId}")
	public ResponseEntity<Map<String,String>> getEmployeeRole(
			@PathVariable(name="empId",required = true) Integer empId)
			throws DynamicFormsServiceException {
		Map<String,String> response = null;
		if (Optional.of(empId).isPresent()) {
			response = salesGapService.getEmployeeRole(empId);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@CrossOrigin
	@DeleteMapping(value = "delete_target_mapping_admin")
	public ResponseEntity<?> deleteAdminTargetMapping(
			@RequestParam(name="recordId",required = true) Integer recordId
			)
			throws DynamicFormsServiceException {
		String response = null;
		if (Optional.of(recordId).isPresent()) {
			response = salesGapService.deleteAdminTargetMapping(recordId);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	
	@CrossOrigin
	@PostMapping(value = "target-update")
	public ResponseEntity<?> targetUpdate(@RequestBody TargetsUpdateDto targetsUpdateDto) {
		//System.out.println("entered into controller");
		TargetSettingsResponseDto response=new TargetSettingsResponseDto();
		int updateTargetSetings=0;
		List<TargetUpdateBasedOnEmplyeeDto> targetemployeesupdatedto = targetsUpdateDto.getTargets();
		for(TargetUpdateBasedOnEmplyeeDto targetemployeeupdatedto: targetemployeesupdatedto) {
			 List<TargetsDto> targets = targetemployeeupdatedto.getTargets();
		ObjectMapper mapper = new ObjectMapper();
		String json =null;
		
		try {
		
		   json = mapper.writeValueAsString(targets);
		  //System.out.println("ResultingJSONstring = " + json);
		  ////System.out.println(json);
		} catch (JsonProcessingException e) {
		   e.printStackTrace();
		}
		 updateTargetSetings = targetuserrepo.updateTargetSetings(json,
				targetemployeeupdatedto.getEmployeeId(), targetsUpdateDto.getOrgId(), targetemployeeupdatedto.getBranch(),
				targetemployeeupdatedto.getDepartment(), targetemployeeupdatedto.getDesignation()
				,targetsUpdateDto.getStart_date(),targetsUpdateDto.getEnd_date()
				);
		}
		if (updateTargetSetings > 0) {
			response.setMessage("Update Sucessfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.setMessage("Not Updated");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		
	}

	
	
	@CrossOrigin
	@PostMapping(value = "target-update1")
	public ResponseEntity<?> targetUpdate1(@RequestBody TargetsUpdateDto1 targetsUpdateDto) {
		ObjectMapper mapper = new ObjectMapper();
		String json =null;
		TargetSettingsResponseDto response=new TargetSettingsResponseDto();
		try {
			
		   json = mapper.writeValueAsString(targetsUpdateDto.getTargets());
		  //System.out.println("ResultingJSONstring = " + json);
		  ////System.out.println(json);
			
		} catch (JsonProcessingException e) {
		   e.printStackTrace();
		}
		int updateTargetSetings = targetuserrepo.updateTargetSetings(json,
				targetsUpdateDto.getEmployeeId(), targetsUpdateDto.getOrgId(), targetsUpdateDto.getBranch(),
				targetsUpdateDto.getDepartment(), targetsUpdateDto.getDesignation()
				,targetsUpdateDto.getStart_date(),targetsUpdateDto.getEnd_date()
				);
		if (updateTargetSetings > 0) {
			response.setMessage("Update Sucessfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.setMessage("Not Updated");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	
	}
}
