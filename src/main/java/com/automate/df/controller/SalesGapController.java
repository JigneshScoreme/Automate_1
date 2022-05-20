package com.automate.df.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.automate.df.entity.salesgap.TSAdminUpdateReq;
import com.automate.df.entity.salesgap.TargetEntity;
import com.automate.df.entity.salesgap.TargetRoleReq;
import com.automate.df.exception.DynamicFormsServiceException;
import com.automate.df.model.salesgap.TargetDropDown;
import com.automate.df.model.salesgap.TargetMappingAddReq;
import com.automate.df.model.salesgap.TargetSearch;
import com.automate.df.model.salesgap.TargetSettingReq;
import com.automate.df.model.salesgap.TargetSettingRes;
import com.automate.df.service.SalesGapService;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Api(value = "/sales-gap", tags = "sales-gap", description = "sales-gap")
@RequestMapping(value="/sales-gap")
public class SalesGapController {

	
	@Autowired
	SalesGapService salesGapService;
	
	@Autowired
	Environment env;
	
	
	@CrossOrigin
	@GetMapping(value = "getall_target_mapping_admin")
	public ResponseEntity<?> getTargetSettingData(@RequestParam(defaultValue = "0") int pageNo,
			@RequestParam(defaultValue = "10") int size)
			throws DynamicFormsServiceException {
		List<TargetSettingRes> response = null;
		if (Optional.of(pageNo).isPresent()) {
			response = salesGapService.getTargetSettingData(pageNo,size);
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

}
