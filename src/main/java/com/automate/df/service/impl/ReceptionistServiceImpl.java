package com.automate.df.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.automate.df.dao.LeadStageRefDao;
import com.automate.df.dao.SourceAndIddao;
import com.automate.df.dao.SubSourceRepository;
import com.automate.df.dao.dashboard.DmsLeadDao;
import com.automate.df.dao.dashboard.DmsLeadDropDao;
import com.automate.df.dao.dashboard.DmsWfTaskDao;
import com.automate.df.dao.salesgap.DmsEmployeeRepo;
import com.automate.df.dao.salesgap.TargetSettingRepo;
import com.automate.df.dao.salesgap.TargetUserRepo;
import com.automate.df.entity.LeadStageRefEntity;
import com.automate.df.entity.SourceAndId;
import com.automate.df.entity.SubSource;
import com.automate.df.entity.dashboard.DmsLead;
import com.automate.df.entity.dashboard.DmsWFTask;
import com.automate.df.entity.salesgap.DmsEmployee;
import com.automate.df.exception.DynamicFormsServiceException;
import com.automate.df.model.df.dashboard.DigitalManagerDashBoardReq;
import com.automate.df.model.df.dashboard.ReceptionistDashBoardReq;
import com.automate.df.model.df.dashboard.ReceptionistLeadRes;
import com.automate.df.model.df.dashboard.SourceRes;
import com.automate.df.model.df.dashboard.VehicleModelRes;
import com.automate.df.service.DashBoardServiceV2;
import com.automate.df.service.ReceptionistService;
import com.automate.df.util.DashBoardUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jignesh
 *
 */
@Slf4j
@Service
public class ReceptionistServiceImpl implements ReceptionistService {

	@Autowired
	Environment env;

	@Autowired
	TargetSettingRepo targetSettingRepo;

	@Autowired
	TargetUserRepo targetUserRepo;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	Gson gson;

	@Autowired
	SalesGapServiceImpl salesGapServiceImpl;

	@Autowired
	DmsEmployeeRepo dmsEmployeeRepo;

	@Autowired
	DashBoardUtil dashBoardUtil;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	DmsLeadDao dmsLeadDao;

	@Autowired
	DmsWfTaskDao dmsWfTaskDao;

	@Autowired
	DmsLeadDropDao dmsLeadDropDao;

	@Autowired
	LeadStageRefDao leadStageRefDao;

	@Autowired
	SourceAndIddao repository;
	
	@Autowired
	SubSourceRepository subSourceRepository;
	
	@Autowired
	DashBoardServiceV2 dashBoardServiceV2;

	public static final String ENQUIRY = "Enquiry";
	public static final String DROPPED = "DROPPED";
	public static final String TEST_DRIVE = "Test Drive";
	public static final String HOME_VISIT = "Home Visit";
	public static final String FINANCE = "Finance";
	public static final String INSURANCE = "Insurance";
	public static final String VIDEO_CONFERENCE = "Video Conference";
	public static final String PROCEED_TO_BOOKING = "Proceed to Booking";
	public static final String BOOKING_FOLLOWUP_DSE = "Booking Follow Up - DSE";
	public static final String BOOKING_FOLLOWUP_CRM = "Booking Follow Up - CRM";
	public static final String BOOKING_FOLLOWUP_ACCCESSORIES = "Booking Follow Up - Accessories";
	public static final String VERIFY_EXCHANGE_APPROVAL = "Verify Exchange Approval";

	public static final String READY_FOR_INVOICE = "Ready for invoice - Accounts";
	public static final String PROCEED_TO_INVOICE = "Proceed to Invoice";
	public static final String INVOICE_FOLLOWUP_DSE = "Invoice Follow Up - DSE";
	public static final String INVOICE = "INVOICE";

	public static final String PRE_BOOKING = "Pre Booking";
	public static final String DELIVERY = "Delivery";

	public static final String BOOKING = "Booking";
	public static final String EXCHANGE = "Exchange";
	public static final String ACCCESSORIES = "Accessories";
	public static final String EVENTS = "Events";

	public String getStartDate(String inputStartDate) {
		if (null == inputStartDate && null == inputStartDate) {
			return getFirstDayOfMonth();
		} else {
			return inputStartDate;
		}

	}

	public String getEndDate(String inputEndDate) {
		if (null == inputEndDate && null == inputEndDate) {
			return getLastDayOfMonth();
		} else {
			return inputEndDate;
		}
	}

	public String getFirstDayOfMonth() {
		return LocalDate.ofEpochDay(System.currentTimeMillis() / (24 * 60 * 60 * 1000)).withDayOfMonth(1).toString()
				+ " 00:00:00";
	}

	public String getLastDayOfMonth() {
		return LocalDate.ofEpochDay(System.currentTimeMillis() / (24 * 60 * 60 * 1000)).plusMonths(1).withDayOfMonth(1)
				.minusDays(1).toString() + " 00:00:00";
	}

	public Map getReceptionistData(ReceptionistDashBoardReq req, String roleName) {

		DmsEmployee dmsEmployeeObj = dmsEmployeeRepo.getById(req.getLoggedInEmpId());
		String loginEmpName = dmsEmployeeObj.getEmpName();

		Map map = new HashMap();
		String startDate = getStartDate(req.getStartDate());
		String endDate = getEndDate(req.getEndDate());
		int orgId = req.getOrgId();
		String dealerCode = req.getDealerCode();

		List<String> dmsEmployeeList = dmsEmployeeRepo.findEmpNames(orgId);

		List consultantList = new ArrayList();
		for (String dmsEmployee : dmsEmployeeList) {
			Map m = new HashMap();
			// m.put("emp_id", dmsEmployee.getEmp_id());
			m.put("emp_name", dmsEmployee);
			m.put("allocatedCount", getAllocatedLeadsCountByEmp(dmsEmployee, startDate, endDate, orgId, dealerCode,
					loginEmpName, roleName));
			m.put("droppedCount", getDropeedLeadsCountByEmp(dmsEmployee, startDate, endDate, orgId, dealerCode,
					loginEmpName, roleName));
			consultantList.add(m);
		}
		map.put("consultantList", consultantList);
		map.put("totalAllocatedCount",
				getAllocatedLeadsCount(startDate, endDate, orgId, dealerCode, loginEmpName, roleName));
		map.put("totalDroppedCount",
				getDroppedLeadsCount(startDate, endDate, orgId, dealerCode, loginEmpName, roleName));
		map.put("bookingCount",
				getAllLeadsCount(startDate, endDate, "BOOKING", orgId, dealerCode, loginEmpName, roleName));
		map.put("RetailCount",
				getAllLeadsCount(startDate, endDate, "INVOICE", orgId, dealerCode, loginEmpName, roleName));

		return map;
	}

	Integer getAllocatedLeadsCountByEmp(String empName, String startDate, String endDate, int orgId, String dealerCode,
			String loginEmpName, String roleName) {
		if (StringUtils.isEmpty(dealerCode))
			return dmsLeadDao.getAllocatedLeadsCountByEmp(empName, startDate, endDate, orgId, loginEmpName, roleName);
		else
			return dmsLeadDao.getAllocatedLeadsCountByEmp(empName, startDate, endDate, orgId, dealerCode, loginEmpName,
					roleName);
	}

	Integer getDropeedLeadsCountByEmp(String empName, String startDate, String endDate, int orgId, String dealerCode,
			String loginEmpName, String roleName) {
		if (StringUtils.isEmpty(dealerCode))
			return dmsLeadDao.getDropeedLeadsCountByEmp(empName, startDate, endDate, orgId, loginEmpName, roleName);
		else
			return dmsLeadDao.getDropeedLeadsCountByEmp(empName, startDate, endDate, orgId, dealerCode, loginEmpName,
					roleName);
	}

	Integer getAllocatedLeadsCount(String startDate, String endDate, int orgId, String dealerCode, String loginEmpName,
			String roleName) {
		if (StringUtils.isEmpty(dealerCode))
			return dmsLeadDao.getAllocatedLeadsCount(startDate, endDate, orgId, loginEmpName, roleName);
		else
			return dmsLeadDao.getAllocatedLeadsCount(startDate, endDate, orgId, dealerCode, loginEmpName, roleName);
	}

	Integer getDroppedLeadsCount(String startDate, String endDate, int orgId, String dealerCode, String loginEmpName,
			String roleName) {
		if (StringUtils.isEmpty(dealerCode))
			return dmsLeadDao.getDroppedLeadsCount(startDate, endDate, orgId, loginEmpName, roleName);
		else
			return dmsLeadDao.getDroppedLeadsCount(startDate, endDate, orgId, dealerCode, loginEmpName, roleName);
	}

	Integer getAllLeadsCount(String startDate, String endDate, String leadType, int orgId, String dealerCode,
			String loginEmpName, String roleName) {
		if (StringUtils.isEmpty(dealerCode))
			return dmsLeadDao.getAllLeadsCount(startDate, endDate, leadType, orgId, loginEmpName, roleName);
		else
			return dmsLeadDao.getAllLeadsCount(startDate, endDate, leadType, orgId, dealerCode, loginEmpName, roleName);
	}

	public List<VehicleModelRes> getReceptionistModelData(ReceptionistDashBoardReq req, String roleName) {
		List<VehicleModelRes> resList = new ArrayList<>();

		DmsEmployee dmsEmployeeObj = dmsEmployeeRepo.getById(req.getLoggedInEmpId());
		String loginEmpName = dmsEmployeeObj.getEmpName();
		String startDate = getStartDate(req.getStartDate());
		String endDate = getEndDate(req.getEndDate());
		String dealerCode = req.getDealerCode();

		String orgId = req.getOrgId().toString();
		Map<String, Integer> vehicalList = dashBoardUtil.getVehilceDetailsByOrgId(orgId);
		for (String model : vehicalList.keySet()) {

			if (null != model) {
				VehicleModelRes vehicleRes = new VehicleModelRes();
				log.info("Generating data for model " + model);
				List<DmsLead> dmsLeadList;
				if (StringUtils.isEmpty(dealerCode))
					dmsLeadList = dmsLeadDao.getAllEmployeeLeadsByModel(orgId, startDate, endDate, model, loginEmpName);
				else
					dmsLeadList = dmsLeadDao.getAllEmployeeLeadsByModel(orgId, startDate, endDate, model, loginEmpName,
							dealerCode, roleName);

				Long enqLeadCnt = 0L;
				Long bookCount = 0L;

				List<Integer> dmsLeadIdList = dmsLeadList.stream().map(DmsLead::getId).collect(Collectors.toList());
				log.debug("dmsLeadList::" + dmsLeadList);
				List<LeadStageRefEntity> leadRefList = leadStageRefDao.getLeadsByStageandDate(orgId, dmsLeadIdList,
						startDate, endDate);
				if (null != leadRefList && !leadRefList.isEmpty()) {
					log.debug("Total leads in LeadReF table is ::" + leadRefList.size());
					enqLeadCnt = leadRefList.stream().filter(x -> x.getStageName().equalsIgnoreCase("ENQUIRY")).count();
					bookCount = leadRefList.stream().filter(x -> x.getStageName().equalsIgnoreCase("BOOKING")).count();
				}

				Long droppedCnt = 0L;
				if (null != dmsLeadList) {
					log.info("size of dmsLeadList " + dmsLeadList.size());
					enqLeadCnt = getEnqLeadCount(dmsLeadList);
					droppedCnt = getDroppedCount(dmsLeadList);
					vehicleRes.setR(getInvoiceCount(dmsLeadList));

					log.info("enqLeadCnt: " + enqLeadCnt + " ,droppedCnt : " + droppedCnt);
				}
				vehicleRes.setModel(model);
				vehicleRes.setE(enqLeadCnt);
				vehicleRes.setL(droppedCnt);

				List<String> leadUniversalIdList = dmsLeadList.stream().map(DmsLead::getCrmUniversalId)
						.collect(Collectors.toList());
				log.info("leadUniversalIdList " + leadUniversalIdList);

				List<DmsWFTask> wfTaskList = dmsWfTaskDao.getWfTaskByUniversalId(leadUniversalIdList, startDate,
						endDate);

				vehicleRes.setT(getTestDriveCount(wfTaskList));
				vehicleRes.setV(getHomeVisitCount(wfTaskList));
				vehicleRes.setB(bookCount);
				resList.add(vehicleRes);
			}
		}
		return resList;

	}

	private Long getDroppedCount(List<DmsLead> dmsLeadList) {
		return dmsLeadList.stream().filter(x -> x.getLeadStage().equalsIgnoreCase(DROPPED)).count();
	}

	private Long getEnqLeadCount(List<DmsLead> dmsLeadList) {
		return dmsLeadList.stream().filter(x -> x.getLeadStage().equalsIgnoreCase(ENQUIRY)).count();
	}

	private Long getInvoiceCount(List<DmsLead> dmsLeadList) {
		return dmsLeadList.stream().filter(x -> x.getLeadStage().equalsIgnoreCase(INVOICE)).count();
	}

	private Long getHomeVisitCount(List<DmsWFTask> wfTaskList) {
		return wfTaskList.stream().filter(
				x -> x.getTaskName().equalsIgnoreCase(HOME_VISIT) && x.getTaskStatus().equalsIgnoreCase("CLOSED"))
				.count();
	}

	public Long getTestDriveCount(List<DmsWFTask> wfTaskList) {
		// TEST_DRIVE_APPROVAL
		return wfTaskList.stream().filter(
				x -> (x.getTaskName().equalsIgnoreCase(TEST_DRIVE) && x.getTaskStatus().equalsIgnoreCase("CLOSED")))
				.count();
	}

	public List<SourceRes> getReceptionistSourceData(ReceptionistDashBoardReq req, String roleName) {
		List<SourceRes> resList = new ArrayList<>();

		DmsEmployee dmsEmployeeObj = dmsEmployeeRepo.getById(req.getLoggedInEmpId());
		String loginEmpName = dmsEmployeeObj.getEmpName();
		String startDate = getStartDate(req.getStartDate());
		String endDate = getEndDate(req.getEndDate());
		String dealerCode = req.getDealerCode();

		String orgId = req.getOrgId().toString();
		List<SubSource> reslist = subSourceRepository.getAllSubsource(orgId);
		//List<String> reslist = repository.getSubSources(orgId);
		Map<String, Integer> map = new LinkedHashMap<>();
		reslist.stream().forEach(res -> {
			SourceRes leadSource = new SourceRes();
			List<DmsLead> dmsLeadList;
			/* if (StringUtils.isEmpty(dealerCode))
				dmsLeadList = dmsLeadDao.getAllEmployeeLeadsBySource(orgId, startDate, endDate, res.getId(),
						loginEmpName);
			else
				dmsLeadList = dmsLeadDao.getAllEmployeeLeadsBySource(orgId, startDate, endDate, res.getId(),
						loginEmpName, dealerCode, roleName); */
			
			if (StringUtils.isEmpty(dealerCode))
				dmsLeadList = dmsLeadDao.getAllEmployeeLeadsBySubSource(orgId, startDate, endDate, res.getSubSource(),
						loginEmpName);
			else
				dmsLeadList = dmsLeadDao.getAllEmployeeLeadsBySubSource(orgId, startDate, endDate, res.getSubSource(),
						loginEmpName, dealerCode, roleName);
			
			Long enqLeadCnt = 0L;
			Long bookCount = 0L;

			List<Integer> dmsLeadIdList = dmsLeadList.stream().map(DmsLead::getId).collect(Collectors.toList());
			log.debug("dmsLeadList::" + dmsLeadList);
			List<LeadStageRefEntity> leadRefList = leadStageRefDao.getLeadsByStageandDate(orgId, dmsLeadIdList,
					startDate, endDate);
			if (null != leadRefList && !leadRefList.isEmpty()) {
				log.debug("Total leads in LeadReF table is ::" + leadRefList.size());
				enqLeadCnt = leadRefList.stream().filter(x -> x.getStageName().equalsIgnoreCase("ENQUIRY")).count();
				bookCount = leadRefList.stream().filter(x -> x.getStageName().equalsIgnoreCase("BOOKING")).count();
			}

			Long droppedCnt = 0L;
			if (null != dmsLeadList) {
				log.info("size of dmsLeadList " + dmsLeadList.size());
				enqLeadCnt = getEnqLeadCount(dmsLeadList);
				droppedCnt = getDroppedCount(dmsLeadList);
				leadSource.setR(getInvoiceCount(dmsLeadList));

				log.info("enqLeadCnt: " + enqLeadCnt + " ,droppedCnt : " + droppedCnt);
			}
			leadSource.setSubSource(res.getSubSource());
			leadSource.setSource(res.getSource());
			leadSource.setE(enqLeadCnt);
			leadSource.setL(droppedCnt);

			List<String> leadUniversalIdList = dmsLeadList.stream().map(DmsLead::getCrmUniversalId)
					.collect(Collectors.toList());
			log.info("leadUniversalIdList " + leadUniversalIdList);

			List<DmsWFTask> wfTaskList = dmsWfTaskDao.getWfTaskByUniversalId(leadUniversalIdList, startDate, endDate);

			leadSource.setT(getTestDriveCount(wfTaskList));
			leadSource.setV(getHomeVisitCount(wfTaskList));
			leadSource.setB(bookCount);
			resList.add(leadSource);

		});

		return resList;

	}

	public List<ReceptionistLeadRes> getReceptionistLeadData(ReceptionistDashBoardReq req, String roleName) {

		DmsEmployee dmsEmployeeObj = dmsEmployeeRepo.getById(req.getLoggedInEmpId());
		String loginEmpName = dmsEmployeeObj.getEmpName();
		String startDate = getStartDate(req.getStartDate());
		String endDate = getEndDate(req.getEndDate());
		String dealerCode = req.getDealerCode();

		int orgId = req.getOrgId();

		if (StringUtils.isEmpty(req.getEmpName())) {
			return null;
		}
		String empName = req.getEmpName();

		List<ReceptionistLeadRes> result = new ArrayList();
		List<Object[]> resultList;
		if (StringUtils.isEmpty(dealerCode))
			resultList = dmsLeadDao.getAllocatedLeadsByEmp(empName, startDate, endDate, orgId, loginEmpName, roleName);
		else
			resultList = dmsLeadDao.getAllocatedLeadsByEmp(empName, startDate, endDate, orgId, dealerCode, loginEmpName,
					roleName);

		for (Object[] record : resultList) {
			result.add(new ReceptionistLeadRes((String) record[0], (String) record[1], (Date) record[2],
					(String) record[3], (String) record[4], (String) record[5], (String) record[6],
					(String) record[7]));
		}
		return result;
	}

	public List<ReceptionistLeadRes> getReceptionistDroppedLeadData(ReceptionistDashBoardReq req, String roleName) {

		DmsEmployee dmsEmployeeObj = dmsEmployeeRepo.getById(req.getLoggedInEmpId());
		String loginEmpName = dmsEmployeeObj.getEmpName();
		String startDate = getStartDate(req.getStartDate());
		String endDate = getEndDate(req.getEndDate());
		String dealerCode = req.getDealerCode();

		int orgId = req.getOrgId();

		if (StringUtils.isEmpty(req.getEmpName())) {
			return null;
		}
		String empName = req.getEmpName();

		List<ReceptionistLeadRes> result = new ArrayList();
		List<Object[]> resultList;
		if (StringUtils.isEmpty(dealerCode))
			resultList = dmsLeadDao.getDroppedLeadsByEmp(empName, startDate, endDate, orgId, loginEmpName, roleName);
		else
			resultList = dmsLeadDao.getDroppedLeadsByEmp(empName, startDate, endDate, orgId, dealerCode, loginEmpName,
					roleName);

		for (Object[] record : resultList) {
			result.add(new ReceptionistLeadRes((String) record[0], (String) record[1], (Date) record[2],
					(String) record[3], (String) record[4], (String) record[5], (String) record[6], (String) record[7],
					(Date) record[8]));
		}
		return result;
	}
	
	
	public List<ReceptionistLeadRes> getReceptionistDroppedLeadDataByStage(ReceptionistDashBoardReq req, String roleName) {

		DmsEmployee dmsEmployeeObj = dmsEmployeeRepo.getById(req.getLoggedInEmpId());
		String loginEmpName = dmsEmployeeObj.getEmpName();
		String startDate = getStartDate(req.getStartDate());
		String endDate = getEndDate(req.getEndDate());
		List<String> stage = req.getStage();
		List<String> status = req.getStatus();
		String category = req.getCategory();

		int orgId = req.getOrgId();

		List<ReceptionistLeadRes> result = new ArrayList();
		List<Object[]> resultList;

		if (!status.isEmpty() && !status.contains(""))
			resultList = dmsLeadDao.getDroppedLeadsByStageAndStatus(stage, status,  startDate, endDate, orgId, loginEmpName, roleName, category);
		else
			resultList = dmsLeadDao.getDroppedLeadsByStage(stage, startDate, endDate, orgId, loginEmpName,
					roleName, category);

		for (Object[] record : resultList) {
			result.add(new ReceptionistLeadRes((String) record[0], (String) record[1], (Date) record[2],
					(String) record[3], (String) record[4], (String) record[5], (String) record[6], (String) record[7],
					(Date) record[8], (String) record[9]));
		}
		return result;
	}

	
	public Map getDigitalManagerDahboardData(DigitalManagerDashBoardReq req
			) throws DynamicFormsServiceException {

		DmsEmployee dmsEmployeeObj = dmsEmployeeRepo.getById(req.getLoggedInEmpId());
		String loginEmpName = dmsEmployeeObj.getEmpName();

		Map map = new HashMap();
		String startDate = getStartDate(req.getStartDate());
		String endDate = getEndDate(req.getEndDate());
		int orgId = req.getOrgId();
		String dealerCode = req.getDealerCode();

		String empId = req.getLoggedInEmpId().toString();
		List<String> dmsTeamEmployeeList = dashBoardServiceV2.getReportingEmployeeNames(req.getLoggedInEmpId());
		List<Integer> dmsTeamEmployeeListInt ;
		//
		//List<Integer> empIdsUnderReporting = dashBoardUtil.getEmployeesUnderTL(empId);
		//List<Integer> empIdsUnderReporting = dashBoardUtil.getEmployeesUnderMgr(empId);
		//List<Integer> empIdsUnderReporting = dashBoardUtil.getEmployeesUnderBranchMgr(empId);
		//List<Integer> empIdsUnderReporting = dashBoardUtil.getEmployeesUnderGeneralMgr(empId);
		
		/*
		
		if(null== req.getBranchId() && null==req.getLocationId() && null==req.getBranchmangerId() && null==req.getManagerId() && null==req.getTeamLeadId() && null == req.getEmployeeId() && empId!=null) {
			log.info("General Manager PageLoad");
			dmsTeamEmployeeListInt= dashBoardUtil.getEmployeesUnderGeneralMgr(empId);
		}
		else if(null != req.getBranchId() && null==req.getLocationId() && null==req.getBranchmangerId() && null==req.getManagerId() && null==req.getTeamLeadId() && null == req.getEmployeeId() && empId!=null) {
			log.info("Only Branch ID is present");
			dmsTeamEmployeeListInt =dashBoardUtil.getEmployeesUnderBranch(branch);
		}
		else if(null != req.getBranchId() && null!=req.getLocationId() && null==req.getBranchmangerId() && null==req.getManagerId() && null==req.getTeamLeadId() && null == req.getEmployeeId() && empId!=null) {
			log.info("Only Branch ID and Location ID is present");
			dmsTeamEmployeeListInt =buildTodaysDataForLocation(tRole,req);
		}
		
		else if(null != req.getBranchId() && null!=req.getLocationId() && null!=req.getBranchmangerId() && null==req.getManagerId() && null==req.getTeamLeadId() && null == req.getEmployeeId() && empId!=null) {
			log.info("Only Branch ID,Location ID and BranchMgr ID is present");
			list=buildTodaysDataForBranchMgr(tRole,req,req.getBranchmangerId());
			}
		else if(null != req.getBranchId() && null!=req.getLocationId() && null!=req.getBranchmangerId() && null!=req.getManagerId() && null==req.getTeamLeadId() && null == req.getEmployeeId() && empId!=null) {
			log.info("Only Branch ID,Location ID ,BranchMgr ID and Manager ID is present");
			list=buildTodaysDataForMgr(tRole,req,req.getManagerId());
		}
		else if(null != req.getBranchId() && null!=req.getLocationId() && null!=req.getBranchmangerId() && null!=req.getManagerId() && null!=req.getTeamLeadId() && null == req.getEmployeeId() && empId!=null) {
			log.info("Only Branch ID,Location ID ,BranchMgr ID and Manager ID is present");
			list=buildTodaysDataForTL(tRole,req,req.getTeamLeadId());
			}
		else if(null != req.getBranchId() && null!=req.getLocationId() && null!=req.getBranchmangerId() && null!=req.getManagerId() && null!=req.getTeamLeadId() && null != req.getEmployeeId() && empId!=null) {
			log.info("Only Branch ID,Location ID ,BranchMgr ID , Manager ID and Employeed ID is present");
			list=buildTodaysDataForDSE(tRole,req,req.getEmployeeId());
		}
		
		*/
		
		//TODO select reporting employee filter wise. 
		
		// if sales comtruct in not null  then selet list based on it only.
		List<String> dmsEmployeeList = dmsEmployeeRepo.findEmpNames(orgId);

		List consultantList = new ArrayList();
		for (String dmsEmployee : dmsEmployeeList) {
			Map m = new HashMap();
			// m.put("emp_id", dmsEmployee.getEmp_id());
			m.put("emp_name", dmsEmployee);
			m.put("allocatedCount", getAllocatedLeadsCountForManagerByEmp(dmsEmployee, startDate, endDate,  req , 
					dmsTeamEmployeeList));
			m.put("droppedCount", getDropeedLeadsCountForManagerByEmp(dmsEmployee, startDate, endDate, req, 
					dmsTeamEmployeeList));
			consultantList.add(m);
		}
		map.put("consultantList", consultantList);
		map.put("totalAllocatedCount",
				getAllocatedLeadsCountForManagerByEmp(null, startDate, endDate, req, dmsTeamEmployeeList));
		map.put("totalDroppedCount",
				getDropeedLeadsCountForManagerByEmp(null, startDate, endDate, req, dmsTeamEmployeeList));
		map.put("bookingCount",
				getAllLeadsCount(startDate, endDate, "BOOKING", req, dmsTeamEmployeeList));
		map.put("RetailCount",
				getAllLeadsCount(startDate, endDate, "INVOICE", req, dmsTeamEmployeeList));

		return map;
	}
	
	Integer getAllocatedLeadsCountForManagerByEmp(String empName, String startDate, String endDate, DigitalManagerDashBoardReq req, 
			List<String> empNameList) {
		//if (StringUtils.isEmpty(dealerCode))
			return dmsLeadDao.getAllocatedLeadsCountByEmp(empName, startDate, endDate,  req.getOrgId(), empNameList);
		/*else
			return dmsLeadDao.getAllocatedLeadsCountByEmp(empName, startDate, endDate, orgId, dealerCode, loginEmpName,
					roleName); */
	}

	Integer getDropeedLeadsCountForManagerByEmp(String empName, String startDate, String endDate, DigitalManagerDashBoardReq req,
			List<String> empNameList) {
		//if (StringUtils.isEmpty(dealerCode))
			return dmsLeadDao.getDroppedLeadsCountByEmp(empName, startDate, endDate,  req.getOrgId(), empNameList);
		/*else
			return dmsLeadDao.getDropeedLeadsCountByEmp(empName, startDate, endDate, orgId, dealerCode, loginEmpName,
					roleName); */
	}
	

	Integer getAllLeadsCount(String startDate, String endDate, String leadType, DigitalManagerDashBoardReq req,
			List<String> empNameList) {
		//if (StringUtils.isEmpty(dealerCode))
			return dmsLeadDao.getAllLeadsCount(startDate, endDate, leadType,  req.getOrgId(), empNameList);
		//else
		//	return dmsLeadDao.getAllLeadsCount(startDate, endDate, leadType, orgId, dealerCode, loginEmpName, roleName);
	}
	public List getDigitalManagerDahboardTeamData(DigitalManagerDashBoardReq req
			) throws DynamicFormsServiceException {

		DmsEmployee dmsEmployeeObj = dmsEmployeeRepo.getById(req.getLoggedInEmpId());
		String loginEmpName = dmsEmployeeObj.getEmpName();

		
		String startDate = getStartDate(req.getStartDate());
		String endDate = getEndDate(req.getEndDate());
		int orgId = req.getOrgId();
		String dealerCode = req.getDealerCode();

		List<String> dmsTeamList = dashBoardServiceV2.getReportingEmployeeNames(req.getLoggedInEmpId());
		List resultList = new ArrayList();
		
		for(String empTeamName: dmsTeamList)
		{
			Map map = new HashMap();
			List<String> dmsTeamEmployeeList = new ArrayList();
			dmsTeamEmployeeList.add(empTeamName);
				
		List<String> dmsEmployeeList = dmsEmployeeRepo.findEmpNames(orgId);

		List consultantList = new ArrayList();
		for (String dmsEmployee : dmsEmployeeList) {
			Map m = new HashMap();
			// m.put("emp_id", dmsEmployee.getEmp_id());
			m.put("emp_name", dmsEmployee);
			m.put("allocatedCount", getAllocatedLeadsCountForManagerByEmp(dmsEmployee, startDate, endDate,  req , 
					dmsTeamEmployeeList));
			m.put("droppedCount", getDropeedLeadsCountForManagerByEmp(dmsEmployee, startDate, endDate, req, 
					dmsTeamEmployeeList));
			consultantList.add(m);
		}
		map.put("name", empTeamName);
		map.put("consultantList", consultantList);
		map.put("totalAllocatedCount",
				getAllocatedLeadsCountForManagerByEmp(null, startDate, endDate, req, dmsTeamEmployeeList));
		map.put("totalDroppedCount",
				getDropeedLeadsCountForManagerByEmp(null, startDate, endDate, req, dmsTeamEmployeeList));
		resultList.add(map);
		}
		return resultList;
	}
}
