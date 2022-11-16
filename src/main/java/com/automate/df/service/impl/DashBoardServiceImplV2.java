package com.automate.df.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.automate.df.constants.DynamicFormConstants;
import com.automate.df.dao.DmsDeliveryDao;
import com.automate.df.dao.DmsExchangeBuyerDao;
import com.automate.df.dao.DmsFinanceDao;
import com.automate.df.dao.DmsSourceOfEnquiryDao;
import com.automate.df.dao.EmployeeAllocation;
import com.automate.df.dao.LeadStageRefDao;
import com.automate.df.dao.SourceAndIddao;
import com.automate.df.dao.dashboard.ComplaintTrackerDao;
import com.automate.df.dao.dashboard.DmsLeadDao;
import com.automate.df.dao.dashboard.DmsLeadDropDao;
import com.automate.df.dao.dashboard.DmsWfTaskDao;
import com.automate.df.dao.dashboard.TargetAchivementModelandSource;
import com.automate.df.dao.dashboard.TargetAchivementResponseDto;
import com.automate.df.dao.oh.DmsBranchDao;
import com.automate.df.dao.oh.EmpLocationMappingDao;
import com.automate.df.dao.salesgap.DmsEmployeeRepo;
import com.automate.df.dao.salesgap.TargetSettingRepo;
import com.automate.df.dao.salesgap.TargetUserRepo;
import com.automate.df.entity.LeadStageRefEntity;
import com.automate.df.entity.SourceAndId;
import com.automate.df.entity.dashboard.ComplaintsTracker;
import com.automate.df.entity.dashboard.DmsLead;
import com.automate.df.entity.dashboard.DmsWFTask;
import com.automate.df.entity.sales.master.DmsSourceOfEnquiry;
import com.automate.df.entity.salesgap.DmsEmployee;
import com.automate.df.entity.salesgap.TargetRoleReq;
import com.automate.df.entity.sales.allocation.DmsEmployeeAllocation;
import com.automate.df.exception.DynamicFormsServiceException;
import com.automate.df.model.MyTaskReq;
import com.automate.df.model.df.dashboard.DashBoardReqImmediateHierarchyV2;
import com.automate.df.model.df.dashboard.DashBoardReqV2;
import com.automate.df.model.df.dashboard.DropRes;
import com.automate.df.model.df.dashboard.EmployeeTargetAchievement;
import com.automate.df.model.df.dashboard.EmployeeTargetAchievementModelAndView;
import com.automate.df.model.df.dashboard.EventDataRes;
import com.automate.df.model.df.dashboard.LeadSourceRes;
import com.automate.df.model.df.dashboard.LostRes;
import com.automate.df.model.df.dashboard.OverAllTargetAchivements;
import com.automate.df.model.df.dashboard.SalesDataRes;
import com.automate.df.model.df.dashboard.TargetAchivement;
import com.automate.df.model.df.dashboard.TargetRankingRes;
import com.automate.df.model.df.dashboard.TodaysRes;
import com.automate.df.model.df.dashboard.VehicleModelRes;
import com.automate.df.model.oh.EmpTask;
import com.automate.df.model.oh.MyTask;
import com.automate.df.model.oh.TodaysTaskRes;
import com.automate.df.model.salesgap.TargetDropDownV2;
import com.automate.df.model.salesgap.TargetRoleRes;
import com.automate.df.model.salesgap.TargetSettingRes;
import com.automate.df.service.DashBoardServiceV2;
import com.automate.df.service.DashBoardServiceV3;
import com.automate.df.util.DashBoardUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author srujan
 *
 */
@Slf4j
@Service
public class DashBoardServiceImplV2 implements DashBoardServiceV2{
	
	@Autowired
	Environment env;
	
	@Autowired
	DmsSourceOfEnquiryDao dmsSourceOfEnquiryDao;
	
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
	OHServiceImpl ohServiceImpl;
	
	@Autowired
	RestTemplate restTemplate;
	

	
	@Autowired
	DmsExchangeBuyerDao buyerDao;
	
	@Autowired
	DmsDeliveryDao  deliveryDao;
	
	@Autowired
	DmsFinanceDao dmsFinanceDao;

	@Autowired
	SourceAndIddao repository; 

	
	@Autowired
	ObjectMapper om;
	
	@Autowired
	LeadStageRefDao leadStageRefDao;
	
	@Autowired
	EmployeeAllocation employeeAllocation;
	
	@Value("${lead.enquiry.url}")
	String leadSourceEnqUrl;
	
	public static final String ENQUIRY = "Enquiry";
	public static final String DROPPED = "DROPPED";
	public static final String TEST_DRIVE= "Test Drive";
	public static final String TEST_DRIVE_APPROVAL= "Test Drive Approval";
	
	public static final String HOME_VISIT= "Home Visit";
	public static final String HOME_VISIT_APPROVAL= "Home Visit Approval";
	public static final String FINANCE= "Finance";
	public static final String INSURANCE= "Insurance";
	public static final String VIDEO_CONFERENCE= "Video Conference";
	public static final String PROCEED_TO_BOOKING= "Proceed to Booking";
	public static final String BOOKING_FOLLOWUP_DSE= "Booking Follow Up - DSE";
	public static final String BOOKING_FOLLOWUP_CRM= "Booking Follow Up - CRM";
	public static final String BOOKING_FOLLOWUP_ACCCESSORIES= "Booking Follow Up - Accessories";
	public static final String VERIFY_EXCHANGE_APPROVAL = "Verify Exchange Approval";
	
	
	public static final String READY_FOR_INVOICE= "Ready for invoice - Accounts";
	public static final String PROCEED_TO_INVOICE= "Proceed to Invoice";
	public static final String INVOICE_FOLLOWUP_DSE = "Invoice Follow Up - DSE";
	public static final String INVOICE = "INVOICE";
	public static final String EXTENDED_WARRANTY = "EXTENDEDWARRANTY";
	
	public static final String PRE_BOOKING = "Pre Booking";
	public static final String DELIVERY = "Delivery";
	
	public static final String BOOKING = "Booking";
	public static final String EXCHANGE = "Exchange";
	public static final String ACCCESSORIES = "Accessories";
	public static final String EVENTS = "Events";
	
	
	public static final String preenqCompStatus = "PREENQUIRYCOMPLETED";
	public static final String enqCompStatus = "ENQUIRYCOMPLETED";
	public static final String preBookCompStatus = "PREBOOKINGCOMPLETED";
	public static final String bookCompStatus = "BOOKINGCOMPLETED";
	public static final String invCompStatus = "INVOICECOMPLETED";
	public static final String preDelCompStatus = "PREDELIVERYCOMPLETED";
	public static final String delCompStatus="DELIVERYCOMPLETED";

	private static final String RETAIL_TARGET = "RETAIL_TARGET";
	
	String roleMapQuery = "SELECT rolemap.organization_id,rolemap.branch_id,rolemap.emp_id,role.role_name,role.role_id FROM dms_role role INNER JOIN dms_employee_role_mapping rolemap ON rolemap.role_id=role.role_id\r\n"
			+ "AND rolemap.emp_id=<EMP_ID>;";
	String dmsEmpByidQuery =  "SELECT * FROM dms_employee where emp_id=<EMP_ID>";
	String getEmpUnderTLQuery = "SELECT emp_id FROM dms_employee where reporting_to=<ID>";

	private String EXTENDEDWARRANTY;

	@Override
	public List<TargetAchivement> getTargetAchivementParams(DashBoardReqV2 req) throws DynamicFormsServiceException {
		log.info("Inside getTargetAchivementParams(){}");
		List<TargetAchivement> resList = new ArrayList<>();
		try {
			long startTime = System.currentTimeMillis();
			List<List<TargetAchivement>> allTargets = new ArrayList<>();
			Integer empId = req.getLoggedInEmpId();
			log.debug("Getting Target Data, LoggedIn emp id " + empId);

			List<Integer> selectedEmpIdList = req.getEmpSelected();
			List<Integer> selectedNodeList = req.getLevelSelected();
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(empId);
			String orgId = tRole.getOrgId();
			log.debug("tRole getTargetAchivementParams " + tRole);
			if (null != selectedEmpIdList && !selectedEmpIdList.isEmpty()) {
				log.debug("Fetching empReportingIdList for selected employees,selectedEmpIdList" + selectedEmpIdList);

				for (Integer eId : selectedEmpIdList) {
					List<Integer> empReportingIdList = new ArrayList<>();
					empReportingIdList.add(eId);
					//empIdList = getEmployeeHiearachyData(orgId,req.getLoggedInEmpId());
					empReportingIdList.addAll(getEmployeeHiearachyData(Integer.parseInt(orgId),eId));
					log.info("&&&&&&&&&&&&&&&&&&empReportingIdList for given selectedEmpIdList " + empReportingIdList);
					List<TargetAchivement> targetList = getTargetAchivementParamsForMultipleEmp(empReportingIdList, req,
							orgId);
					log.debug("targetList::::::" + targetList);
					allTargets.add(targetList);
				}

				resList = buildFinalTargets(allTargets);
			} else if (null != selectedNodeList && !selectedNodeList.isEmpty()) {
				log.debug("Fetching empReportingIdList for selected LEVEL NODES");

				for (Integer node : selectedNodeList) {
					List<Integer> empReportingIdList = new ArrayList<>();
					empReportingIdList.add(req.getLoggedInEmpId());
					List<Integer> nodeList = new ArrayList<>();
					nodeList.add(node);

					Map<String, Object> datamap = ohServiceImpl.getActiveDropdownsV2(nodeList,
							Integer.parseInt(tRole.getOrgId()), empId);
					datamap.forEach((k, v) -> {
						Map<String, Object> innerMap = (Map<String, Object>) v;
						innerMap.forEach((x, y) -> {
							List<TargetDropDownV2> dd = (List<TargetDropDownV2>) y;
							empReportingIdList.addAll(dd.stream().map(z -> z.getCode()).map(Integer::parseInt)
									.collect(Collectors.toList()));
						});
					});
					List<TargetAchivement> targetList = getTargetAchivementParamsForMultipleEmp(empReportingIdList, req,
							orgId);
					allTargets.add(targetList);

				}
				resList = buildFinalTargets(allTargets);
			} else {
				log.debug("Fetching empReportingIdList for logged in emp in else :" + req.getLoggedInEmpId());
				List<Integer> empReportingIdList = getEmployeeHiearachyData(Integer.parseInt(orgId),req.getLoggedInEmpId());
				empReportingIdList.add(req.getLoggedInEmpId());
				log.debug("empReportingIdList for emp " + req.getLoggedInEmpId());
				log.debug("Calling getTargetAchivemetns in else" + empReportingIdList);
				resList = getTargetAchivementParamsForMultipleEmp(empReportingIdList, req, orgId);
			}
			log.debug("Total time taken for getTargetparams "+(System.currentTimeMillis()-startTime));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resList;
	}
	
	@Override
	public OverAllTargetAchivements getTargetAchivementParamsWithEmps(DashBoardReqV2 req) throws DynamicFormsServiceException {
		log.info("Inside getTargetAchivementParams(){}");
		OverAllTargetAchivements overAllTargetAchivements = new OverAllTargetAchivements();
		List<EmployeeTargetAchievement> empTargetAchievements = new ArrayList<>();
		List<EmployeeTargetAchievement> finalEmpTargetAchievements = new ArrayList<>();
		long startTime1 = System.currentTimeMillis();
		List<TargetAchivement> resList = new ArrayList<>();
		try {
			List<List<TargetAchivement>> allTargets = new ArrayList<>();
			Integer empId = req.getLoggedInEmpId();
			log.debug("Getting Target Data, LoggedIn emp id "+empId );
			String startDate = null;
			String endDate = null;
			if (null == req.getStartDate() && null == req.getEndDate()) {
				startDate = getFirstDayOfMonth();
				endDate = getLastDayOfMonth();
			} else {
				startDate = req.getStartDate()+" 00:00:00";
				endDate = req.getEndDate()+" 23:59:59";
			}

			List<Integer> selectedEmpIdList = req.getEmpSelected();
			List<Integer> selectedNodeList = req.getLevelSelected();
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(empId);
			String orgId = tRole.getOrgId();
			log.debug("tRole getTargetAchivementParams "+tRole);
			if (null != selectedEmpIdList && !selectedEmpIdList.isEmpty()) {
				log.debug("Fetching empReportingIdList for selected employees,selectedEmpIdList" + selectedEmpIdList);
				for (Integer eId : selectedEmpIdList) {
					List<Integer> empReportingIdList = new ArrayList<>();
					empReportingIdList.add(eId);
					empReportingIdList.addAll(getEmployeeHiearachyData(Integer.parseInt(orgId),eId));
					log.debug("empReportingIdList for given selectedEmpIdList " + empReportingIdList);
					List<TargetAchivement> targetList = getTargetAchivementParamsForMultipleEmpAndEmps(
							empReportingIdList, req, orgId, empTargetAchievements, startDate, endDate);
					log.debug("targetList::::::" + targetList);
					allTargets.add(targetList);
				}

				resList = buildFinalTargets(allTargets);
			}
			else if(null!=selectedNodeList && !selectedNodeList.isEmpty()) {
				log.debug("Fetching empReportingIdList for selected LEVEL NODES");
				
				for (Integer node : selectedNodeList) {
					List<Integer> empReportingIdList = new ArrayList<>();
					empReportingIdList.add(req.getLoggedInEmpId());
					List<Integer> nodeList = new ArrayList<>();
					nodeList.add(node);

					Map<String, Object> datamap = ohServiceImpl.getActiveDropdownsV2(nodeList,
							Integer.parseInt(tRole.getOrgId()), empId);
					datamap.forEach((k, v) -> {
						Map<String, Object> innerMap = (Map<String, Object>) v;
						innerMap.forEach((x, y) -> {
							List<TargetDropDownV2> dd = (List<TargetDropDownV2>) y;
							empReportingIdList.addAll(dd.stream().map(z -> z.getCode()).map(Integer::parseInt)
									.collect(Collectors.toList()));
						});
					});
					List<TargetAchivement> targetList = getTargetAchivementParamsForMultipleEmpAndEmps(empReportingIdList,req,orgId,empTargetAchievements,startDate,endDate);
					allTargets.add(targetList);
					
				}
				resList = buildFinalTargets(allTargets);
			}
			else {
				log.debug("Fetching empReportingIdList for logged in emp in else :"+req.getLoggedInEmpId());
				List<Integer> empReportingIdList =  getEmployeeHiearachyData(Integer.parseInt(orgId),req.getLoggedInEmpId());
				empReportingIdList.add(req.getLoggedInEmpId());
				//resList = process(empReportingIdList,req,orgId,empTargetAchievements,startDate,endDate);
				
				
				//log.debug("empReportingIdList for emp "+req.getLoggedInEmpId());
				//log.debug("Calling getTargetAchivemetns in else" +empReportingIdList);
				resList = getTargetAchivementParamsForMultipleEmpAndEmps(empReportingIdList,req,orgId,empTargetAchievements,startDate,endDate);
			}
			//log.debug("Fetching empReportingIdList for logged in emp outside:"+req.getLoggedInEmpId());
			log.info("Time taken in get all target - Step1:::"+(System.currentTimeMillis()-startTime1));
			long startTime = System.currentTimeMillis();
			final List<TargetAchivement> resListFinal = resList;
			final String startDt = startDate;
			final String endDt = endDate;
			int empId1 = req.getLoggedInEmpId();
			if(empTargetAchievements.size()>1) {
			List<List<EmployeeTargetAchievement>> targetAchiPartList = partitionListEmpTarget(empTargetAchievements);
			ExecutorService executor = Executors.newFixedThreadPool(targetAchiPartList.size());
			
			List<CompletableFuture<List<EmployeeTargetAchievement>>> futureList  = targetAchiPartList.stream()
					.map(strings -> CompletableFuture.supplyAsync(() -> processEmployeeTargetAchiveList(strings,resListFinal,startDt,endDt,empId1), executor))
					.collect(Collectors.toList());
			
			if (null != futureList) {
				CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
				Stream<List<EmployeeTargetAchievement>> dataStream = (Stream<List<EmployeeTargetAchievement>>) futureList.stream().map(CompletableFuture::join);
				dataStream.forEach(x -> {
					finalEmpTargetAchievements.addAll(x);
				});

			}
		
			
			}
			
			else if(empTargetAchievements.size()==1){
				empTargetAchievements.get(0).setTargetAchievements(resList);
				finalEmpTargetAchievements.addAll(empTargetAchievements);
			}
					
			
			/*if(empTargetAchievements.size()>1) {
			
			empTargetAchievements.stream().forEach(employeeTarget->{
				List<TargetAchivement> responseList = new ArrayList();
				employeeTarget.setTargetAchievements(getTaskAndBuildTargetAchievements(Arrays.asList(employeeTarget.getEmpId()), employeeTarget.getOrgId(), responseList, Arrays.asList(employeeTarget.getEmpName()), startDt,endDt, employeeTarget.getTargetAchievementsMap()));
			});
			}else if(empTargetAchievements.size()==1){
				empTargetAchievements.get(0).setTargetAchievements(resList);
			}
		*/
			log.info("Time taken in get all target - Step2::::"+(System.currentTimeMillis()-startTime));
			log.debug("Time taken in get all target - Step2::::"+(System.currentTimeMillis()-startTime));
		}catch(Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		overAllTargetAchivements.setOverallTargetAchivements(resList);
		//overAllTargetAchivements.setEmployeeTargetAchievements(empTargetAchievements);
		overAllTargetAchivements.setEmployeeTargetAchievements(finalEmpTargetAchievements);
		return overAllTargetAchivements;
	}
	
	
	
	public List<EmployeeTargetAchievement> processEmployeeTargetAchiveList(List<EmployeeTargetAchievement> empTargetAchievements,
			List<TargetAchivement> resList, String startDt, String endDt, int empId1) {
		List<EmployeeTargetAchievement> res = new ArrayList<>();
		try {
			
				empTargetAchievements.stream().forEach(employeeTarget->{
					List<TargetAchivement> responseList = new ArrayList();
					employeeTarget.setTargetAchievements(getTaskAndBuildTargetAchievements(Arrays.asList(employeeTarget.getEmpId()), employeeTarget.getOrgId(), responseList, Arrays.asList(employeeTarget.getEmpName()), startDt,endDt, employeeTarget.getTargetAchievementsMap(), empId1));
					res.add(employeeTarget);
				});
				
			
		}catch(Exception e) {
			e.printStackTrace();
			log.error("Exception ",e);
			
		}
		return res;
	}

	private List<TargetAchivement> process(List<Integer> empReportingIdList, DashBoardReqV2 req, String orgId,
			List<EmployeeTargetAchievement> empTargetAchievements, String startDate, String endDate) {
		List<TargetAchivement> finalList = new ArrayList<>();
		try {
			List<List<Integer>> empIdPartionList = partitionList(empReportingIdList);
			log.debug("empIdPartionList ::" + empIdPartionList.size());
			ExecutorService executor = Executors.newFixedThreadPool(empIdPartionList.size());
			
			List<CompletableFuture<List<TargetAchivement>>> futureList  = empIdPartionList.stream()
					.map(strings -> CompletableFuture.supplyAsync(() -> {
						List<TargetAchivement> list = new ArrayList<>();
						try {
							list= getTargetAchivementParamsForMultipleEmpAndEmps(strings,req,orgId,empTargetAchievements,startDate,endDate);
						} catch (ParseException | DynamicFormsServiceException e) {
							log.error("exception ",e);
							e.printStackTrace();
						}
						return list;
					}, executor))
					.collect(Collectors.toList());
			
			if (null != futureList) {
				log.debug("futureList size "+futureList.size());
				CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
				Stream<List<TargetAchivement>> dataStream = (Stream<List<TargetAchivement>>) futureList.stream().map(CompletableFuture::join);
				dataStream.forEach(x -> {
					finalList.addAll(x);
				});
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			log.error("Exception ",e);
		}
		return finalList;
	}

	@Override
	public List<TargetAchivement> getTargetAchivementParamsForSingleEmp(DashBoardReqV2 req) throws DynamicFormsServiceException {
		log.info("Inside getTargetAchivementParams(){}");
		List<TargetAchivement> resList = new ArrayList<>();
		try {
			List<List<TargetAchivement>> allTargets = new ArrayList<>();
			Integer empId = req.getLoggedInEmpId();
			log.debug("Getting Target Data, LoggedIn emp id "+empId );
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(empId);
			String orgId = tRole.getOrgId();
			log.debug("Fetching empReportingIdList for logged in emp in else :"+req.getLoggedInEmpId());
			log.debug("empReportingIdList for emp "+req.getLoggedInEmpId());
			resList = getTargetAchivementParamsForEmp(req.getLoggedInEmpId(),req,orgId);
			}catch(Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resList;
	}
/////Immediate Hierarchy
	@Override
	public List<TargetAchivement> getTargetAchivementParamsForSingleEmpImmediateHierarchy(DashBoardReqImmediateHierarchyV2 req) throws DynamicFormsServiceException {
		log.info("Inside getTargetAchivementParams(){}");
		List<TargetAchivement> resList = new ArrayList<>();
		 List<Integer> loggedInEmpId =req.getLoggedInEmpId();
		try {
			List<List<TargetAchivement>> allTargets = new ArrayList<>();
			List<Integer> empId = req.getLoggedInEmpId();
			log.debug("Getting Target Data, LoggedIn emp id "+empId );
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2ImmediateHirarchy(empId);
			String orgId = tRole.getOrgId();
			log.debug("Fetching empReportingIdList for logged in emp in else :"+req.getLoggedInEmpId());
			log.debug("empReportingIdList for emp "+req.getLoggedInEmpId());
			resList = getTargetAchivementParamsForEmpImmediateHierarchy(loggedInEmpId,req,orgId);
			}catch(Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resList;
	}
	
/*	@Override
	public List<TargetRankingRes> getEmployeeTargetRankingByOrg(Integer orgId,DashBoardReqV2 req) throws DynamicFormsServiceException {
		Integer empId = req.getLoggedInEmpId();
		Integer roleId = dmsEmployeeRepo.getEmpHrmsRole(empId);
		return getEmployeeTargetRanking(dmsEmployeeRepo.findAllByOrgId(orgId,roleId),req);
	}
	
	@Override
	public List<TargetRankingRes> getEmployeeTargetRankingByOrgAndBranch(Integer orgId,Integer branchId,DashBoardReqV2 req) throws DynamicFormsServiceException {
		Integer empId = req.getLoggedInEmpId();
		Integer roleId = dmsEmployeeRepo.getEmpHrmsRole(empId);
		return getEmployeeTargetRanking(dmsEmployeeRepo.getEmployeesByOrgBranch(orgId,branchId),req);
	}
	
*/	
	@Override
	public List<TargetRankingRes> getEmployeeTargetRankingByOrg(Integer orgId,DashBoardReqV2 req) throws DynamicFormsServiceException {
		Integer empId = req.getLoggedInEmpId();
		Integer roleId = dmsEmployeeRepo.getEmpHrmsRole(empId);
		return getEmployeeTargetRanking(dmsEmployeeRepo.findAllByOrgId(orgId,roleId),req);
	}
	
	@Override
	public List<TargetRankingRes> getEmployeeTargetRankingByOrgAndBranch(Integer orgId,Integer branchId,DashBoardReqV2 req) throws DynamicFormsServiceException {
		Integer empId = req.getLoggedInEmpId();
		Integer roleId = dmsEmployeeRepo.getEmpHrmsRole(empId);
		return getEmployeeTargetRanking(dmsEmployeeRepo.getEmployeesByOrgBranch(orgId,branchId,roleId),req);
	}

	
	private List<TargetRankingRes> getEmployeeTargetRanking(List<DmsEmployee> empList,DashBoardReqV2 req) throws DynamicFormsServiceException {
		// TODO Auto-generated method stub
		List<TargetRankingRes> targetRankingList = new ArrayList<>();
		Set<Integer> targetAchievementPercentSet = new HashSet<>();
		empList.stream().forEach(employee->{
		try {
			TargetRankingRes targetRankingResponse = new TargetRankingRes();
			req.setLoggedInEmpId(employee.getEmp_id());
			List<TargetAchivement> retailAchivementList = getTargetAchivementParamsForEmp(employee.getEmp_id(), req, employee.getOrg()).stream().filter(x->x.getParamName().equalsIgnoreCase(INVOICE)).collect(Collectors.toList());
			if(retailAchivementList.size()>0) {
				TargetAchivement invoiceTarrgetAchievement = retailAchivementList.get(0);
				targetRankingResponse.setAchivementPerc(Double.parseDouble(invoiceTarrgetAchievement.getAchivementPerc().replace("%", "")));
				targetRankingResponse.setTargetAchivements(Integer.parseInt(invoiceTarrgetAchievement.getAchievment()));
			
			}
			
			targetRankingResponse.setEmpId(employee.getEmp_id());
			targetRankingResponse.setEmpName(employee.getEmpName());
			targetRankingResponse.setOrgId(Integer.parseInt(employee.getOrg()));
			targetRankingResponse.setBranchId(Integer.parseInt(employee.getBranch()));
			targetRankingResponse.setBranchName(getBranchName(Integer.parseInt(employee.getBranch())));
			targetRankingResponse.setBranchCode(getBranchCode(Integer.parseInt(employee.getBranch())));
			targetRankingList.add(targetRankingResponse);
			//targetAchievementPercentSet.add(targetRankingResponse.getAchivementPerc());
			targetAchievementPercentSet.add(targetRankingResponse.getTargetAchivements());			
		} catch (ParseException | DynamicFormsServiceException e) {
			// TODO Auto-generated catch block
			log.error("Exception ",e);
			e.printStackTrace();
		}
		});
		
		

		
		
		List<Integer> targetAchievementPercentList = targetAchievementPercentSet.stream().collect(Collectors.toList());
		Collections.sort(targetAchievementPercentList,Collections.reverseOrder());
		AtomicInteger rank= new AtomicInteger(0);
		targetAchievementPercentList.stream().forEach(targetAchivementPercent->{
			rank.set(rank.addAndGet(1));
			List<TargetRankingRes> filteredList = targetRankingList.stream().filter(z->z.getTargetAchivements().equals(targetAchivementPercent)).collect(Collectors.toList());
			filteredList.stream().forEach(y->{
				y.setRank(rank.get());
			});
		});
		
		
		Collections.sort(targetRankingList,(o1,o2)->{
			return o2.getTargetAchivements()-o1.getTargetAchivements();
		});
		
		return targetRankingList;
	}

	
	private String getBranchName(int branchId) {
		log.info("Inside getBranchName,Given Branch ID : " + branchId);
		String res = null;
		String deptQuery = "SELECT name FROM dms_branch where branch_id=<ID>;";
		try {

			Object obj = entityManager.createNativeQuery(deptQuery.replaceAll("<ID>", String.valueOf(branchId)))
					.getSingleResult();
			res = (String) obj;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private String getBranchCode(int branchId) {
		log.info("Inside getBranchCode,Given Branch ID : " + branchId);
		String res = null;
		String deptQuery = "SELECT dealer_code FROM dms_branch where branch_id=<ID>;";
		try {

			Object obj = entityManager.createNativeQuery(deptQuery.replaceAll("<ID>", String.valueOf(branchId)))
					.getSingleResult();
			res = (String) obj;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	//@CachePut (value = "empDataCache",key="#empId")
	public List<Integer> getReportingEmployes(Integer empId) throws DynamicFormsServiceException {
		List<String> empReportingIdList = new ArrayList<>();
		log.debug("getReportingEmployes , Empid "+empId);
		List<Integer> empReportingIdList_1 = new ArrayList<>();
		Optional<DmsEmployee> empOpt = dmsEmployeeRepo.findEmpById(empId);
		if(empOpt.isPresent()) {
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV3(empId);
			log.debug("tRole::"+tRole);
			log.debug("tRole.getOrgMapBranches():::"+tRole.getOrgMapBranches());
			
			for(String orgMapBranchId : tRole.getOrgMapBranches()) {
				//Map<String, Object> hMap = ohServiceImpl.getReportingHierarchy(empOpt.get(),Integer.parseInt(orgMapBranchId),Integer.parseInt((tRole.getOrgId())));
				Map<String, Object> hMap = ohServiceImpl.getReportingHierarchyV2(empOpt.get(),Integer.parseInt(orgMapBranchId),Integer.parseInt((tRole.getOrgId())));
				
				
				if(null!=hMap) {
				for(Map.Entry<String, Object> mapentry : hMap.entrySet()) {
					Map<String, Object> map2 = (Map<String, Object>)mapentry.getValue();
					for(Map.Entry<String, Object> mapentry_1 :map2.entrySet()) {
						List<TargetDropDownV2> ddList = (List<TargetDropDownV2>)mapentry_1.getValue();
						empReportingIdList.addAll(ddList.stream().map(x->x.getCode()).collect(Collectors.toList()));
					}
				}
			}
			}
			Set<String> s = new HashSet<>();
			s.addAll(empReportingIdList);
			empReportingIdList = new ArrayList<>(s);
			
			
		}else {
			throw new DynamicFormsServiceException("Logged in emp is not valid employee,no record found in dms_employee", HttpStatus.BAD_REQUEST);
		}
		empReportingIdList_1 = empReportingIdList.stream().map(Integer::parseInt).collect(Collectors.toList());
		
		/*HashOperations hashOperations = redisTemplate.opsForHash();
		hashOperations.put("EMPHIERARCHY", empReportingIdList_1, empId);
		redisTemplate.expire("EMPHIERARCHY", 10,TimeUnit.MINUTES);
		List<String> empReportingIdListCache = (List<String>)hashOperations.get("EMPHIERARCHY", empId);
		log.debug("empReportingIdListCache::"+empReportingIdListCache);*/
		
		return empReportingIdList_1;
	}
	
	private List<Integer> getReportingEmployesBranch(Integer empId, String branchId)
			throws DynamicFormsServiceException {
		List<String> empReportingIdList = new ArrayList<>();
		List<Integer> empReportingIdList_1 = new ArrayList<>();
		Optional<DmsEmployee> empOpt = dmsEmployeeRepo.findEmpById(empId);
		if (empOpt.isPresent()) {
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(empId);
			////System.out.println("tRole.getBranchId() ::" + tRole.getBranchId() + ",branchId:" + branchId);
			if (tRole.getBranchId().equals(branchId)) {
				Map<String, Object> hMap = ohServiceImpl.getReportingHierarchy(empOpt.get(),
						Integer.parseInt(tRole.getBranchId()), Integer.parseInt((tRole.getOrgId())));
				if (null != hMap) {
					for (Map.Entry<String, Object> mapentry : hMap.entrySet()) {
						Map<String, Object> map2 = (Map<String, Object>) mapentry.getValue();
						for (Map.Entry<String, Object> mapentry_1 : map2.entrySet()) {
							List<TargetDropDownV2> ddList = (List<TargetDropDownV2>) mapentry_1.getValue();
							empReportingIdList.addAll(ddList.stream().map(x -> x.getCode()).collect(Collectors.toList()));
						}
					}
				}
			}
		} else {
			throw new DynamicFormsServiceException(
					"Logged in emp is not valid employee,no record found in dms_employee", HttpStatus.BAD_REQUEST);
		}
		empReportingIdList_1 = empReportingIdList.stream().map(Integer::parseInt).collect(Collectors.toList());
		return empReportingIdList_1;
	}



	
	public List<TargetAchivement> getTargetAchivementParamsForMultipleEmp(
			List<Integer> empIdsUnderReporting, DashBoardReqV2 req,String orgId) throws ParseException, DynamicFormsServiceException {
		List<TargetAchivement> resList = new ArrayList<>();
		List<String> empNamesList = dmsEmployeeRepo.findEmpNamesById(empIdsUnderReporting);
		int empId = req.getLoggedInEmpId();
		log.info("$$$$$$$$$$$$$$$$empNamesList::" + empNamesList);
		log.debug("Calling getTargetAchivementParamsForMultipleEmp");
		final String startDate;
		final String endDate;
		if (null == req.getStartDate() && null == req.getEndDate()) {
			startDate = getFirstDayOfMonth();
			endDate = getLastDayOfMonth();
		} else {
			startDate = req.getStartDate()+" 00:00:00";
			endDate = req.getEndDate()+" 23:59:59";
		}

		
		log.info("StartDate " + startDate + ", EndDate " + endDate);
		Map<String, Integer> map = new ConcurrentHashMap<>();
		Map<String, Integer> finalMap = new ConcurrentHashMap<>();
		
		if(empIdsUnderReporting.size()>0) {
			List<List<Integer>> empIdPartionList = partitionList(empIdsUnderReporting);
			log.info("$$$$$$$$$$$$$$$$$$$$$$$$empIdPartionList ::" + empIdPartionList.size());
			ExecutorService executor = Executors.newFixedThreadPool(empIdPartionList.size());
			
			List<CompletableFuture<Map<String, Integer>>> futureList = empIdPartionList.stream()
					.map(strings -> CompletableFuture.supplyAsync(() -> processTargetMap(strings,map,startDate,endDate), executor))
					.collect(Collectors.toList());
			
			CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
			Stream<Map<String, Integer>> dataStream = (Stream<Map<String, Integer>>) futureList.stream().map(CompletableFuture::join);
			dataStream.forEach(x -> {
				finalMap.putAll(map);
			});
		}
		
		/*
		for(Integer empId : empIdsUnderReporting) {
			log.debug("Getting target params for user "+empId);
			Map<String, Integer> innerMap = getTargetParams(String.valueOf(empId), startDate, endDate);
			log.debug("innerMap::"+innerMap);
			map = validateAndUpdateMapData(ENQUIRY,innerMap,map);
			map = validateAndUpdateMapData(TEST_DRIVE,innerMap,map);
			map = validateAndUpdateMapData(HOME_VISIT,innerMap,map);
			map = validateAndUpdateMapData(VIDEO_CONFERENCE,innerMap,map);
			map = validateAndUpdateMapData(BOOKING,innerMap,map);
			map = validateAndUpdateMapData(EXCHANGE,innerMap,map);
			map = validateAndUpdateMapData(FINANCE,innerMap,map);
			map = validateAndUpdateMapData(INSURANCE,innerMap,map);
			map = validateAndUpdateMapData(ACCCESSORIES,innerMap,map);
			map = validateAndUpdateMapData(EVENTS,innerMap,map);
			map = validateAndUpdateMapData(INVOICE,innerMap,map);
			
		}
		*/
		//List<DmsLead> dmsLeadList = dmsLeadDao.getAllEmployeeLeads(empNamesList, startDate, endDate, ENQUIRY);
	
		
		
		return getTaskAndBuildTargetAchievements(empIdsUnderReporting, orgId, resList, empNamesList, startDate, endDate,map,empId);
	}

	private Map<String, Integer> processTargetMap(List<Integer> empIdsUnderReporting,Map<String, Integer> map, String startDate,String endDate) {
		try {
		for(Integer empId : empIdsUnderReporting) {
			log.debug("Getting target params for user "+empId);
			Map<String, Integer> innerMap = getTargetParams(String.valueOf(empId), startDate, endDate);
			
			map = validateAndUpdateMapData(ENQUIRY,innerMap,map);
			map = validateAndUpdateMapData(TEST_DRIVE,innerMap,map);
			map = validateAndUpdateMapData(HOME_VISIT,innerMap,map);
			map = validateAndUpdateMapData(VIDEO_CONFERENCE,innerMap,map);
			map = validateAndUpdateMapData(BOOKING,innerMap,map);
			map = validateAndUpdateMapData(EXCHANGE,innerMap,map);
			map = validateAndUpdateMapData(FINANCE,innerMap,map);
			map = validateAndUpdateMapData(INSURANCE,innerMap,map);
			map = validateAndUpdateMapData(ACCCESSORIES,innerMap,map);
			map = validateAndUpdateMapData(EVENTS,innerMap,map);
			map = validateAndUpdateMapData(INVOICE,innerMap,map);
			map = validateAndUpdateMapData(EXTENDED_WARRANTY,innerMap,map);
			
		}
		}catch(Exception e) {
			e.printStackTrace();
			log.error("Exception in processTargetMap ",e);
		}
		return map;
	}

	public List<TargetAchivement> getTargetAchivementParamsForMultipleEmpAndEmps(
			List<Integer> empIdsUnderReporting, DashBoardReqV2 req,String orgId,List<EmployeeTargetAchievement> empTargetAchievements,String startDate,String endDate) throws ParseException, DynamicFormsServiceException {
		log.debug("Calling getTargetAchivementParamsForMultipleEmp");

		List<TargetAchivement> resList = new ArrayList<>();
		List<DmsEmployee> employees = dmsEmployeeRepo.findAllById(empIdsUnderReporting);
		List<String> empNamesList = employees.stream().map(x->x.getEmpName()).collect(Collectors.toList());
		
		int empId = req.getLoggedInEmpId();
		Map<String, Integer> map = new LinkedHashMap<>();
				
		List<List<DmsEmployee>> empIdPartionList = partitionListEmp(employees);
		log.debug("empIdPartionList ::" + empIdPartionList.size());
		ExecutorService executor = Executors.newFixedThreadPool(empIdPartionList.size());
		
		List<CompletableFuture<List<EmployeeTargetAchievement>>> futureList = empIdPartionList.stream()
				.map(strings -> CompletableFuture.supplyAsync(() -> processTargetAchivementFormMultipleEmp(strings,map,startDate, endDate), executor))
				.collect(Collectors.toList());
		if (null != futureList) {
			CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
			Stream<List<EmployeeTargetAchievement>> dataStream = (Stream<List<EmployeeTargetAchievement>>) futureList.stream().map(CompletableFuture::join);
			dataStream.forEach(x -> {
				empTargetAchievements.addAll(x);
			});

		}
	
		/*for(DmsEmployee employee : employees) {
			EmployeeTargetAchievement employeeTargetAchievement = new EmployeeTargetAchievement();
			log.debug("Getting target params for user "+employee.getEmp_id());
			Map<String, Integer> innerMap = getTargetParams(String.valueOf(employee.getEmp_id()), startDate, endDate);
			log.debug("innerMap::"+innerMap);
			employeeTargetAchievement.setEmpId(employee.getEmp_id());
			employeeTargetAchievement.setEmpName(employee.getEmpName());
			employeeTargetAchievement.setBranchId(employee.getBranch());
			employeeTargetAchievement.setOrgId(employee.getOrg());
			employeeTargetAchievement.setTargetAchievementsMap(innerMap);
			empTargetAchievements.add(employeeTargetAchievement);
			/*map = validateAndUpdateMapData(ENQUIRY,innerMap,map);
			map = validateAndUpdateMapData(TEST_DRIVE,innerMap,map);
			map = validateAndUpdateMapData(HOME_VISIT,innerMap,map);
			map = validateAndUpdateMapData(VIDEO_CONFERENCE,innerMap,map);
			map = validateAndUpdateMapData(BOOKING,innerMap,map);
			map = validateAndUpdateMapData(EXCHANGE,innerMap,map);
			map = validateAndUpdateMapData(FINANCE,innerMap,map);
			map = validateAndUpdateMapData(INSURANCE,innerMap,map);
			map = validateAndUpdateMapData(ACCCESSORIES,innerMap,map);
			map = validateAndUpdateMapData(EVENTS,innerMap,map);
			map = validateAndUpdateMapData(INVOICE,innerMap,map);
		}*/
		return getTaskAndBuildTargetAchievements(empIdsUnderReporting, orgId, resList, empNamesList, startDate, endDate,map,empId);
	}

	private List<EmployeeTargetAchievement>  processTargetAchivementFormMultipleEmp(List<DmsEmployee> employees,Map<String, Integer> map,
			 String startDate, String endDate) {
		List<EmployeeTargetAchievement> empTargetAchievements = new ArrayList<>();
		try {
			for (DmsEmployee employee : employees) {
				EmployeeTargetAchievement employeeTargetAchievement = new EmployeeTargetAchievement();
				log.debug("Getting target params for user " + employee.getEmp_id());
				Map<String, Integer> innerMap = getTargetParams(String.valueOf(employee.getEmp_id()), startDate,
						endDate);
				log.debug("innerMap::" + innerMap);
				employeeTargetAchievement.setEmpId(employee.getEmp_id());
				employeeTargetAchievement.setEmpName(employee.getEmpName());
				employeeTargetAchievement.setBranchId(employee.getBranch());
				employeeTargetAchievement.setOrgId(employee.getOrg());
				employeeTargetAchievement.setTargetAchievementsMap(innerMap);
				empTargetAchievements.add(employeeTargetAchievement);
				
				map = validateAndUpdateMapData(ENQUIRY,innerMap,map);
				map = validateAndUpdateMapData(TEST_DRIVE,innerMap,map);
				map = validateAndUpdateMapData(HOME_VISIT,innerMap,map);
				map = validateAndUpdateMapData(VIDEO_CONFERENCE,innerMap,map);
				map = validateAndUpdateMapData(BOOKING,innerMap,map);
				map = validateAndUpdateMapData(EXCHANGE,innerMap,map);
				map = validateAndUpdateMapData(FINANCE,innerMap,map);
				map = validateAndUpdateMapData(INSURANCE,innerMap,map);
				map = validateAndUpdateMapData(ACCCESSORIES,innerMap,map);
				map = validateAndUpdateMapData(EVENTS,innerMap,map);
				map = validateAndUpdateMapData(INVOICE,innerMap,map);
				map = validateAndUpdateMapData(EXTENDED_WARRANTY,innerMap,map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in processTargetAchivementFormMultipleEmp ", e);
		}
		return empTargetAchievements;
	}

	private List<TargetAchivement> getTaskAndBuildTargetAchievements(List<Integer> empIdsUnderReporting, String orgId,
			List<TargetAchivement> resList, List<String> empNamesList, String startDate, String endDate,
			Map<String, Integer> map, int empId) {
		Long dropLeadCnt = 0L;
		Long enqLeadCnt = 0L;
		Long preBookCount = 0L;
		Long bookCount = 0L;
		Long invCount = 0L;
		Long preDeliveryCnt = 0L;
		Long delCnt = 0L;
		List<DmsEmployeeAllocation> dmsEmployeeAllocations = employeeAllocation.findByEmployeeId(empId);
		
		
		
		List<Integer> dmsLeadList = dmsLeadDao.getLeadIdsByEmpNamesWithOutDrop(empNamesList);
        //System.out.println("dmsLeadList Before Adding"+dmsLeadList.size());
        //System.out.println("empNamesList"+empNamesList.toString());
		dmsLeadList.addAll(dmsEmployeeAllocations.stream().filter(res -> !res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")
				&& empNamesList.equals(res.getDmsLead().getSalesConsultant())).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));

		//dmsLeadList.addAll(dmsEmployeeAllocations.stream().filter(res -> !res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
		
		//System.out.println("dmsLeadList After Adding"+dmsLeadList.size());	
		
		List<Integer> dmsLeadListDropped = dmsLeadDao.getLeadIdsByEmpNamesWithDrop(empNamesList);
		
		//System.out.println("dmsLeadListDropped Before Adding"+dmsLeadListDropped.size());
		
		dmsLeadListDropped.addAll(dmsEmployeeAllocations.stream().filter(res -> res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")
				&& empNamesList.equals(res.getDmsLead().getSalesConsultant())).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
		//dmsLeadListDropped.addAll(dmsEmployeeAllocations.stream().filter(res -> res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
		
		//System.out.println("dmsLeadListDropped After Adding"+dmsLeadListDropped.size());
		
		dmsLeadList = dmsLeadList.stream().distinct().collect(Collectors.toList());
		
		//System.out.println("dmsLeadList After Deleting Duplicates"+dmsLeadList.size());
		
		dmsLeadListDropped = dmsLeadListDropped.stream().distinct().collect(Collectors.toList());
		
		//System.out.println("dmsLeadListDropped After Deleting Duplicates"+dmsLeadListDropped.size());
		
		List<LeadStageRefEntity> leadRefList  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadList,startDate,endDate);
		
		//System.out.println("leadRefList Before Duplicates"+leadRefList.size());
		
		leadRefList = leadRefList.stream().distinct().collect(Collectors.toList());
		
		//System.out.println("leadRefList After Duplicates"+leadRefList.size());
		
		List<LeadStageRefEntity> leadRefListDropped  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadListDropped,startDate,endDate);
		leadRefListDropped = leadRefListDropped.stream().distinct().collect(Collectors.toList());
		Set<Integer> hashSet = new LinkedHashSet(leadRefList);
		List<LeadStageRefEntity> leadRefListNoDuplicates = new ArrayList(hashSet);
        //log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$Total leads in leadRefListNoDuplicates table is ::"+leadRefListNoDuplicates.size());
		log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$Total leads in leadRefListNoDuplicates table is ::"+leadRefListNoDuplicates);
		
		if(null!=leadRefList && !leadRefList.isEmpty()) {
			//log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$Total leads in LeadReF table is ::"+leadRefList.size());
			log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$Total leads in LeadReF table is ::"+leadRefList);
			log.debug("Total leads in LeadReF table is ::"+leadRefList.size());
			
			enqLeadCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).distinct().count();
			//enqLeadCnt = leadRefList.stream().filter(x-> x.getLeadStatus()!=null &&  x.getLeadStatus().equalsIgnoreCase(preenqCompStatus)).count();
			preBookCount =leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("PREBOOKING")).count();
			bookCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("BOOKING")).distinct().count();
			//bookCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("BOOKING") && (x.getStageName().equalsIgnoreCase("INVOICE") && x.getLeadStatus().equalsIgnoreCase("BOOKINGCOMPLETED"))).count();
			//bookCount = leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(preBookCompStatus)).count();
			//invCount = leadRefList.stream().filter(x->(x.getStageName().equalsIgnoreCase("INVOICE") && x.getLeadStatus().equalsIgnoreCase("INVOICECOMPLETED")) && (x.getStageName().equalsIgnoreCase("PREDELIVERY")) && (x.getStageName().equalsIgnoreCase("DELIVERY") && x.getLeadStatus().equalsIgnoreCase("PREDELIVERYCOMPLETED"))).count();
			//invCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("INVOICE") 
			//		&& x.getLeadStatus().equalsIgnoreCase("INVOICECOMPLETED")).count();
			//invCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("PREDELIVERY") 
			//		&& x.getLeadStatus().equalsIgnoreCase("INVOICECOMPLETED") && x.getLeadStatus().equalsIgnoreCase("PREDELIVERYCOMPLETED")).count();
			invCount = leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(invCompStatus)).distinct().count();
			preDeliveryCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("PREDELIVERY")).count();
			//delCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("DELIVERY") && x.getLeadStatus().equalsIgnoreCase("DELIVERYCOMPLETED")).count();
			delCnt = leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(delCompStatus)).count();
			
			if(null!=leadRefListDropped && !leadRefListDropped.isEmpty()) {
				
				dropLeadCnt = leadRefListDropped.stream().count();
			}
		}
		System.out.println("@@@@@@@@@#############leadRefList2:::::::::"+leadRefList.stream().map(res->res.getLeadId()).distinct().collect(Collectors.toList()));

		//System.out.println("Enq :"+leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).map(res -> res.getLeadId()).collect(Collectors.toList()));
		//System.out.println("Enq :"+leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).map(res -> res.getLeadId()).distinct().collect(Collectors.toList()));
		//System.out.println("EnqDist : "+leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).distinct().count());
		
		/*
		 * List<Object> dataList = new ArrayList<>(); List<LeadStageRefEntity> tmpList =
		 * leadRefList.stream()
		 * .filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).collect(Collectors.
		 * toList());
		 * 
		 * for (LeadStageRefEntity l : tmpList) { String uId = l.getUniversalId();
		 * log.debug("universalID "+uId); //log.info("Enquiry LeadId "+l.getLeadId());
		 * if (uId != null && uId.length() > 0) { dataList.add(getLeadData(uId)); } }
		 */
		List<DmsWFTask> wfTaskList = dmsWfTaskDao.getWfTaskByAssigneeIdList(empIdsUnderReporting, startDate, endDate);
		//return buildTargetAchivements(resList, map, finalEnqLeadCnt,finalBookCnt, finalInvCount,wfTaskList);
		return buildTargetAchivements(resList, map, dropLeadCnt,enqLeadCnt,preBookCount, bookCount,invCount,preDeliveryCnt,delCnt,wfTaskList,leadRefList);
	}
	
///Immediate Hierarchy
	private List<TargetAchivement> getTaskAndBuildTargetAchievementsImmediateHierarchy(List<Integer> empIdsUnderReporting, String orgId,
			List<TargetAchivement> resList, List<String> empNamesList, String startDate, String endDate,
			Map<String, Integer> map, List<Integer> empId) {
		Long dropLeadCnt = 0L;
		Long enqLeadCnt = 0L;
		Long preBookCount = 0L;
		Long bookCount = 0L;
		Long invCount = 0L;
		Long preDeliveryCnt = 0L;
		Long delCnt = 0L;
		List<DmsEmployeeAllocation> dmsEmployeeAllocations = employeeAllocation.findByEmployeeIdImmediate(empId);
		
		
		
		List<Integer> dmsLeadList = dmsLeadDao.getLeadIdsByEmpNamesWithOutDrop(empNamesList);
        //System.out.println("dmsLeadList Before Adding"+dmsLeadList.size());
        //System.out.println("empNamesList"+empNamesList.toString());
		dmsLeadList.addAll(dmsEmployeeAllocations.stream().filter(res -> !res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")
				&& empNamesList.equals(res.getDmsLead().getSalesConsultant())).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));

		//dmsLeadList.addAll(dmsEmployeeAllocations.stream().filter(res -> !res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
		
		//System.out.println("dmsLeadList After Adding"+dmsLeadList.size());	
		
		List<Integer> dmsLeadListDropped = dmsLeadDao.getLeadIdsByEmpNamesWithDrop(empNamesList);
		
		//System.out.println("dmsLeadListDropped Before Adding"+dmsLeadListDropped.size());
		
		dmsLeadListDropped.addAll(dmsEmployeeAllocations.stream().filter(res -> res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
		
		//System.out.println("dmsLeadListDropped After Adding"+dmsLeadListDropped.size());
		
		dmsLeadList = dmsLeadList.stream().distinct().collect(Collectors.toList());
		
		//System.out.println("dmsLeadList After Deleting Duplicates"+dmsLeadList.size());
		
		dmsLeadListDropped = dmsLeadListDropped.stream().distinct().collect(Collectors.toList());
		
		//System.out.println("dmsLeadListDropped After Deleting Duplicates"+dmsLeadListDropped.size());
		
		List<LeadStageRefEntity> leadRefList  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadList,startDate,endDate);
		
		//System.out.println("leadRefList Before Duplicates"+leadRefList.size());
		
		leadRefList = leadRefList.stream().distinct().collect(Collectors.toList());
		
		//System.out.println("leadRefList After Duplicates"+leadRefList.size());
		
		List<LeadStageRefEntity> leadRefListDropped  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadListDropped,startDate,endDate);
		leadRefListDropped = leadRefListDropped.stream().distinct().collect(Collectors.toList());
		Set<Integer> hashSet = new LinkedHashSet(leadRefList);
		List<LeadStageRefEntity> leadRefListNoDuplicates = new ArrayList(hashSet);
        //log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$Total leads in leadRefListNoDuplicates table is ::"+leadRefListNoDuplicates.size());
		log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$Total leads in leadRefListNoDuplicates table is ::"+leadRefListNoDuplicates);
		
		if(null!=leadRefList && !leadRefList.isEmpty()) {
			//log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$Total leads in LeadReF table is ::"+leadRefList.size());
			log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$Total leads in LeadReF table is ::"+leadRefList);
			log.debug("Total leads in LeadReF table is ::"+leadRefList.size());
			
			enqLeadCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).distinct().count();
			//enqLeadCnt = leadRefList.stream().filter(x-> x.getLeadStatus()!=null &&  x.getLeadStatus().equalsIgnoreCase(preenqCompStatus)).count();
			preBookCount =leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("PREBOOKING")).count();
			bookCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("BOOKING")).distinct().count();
			//bookCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("BOOKING") && (x.getStageName().equalsIgnoreCase("INVOICE") && x.getLeadStatus().equalsIgnoreCase("BOOKINGCOMPLETED"))).count();
			//bookCount = leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(preBookCompStatus)).count();
			//invCount = leadRefList.stream().filter(x->(x.getStageName().equalsIgnoreCase("INVOICE") && x.getLeadStatus().equalsIgnoreCase("INVOICECOMPLETED")) && (x.getStageName().equalsIgnoreCase("PREDELIVERY")) && (x.getStageName().equalsIgnoreCase("DELIVERY") && x.getLeadStatus().equalsIgnoreCase("PREDELIVERYCOMPLETED"))).count();
			//invCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("INVOICE") 
			//		&& x.getLeadStatus().equalsIgnoreCase("INVOICECOMPLETED")).count();
			//invCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("PREDELIVERY") 
			//		&& x.getLeadStatus().equalsIgnoreCase("INVOICECOMPLETED") && x.getLeadStatus().equalsIgnoreCase("PREDELIVERYCOMPLETED")).count();
			invCount = leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(invCompStatus)).distinct().count();
			preDeliveryCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("PREDELIVERY")).count();
			//delCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("DELIVERY") && x.getLeadStatus().equalsIgnoreCase("DELIVERYCOMPLETED")).count();
			delCnt = leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(delCompStatus)).count();
			
			if(null!=leadRefListDropped && !leadRefListDropped.isEmpty()) {
				
				dropLeadCnt = leadRefListDropped.stream().count();
			}
		}
		//System.out.println("Enq :"+leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).map(res -> res.getLeadId()).collect(Collectors.toList()));
		//System.out.println("Enq :"+leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).map(res -> res.getLeadId()).distinct().collect(Collectors.toList()));
		//System.out.println("EnqDist : "+leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).distinct().count());
		
		/*
		 * List<Object> dataList = new ArrayList<>(); List<LeadStageRefEntity> tmpList =
		 * leadRefList.stream()
		 * .filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).collect(Collectors.
		 * toList());
		 * 
		 * for (LeadStageRefEntity l : tmpList) { String uId = l.getUniversalId();
		 * log.debug("universalID "+uId); //log.info("Enquiry LeadId "+l.getLeadId());
		 * if (uId != null && uId.length() > 0) { dataList.add(getLeadData(uId)); } }
		 */
		List<DmsWFTask> wfTaskList = dmsWfTaskDao.getWfTaskByAssigneeIdList(empIdsUnderReporting, startDate, endDate);
		//return buildTargetAchivements(resList, map, finalEnqLeadCnt,finalBookCnt, finalInvCount,wfTaskList);
		return buildTargetAchivements(resList, map, dropLeadCnt,enqLeadCnt,preBookCount, bookCount,invCount,preDeliveryCnt,delCnt,wfTaskList,leadRefList);
	}

	private List<TargetAchivement> getTargetAchivementParamsForEmp(
			Integer empId, DashBoardReqV2 req,String orgId) throws ParseException, DynamicFormsServiceException {
		List<TargetAchivement> resList = new ArrayList<>();
		Optional<DmsEmployee> dmsEmployee = dmsEmployeeRepo.findById(empId);
		String empName="";
		int empId1 = req.getLoggedInEmpId();
		if(dmsEmployee.isPresent()) {
			empName = dmsEmployee.get().getEmpName();
		}
		log.info("Calling getTargetAchivementParamsForEmp");
		String startDate = null;
		String endDate = null;
		if (null == req.getStartDate() && null == req.getEndDate()) {
			startDate = getFirstDayOfMonth();
			endDate = getLastDayOfMonth();
		} else {
			startDate = req.getStartDate()+" 00:00:00";
			endDate = req.getEndDate()+" 23:59:59";
		}
		log.info("StartDate " + startDate + ", EndDate " + endDate);
		Map<String, Integer> map = new LinkedHashMap<>();
		log.debug("Getting target params for user "+empId);
		Map<String, Integer> innerMap = getTargetParams(String.valueOf(empId), startDate, endDate);
		log.debug("innerMap::"+innerMap);
		map = validateAndUpdateMapData(ENQUIRY,innerMap,map);
		map = validateAndUpdateMapData(TEST_DRIVE,innerMap,map);
		map = validateAndUpdateMapData(HOME_VISIT,innerMap,map);
		map = validateAndUpdateMapData(VIDEO_CONFERENCE,innerMap,map);
		map = validateAndUpdateMapData(BOOKING,innerMap,map);
		map = validateAndUpdateMapData(EXCHANGE,innerMap,map);
		map = validateAndUpdateMapData(FINANCE,innerMap,map);
		map = validateAndUpdateMapData(INSURANCE,innerMap,map);
		map = validateAndUpdateMapData(ACCCESSORIES,innerMap,map);
		map = validateAndUpdateMapData(EVENTS,innerMap,map);
		map = validateAndUpdateMapData(INVOICE,innerMap,map);
		map = validateAndUpdateMapData(EXTENDED_WARRANTY,innerMap,map);
		return getTaskAndBuildTargetAchievements(Arrays.asList(empId), orgId, resList, Arrays.asList(empName), startDate, endDate,
				map,empId1);
	}
///////////Immediate Hierarchy
	
	private List<TargetAchivement> getTargetAchivementParamsForEmpImmediateHierarchy(
			List<Integer> empId, DashBoardReqImmediateHierarchyV2 req,String orgId) throws ParseException, DynamicFormsServiceException {
		List<TargetAchivement> resList = new ArrayList<>();
		List<DmsEmployee> dmsEmployee = dmsEmployeeRepo.findByImmediateId(empId);
		ArrayList<String> empName =new ArrayList<>();
		
		List<Integer> empId1 = req.getLoggedInEmpId();
		if(!dmsEmployee.isEmpty()) {
//		       empName = dmsEmployee.stream().map(res -> res.getEmpName()).collect(Collectors.toList());
			empName.addAll(dmsEmployee.stream().map(res -> res.getEmpName()).collect(Collectors.toList()));
		}
		log.info("Calling getTargetAchivementParamsForEmp");
		String startDate = null;
		String endDate = null;
		if (null == req.getStartDate() && null == req.getEndDate()) {
			startDate = getFirstDayOfMonth();
			endDate = getLastDayOfMonth();
		} else {
			startDate = req.getStartDate()+" 00:00:00";
			endDate = req.getEndDate()+" 23:59:59";
		}
		log.info("StartDate " + startDate + ", EndDate " + endDate);
		Map<String, Integer> map = new LinkedHashMap<>();
		log.debug("Getting target params for user "+empId);
		Map<String, Integer> innerMap = getTargetParamsHir(empId, startDate, endDate);
		log.debug("innerMap::"+innerMap);
		map = validateAndUpdateMapData(ENQUIRY,innerMap,map);
		map = validateAndUpdateMapData(TEST_DRIVE,innerMap,map);
		map = validateAndUpdateMapData(HOME_VISIT,innerMap,map);
		map = validateAndUpdateMapData(VIDEO_CONFERENCE,innerMap,map);
		map = validateAndUpdateMapData(BOOKING,innerMap,map);
		map = validateAndUpdateMapData(EXCHANGE,innerMap,map);
		map = validateAndUpdateMapData(FINANCE,innerMap,map);
		map = validateAndUpdateMapData(INSURANCE,innerMap,map);
		map = validateAndUpdateMapData(ACCCESSORIES,innerMap,map);
		map = validateAndUpdateMapData(EVENTS,innerMap,map);
		map = validateAndUpdateMapData(INVOICE,innerMap,map);
		map = validateAndUpdateMapData(EXTENDED_WARRANTY,innerMap,map);
		return getTaskAndBuildTargetAchievementsImmediateHierarchy(empId, orgId, resList,empName, startDate, endDate,
				map,empId1);
	}

	private Map<String, Integer> validateAndUpdateMapData(String targetParmType, Map<String, Integer> innerMap,
			Map<String, Integer> map) {

		if (map.containsKey(targetParmType)) {
			map.put(targetParmType, map.get(targetParmType) + innerMap.get(targetParmType));
		} else {
			map.put(targetParmType, innerMap.get(targetParmType));
		}
		return map;
	}

	
	private String getLeadData(String universalId) {
		String response =null;
		JsonObject jo =null;
		try {
			String url = leadSourceEnqUrl;
			url = url.replaceAll("universal_id", universalId);
			log.debug("leadSourceEnqUrl::::"+url);
			ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
			response = res.getBody();
			/*
			 * JsonParser jsonParser = new JsonParser(); jo =
			 * (JsonObject)jsonParser.parse(res.getBody());
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	
	
	private List<TargetAchivement> buildTargetAchivements(List<TargetAchivement> resList,
			Map<String, Integer> targetParamMap, Long dropLeadCnt, Long enqLeadCnt,Long preBookCount, Long bookCount, Long invCount, Long preDeliveryCnt, Long delCnt, List<DmsWFTask> wfTaskList, List<LeadStageRefEntity> leadRefList) {
		

		// Getting Test Drive Cnt
		Long testDriveCnt = getTestDriveCount(wfTaskList);
		//Long financeCnt = getFinanceCount(wfTaskList);
		//Long insuranceCnt =getInsuranceCount(wfTaskList);
		
		//Long bookingCnt = getBookingCount(wfTaskList);
		Long homeVistCnt = getHomeVisitCount(wfTaskList);
		//Long videoConfCnt = wfTaskList.stream().filter(x->x.getTaskName().equalsIgnoreCase(VIDEO_CONFERENCE)).count();
		//Long exchangeCnt = getExchangeCount(wfTaskList);
		//Long invoceCnt = getInvoiceCountTarget(wfTaskList);
		//Long retailCnt = 0L;
		Long bookingCnt = bookCount;
		Long invoceCnt = invCount;
		
		
		Long exchangeCnt  = 0L;
		Long insuranceCnt = 0L;
		Long accessoriesCnt = 0L;
		Long extendedWarntyCnt  =0L;
		Long financeCnt = 0L;
		List<Integer> leadIdList = leadRefList.stream().map(x->x.getLeadId()).distinct().collect(Collectors.toList());
		System.out.println(""+leadIdList.size()+"@@@@@@@ID's"+leadIdList);
		List<Integer> leadIdListV1 = leadRefList.stream().filter(x->null!=x.getLeadStatus() && x.getLeadStatus().equals("INVOICECOMPLETED")).map(x->x.getLeadId()).distinct().collect(Collectors.toList());
		System.out.println(""+leadIdListV1.size()+"@@@@@@@ID's"+leadIdListV1);

		if(leadIdListV1!=null && !leadIdListV1.isEmpty()) {
			exchangeCnt  = getExchangeCntSupportParam(leadIdListV1);
			insuranceCnt = getInsuranceCntSupportParam(leadIdListV1);
			accessoriesCnt = getAccessoriesCount(leadIdListV1);
			if(accessoriesCnt==null || leadIdListV1.isEmpty())
			{
				accessoriesCnt = 0L;
			}
			financeCnt = getFinanceCntSupportParam(leadIdListV1);	
		}
		
		extendedWarntyCnt  =getExtendedWarrntySupportParam(leadIdList);
		
		TargetAchivement enqTargetAchivement = new TargetAchivement();
		enqTargetAchivement.setParamName(ENQUIRY);
		enqTargetAchivement.setParamShortName("Enq");
		enqTargetAchivement.setAchievment(String.valueOf(enqLeadCnt));;
		if(targetParamMap.containsKey(ENQUIRY)) {
			enqTargetAchivement.setTarget(String.valueOf(targetParamMap.get(ENQUIRY)));
			
			enqTargetAchivement.setAchivementPerc(getAchievmentPercentage(enqLeadCnt,targetParamMap.get(ENQUIRY)));
			enqTargetAchivement.setShortfall(getShortFallCount(enqLeadCnt,targetParamMap.get(ENQUIRY)));
			enqTargetAchivement.setShortFallPerc(getShortFallPercentage(enqLeadCnt,targetParamMap.get(ENQUIRY)));;
		}else {
			enqTargetAchivement.setTarget(String.valueOf("0"));
			enqTargetAchivement.setAchivementPerc(String.valueOf("0"));
			enqTargetAchivement.setShortfall(String.valueOf("0"));
			enqTargetAchivement.setShortFallPerc(String.valueOf("0"));
		}
		//enqTargetAchivement.setData(buildDataList(leadRefList,ENQUIRY));
		//enqTargetAchivement.setData(buildEnqDataList(leadRefList,ENQUIRY));
	
		resList.add(enqTargetAchivement);
		
		
		TargetAchivement lostTargetAchivement = new TargetAchivement();
		lostTargetAchivement.setParamName(DROPPED);
		lostTargetAchivement.setParamShortName("Lost");
		lostTargetAchivement.setAchievment(String.valueOf(dropLeadCnt));;
		if(targetParamMap.containsKey(DROPPED)) {
			lostTargetAchivement.setTarget(String.valueOf(targetParamMap.get(DROPPED)));
			
			lostTargetAchivement.setAchivementPerc(getAchievmentPercentage(dropLeadCnt,targetParamMap.get(DROPPED)));
			lostTargetAchivement.setShortfall(getShortFallCount(dropLeadCnt,targetParamMap.get(DROPPED)));
			lostTargetAchivement.setShortFallPerc(getShortFallPercentage(dropLeadCnt,targetParamMap.get(DROPPED)));;
		}else {
			lostTargetAchivement.setTarget(String.valueOf("0"));
			lostTargetAchivement.setAchivementPerc(String.valueOf("0"));
			lostTargetAchivement.setShortfall(String.valueOf("0"));
			lostTargetAchivement.setShortFallPerc(String.valueOf("0"));
		}
		//enqTargetAchivement.setData(buildDataList(leadRefList,ENQUIRY));
		//lostTargetAchivement.setData(buildEnqDataList(leadRefList,DROPPED));
	
		resList.add(lostTargetAchivement);
		
		TargetAchivement testDriveTA = new TargetAchivement();
	
		testDriveTA.setParamName(TEST_DRIVE);
		testDriveTA.setParamShortName("Tdr");
		testDriveTA.setAchievment(String.valueOf(testDriveCnt));
		if(targetParamMap.containsKey(TEST_DRIVE)) {
			testDriveTA.setTarget(String.valueOf(targetParamMap.get(TEST_DRIVE)));
			testDriveTA.setAchivementPerc(getAchievmentPercentage(testDriveCnt,targetParamMap.get(TEST_DRIVE)));
			testDriveTA.setShortfall(getShortFallCount(testDriveCnt,targetParamMap.get(TEST_DRIVE)));
			testDriveTA.setShortFallPerc(getShortFallPercentage(testDriveCnt,targetParamMap.get(TEST_DRIVE)));
		}else {
			testDriveTA.setTarget(String.valueOf("0"));
			testDriveTA.setAchivementPerc(String.valueOf("0"));
			testDriveTA.setShortfall(String.valueOf("0"));
			testDriveTA.setShortFallPerc(String.valueOf("0"));
		}
		//testDriveTA.setData(buildDataList(leadRefList,TEST_DRIVE));
		resList.add(testDriveTA);
		
		
		TargetAchivement financeTA = new TargetAchivement();
		
		financeTA.setParamName(FINANCE);
		financeTA.setParamShortName("Fin");
		financeTA.setAchievment(String.valueOf(financeCnt));
		if(targetParamMap.containsKey(FINANCE)) {
			financeTA.setTarget(String.valueOf(targetParamMap.get(FINANCE)));
			financeTA.setAchivementPerc(getAchievmentPercentage(financeCnt,targetParamMap.get(FINANCE)));
			financeTA.setShortfall(getShortFallCount(financeCnt,targetParamMap.get(FINANCE)));
			financeTA.setShortFallPerc(getShortFallPercentage(financeCnt,targetParamMap.get(FINANCE)));
		}else {
			financeTA.setTarget(String.valueOf("0"));
			financeTA.setAchivementPerc(String.valueOf("0"));
			financeTA.setShortfall(String.valueOf("0"));
			financeTA.setShortFallPerc(String.valueOf("0"));
		}
		//financeTA.setData(buildDataList(leadRefList,FINANCE));
		resList.add(financeTA);
		
		TargetAchivement insuranceTA = new TargetAchivement();
		
		insuranceTA.setParamName(INSURANCE);
		insuranceTA.setParamShortName("Ins");
		insuranceTA.setAchievment(String.valueOf(insuranceCnt));
		if(targetParamMap.containsKey(INSURANCE)) {
			insuranceTA.setTarget(String.valueOf(targetParamMap.get(INSURANCE)));
			insuranceTA.setAchivementPerc(getAchievmentPercentage(insuranceCnt,targetParamMap.get(INSURANCE)));
			insuranceTA.setShortfall(getShortFallCount(insuranceCnt,targetParamMap.get(INSURANCE)));
			insuranceTA.setShortFallPerc(getShortFallPercentage(insuranceCnt,targetParamMap.get(INSURANCE)));
		}else {
			insuranceTA.setTarget(String.valueOf("0"));
			insuranceTA.setAchivementPerc(String.valueOf("0"));
			insuranceTA.setShortfall(String.valueOf("0"));
			insuranceTA.setShortFallPerc(String.valueOf("0"));
		}
		//insuranceTA.setData(buildDataList(leadRefList,INSURANCE));
		resList.add(insuranceTA);
		
		
		TargetAchivement accessoriesTA = new TargetAchivement();
		
		accessoriesTA.setParamName(ACCCESSORIES);
		accessoriesTA.setParamShortName("Acc");
		accessoriesTA.setAchievment(String.valueOf(accessoriesCnt));
		if(targetParamMap.containsKey(ACCCESSORIES)) {
			accessoriesTA.setTarget(String.valueOf(targetParamMap.get(ACCCESSORIES)));
			accessoriesTA.setAchivementPerc(getAchievmentPercentage(accessoriesCnt,targetParamMap.get(ACCCESSORIES)));
			accessoriesTA.setShortfall(getShortFallCount(accessoriesCnt,targetParamMap.get(ACCCESSORIES)));
			accessoriesTA.setShortFallPerc(getShortFallPercentage(accessoriesCnt,targetParamMap.get(ACCCESSORIES)));
		}else {
			accessoriesTA.setTarget(String.valueOf("0"));
			accessoriesTA.setAchivementPerc(String.valueOf("0"));
			accessoriesTA.setShortfall(String.valueOf("0"));
			accessoriesTA.setShortFallPerc(String.valueOf("0"));
		}
		//accessoriesTA.setData(buildDataList(leadRefList,ACCCESSORIES));
		resList.add(accessoriesTA);
		
		
		TargetAchivement bookingTA = new TargetAchivement();

		bookingTA.setParamName(BOOKING);
		bookingTA.setParamShortName("Bkg");
		bookingTA.setAchievment(String.valueOf(bookingCnt));
		if(targetParamMap.containsKey(BOOKING)) {
			bookingTA.setTarget(String.valueOf(targetParamMap.get(BOOKING)));
			bookingTA.setAchivementPerc(getAchievmentPercentage(bookingCnt,targetParamMap.get(BOOKING)));
			bookingTA.setShortfall(getShortFallCount(bookingCnt,targetParamMap.get(BOOKING)));
			bookingTA.setShortFallPerc(getShortFallPercentage(bookingCnt,targetParamMap.get(BOOKING)));
		}else {
			bookingTA.setTarget(String.valueOf("0"));
			bookingTA.setAchivementPerc(String.valueOf("0"));
			bookingTA.setShortfall(String.valueOf("0"));
			bookingTA.setShortFallPerc(String.valueOf("0"));
		}
		//bookingTA.setData(buildBkgDataList(leadRefList,BOOKING));
		resList.add(bookingTA);
		
		TargetAchivement homeVisitTA = new TargetAchivement();
		
		homeVisitTA.setParamName(HOME_VISIT);
		homeVisitTA.setParamShortName("Hvt");
		homeVisitTA.setAchievment(String.valueOf(homeVistCnt));
		if(targetParamMap.containsKey(BOOKING)) {
			homeVisitTA.setTarget(String.valueOf(targetParamMap.get(HOME_VISIT)));
			homeVisitTA.setAchivementPerc(getAchievmentPercentage(homeVistCnt,targetParamMap.get(HOME_VISIT)));
			homeVisitTA.setShortfall(getShortFallCount(homeVistCnt,targetParamMap.get(HOME_VISIT)));
			homeVisitTA.setShortFallPerc(getShortFallPercentage(homeVistCnt,targetParamMap.get(HOME_VISIT)));
		}else {
			homeVisitTA.setTarget(String.valueOf("0"));
			homeVisitTA.setAchivementPerc(String.valueOf("0"));
			homeVisitTA.setShortfall(String.valueOf("0"));
			homeVisitTA.setShortFallPerc(String.valueOf("0"));
		}
		homeVisitTA.setData(buildDataList(leadRefList,HOME_VISIT));
		resList.add(homeVisitTA);
		
		TargetAchivement exchangeTA = new TargetAchivement();
		exchangeTA.setParamName(EXCHANGE);
		exchangeTA.setParamShortName("Exg");
		exchangeTA.setAchievment(String.valueOf(exchangeCnt));
		if(targetParamMap.containsKey(EXCHANGE)) {
			exchangeTA.setTarget(String.valueOf(targetParamMap.get(EXCHANGE)));
			exchangeTA.setAchivementPerc(getAchievmentPercentage(exchangeCnt,targetParamMap.get(EXCHANGE)));
			exchangeTA.setShortfall(getShortFallCount(exchangeCnt,targetParamMap.get(EXCHANGE)));
			exchangeTA.setShortFallPerc(getShortFallPercentage(exchangeCnt,targetParamMap.get(EXCHANGE)));
		}else {
			exchangeTA.setTarget(String.valueOf("0"));
			exchangeTA.setAchivementPerc(String.valueOf("0"));
			exchangeTA.setShortfall(String.valueOf("0"));
			exchangeTA.setShortFallPerc(String.valueOf("0"));
		}
		//exchangeTA.setData(buildDataList(leadRefList,EXCHANGE));
		resList.add(exchangeTA);
		
		/*
		TargetAchivement vcTA = new TargetAchivement();
		vcTA.setTarget(String.valueOf(targetParamMap.get(VIDEO_CONFERENCE)));
		vcTA.setParamName(VIDEO_CONFERENCE);
		vcTA.setParamShortName("VC");
		vcTA.setAchievment(String.valueOf(0));
		vcTA.setAchivementPerc(String.valueOf(0));
		vcTA.setShortfall(String.valueOf(0));
		vcTA.setShortFallPerc(String.valueOf(0));
		resList.add(vcTA);*/
		
		TargetAchivement rTa = new TargetAchivement();
		rTa.setParamName(INVOICE);
		rTa.setParamShortName("Ret");
		rTa.setAchievment(String.valueOf(invoceCnt));
		if(targetParamMap.containsKey(INVOICE)) {
			rTa.setTarget(String.valueOf(targetParamMap.get(INVOICE)));
			rTa.setAchivementPerc(getAchievmentPercentage(invoceCnt,targetParamMap.get(INVOICE)));
			rTa.setShortfall(getShortFallCount(invoceCnt,targetParamMap.get(INVOICE)));
			rTa.setShortFallPerc(getShortFallPercentage(invoceCnt,targetParamMap.get(INVOICE)));
		}else {
			rTa.setTarget(String.valueOf("0"));
			rTa.setAchivementPerc(String.valueOf("0"));
			rTa.setShortfall(String.valueOf("0"));
			rTa.setShortFallPerc(String.valueOf("0"));
		}
		//rTa.setData(buildInvDataList(leadRefList,INVOICE));
		resList.add(rTa);
		
		
		
		
		
		TargetAchivement extendedWarantyTA = new TargetAchivement();
		extendedWarantyTA.setParamName(EXTENDED_WARRANTY);
		extendedWarantyTA.setParamShortName("ExW");
		extendedWarantyTA.setAchievment(String.valueOf(extendedWarntyCnt));
		if(targetParamMap.containsKey(EXTENDED_WARRANTY)) {
			extendedWarantyTA.setTarget(String.valueOf(targetParamMap.get(EXTENDED_WARRANTY)));
			extendedWarantyTA.setAchivementPerc(getAchievmentPercentage(invoceCnt,targetParamMap.get(EXTENDED_WARRANTY)));
			extendedWarantyTA.setShortfall(getShortFallCount(invoceCnt,targetParamMap.get(EXTENDED_WARRANTY)));
			extendedWarantyTA.setShortFallPerc(getShortFallPercentage(invoceCnt,targetParamMap.get(EXTENDED_WARRANTY)));
		}else {
			extendedWarantyTA.setTarget(String.valueOf("0"));
			extendedWarantyTA.setAchivementPerc(String.valueOf("0"));
			extendedWarantyTA.setShortfall(String.valueOf("0"));
			extendedWarantyTA.setShortFallPerc(String.valueOf("0"));
		}
		//extendedWarantyTA.setData(buildDataList(leadRefList,EXTENDED_WARRANTY));
		resList.add(extendedWarantyTA);
		return resList;
	}



	
	
	private Long getFinanceCntSupportParam(List<Integer> leadIdList) {
		  Long cnt=0L;
			 List<String> list = dmsFinanceDao.getFinanceTypeLeads(leadIdList);
	     	 if(null!=list) {
	     		 cnt= (long) list.size();
	     		 
	     	 }
			 return cnt;
	}
	
	private Long getInsuranceCntSupportParam(List<Integer> leadIdList) {
		  Long cnt=0L;
			 List<String> list = deliveryDao.getInsuranceTakenLeads(leadIdList,"In House");
	     	 if(null!=list) {
	     		 cnt= (long) list.size();
	     		 
	     	 }
			 return cnt;
	}

	private Long getExtendedWarrntySupportParam(List<Integer> leadIdList) {
		  Long cnt=0L;
			 List<String> list = deliveryDao.getWarrantyTakenLeads(leadIdList);
	     	 if(null!=list) {
	     		 cnt= (long) list.size();
	     		 
	     	 }
			 return cnt;
	}


	private Long getExchangeCntSupportParam(List<Integer> leadIdList) {
		  Long cnt=0L;
		 List<String> list = buyerDao.getAllDmsExchangeBuyersByLeadIdList(leadIdList,"Exchange Buyer");
     	 if(null!=list) {
     		 cnt= (long) list.size();
     		 
     	 }
		 return cnt;
	}

	private List<Object> buildDataList(List<LeadStageRefEntity> leadRefList, String type) {
		List<Object> dataList = new ArrayList<>();
		if (null != leadRefList && !leadRefList.isEmpty()) {

			List<LeadStageRefEntity> tmpList = leadRefList.stream()
					.filter(x -> x.getStageName().equalsIgnoreCase(type)).collect(Collectors.toList());
			for (LeadStageRefEntity l : tmpList) {
				String uId = l.getUniversalId();
				log.debug("universalID "+uId);			
				if (uId != null && uId.length() > 0) {
					dataList.add(getLeadData(uId));
				}
			}
		}
		return dataList;
	}
	
	private List<Object> buildInvDataList(List<LeadStageRefEntity> leadRefList, String type) {
		List<Object> dataList = new ArrayList<>();
		if (null != leadRefList && !leadRefList.isEmpty()) {

			List<LeadStageRefEntity> tmpList = leadRefList.stream()
					.filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase("INVOICECOMPLETED")).collect(Collectors.toList());
			for (LeadStageRefEntity l : tmpList) {
				String uId = l.getUniversalId();
				log.debug("universalID "+uId);	
				//log.info("Invoice LeadId "+l.getLeadId());
				if (uId != null && uId.length() > 0) {
					dataList.add(getLeadData(uId));
				}
			}
		}
		return dataList;
	}
	
	private List<Object> buildBkgDataList(List<LeadStageRefEntity> leadRefList, String type) {
		List<Object> dataList = new ArrayList<>();
		if (null != leadRefList && !leadRefList.isEmpty()) {

			List<LeadStageRefEntity> tmpList = leadRefList.stream()
					.filter(x->x.getStageName().equalsIgnoreCase("BOOKING")).collect(Collectors.toList());
			for (LeadStageRefEntity l : tmpList) {
				String uId = l.getUniversalId();
				log.debug("universalID "+uId);	
				//log.info("Booking LeadId "+l.getLeadId());
				if (uId != null && uId.length() > 0) {
					dataList.add(getLeadData(uId));
				}
			}
		}
		return dataList;
	}
	
	
	private List<Object> buildEnqDataList(List<LeadStageRefEntity> leadRefList, String type) {
		List<Object> dataList = new ArrayList<>();
		if (null != leadRefList && !leadRefList.isEmpty()) {

			List<LeadStageRefEntity> tmpList = leadRefList.stream()
					.filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).collect(Collectors.toList());
			for (LeadStageRefEntity l : tmpList) {
				String uId = l.getUniversalId();
				log.debug("universalID "+uId);	
				//log.info("Enquiry LeadId "+l.getLeadId());
				if (uId != null && uId.length() > 0) {
					dataList.add(getLeadData(uId));
				}
			}
		}
		return dataList;
	}
	
	private List<Object> buildDrpDataList(List<LeadStageRefEntity> leadRefList, String type) {
		List<Object> dataList = new ArrayList<>();
		if (null != leadRefList && !leadRefList.isEmpty()) {

			List<LeadStageRefEntity> tmpList = leadRefList.stream()
					.filter(x->x.getStageName().equalsIgnoreCase("DROPPED")).collect(Collectors.toList());
			for (LeadStageRefEntity l : tmpList) {
				String uId = l.getUniversalId();
				log.debug("universalID "+uId);	
				log.info("Dropped LeadId "+l.getLeadId());
				if (uId != null && uId.length() > 0) {
					dataList.add(getLeadData(uId));
				}
			}
		}
		return dataList;
	}




	private Long getEnqTaskCount(List<DmsWFTask> wfTaskList) {
		return wfTaskList.stream().
				filter(x->x.getTaskName().equalsIgnoreCase("Create Enquiry")).count();
	}


	private Map<String,Integer> getTargetParams(String empId, String start, String end) throws ParseException, DynamicFormsServiceException {
		TargetRoleReq targetRoleReq = new TargetRoleReq();
		targetRoleReq.setEmpId(Integer.valueOf(empId));
		targetRoleReq.setPageNo(1);
		targetRoleReq.setSize(10);
	///	Map<String, Object> tagetAdminMap = salesGapServiceImpl.getTargetDataWithRole(targetRoleReq);
		List<TargetSettingRes> adminTargetSettingData = salesGapServiceImpl.getTSDataForRoleV3(Integer.parseInt(empId));
		log.info("size of adminTargetSettingData for empID  "+ empId+ " is "+adminTargetSettingData.size());
		Date startDate = parseDate(start);  
		Date endDate = parseDate(end); 
		log.info("startDate:"+startDate+",endDate:"+endDate);
		List<TargetSettingRes> filteredList = new ArrayList<>();
		for(TargetSettingRes res :adminTargetSettingData) {
			
			Date resStartDate = parseDate(res.getStartDate());
			Date resEndDate = parseDate(res.getEndDate());
			log.info("resStartDate:"+resStartDate+",resEndDate:"+resEndDate);
			////System.out.println("startDate equals "+startDate.equals(resStartDate));


			if((resStartDate.after(startDate) || resStartDate.equals(startDate)) 
					&& (resStartDate.before(endDate) || resStartDate.equals(endDate)) 
					&& ( resEndDate.before(endDate)|| resEndDate.equals(endDate)) 
					&& ( resEndDate.equals(startDate) || resEndDate.after(startDate))) {
				filteredList.add(res);
			}
		}
		log.info("filteredList for given date range "+filteredList.size());
		log.debug("filteredList::"+filteredList);
		Integer retailTarget = 0;
		Integer enquiry=0;
		Integer testdrive=0;
		Integer homeVisit=0;
		Integer videoConf=0;
		Integer booking=0;
		Integer exchange=0;
		Integer finance=0;
		Integer insurance=0;
		Integer accessories=0;
		Integer events = 0;
		Integer invoice=0;
		Integer exwarranty=0;
		
		for(TargetSettingRes res :filteredList) {
			retailTarget += validateNumber(res.getRetailTarget());
			enquiry += validateNumber(res.getEnquiry());
			testdrive += validateNumber(res.getTestDrive());
			homeVisit += validateNumber(res.getHomeVisit());
			videoConf += validateNumber(res.getVideoConference());
			booking += validateNumber(res.getBooking());
			exchange += validateNumber(res.getExchange());
			finance += validateNumber(res.getFinance());
			insurance += validateNumber(res.getInsurance());
			accessories += validateNumber(res.getAccessories());
			//exW += Integer.valueOf(res.getExchange());
			events += validateNumber(res.getEvents());
			invoice += validateNumber(res.getInvoice());
			exwarranty += validateNumber(res.getExWarranty());
		}
		
		Map<String,Integer> map = new HashMap<>();
		map.put(ENQUIRY,enquiry);
		map.put(TEST_DRIVE, testdrive);
		map.put(HOME_VISIT, homeVisit);
		map.put(VIDEO_CONFERENCE, videoConf);
		map.put(BOOKING, booking);
		map.put(EXCHANGE, exchange);
		map.put(FINANCE, finance);
		map.put(INSURANCE, insurance);
		map.put(ACCCESSORIES, accessories);
		map.put(EVENTS, events);
		map.put(INVOICE, retailTarget);
		map.put(EXTENDED_WARRANTY, exwarranty);
		//map.put(RETAIL_TARGET, retailTarget);
		return map;
	}
	///immidiate hira
	private Map<String,Integer> getTargetParamsHir(List<Integer> empId, String start, String end) throws ParseException, DynamicFormsServiceException {
		TargetRoleReq targetRoleReq = new TargetRoleReq();
	     for(int singleEmp : empId ) {
	    	 
	    	 targetRoleReq.setEmpId(singleEmp);
	     }
		targetRoleReq.setPageNo(1);
		targetRoleReq.setSize(10);
	///	Map<String, Object> tagetAdminMap = salesGapServiceImpl.getTargetDataWithRole(targetRoleReq);
		List<TargetSettingRes> adminTargetSettingData = salesGapServiceImpl.getTSDataForRoleForEmps(empId);
		log.info("size of adminTargetSettingData for empID  "+ empId+ " is "+adminTargetSettingData.size());
		Date startDate = parseDate(start);  
		Date endDate = parseDate(end); 
		log.info("startDate:"+startDate+",endDate:"+endDate);
		List<TargetSettingRes> filteredList = new ArrayList<>();
		for(TargetSettingRes res :adminTargetSettingData) {
			
			Date resStartDate = parseDate(res.getStartDate());
			Date resEndDate = parseDate(res.getEndDate());
			log.info("resStartDate:"+resStartDate+",resEndDate:"+resEndDate);
			////System.out.println("startDate equals "+startDate.equals(resStartDate));


			if((resStartDate.after(startDate) || resStartDate.equals(startDate)) 
					&& (resStartDate.before(endDate) || resStartDate.equals(endDate)) 
					&& ( resEndDate.before(endDate)|| resEndDate.equals(endDate)) 
					&& ( resEndDate.equals(startDate) || resEndDate.after(startDate))) {
				filteredList.add(res);
			}
		}
		log.info("filteredList for given date range "+filteredList.size());
		log.debug("filteredList::"+filteredList);
		Integer retailTarget = 0;
		Integer enquiry=0;
		Integer testdrive=0;
		Integer homeVisit=0;
		Integer videoConf=0;
		Integer booking=0;
		Integer exchange=0;
		Integer finance=0;
		Integer insurance=0;
		Integer accessories=0;
		Integer events = 0;
		Integer invoice=0;
		Integer exwarranty=0;
		
		for(TargetSettingRes res :filteredList) {
			retailTarget += validateNumber(res.getRetailTarget());
			enquiry += validateNumber(res.getEnquiry());
			testdrive += validateNumber(res.getTestDrive());
			homeVisit += validateNumber(res.getHomeVisit());
			videoConf += validateNumber(res.getVideoConference());
			booking += validateNumber(res.getBooking());
			exchange += validateNumber(res.getExchange());
			finance += validateNumber(res.getFinance());
			insurance += validateNumber(res.getInsurance());
			accessories += validateNumber(res.getAccessories());
			//exW += Integer.valueOf(res.getExchange());
			events += validateNumber(res.getEvents());
			invoice += validateNumber(res.getInvoice());
			exwarranty += validateNumber(res.getExWarranty());
		}
		
		Map<String,Integer> map = new HashMap<>();
		map.put(ENQUIRY,enquiry);
		map.put(TEST_DRIVE, testdrive);
		map.put(HOME_VISIT, homeVisit);
		map.put(VIDEO_CONFERENCE, videoConf);
		map.put(BOOKING, booking);
		map.put(EXCHANGE, exchange);
		map.put(FINANCE, finance);
		map.put(INSURANCE, insurance);
		map.put(ACCCESSORIES, accessories);
		map.put(EVENTS, events);
		map.put(INVOICE, retailTarget);
		map.put(EXTENDED_WARRANTY, exwarranty);
		//map.put(RETAIL_TARGET, retailTarget);
		return map;
	}


	private Integer validateNumber(String retailTarget) {
		if(null!=retailTarget) {
			return Integer.valueOf(retailTarget);
		}
		return 0;
	}

	private Date parseDate(String date) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd").parse(date);
	}

	private String getShortFallPercentage(Long cnt, Integer target) {
		Double perc = 0D;
		Double shortfall = Double.valueOf(target)- Double.valueOf(cnt);
		if(target>0) {
		 perc = (shortfall/Double.valueOf(target))*100;
		}
		String tmp = String.format("%.0f", perc);
		tmp = tmp+"%";
		return tmp;
		
	}

	private String getShortFallCount(Long cnt, Integer target) {
		 
		Long shorfall = Long.valueOf(target)- Long.valueOf(cnt);
		return String.valueOf(shorfall);
	}

	private String getAchievmentPercentage(Long cnt, Integer target) {
		Double perc = 0D;
		////System.out.println("target "+target);
	
		if(target>0) {
			perc = (Double.valueOf(cnt)/Double.valueOf(target))*100;
			
		}
		String tmp = String.format("%.0f", perc);
		tmp = tmp+"%";
		return tmp;
	}

	
	private TargetRoleRes getEmployeeRoleInfo(String empId) {
		String tmpQuery = dmsEmpByidQuery.replaceAll("<EMP_ID>",String.valueOf(empId));
		
		tmpQuery = roleMapQuery.replaceAll("<EMP_ID>",String.valueOf(empId));
		List<Object[]> data = entityManager.createNativeQuery(tmpQuery).getResultList();
		TargetRoleRes trRoot = new TargetRoleRes();
		for(Object[] arr : data) {
			trRoot.setOrgId(String.valueOf(arr[0]));
			trRoot.setBranchId(String.valueOf(arr[1]));
			trRoot.setEmpId(String.valueOf(arr[2]));
			trRoot.setRoleName(String.valueOf(arr[3]));
			trRoot.setRoleId(String.valueOf(arr[4]));
		}
		
		return trRoot;
	}
	
	
	public String getFirstDayOfMonth() {
			return LocalDate.ofEpochDay(System.currentTimeMillis() / (24 * 60 * 60 * 1000) ).withDayOfMonth(1).toString()+" 00:00:00";
	}
	public String getLastDayOfMonth() {
			return LocalDate.ofEpochDay(System.currentTimeMillis() / (24 * 60 * 60 * 1000) ).plusMonths(1).withDayOfMonth(1).minusDays(1).toString()+" 23:59:59";
	}
	

	public String getTodayDateStart(String inputStartDate) {
		
		if (null == inputStartDate && null == inputStartDate) {
			return LocalDate.now().toString()+" 00:00:00";
		} else {
			return inputStartDate;
		}
			
	}
	

	public String getTodayDateEnd(String  inputStartDate) {
		if (null == inputStartDate && null == inputStartDate) {
			return LocalDate.now().toString()+" 23:59:59";
		} else {
			return inputStartDate;
		}
	}

	@Override
	public List<VehicleModelRes> getVehicleModelData(DashBoardReqV2 req) throws DynamicFormsServiceException {
		log.info("Inside getVehicleModelData(){}");
		List<VehicleModelRes> resList = new ArrayList<>();
		//System.out.println("model"+resList);
		try {

			Integer empId = req.getLoggedInEmpId();
			log.debug("Getting Target Data, LoggedIn emp id " + empId);
			
			List<Integer> selectedEmpIdList = req.getEmpSelected();
		
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(empId);

			String orgId = tRole.getOrgId();
			String branchId = tRole.getBranchId();

			Map<Integer, String> vehicleDataMap = dashBoardUtil.getVehilceDetails(orgId).get("main");
			List<String> vehicleModelList = new ArrayList<>();
			vehicleDataMap.forEach((k, v) -> {
				vehicleModelList.add(v);
			});
			List<Integer> selectedNodeList = req.getLevelSelected();
			resList = getVehicleModelData(getEmpReportingList(empId,selectedEmpIdList,selectedNodeList,orgId,branchId), req, orgId, branchId, vehicleModelList);
			Double totalBookingCnt=0D;
			for(VehicleModelRes vr: resList) {
				totalBookingCnt= totalBookingCnt+vr.getB();
			}
			for(VehicleModelRes vr: resList) {
				Long tmp = vr.getB();
				
				Double perc = Double.valueOf(tmp)/totalBookingCnt;
				perc = perc*100;
				String t = String.format("%.1f", perc);
				t = t+"%";
				vr.setBookingPercentage(t);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resList;
	}

/*
	private List<Integer> getEmpReportingList(Integer empId, List<Integer> selectedEmpIdList,List<Integer> selectedNodeList,String orgId, String selectedBranch) throws DynamicFormsServiceException {
		List<Integer> empReportingIdList = new ArrayList<>();
		if (null != selectedEmpIdList && !selectedEmpIdList.isEmpty()) {
			log.debug("Fetching empReportingIdList for selected employees");
			empReportingIdList.addAll(selectedEmpIdList);
			for (Integer eId : selectedEmpIdList) {
				empReportingIdList.addAll(getReportingEmployes(eId));
				log.debug("empReportingIdList for given selectedEmpIdList " + selectedEmpIdList);
			}

		} 
		else if(null!=selectedNodeList && !selectedNodeList.isEmpty()) {
			log.debug("Fetching empReportingIdList for selected LEVEL NODES");
			Map<String, Object> datamap = ohServiceImpl.getActiveDropdownsV2(selectedNodeList,Integer.parseInt(orgId),empId);
			datamap.forEach((k,v)->{
				Map<String, Object> innerMap = (Map<String, Object>)v;
				innerMap.forEach((x,y)->{
					List<TargetDropDownV2> dd = (List<TargetDropDownV2>)y;
					empReportingIdList.addAll(dd.stream().map(z->z.getCode()).map(Integer::parseInt).collect(Collectors.toList()));
				});
			});

		}
		
		else {
			empReportingIdList.add(empId);
			log.debug("Fetching empReportingIdList for logged in emp :" + empId);
			empReportingIdList.addAll(getReportingEmployes(empId));
			log.debug("empReportingIdList for emp " + empId);
		}
		
		if(null!=selectedBranch) {
			if(selectedBranch!="") {
				
			}
		}
		
		log.debug("empReportingIdList " + empReportingIdList);
		log.debug("empReportingIdList size:" + empReportingIdList.size());
		return empReportingIdList;
	}
*/
	private List<Integer> getEmpReportingList(Integer empId, List<Integer> selectedEmpIdList,List<Integer> selectedNodeList,String orgId, String selectedBranch) throws DynamicFormsServiceException {
		List<Integer> empReportingIdList = new ArrayList<>();
		log.debug("selectedBranch::"+selectedBranch);
		if (null != selectedEmpIdList && !selectedEmpIdList.isEmpty()) {
			if(null!=selectedBranch && !selectedBranch.isEmpty()) {
				log.debug("branch in geEmpReportinList in SelectedBranch ");
				empReportingIdList.addAll(selectedEmpIdList);
				for (Integer eId : selectedEmpIdList) {
					empReportingIdList.addAll(getReportingEmployesBranch(eId,selectedBranch));
					log.debug("empReportingIdList for given selectedEmpIdList " + selectedEmpIdList);
				}
				
			}else {
				log.debug("Fetching empReportingIdList for selected employees in else");
				empReportingIdList.addAll(selectedEmpIdList);
				for (Integer eId : selectedEmpIdList) {
					empReportingIdList.addAll(getReportingEmployes(eId));
					log.debug("empReportingIdList for given selectedEmpIdList " + selectedEmpIdList);
				}
			}

		} 
		else if(null!=selectedNodeList && !selectedNodeList.isEmpty()) {
			log.debug("Fetching empReportingIdList for selected LEVEL NODES");
			Map<String, Object> datamap = ohServiceImpl.getActiveDropdownsV2(selectedNodeList,Integer.parseInt(orgId),empId);
			
			if(null!=selectedBranch && !selectedBranch.isEmpty()) {
				datamap.forEach((k,v)->{
					if(k.equalsIgnoreCase(selectedBranch)) {
					log.debug("branch in geEmpReportinList in SelectedBranch"+k);
					Map<String, Object> innerMap = (Map<String, Object>)v;
					innerMap.forEach((x,y)->{
						List<TargetDropDownV2> dd = (List<TargetDropDownV2>)y;
						empReportingIdList.addAll(dd.stream().map(z->z.getCode()).map(Integer::parseInt).collect(Collectors.toList()));
					});
					}
				});
			}else {
				datamap.forEach((k,v)->{
					log.debug("branch in geEmpReportinList in else"+k);
					Map<String, Object> innerMap = (Map<String, Object>)v;
					innerMap.forEach((x,y)->{
						List<TargetDropDownV2> dd = (List<TargetDropDownV2>)y;
						empReportingIdList.addAll(dd.stream().map(z->z.getCode()).map(Integer::parseInt).collect(Collectors.toList()));
					});
				});
			}

		}
		
		
		else {
			if(null!=selectedBranch && !selectedBranch.isEmpty()) {
				log.debug("branch in geEmpReportinList in SelectedBranch in loop 3 ");
				empReportingIdList.add(empId);
				empReportingIdList.addAll(getReportingEmployesBranch(empId,selectedBranch));
			}else {
				log.debug("Fetching empReportingIdList for selected employees in else loop 3");
				empReportingIdList.add(empId);
				log.debug("Fetching empReportingIdList for logged in emp :" + empId);
				empReportingIdList.addAll(getReportingEmployes(empId));
				log.debug("empReportingIdList for emp " + empId);
			}
		}
		
		
		log.debug("empReportingIdList " + empReportingIdList);
		log.debug("empReportingIdList size:" + empReportingIdList.size());
		return empReportingIdList;
	}


	private List<VehicleModelRes> buildVehicleModelForBranch(String orgId, String branchId, DashBoardReqV2 req,
			List<String> vehicleModelList) {
			List<VehicleModelRes> resList = new ArrayList<>();
		   try {
			String branch = req.getBranchSelectionInEvents();
			log.info("Generating Data for Branch " + branch);
			List<Integer> empIdsUnderReporting = dashBoardUtil.getEmployeesUnderBranch(branch);
			log.debug("empIdsUnderReporting under branch: "+empIdsUnderReporting);
			resList = getVehicleModelData(empIdsUnderReporting,req,orgId,branchId,vehicleModelList);
		   }catch(Exception e) {
			   e.printStackTrace();
		   }
		return resList;
	}




	private List<VehicleModelRes> getVehicleModelData(List<Integer> empIdsUnderReporting, DashBoardReqV2 req,String orgId, String branchId,
			List<String> vehicleModelList) {
		List<VehicleModelRes> resList = new ArrayList<>();

		List<String> empNamesList = dmsEmployeeRepo.findEmpNamesById(empIdsUnderReporting);
		log.info("empNamesList::" + empNamesList);
		String startDate = getStartDate(req.getStartDate());
		String endDate = getEndDate(req.getEndDate());
		log.info("StartDate " + startDate + ", EndDate " + endDate);
		log.info("vehicleModelList ::" + vehicleModelList);
		for (String model : vehicleModelList) {
			if (null != model) {
				VehicleModelRes vehicleRes = new VehicleModelRes();
				log.info("Generating data for model " + model);
				List<DmsLead> dmsLeadList = dmsLeadDao.getAllEmployeeLeadsWithModel(orgId,empNamesList,startDate, endDate, model);
				
				
				Long enqLeadCnt = 0L;
				Long bookCount = 0L;
			
				List<Integer> dmsLeadIdList = dmsLeadList.stream().map(DmsLead::getId).collect(Collectors.toList());
				log.debug("dmsLeadList::"+dmsLeadList);
				List<LeadStageRefEntity> leadRefList  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadIdList,startDate,endDate);
				if(null!=leadRefList && !leadRefList.isEmpty()) {
					log.debug("Total leads in LeadReF table is ::"+leadRefList.size());
//					//System.out.println("Total leads in LeadReF table is ------ ::"+leadRefList.size());
//					for(LeadStageRefEntity refentity : leadRefList) {
//						//System.out.println("-------------"+refentity.getStageName());
//					}
					enqLeadCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).count();
					bookCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("BOOKING")).count();
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

				List<DmsWFTask> wfTaskList = dmsWfTaskDao.getWfTaskByAssigneeIdListByModel(empIdsUnderReporting,
						leadUniversalIdList, startDate, endDate);

				vehicleRes.setT(getTestDriveCount(wfTaskList));
				vehicleRes.setV(getHomeVisitCount(wfTaskList));
			//	vehicleRes.setB(getBookingCount(wfTaskList));
				vehicleRes.setB(bookCount);
				resList.add(vehicleRes);
			}
		}
		return resList;
	}
	
	


	public String getStartDate(String inputStartDate) {
		if (null == inputStartDate && null == inputStartDate) {
			return getFirstDayOfMonth();
		} else {
			return inputStartDate+" 00:00:00";
		}
	
	}
	
	public String getEndDate(String inputEndDate) {
		if (null == inputEndDate && null == inputEndDate) {
			return getLastDayOfMonth();
		} else {
			return inputEndDate+" 23:59:59";
		}
	}


	@Override
	public List<LeadSourceRes> getLeadSourceData(DashBoardReqV2 req) throws DynamicFormsServiceException {
		log.info("Inside getLeadSourceData(){}");
		List<LeadSourceRes> resList = new ArrayList<>();
		try {
			
			List<Integer> empReportingIdList = getEmployeeHiearachyData(Integer.parseInt(req.getOrgId()),req.getLoggedInEmpId());
			empReportingIdList.add(req.getLoggedInEmpId());
			
			Integer empId = req.getLoggedInEmpId();
			log.debug("Getting Target Data, LoggedIn emp id " + empId);
			
			List<Integer> selectedEmpIdList = req.getEmpSelected();
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(empId);

			
			String orgId = tRole.getOrgId();
			String branchId = tRole.getBranchId();

			Map<Integer, String> vehicleDataMap = dashBoardUtil.getVehilceDetails(orgId).get("main");
			List<String> vehicleModelList = new ArrayList<>();
			vehicleDataMap.forEach((k, v) -> {
				vehicleModelList.add(v);
			});
			List<Integer> selectedNodeList = req.getLevelSelected();
			String selectedBranch = req.getBranchSelectionInEvents();		
			resList = getLeadSourceData(empReportingIdList,req,orgId, branchId,vehicleModelList);
		}catch(Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resList;
	}




/*
	private List<LeadSourceRes> buildLeadSourceForBranch(String orgId,String branchId, DashBoardReqV2 req) {
			List<LeadSourceRes> resList = new ArrayList<>();
		   try {
			String branch = req.getBranchSelectionInEvents();
			log.info("Generating Data for Branch " + branch);
			List<Integer> empIdsUnderReporting = dashBoardUtil.getEmployeesUnderBranch(branch);
			log.debug("empIdsUnderReporting under branch: "+empIdsUnderReporting);
			resList = getLeadSourceData(empIdsUnderReporting,req,orgId,branchId);
		   }catch(Exception e) {
			   e.printStackTrace();
		   }
		return resList;
	}




	@Override
	public List<LeadSourceRes> getLeadSourceDataByBranch(DashBoardReqV2 req) {
		log.info("Inside getLeadSourceDataByBranch(){}");
		List<LeadSourceRes> list = new ArrayList<>();
		try {
			Integer empId = req.getLoggedInEmpId();
			TargetRoleRes tRole = getEmployeeRoleInfo(empId);
			
			list=buildLeadSourceForBranch(tRole.getOrgId(),tRole.getBranchId(),req);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}

*/
	




	private List<LeadSourceRes> getLeadSourceData(List<Integer> empIdsUnderReporting, DashBoardReqV2 req,String orgId,String branchId, List<String> vehicleModelList) {
		List<LeadSourceRes> resList = new ArrayList<>();

		List<String> empNamesList = dmsEmployeeRepo.findEmpNamesById(empIdsUnderReporting);
		log.info("empNamesList::" + empNamesList);

		String startDate = getStartDate(req.getStartDate());
		String endDate = getEndDate(req.getEndDate());
		log.info("StartDate " + startDate + ", EndDate " + endDate);

		getLeadTypes(orgId).forEach((k, v) -> {
			LeadSourceRes leadSource = new LeadSourceRes();
			log.debug("Generating data for Leadsource " + k + " and enq id " + v);
			//List<Integer> dmsAllLeadList = dmsLeadDao.getAllEmployeeLeadsBasedOnEnquiry1(orgId, empNamesList, startDate, endDate, v);

			
			//New Code Starts
			
			log.info("Generating data for LeadSource " + v);
			
			//New Code
			List<DmsEmployeeAllocation> dmsEmployeeAllocations = employeeAllocation.findByEmployeeId(req.getLoggedInEmpId());
			
			
			
			//List<Integer> dmsLeadList = dmsLeadDao.getLeadIdsByEmpNamesWithOutDrop(empNamesList);
			List<Integer> dmsLeadList = dmsLeadDao.getAllEmployeeLeadsBasedOnEnquiry1(orgId,empNamesList,startDate, endDate, v,vehicleModelList);
	        //System.out.println("dmsLeadList Before Adding"+dmsLeadList.size());

	        dmsLeadList.addAll(dmsEmployeeAllocations.stream().filter(res -> !res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")
					&& empNamesList.equals(res.getDmsLead().getSalesConsultant()) && res.getDmsLead().getDmsSourceOfEnquiry().getId()==v).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
			//dmsLeadList.addAll(dmsEmployeeAllocations.stream().filter(res -> !res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
			
			//System.out.println("dmsLeadList After Adding"+dmsLeadList.size());	
			
			List<Integer> dmsLeadListDropped = dmsLeadDao.getAllEmployeeLeadsBasedOnEnquiry1(orgId,empNamesList,startDate, endDate, v,vehicleModelList);
			
			//System.out.println("dmsLeadListDropped Before Adding"+dmsLeadListDropped.size());
			
			dmsLeadListDropped.addAll(dmsEmployeeAllocations.stream().filter(res -> res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")
					&& empNamesList.equals(res.getDmsLead().getSalesConsultant()) && res.getDmsLead().getDmsSourceOfEnquiry().getId()==v).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
			
			//System.out.println("dmsLeadListDropped After Adding"+dmsLeadListDropped.size());
			
			dmsLeadList = dmsLeadList.stream().distinct().collect(Collectors.toList());
			
			//System.out.println("dmsLeadList After Deleting Duplicates"+dmsLeadList.size());
			
			dmsLeadListDropped = dmsLeadListDropped.stream().distinct().collect(Collectors.toList());
			
			//System.out.println("dmsLeadListDropped After Deleting Duplicates"+dmsLeadListDropped.size());
			
			List<LeadStageRefEntity> leadRefList  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadList,startDate,endDate);
			
			//System.out.println("leadRefList Before Duplicates"+leadRefList.size());
			
			leadRefList = leadRefList.stream().distinct().collect(Collectors.toList());
			
			//System.out.println("leadRefList After Duplicates"+leadRefList.size());
			
			List<LeadStageRefEntity> leadRefListDropped  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadListDropped,startDate,endDate);
			
			//New Code Ends
			
			//New Code Ends
			//List<DmsLead> dmsAllLeadList = dmsLeadDao.getAllEmployeeLeasForDate(empNamesList, startDate, endDate);
			//log.debug("Size of dmsAllLeadList "+dmsAllLeadList.size());
			Long enqLeadCnt = 0L;
			Long preBookCount = 0L;
			Long bookCount = 0L;
			Long invCount = 0L;
			Long preDeliveryCnt = 0L;
			Long delCnt = 0L;
			Long droppedCnt =0L;
		
			log.debug("dmsLeadList::"+dmsLeadList);
				
			//List<LeadStageRefEntity> leadRefList  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadList,startDate,endDate);
			log.debug("leadRefList size "+leadRefList.size());
			if(null!=leadRefList && !leadRefList.isEmpty()) {
				
				log.debug("Total leads in LeadReF table is ::"+leadRefList.size());
				
				enqLeadCnt = leadRefList.stream().filter(x-> x.getStageName().equalsIgnoreCase("ENQUIRY")).distinct().count();
				preBookCount =leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("PREBOOKING")).count();
				bookCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("BOOKING")).distinct().count();
				invCount = leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(invCompStatus)).distinct().count();
				preDeliveryCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("PREDELIVERY")).count();
				delCnt = leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(delCompStatus)).count();
			}
			
			leadSource.setE(enqLeadCnt);
			leadSource.setB(bookCount);
			leadSource.setR(invCount);
			
			
			if(dmsLeadListDropped!=null && dmsLeadListDropped.size() > 0)
			{
			droppedCnt = dmsLeadListDropped.stream().distinct().count();
			}
			/*
			 * if (null != dmsAllLeadList) { log.info("size of dmsLeadList " +
			 * dmsAllLeadList.size()); //enqLeadCnt = getEnqLeadCount(dmsLeadList);
			 * droppedCnt = getDroppedCount(dmsAllLeadList);
			 * ///leadSource.setR(getInvoiceCount(dmsLeadList)); //log.info("enqLeadCnt: " +
			 * enqLeadCnt + " ,droppedCnt : " + droppedCnt); }
			 */
			
			
			leadSource.setLead(k);
			
			leadSource.setL(droppedCnt);
			
			List<String> leadUniversalIdList = leadRefList.stream().map(x->x.getUniversalId()).distinct().collect(Collectors.toList());


			/*
			 * List<String> leadUniversalIdList =
			 * dmsAllLeadList.stream().map(DmsLead::getCrmUniversalId)
			 * .collect(Collectors.toList());
			 */
			log.debug("leadUniversalIdList " + leadUniversalIdList);

			List<DmsWFTask> wfTaskList = dmsWfTaskDao.getWfTaskByAssigneeIdListByModel(empIdsUnderReporting,
					leadUniversalIdList, startDate, endDate);

			leadSource.setT(getTestDriveCount(wfTaskList));
			leadSource.setV(getHomeVisitCount(wfTaskList));
//			leadSource.setB(getBookingCount(wfTaskList));
			resList.add(leadSource);
			enqLeadCnt = 0L;
			bookCount = 0L;
			invCount = 0L;
		});
		return resList;

	}

	


//	private Map<String,Integer> getLeadTypes() {
//		Map<String,Integer> map = new LinkedHashMap<>();
//		
//		map.put("Reference",6);//6
//		map.put("Showroom",1); //1
//		map.put("Field",2);  //2
//		map.put("Social Network",5);//5
//		return map;
//	}
	
	private Map<String,Integer> getLeadTypes(String orgId){
		
		List<SourceAndId> reslist=repository.getSources(orgId);
		//System.out.println("reslist"+reslist);
		Map<String,Integer> map = new LinkedHashMap<>();
		reslist.stream().forEach(res->
		{
			map.put(res.getName(), res.getId());
			
		});
		return map;
	}


	
	@Override
	public List<EventDataRes> getEventSourceData(DashBoardReqV2 req) throws DynamicFormsServiceException {
	log.info("Inside getEventSourceData(){}");
		List<EventDataRes> resList = new ArrayList<>();
		try {
			Integer empId = req.getLoggedInEmpId();
			log.debug("Getting Target Data, LoggedIn emp id " + empId);
			
			List<Integer> selectedEmpIdList = req.getEmpSelected();
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(empId);

			String orgId = tRole.getOrgId();
			String branchId = tRole.getBranchId();
			List<Integer> selectedNodeList = req.getLevelSelected();
			resList = getEventSourceData(getEmpReportingList(empId,selectedEmpIdList,selectedNodeList,orgId,null),req,orgId, branchId);
		}catch(Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resList;
	}


	

	private List<EventDataRes> buildEventSourceForBranch(TargetRoleRes loggedInUserRoleData, DashBoardReqV2 req) {
			List<EventDataRes> resList = new ArrayList<>();
		   try {
			String branch = req.getBranchSelectionInEvents();
			log.info("Generating Data for Branch " + branch);
			List<Integer> empIdsUnderReporting = dashBoardUtil.getEmployeesUnderBranch(branch);
			log.debug("empIdsUnderReporting under branch: "+empIdsUnderReporting);
			resList = getEventSourceData(empIdsUnderReporting,req,loggedInUserRoleData.getOrgId(),loggedInUserRoleData.getBranchId());
		   }catch(Exception e) {
			   e.printStackTrace();
		   }
		return resList;
	}



	

	
	private List<EventDataRes> getEventSourceData(List<Integer> empIdsUnderReporting, DashBoardReqV2 req,
			String orgId,String branchId) {
		log.info("Inside getEventSourceData()");
		List<EventDataRes> resList = new ArrayList<>();
		List<String> empNamesList = dmsEmployeeRepo.findEmpNamesById(empIdsUnderReporting);
		log.info("empNamesList::" + empNamesList);
		String startDate = getStartDate(req.getStartDate());
		String endDate = getEndDate(req.getEndDate());
		log.info("StartDate " + startDate + ", EndDate " + endDate);
	
		List<DmsLead> dmsLeadList = dmsLeadDao.getAllEmployeeLeadsBasedOnEnquiry(orgId,empNamesList, startDate, endDate, 4);
		Set<String> eventCodeSet = dmsLeadList.stream().map(DmsLead::getEventCode).collect(Collectors.toSet());
		log.debug("eventCodeSet :" + eventCodeSet);
		
		

		eventCodeSet.forEach(x -> {
			log.debug("Generating data for EventSource " + x);
			EventDataRes EventSource = new EventDataRes();
			//Long enqLeadCnt = 0L;
			//Long droppedCnt = 0L;
			
			Long enqLeadCnt = 0L;
			Long preBookCount = 0L;
			Long bookCount = 0L;
			Long invCount = 0L;
			Long preDeliveryCnt = 0L;
			Long delCnt = 0L;
			Long droppedCnt =0L;
			List<Integer> dmsLeadIdList = dmsLeadList.stream().map(y->y.getId()).collect(Collectors.toList());
			List<LeadStageRefEntity> leadRefList  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadIdList,startDate,endDate);
			log.debug("leadRefList size "+leadRefList.size());
			if(null!=leadRefList && !leadRefList.isEmpty()) {
				
				log.debug("Total leads in LeadReF table is ::"+leadRefList.size());
				/*enqLeadCnt = leadRefList.stream().filter(k->k.getStageName().equalsIgnoreCase("ENQUIRY")).count();
				bookCount = leadRefList.stream().filter(k->k.getStageName().equalsIgnoreCase("BOOKING")).count();
				invCount = leadRefList.stream().filter(k->k.getStageName().equalsIgnoreCase("INVOICE")).count();
				preDeliveryCnt = leadRefList.stream().filter(k->k.getStageName().equalsIgnoreCase("PREDELIVERY")).count();
				delCnt = leadRefList.stream().filter(k->k.getStageName().equalsIgnoreCase("DELIVERY")).count();*/
				
				enqLeadCnt = leadRefList.stream().filter(k-> k.getLeadStatus()!=null &&  k.getLeadStatus().equalsIgnoreCase(enqCompStatus)).count();
				preBookCount =leadRefList.stream().filter(k->k.getStageName().equalsIgnoreCase("PREBOOKING")).count();
				bookCount = leadRefList.stream().filter(k->k.getLeadStatus()!=null && k.getLeadStatus().equalsIgnoreCase(bookCompStatus)).count();
				invCount = leadRefList.stream().filter(k->k.getLeadStatus()!=null && k.getLeadStatus().equalsIgnoreCase(invCompStatus)).count();
				preDeliveryCnt = leadRefList.stream().filter(k->k.getStageName().equalsIgnoreCase("PREDELIVERY")).count();
				delCnt = leadRefList.stream().filter(k->k.getLeadStatus()!=null && k.getLeadStatus().equalsIgnoreCase(delCompStatus)).count();
			}

			

			if (null != dmsLeadList) {
				log.info("size of dmsLeadList " + dmsLeadList.size());
				//enqLeadCnt = getEnqLeadCount(dmsLeadList);
				droppedCnt = getDroppedCount(dmsLeadList);
				EventSource.setR(getInvoiceCount(dmsLeadList));
				log.info("enqLeadCnt: " + enqLeadCnt + " ,droppedCnt : " + droppedCnt);
			}
			EventSource.setEventName(dashBoardUtil.getEventNameFromCode(x));
			EventSource.setE(enqLeadCnt);
			EventSource.setL(droppedCnt);

			List<String> leadUniversalIdList = dmsLeadList.stream().map(DmsLead::getCrmUniversalId)
					.collect(Collectors.toList());
			log.debug("leadUniversalIdList " + leadUniversalIdList);

			List<DmsWFTask> wfTaskList = dmsWfTaskDao.getWfTaskByAssigneeIdListByModel(empIdsUnderReporting,
					leadUniversalIdList, startDate, endDate);

			
			
			
		
			
			//EventSource.setT(getTestDriveCount(wfTaskList));
			//EventSource.setB(getBookingCount(wfTaskList));
			//EventSource.setV(getHomeVisitCount(wfTaskList));
			
			EventSource.setT(getTestDriveCount(wfTaskList));
			EventSource.setB(bookCount);
			EventSource.setV(getHomeVisitCount(wfTaskList));
			
			resList.add(EventSource);

		});
		return resList;
	}


	private Long getExchangeCount(List<DmsWFTask> wfTaskList) {
		return wfTaskList.stream().filter(x->x.getTaskName().equalsIgnoreCase(VERIFY_EXCHANGE_APPROVAL)).count();
	}


	private Long getHomeVisitCount(List<DmsWFTask> wfTaskList) {
		return wfTaskList.stream().filter(x->x.getTaskName().equalsIgnoreCase(HOME_VISIT) && x.getTaskStatus().equalsIgnoreCase("CLOSED")).count();
	}
	
	private Long getHomeVisitCountAssigned(List<DmsWFTask> wfTaskList) {
		return wfTaskList.stream().filter(x->x.getTaskName().equalsIgnoreCase(HOME_VISIT)).count();
	}

	private Long getInvoiceCount(List<DmsLead> dmsLeadList) {
		/*return wfTaskList.stream().
				filter(x->(x.getTaskName().equalsIgnoreCase(READY_FOR_INVOICE)
						|| x.getTaskName().equalsIgnoreCase(PROCEED_TO_INVOICE)
						|| x.getTaskName().equalsIgnoreCase(INVOICE_FOLLOWUP_DSE))).count();*/
		return dmsLeadList.stream().filter(x->x.getLeadStage().equalsIgnoreCase(INVOICE)).count();
	}
	
	private Long getInvoiceCountTarget(List<DmsWFTask> wfTaskList) {
		/*return wfTaskList.stream().
				filter(x->(x.getTaskName().equalsIgnoreCase(READY_FOR_INVOICE)
						|| x.getTaskName().equalsIgnoreCase(PROCEED_TO_INVOICE)
						|| x.getTaskName().equalsIgnoreCase(INVOICE_FOLLOWUP_DSE))).count();*/
		return wfTaskList.stream().filter(x->x.getTaskName().equalsIgnoreCase(INVOICE)).count();
	}
	private Long getBookingCount(List<DmsWFTask> wfTaskList) {
		return wfTaskList.stream().
				filter(x->(x.getTaskName().equalsIgnoreCase(PROCEED_TO_BOOKING)
						|| x.getTaskName().equalsIgnoreCase(BOOKING_FOLLOWUP_DSE)
						|| x.getTaskName().equalsIgnoreCase(BOOKING_FOLLOWUP_CRM))).count();
	}


	private Long getAccessoriesCount(List<Integer> leadIdList) {
		
		 return deliveryDao.getAccessoriesAmt(leadIdList);
     	
		
	}


	private Long getInsuranceCount(List<DmsWFTask> wfTaskList) {
			return  wfTaskList.stream().filter(x->x.getTaskName().equalsIgnoreCase(INSURANCE)).count();
	}


	private Long getFinanceCount(List<DmsWFTask> wfTaskList) {
			return wfTaskList.stream().filter(x->x.getTaskName().equalsIgnoreCase(FINANCE)).count();
	}

	
	
	private Long getVideoConfCount(List<DmsWFTask> wfTaskList) {
		return wfTaskList.stream().filter(x->x.getTaskName().equalsIgnoreCase(VIDEO_CONFERENCE)).count();
	}


	private Long getDroppedCount(List<DmsLead> dmsLeadList) {
		return dmsLeadList.stream().filter(x->x.getLeadStage().equalsIgnoreCase(DROPPED)).count();
	}


	private Long getEnqLeadCount(List<DmsLead> dmsLeadList) {
		return dmsLeadList.stream().filter(x->x.getLeadStage().equalsIgnoreCase(ENQUIRY)).count();
	}


	public Long getTestDriveCount(List<DmsWFTask> wfTaskList) {
		//TEST_DRIVE_APPROVAL
		return wfTaskList.stream().filter(x->(x.getTaskName().equalsIgnoreCase(TEST_DRIVE) && x.getTaskStatus().equalsIgnoreCase("CLOSED")) ).count();
	}

	public Long getTestDriveCountAssigned(List<DmsWFTask> wfTaskList) {
		return wfTaskList.stream().filter(x->x.getTaskName().equalsIgnoreCase(TEST_DRIVE)).count();
	}

	@Override
	public Map<String, Object> getLostDropData(DashBoardReqV2 req) throws DynamicFormsServiceException {
		log.info("Inside getLostDropData(){}");
		Map<String, Object> list = new LinkedHashMap<>();
		try {
			Integer empId = req.getLoggedInEmpId();
			log.debug("Getting Target Data, LoggedIn emp id " + empId);

			List<Integer> selectedEmpIdList = req.getEmpSelected();
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(empId);

			String orgId = tRole.getOrgId();
			String branchId = tRole.getBranchId();
			List<Integer> selectedNodeList = req.getLevelSelected();
			list = getLostDropData(getEmpReportingList(empId, selectedEmpIdList,selectedNodeList,orgId,null), req, orgId, branchId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return list;
	}




		
	private Map<String,Object> getLostDropData(List<Integer> empIdsUnderReporting, DashBoardReqV2 req,
			String orgId,String branchId) {
		log.info("Inside getLostDropData()");
		Map<String,Object> map = new LinkedHashMap<>();
		List<String> empNamesList = dmsEmployeeRepo.findEmpNamesById(empIdsUnderReporting);
		log.info("empNamesList::" + empNamesList);
		String startDate = getStartDate(req.getStartDate());
		String endDate = getEndDate(req.getEndDate());
		log.info("StartDate " + startDate + ", EndDate " + endDate);
		Map<String,Map<Integer,String>> vehicleDetails = dashBoardUtil.getVehilceDetails(orgId);
		
		map.putAll(getEnquiryLostData(vehicleDetails,orgId,branchId,startDate,endDate,empNamesList,empIdsUnderReporting));
		map.putAll(getBookingCancelledData(vehicleDetails,orgId,branchId,startDate,endDate,empNamesList,empIdsUnderReporting));
		return map;
	}


	private Map<String, Object> getBookingCancelledData(Map<String, Map<Integer, String>> vehicleDetails,String orgId,String branchId,
			String startDate, String endDate, List<String> empNamesList, List<Integer> empIdsUnderReporting) {
	
		Map<String, Object> responseMap = new LinkedHashMap<>();
		List<DropRes> dropResList = new ArrayList<>();
		vehicleDetails.get("main").forEach((k, v) -> {
			log.debug("Generating data for " + v);
			List<String> list = new ArrayList<>();
			list.add(v);
			dropResList.add(buildDropData(orgId,branchId,startDate,endDate,empNamesList,v,k,list));
		});
		List<DropRes> dropResOthersList = new ArrayList<>();
		vehicleDetails.get("others").forEach((k, v) -> {
			log.debug("Generating Others data for"+v);
			List<String> list = new ArrayList<>();
			list.add(v);
			dropResOthersList.add(buildDropData(orgId,branchId,startDate,endDate,empNamesList,v,k,list));
		});
		
		log.debug("dropResList "+dropResList);
		log.debug("dropResOthersList "+dropResOthersList);
		DropRes otherDropRes = new DropRes();
		int otherDropCount =0; 
		Long otherDropAmt = 0L;
		for(DropRes lr : dropResOthersList) {
			otherDropCount = otherDropCount+lr.getDropCount();
			otherDropAmt = otherDropAmt+lr.getDropAmount();
		}
		otherDropRes.setModelName("others");
		otherDropRes.setDropAmount(otherDropAmt);
		otherDropRes.setDropCount(otherDropCount);
		log.debug("otherDropRes "+otherDropRes);
		
		dropResList.add(otherDropRes);		
		
		Integer totalDropCnt = dropResList.stream().collect(Collectors.summingInt(DropRes::getDropCount));
		log.debug("totalDropCnt ::"+totalDropCnt);
		
		for(DropRes lr : dropResList) {
			Double perc = 0D;
			if(totalDropCnt!=0) {
				perc = (lr.getDropCount()/Double.valueOf(totalDropCnt))*100;
			}
			lr.setDropPercentage(String.format("%.2f", perc));
		}
		responseMap.put("dropData", dropResList);
		responseMap.put("totalBkgCancelledRevenue", dropResList.stream().collect(Collectors.summingLong(DropRes::getDropAmount)));
		return responseMap;
	}


	private DropRes buildDropData(String orgId,String branchId, String startDate, String endDate, List<String> empNamesList,String model,Integer
			modelId,List<String> list) {
		List<DmsLead> dmsLeadList = dmsLeadDao.getAllEmployeeLeadsWithModelandStage(orgId, empNamesList, startDate, endDate, list, DROPPED);
		List<Integer> dropList = dmsLeadDropDao.getLeads(dmsLeadList.stream().map(DmsLead::getId).collect(Collectors.toList()), BOOKING);
		
		int lostCnt = 0;
		List<DmsLead> filteredDmsList = new ArrayList<>();
		if (null != dropList) {
			lostCnt = dropList.size();
			log.debug("lostCnt " + lostCnt);
			for(DmsLead lead : dmsLeadList) {
				if(dropList.contains(lead.getId())) {
					filteredDmsList.add(lead);
				}
			}
			log.debug("filteredDmsList " + filteredDmsList.size());
		}
		//Map<Integer,Long> variantMap = new HashMap<>();
		Long dropAmt=0L;
		for(DmsLead l: filteredDmsList) {
			int variantId = dashBoardUtil.getVariantId(l.getId());
			Long price = dashBoardUtil.getVehiclePrice(orgId,modelId,variantId).longValue();
			dropAmt = dropAmt+price;
			log.debug("variantId "+variantId+",price "+price+" Lead Id :"+l.getId());
			
		}
		log.debug("dropAmt:: "+dropAmt);
		DropRes dropRes = new DropRes();
		dropRes.setModelName(model);
		dropRes.setDropCount(lostCnt);
		dropRes.setModelId(modelId);
		dropRes.setDropAmount(dropAmt);
		return dropRes;
	}


	private Map<String, Object> getEnquiryLostData(Map<String, Map<Integer, String>> vehicleDetails, String orgId,String branchId,
			String startDate, String endDate, List<String> empNamesList, List<Integer> empIdsUnderReporting) {
		Map<String, Object> responseMap = new LinkedHashMap<>();
		List<LostRes> lostResList = new ArrayList<>();
		vehicleDetails.get("main").forEach((k, v) -> {
			log.debug("Generating data for " + v);
			List<String> list = new ArrayList<>();
			list.add(v);
			lostResList.add(buildLostData(orgId,branchId,startDate,endDate,empNamesList,v,k,list));
		});
		List<LostRes> lostResOthersList = new ArrayList<>();
		vehicleDetails.get("others").forEach((k, v) -> {
			log.debug("Generating Others data for"+v);
			List<String> list = new ArrayList<>();
			list.add(v);
			lostResOthersList.add(buildLostData(orgId,branchId,startDate,endDate,empNamesList,v,k,list));
		});
		
		log.debug("lostResList "+lostResList);
		log.debug("lostResOthersList "+lostResOthersList);
		LostRes otherLostRes = new LostRes();
		int otherLostCount =0; 
		Long otherLostAmt = 0L;
		for(LostRes lr : lostResOthersList) {
			otherLostCount = otherLostCount+lr.getLostCount();
			otherLostAmt = otherLostAmt+lr.getLostAmount();
		}
		otherLostRes.setModelName("others");
		otherLostRes.setLostAmount(otherLostAmt);
		otherLostRes.setLostCount(otherLostCount);
		log.debug("otherLostRes "+otherLostRes);
		
		lostResList.add(otherLostRes);		
		
		Integer totalLostCnt = lostResList.stream().collect(Collectors.summingInt(LostRes::getLostCount));
		log.debug("totalLostCnt ::"+totalLostCnt);
		
		for(LostRes lr : lostResList) {
			Double perc =0D;
			if(totalLostCnt!=0) {
				perc = (lr.getLostCount()/Double.valueOf(totalLostCnt))*100;
			}
			lr.setLostPercentage(String.format("%.2f", perc));
		}
		responseMap.put("lostData", lostResList);
		responseMap.put("totalEnqLostRevenue", lostResList.stream().collect(Collectors.summingLong(LostRes::getLostAmount)));
		return responseMap;
	}
	
	private LostRes buildLostData(String orgId,String branchId,String startDate, String endDate, List<String> empNamesList,String model,Integer modelId,List<String> list) {
		List<DmsLead> dmsLeadList = dmsLeadDao.getAllEmployeeLeadsWithModelandStage(orgId, empNamesList, startDate, endDate, list, DROPPED);
		List<Integer> dropList = dmsLeadDropDao.getLeads(dmsLeadList.stream().map(DmsLead::getId).collect(Collectors.toList()), ENQUIRY);
		
		int lostCnt = 0;
		List<DmsLead> filteredDmsList = new ArrayList<>();
		if (null != dropList) {
			lostCnt = dropList.size();
			log.debug("lostCnt " + lostCnt);
			for(DmsLead lead : dmsLeadList) {
				if(dropList.contains(lead.getId())) {
					filteredDmsList.add(lead);
				}
			}
			log.debug("filteredDmsList " + filteredDmsList.size());
		}
		//Map<Integer,Long> variantMap = new HashMap<>();
		Long lostAmt=0L;
		for(DmsLead l: filteredDmsList) {
			int variantId = dashBoardUtil.getVariantId(l.getId());
			Long price = dashBoardUtil.getVehiclePrice(orgId,modelId,variantId).longValue();
			lostAmt = lostAmt+price;
			log.debug("variantId "+variantId+",price "+price+" Lead Id :"+l.getId());
			
		}
		log.debug("variantMap "+lostAmt);
		LostRes lostRes = new LostRes();
		lostRes.setModelName(model);
		lostRes.setLostCount(lostCnt);
		lostRes.setModelId(modelId);
		lostRes.setLostAmount(lostAmt);
		return lostRes;
	}
	
	

	@Override
	public Map<String, Object> getTodaysPendingUpcomingData(DashBoardReqV2 req) throws DynamicFormsServiceException {
	log.info("Inside getTodaysPendingUpcomingData(){},pageNo "+req.getPageNo()+" and size:"+req.getSize());
	Map<String, Object>  list = new LinkedHashMap<>();
		
		try {
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(req.getLoggedInEmpId());
			Integer empId = req.getLoggedInEmpId();
			log.debug("Getting Target Data, LoggedIn emp id " + empId);
			List<Integer> selectedNodeList = req.getLevelSelected();
			list = getTodaysData(getEmpReportingList(empId, req.getEmpSelected(),selectedNodeList,tRole.getOrgId(),null), req);
			log.debug("List size in getTodaysPendingUpcomingData "+list.size());
			} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return list;
	}

	private Map<String, Object> getTodaysData(List<Integer> empIdsUnderReporting, DashBoardReqV2 req) {
		Map<String, Object> map = new LinkedHashMap<>();
		try {
			int totalCnt = empIdsUnderReporting.size();
			log.debug("empIdsUnderReporting in getTodaysData before pagination"+empIdsUnderReporting.size());
			empIdsUnderReporting = dashBoardUtil.getPaginatedList(empIdsUnderReporting, req.getPageNo(), req.getSize());
			log.debug("empIdsUnderReporting in getTodaysData "+empIdsUnderReporting.size());
			Map<String, Integer> paginationMap = new LinkedHashMap<>();
			paginationMap.put("totalCnt", totalCnt);
			paginationMap.put("pageNo", req.getPageNo());
			paginationMap.put("size", req.getSize());
			
			map.put("paginationData", paginationMap);
			map.put("todaysData", getTodayData(req,empIdsUnderReporting));
			map.put("upcomingData", getUpcomingData(req,empIdsUnderReporting));
			map.put("pendingData", getPendingData(req,empIdsUnderReporting));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}


	private List<TodaysRes> getPendingData(DashBoardReqV2 req, List<Integer> empIdsUnderReporting) {
		log.debug("Inside getPendingData()");
		List<TodaysRes> list = new ArrayList<>();
		int cnt=1;
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			log.debug("generating data for empId " + empId + " and empName:" + empName);
			String startDate = getStartDate(req.getStartDate());
			String endDate = getEndDate(req.getEndDate());
			String todaysDate = getTodaysDate();
			log.info("StartDate " + startDate + ", EndDate " + endDate + " ,todaysDate " + todaysDate);
			TodaysRes todaysRes = new TodaysRes();
			List<DmsWFTask> wfTaskList = dmsWfTaskDao.getTodaysUpcomingTasks(empId, startDate, endDate);
			wfTaskList = wfTaskList.stream().filter(wfTask->validatePendingTask(wfTask.getTaskUpdatedTime(), wfTask.getTaskCreatedTime())).collect(Collectors.toList());
			Long pendingCnt = 0L;
			todaysRes.setEmpName(empName);
			if(null!=wfTaskList) {
				pendingCnt = Long.valueOf(wfTaskList.size());
				todaysRes.setCall(getCallCount(wfTaskList));
				todaysRes.setV(getHomeVisitCountAssigned(wfTaskList));
				todaysRes.setTd(getTestDriveCountAssigned(wfTaskList));
				todaysRes.setD(getDeliveryCount(wfTaskList));
				todaysRes.setPb(getPreBookingCount(wfTaskList));
				todaysRes.setPending(pendingCnt);
			}
			todaysRes.setSNo(cnt++);
			list.add(todaysRes);
		}
		return list;
	}


	private String getTodaysDate() {
		return LocalDate.now().toString();
	}
	
	private String getYesterdaysDate() {
		return LocalDate.now().minusDays(1).toString();
	}


	private List<TodaysRes> getUpcomingData(DashBoardReqV2 req, List<Integer> empIdsUnderReporting) {
		log.debug("Inside getUpcomingData()");
		List<TodaysRes> list = new ArrayList<>();
		int cnt=1;
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			log.debug("generating data for empId " + empId + " and empName:" + empName);
			String startDate = getStartDate(req.getStartDate());
			String endDate = getEndDate(req.getEndDate());
			String todaysDate = getTodaysDate();
			log.info("StartDate " + startDate + ", EndDate " + endDate + " ,todaysDate " + todaysDate);
			
			TodaysRes todaysRes = new TodaysRes();
			List<DmsWFTask> wfTaskList = dmsWfTaskDao.getTodaysUpcomingTasks(empId, startDate, endDate);
			List<DmsWFTask> upcomingWfTaskList = wfTaskList.stream().filter(wfTask->validateUpcomingTask(wfTask.getTaskUpdatedTime(), wfTask.getTaskCreatedTime())).collect(Collectors.toList());
			List<DmsWFTask> pendingWfTaskList = wfTaskList.stream().filter(wfTask->validatePendingTask(wfTask.getTaskUpdatedTime(), wfTask.getTaskCreatedTime())).collect(Collectors.toList());
			Long pendingCnt = 0L;
			if(null!=pendingWfTaskList) {
				pendingCnt = Long.valueOf(pendingWfTaskList.size());
			}
			todaysRes.setEmpName(empName);
			if(null!=upcomingWfTaskList) {
				todaysRes.setCall(getCallCount(upcomingWfTaskList));
				todaysRes.setV(getHomeVisitCountAssigned(upcomingWfTaskList));
				todaysRes.setTd(getTestDriveCountAssigned(upcomingWfTaskList));
				todaysRes.setD(getDeliveryCount(upcomingWfTaskList));
				todaysRes.setPb(getPreBookingCount(upcomingWfTaskList));
			}
			todaysRes.setPending(pendingCnt);
			todaysRes.setSNo(cnt++);
			list.add(todaysRes);
		
		}
		return list;
	
	}



	private List<TodaysRes> getTodayData(DashBoardReqV2 req, List<Integer> empIdsUnderReporting)
			throws ParseException {
		log.debug("Inside getTodayData()");
		int cnt=1;
		List<TodaysRes> list = new ArrayList<>();
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			log.debug("generating data for empId " + empId + " and empName:" + empName);
			String startDate = getStartDate(req.getStartDate());
			String endDate = getEndDate(req.getEndDate());
			String todaysDate = getTodaysDate();
			log.info("StartDate " + startDate + ", EndDate " + endDate + " ,todaysDate " + todaysDate);
			
			TodaysRes todaysRes = new TodaysRes();
			List<DmsWFTask> wfTaskList = dmsWfTaskDao.getTodaysUpcomingTasks(empId, startDate, endDate);
			List<DmsWFTask> todayWfTaskList = wfTaskList.stream().filter(wfTask->validateTodaysDate(wfTask.getTaskUpdatedTime(), wfTask.getTaskCreatedTime())).collect(Collectors.toList());
			List<DmsWFTask> pendingWfTaskList = wfTaskList.stream().filter(wfTask->validatePendingTask(wfTask.getTaskUpdatedTime(), wfTask.getTaskCreatedTime())).collect(Collectors.toList());
			Long pendingCnt = 0L;
			if(null!=pendingWfTaskList) {
				pendingCnt = Long.valueOf(pendingWfTaskList.size());
			}
			todaysRes.setEmpName(empName);
			if(null!=todayWfTaskList) {
				todaysRes.setCall(getCallCount(todayWfTaskList));
				todaysRes.setV(getHomeVisitCountAssigned(todayWfTaskList));
				todaysRes.setTd(getTestDriveCountAssigned(todayWfTaskList));
				todaysRes.setD(getDeliveryCount(todayWfTaskList));;
				todaysRes.setPb(getPreBookingCount(todayWfTaskList));
			}
			todaysRes.setPending(pendingCnt);
			todaysRes.setSNo(cnt++);;
			list.add(todaysRes);
		
		}
		log.debug("getTodayData data size "+list.size());
		return list;
	}

	private Long getPreBookingCount(List<DmsWFTask> wfTaskList) {
		return wfTaskList.stream().filter(x->StringUtils.containsIgnoreCase(x.getTaskName(), "Booking Follow Up - DSE")).count();
	}


	private Long getDeliveryCount(List<DmsWFTask> wfTaskList) {
		return wfTaskList.stream().filter(x->x.getTaskName().equalsIgnoreCase("Delivery Follow UP")).count();
	}


	private Long getCallCount(List<DmsWFTask> wfTaskList) {
		return wfTaskList.stream().filter(x->StringUtils.containsIgnoreCase(x.getTaskName(), "Enquiry Follow Up")).count();
	}
	


	private boolean validateTodaysDate(String taskUpdatedTime, String taskCreatedTime) {
		log.debug("inside validateTodaysDate()");
		boolean flag = false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date updatedDate = null;
			if (taskUpdatedTime != null) {
				updatedDate = sdf.parse(taskUpdatedTime);
			} else {
				updatedDate = sdf.parse(taskCreatedTime);
			}
			Date todaysDate = sdf.parse(LocalDate.now().toString());
			flag = updatedDate.equals(todaysDate);
			log.debug("updatedDate " + updatedDate + ", and todays date "+ todaysDate+ " and flag is "+flag);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	

	private boolean validateUpcomingTask(String taskUpdatedTime, String taskCreatedTime) {
		boolean flag = false;
		try {
			log.debug("inside validateUpcomingTask");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date updatedDate = null;
			if (taskUpdatedTime != null) {
				updatedDate = sdf.parse(taskUpdatedTime);
			} else {
				updatedDate = sdf.parse(taskCreatedTime);
			}
			Date todaysDate = sdf.parse(LocalDate.now().toString());
			flag = updatedDate.after(todaysDate);
			log.debug("updatedDate " + updatedDate + ", and todays date "+ todaysDate+ " and flag is "+flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	
	private boolean validatePendingTask(String taskUpdatedTime, String taskCreatedTime) {
		boolean flag = false;
		try {
			log.debug("inside validatePendingTask");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date updatedDate = null;
			if (taskUpdatedTime != null) {
				updatedDate = sdf.parse(taskUpdatedTime);
			} else {
				updatedDate = sdf.parse(taskCreatedTime);
			}
			Date todaysDate = sdf.parse(LocalDate.now().toString());
			flag = updatedDate.before(todaysDate);
			log.debug("updatedDate " + updatedDate + ", and todays date "+ todaysDate+ " and flag is "+flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}


	@Override
	public List<VehicleModelRes> getVehicleModelDataByBranch(DashBoardReqV2 req) throws DynamicFormsServiceException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<LeadSourceRes> getLeadSourceDataByBranch(DashBoardReqV2 req) throws DynamicFormsServiceException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<EventDataRes> getEventSourceDataByBranch(DashBoardReqV2 req) throws DynamicFormsServiceException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Map<String, Object> getLostDropDataByBranch(DashBoardReqV2 req) throws DynamicFormsServiceException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public SalesDataRes getSalesData(DashBoardReqV2 req) throws DynamicFormsServiceException {
		log.info("Inside getSalesData(){}");
		SalesDataRes salesRes = new SalesDataRes();
		List<Integer> empReportingIdList = new ArrayList<>();
		try {
			Integer empId = req.getLoggedInEmpId();
			log.debug("Getting Target Data, LoggedIn emp id " + empId);

			List<Integer> selectedEmpIdList = req.getEmpSelected();
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(empId);
			String orgId = tRole.getOrgId();
			List<Integer> selectedNodeList = req.getLevelSelected();
			empReportingIdList = getEmpReportingList(empId, selectedEmpIdList,selectedNodeList,orgId,null);
			List<String> empNamesList = dmsEmployeeRepo.findEmpNamesById(empReportingIdList);
			log.info("empNamesList::" + empNamesList);
			
			String startDate = null;
			String endDate = null;
			if (null == req.getStartDate() && null == req.getEndDate()) {
				startDate = getFirstDayOfMonth();
				endDate = getLastDayOfMonth();
			} else {
				startDate = req.getStartDate();
				endDate = req.getEndDate();
			}
			log.info("StartDate " + startDate + ", EndDate " + endDate);
			
			//salesRes.setTodaySales(buildTodaySales(empNamesList,startDate,endDate,orgId));
			//salesRes.setTodayVisitors(buildTodaySales(empNamesList,startDate,endDate,orgId));
			//salesRes.setTotalEarnings(buildTotalEarnings(empNamesList,startDate,endDate,orgId));
			salesRes.setPendingOrders(buildPendingOrders(req.getLoggedInEmpId(),empNamesList,startDate,endDate,orgId));
		//	salesRes.setTotalRevenue(buildTotalRevenue(empNamesList,startDate,endDate,orgId));
			salesRes.setTotalRevenue(salesRes.getTotalEarnings());
			salesRes.setDropRevenue(buildDropRevenue(empNamesList,startDate,endDate,orgId));
			salesRes.setComplaints(buildComplaints(empNamesList,startDate,endDate,orgId));
			salesRes.setLiveBookings(buildLivebookings(empNamesList,startDate,endDate,orgId));
			salesRes.setDeliveries(buildDeliveries(empNamesList,startDate,endDate,orgId));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return salesRes;
	}

	private Long buildDeliveries(List<String> empNamesList, String startDate, String endDate, String orgId) {
		log.info("Inside buildDeliveries(){}");
		Long cnt=0L;
		try {
			List<Integer> dmsLeadList = dmsLeadDao.getLeadIdsByEmpNamesWithOutDrop(empNamesList);
			log.debug("dmsLeadList::"+dmsLeadList);
			List<LeadStageRefEntity> leadRefList  =  leadStageRefDao.getLeadsByDeliveryStage(dmsLeadList,startDate,endDate);
			log.debug("leadRefList in buildDeliveries "+leadRefList.size());
			if(null!=leadRefList && !leadRefList.isEmpty()) {
				cnt = Long.valueOf(leadRefList.size());					
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return cnt;
	}

	@Autowired
	ComplaintTrackerDao complaintTrackerDao;


	private Long buildLivebookings(List<String> empNamesList, String startDate, String endDate, String orgId) {
		log.info("Inside buildLivebookings(){}");
		Long cnt=0L;
		try {
			List<Integer> dmsLeadList = dmsLeadDao.getLeadIdsByEmpNamesWithOutDrop(empNamesList);
			log.debug("dmsLeadList::"+dmsLeadList);
			List<LeadStageRefEntity> leadRefList  =  leadStageRefDao.getLeadsByBookingStage(dmsLeadList,startDate,endDate);
			
			if(null!=leadRefList && !leadRefList.isEmpty()) {
				log.debug("leadRefList is not empty,size is "+leadRefList.size());
				/*for(LeadStageRefEntity ref : leadRefList) {
					List<LeadStageRefEntity> list = leadStageRefDao.verifyActiveBooking(ref.getLeadId());
					if(list!=null && list.isEmpty()) {
						cnt++;
					}
					
				}*/
				cnt = Long.valueOf(leadRefList.size());
				log.debug("TOTAL LIVE BOOKINGS  "+cnt);
					
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return cnt;
	}



	@Autowired
	EmpLocationMappingDao empLocationMappingDao;
	
	@Autowired
	DmsBranchDao dmsBranchDao;
	
	private Long buildComplaints(List<String> empNamesList, String startDate, String endDate, String orgId) {
		log.info("Inside buildComplaints(){}");
		Long cnt=0L;
		try {
			List<Integer> idList = dmsEmployeeRepo.findEmpIdsByNames(empNamesList);
			log.debug("buildComplaints:IDLIST "+idList);
			
			Set<Integer> locationNodeList = empLocationMappingDao.findMappingsByEmpId(idList);
			//c
			List<String> branchNameList = dmsBranchDao.findBrancheNamesByIds(locationNodeList);
			log.debug("branchNameList::"+branchNameList);
			List<String> updatedBranchnameList = new ArrayList<>();
			for(String str:branchNameList) {
				if(null!=str && str.contains("-")) {
					updatedBranchnameList.add(str.split("-")[1]);
				}else {
					updatedBranchnameList.add(str);
				}
			}
			log.debug("updatedBranchnameList::"+updatedBranchnameList);
			List<ComplaintsTracker> list = complaintTrackerDao.findComplaintByBranchList(updatedBranchnameList);
			
			if(null!=list && list.size()>0) {
				cnt = Long.valueOf(list.size());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return cnt;
	}




	@Override
	public List<Map<String, Long>> getSalesComparsionData(DashBoardReqV2 req) throws DynamicFormsServiceException {
		log.info("Inside getSalesComparsionData(){}");
		List<Integer> empReportingIdList = new ArrayList<>();
		List<Map<String, Long>> subList = new ArrayList<>();
		List<Map<String, Long>> resList = new ArrayList<>();
		try {
			Integer empId = req.getLoggedInEmpId();
			log.debug("Getting getSalesComparsionData, LoggedIn emp id " + empId);

			List<Integer> selectedEmpIdList = req.getEmpSelected();
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(empId);
			String orgId = tRole.getOrgId();
			List<Integer> selectedNodeList = req.getLevelSelected();
			empReportingIdList = getEmpReportingList(empId, selectedEmpIdList, selectedNodeList, orgId, null);
			List<String> empNamesList = dmsEmployeeRepo.findEmpNamesById(empReportingIdList);
			log.info("empNamesList::" + empNamesList);
			String startDate = null;
			String endDate = null;
			if (null == req.getStartDate() && null == req.getEndDate()) {
				startDate = getFirstDayOfMonth();
				endDate = getLastDayOfMonth();
			} else {
				startDate = req.getStartDate();
				endDate = req.getEndDate();
			}
			log.info("StartDate " + startDate + ", EndDate " + endDate);
			log.info("StartDate " + startDate + ", EndDate " + endDate);

			List<String> leadStages = new ArrayList<>();
			//leadStages.add("DELIVERYCOMPLETED");
			leadStages.add("INVOICE");
			List<Integer> dmsAllLeadList = dmsLeadDao.getLeadsBasedonEmpNames(empNamesList, startDate, endDate, orgId);
			
			List<LeadStageRefEntity> dmsLeadStageIdList = new ArrayList<>();
			List<LeadStageRefEntity> leadRefList  =  leadStageRefDao.getLeadsByBookingStage(dmsAllLeadList,"INVOICE",startDate,endDate);
			if(null!=leadRefList && !leadRefList.isEmpty()) {
				
				log.debug("Total leads in LeadReF table is ::"+leadRefList.size());
				dmsLeadStageIdList = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("INVOICE")).collect(Collectors.toList());
			}
			
			List<DmsLead> dmsLeadList = dmsLeadDao.getLeadsBasedonId(dmsLeadStageIdList.stream().map(x->x.getLeadId()).collect(Collectors.toList()));
			log.debug("dmsLeadList in salesComparsion "+dmsLeadList.size());
		
			Map<String, Integer> map = dashBoardUtil.getVehilceDetailsByOrgId(orgId);
			log.debug("Vehicle details for org "+orgId + " is "+map);
			if (null != map) {
				map.forEach((k, v) -> {
					Map<String, Long> dataMap = new LinkedHashMap<>();
					dataMap.put(k, dmsLeadList.stream().filter(x -> x.getModel() != null && x.getModel().equalsIgnoreCase(k)).count());
					resList.add(dataMap);
				});
			}
			log.debug("resList::"+resList);;
			int cnt = 0;
			AtomicLong al = new AtomicLong(0);
			
			Map<String,Integer> map1 = new LinkedHashMap<>();
			for (Map<String, Long> m : resList) {
				for(Map.Entry<String, Integer> entry : map.entrySet()) {
					map1.put(entry.getKey(), entry.getValue());
				}
			 
			}
		/*
			
			map1 = map1.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			log.debug("map1::"+map1);
			
			*/
			for (Map<String, Long> m : resList) {
				if (cnt <= 20) {
					subList.add(m);
				} else {
					m.forEach((k, v) -> {
						al.addAndGet(v);
					});
				}
				cnt++;				
			}
			
			Map<String, Long> otherMap = new HashMap<>();
			if(al.get()!=0L) {
			otherMap.put("others", al.get());
			subList.add(otherMap);
		}

			log.debug("sales comparsion data " + subList);
			/*Code to calcuate the percentage of sales data*/
			
			//Iterate the list
			 Long totalSalesCount = 0L ;
				for (Map<String, Long> salesData : subList) {
					for(Long value : salesData.values()) {
						//Sum the total sales 
						totalSalesCount =  totalSalesCount + value;
					}
				}
			
			//Calculate percentage for each
				if(totalSalesCount  > 0L) {
				for (Map<String, Long> salesData : subList) {
					//add percentage to the map
						getSalesPercentage(salesData, totalSalesCount);
					}	
				}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return subList;
	}
	
	private Long buildDropRevenue(List<String> empNamesList, String startDate, String endDate, String orgId) throws DynamicFormsServiceException {
		Long amt=0L;
		log.debug("Inside buildDropRevenue");
		try {
			
			List<String> leadStages = new ArrayList<>();
			leadStages.add("DROPPED");
			List<DmsLead> dmsLeadList = dmsLeadDao.getLeadsBasedonStage(empNamesList,startDate, endDate,orgId,leadStages);
			Map<String,Integer> map = dashBoardUtil.getVehilceDetailsByOrgId(orgId);
			log.debug("dmsLeadList in buildDropRevenue::"+dmsLeadList);
			log.debug("Vehicle Details by OrgID "+map);
			if(null!=dmsLeadList) {
				
				for(DmsLead l: dmsLeadList) {
					int variantId = dashBoardUtil.getVariantId(l.getId());
					String model = l.getModel();
					Integer modelId = map.get(model);
					log.debug("model::"+model+",modelId:"+modelId);
					Long price = dashBoardUtil.getVehiclePrice(orgId,modelId,variantId).longValue();
					log.debug("price:::"+price);
					amt = amt+price;
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return amt;
	}


	private Long buildTotalRevenue(List<String> empNamesList, String startDate, String endDate, String orgId) throws DynamicFormsServiceException {
		Long amt=0L;
		log.debug("Inside buildTotalEarnings");
		try {
			
			List<String> leadStages = new ArrayList<>();
			leadStages.add("BOOKING");
			List<DmsLead> dmsLeadList = dmsLeadDao.getLeadsBasedonStage(empNamesList,startDate, endDate,orgId,leadStages);
			Map<String,Integer> map = dashBoardUtil.getVehilceDetailsByOrgId(orgId);
			if(null!=dmsLeadList) {
				
				for(DmsLead l: dmsLeadList) {
					int variantId = dashBoardUtil.getVariantId(l.getId());
					String model = l.getModel();
					Integer modelId = map.get(model);
					log.debug("model::"+model+",modelId:"+modelId);
					Long price = dashBoardUtil.getVehiclePrice(orgId,modelId,variantId).longValue();
					log.debug("price:::"+price);
					amt = amt+price;
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return amt;
	}


	private String buildPendingOrders(Integer empId, List<String> empNamesList, String startDate, String endDate, String orgId) throws DynamicFormsServiceException {
		String res = null;
		try {
			List<DmsWFTask> wfTaskList = dmsWfTaskDao.getTodaysUpcomingTasks(empId, startDate, endDate);
			wfTaskList = wfTaskList.stream().filter(wfTask->validatePendingTask(wfTask.getTaskUpdatedTime(), wfTask.getTaskCreatedTime())).collect(Collectors.toList());
			Long pendingCnt = 0L;
			
			if(null!=wfTaskList) {
				pendingCnt = Long.valueOf(wfTaskList.size());
			}
			
				res = String.valueOf(pendingCnt);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return res;
	}


	private Long buildTotalEarnings(List<String> empNamesList, String startDate, String endDate, String orgId) throws DynamicFormsServiceException {
		Long amt=0L;
		log.debug("Inside buildTotalEarnings");
		try {
			
			List<String> leadStages = new ArrayList<>();
			leadStages.add("BOOKING");
			List<DmsLead> dmsLeadList = dmsLeadDao.getLeadsBasedonStage(empNamesList,startDate, endDate,orgId,leadStages);
			Map<String,Integer> map = dashBoardUtil.getVehilceDetailsByOrgId(orgId);
			if(null!=dmsLeadList) {
				
				for(DmsLead l: dmsLeadList) {
					int variantId = dashBoardUtil.getVariantId(l.getId());
					String model = l.getModel();
					Integer modelId = map.get(model);
					log.debug("model::"+model+",modelId:"+modelId);
					Long price = dashBoardUtil.getVehiclePrice(orgId,modelId,variantId).longValue();
					log.debug("price:::"+price);
					amt = amt+price;
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return amt;
	}


	private String buildTodaySales(List<String> empNamesList, String startDate, String endDate, String orgId) throws DynamicFormsServiceException {
		String res = null;
		try {
			List<String> leadStages = new ArrayList<>();
			leadStages.add("BOOKING");
			List<DmsLead> dmsLeadList = dmsLeadDao.getLeadsBasedonStage(empNamesList,startDate, endDate,orgId,leadStages);
			if(null!=dmsLeadList) {
				res = String.valueOf(dmsLeadList.size());
			}else {
				res = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return res;
	}



	public List<TargetAchivement> buildFinalTargets(List<List<TargetAchivement>> allTargets) {

		List<TargetAchivement> resList = new ArrayList<>();
		Integer finalEnq = 0;
		Integer finalEnqTarget = 0;
		Double finalEnqAchivePerc = 0D;
		Integer finalEnqShortfall = 0;
		Double finalEnqShortfallPerc = 0D;

		Integer finalTD = 0;
		Integer finalTDTarget = 0;
		Double finalTDAchivePerc = 0D;
		Integer finalTDShortfall = 0;
		Double finalTDShortfallPerc = 0D;

		Integer finalFIN = 0;
		Integer finalFINTarget = 0;
		Double finalFINAchivePerc = 0D;
		Integer finalFINShortfall = 0;
		Double finalFINShortfallPerc = 0D;

		Integer finalINS = 0;
		Integer finalINSTarget = 0;
		Double finalINSAchivePerc = 0D;
		Integer finalINSShortfall = 0;
		Double finalINSShortfallPerc = 0D;

		Integer finalHV = 0;
		Integer finalHVTarget = 0;
		Double finalHVAchivePerc = 0D;
		Integer finalHVShortfall = 0;
		Double finalHVShortfallPerc = 0D;

		Integer finalBOOK = 0;
		Integer finalBOOKTarget = 0;
		Double finalBOOKAchivePerc = 0D;
		Integer finalBOOKShortfall = 0;
		Double finalBOOKShortfallPerc = 0D;

		Integer finalEXC = 0;
		Integer finalEXCTarget = 0;
		Double finalEXCAchivePerc = 0D;
		Integer finalEXCShortfall = 0;
		Double finalEXCShortfallPerc = 0D;

		Integer finalRETAIL = 0;
		Integer finalRETAILTarget = 0;
		Double finalRETAILAchivePerc = 0D;
		Integer finalRETAILShortfall = 0;
		Double finalRETAILShortfallPerc = 0D;

		Integer finalACC = 0;
		Integer finalACCTarget = 0;
		Double finalACCAchivePerc = 0D;
		Integer finalACCShortfall = 0;
		Double finalACCShortfallPerc = 0D;

		for (List<TargetAchivement> targetList : allTargets) {

			for (TargetAchivement target : targetList) {

				if (target.getParamName().equalsIgnoreCase(ENQUIRY)) {

					if (null != target.getAchievment()) {
						finalEnq = finalEnq + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						
						finalEnqTarget = finalEnqTarget + Integer.parseInt(target.getTarget());
					
					}
					////System.out.println("finalEnqAchivePerc::"+finalEnqAchivePerc);
					////System.out.println("target.getAchivementPerc()::"+target.getAchivementPerc());
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
					    finalEnqAchivePerc = finalEnqAchivePerc + Double.parseDouble(tmp);
					}
				
					if (null != target.getShortfall()) {
						log.debug("Integer.parseInt(target.getShortfall())  "+Integer.parseInt(target.getShortfall()));
						finalEnqShortfall = finalEnqShortfall + Integer.parseInt(target.getShortfall());
						log.debug("finalEnqShortfall::"+finalEnqShortfall);
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalEnqShortfallPerc = finalEnqShortfallPerc + Double.parseDouble(tmp);
					}
				}
				if (target.getParamName().equalsIgnoreCase(TEST_DRIVE)) {
					if (null != target.getAchievment()) {
						finalTD = finalTD + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalTDTarget = finalTDTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalTDAchivePerc = finalTDAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalTDShortfall = finalTDShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalTDShortfallPerc = finalTDShortfallPerc + Double.parseDouble(tmp);
					}
				}

				if (target.getParamName().equalsIgnoreCase(FINANCE)) {
					if (null != target.getAchievment()) {
						finalFIN = finalFIN + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalFINTarget = finalFINTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalFINAchivePerc = finalFINAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalFINShortfall = finalFINShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalFINShortfallPerc = finalFINShortfallPerc + Double.parseDouble(tmp);
					}

				}

				if (target.getParamName().equalsIgnoreCase(BOOKING)) {

					if (null != target.getAchievment()) {
						finalBOOK = finalBOOK + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalBOOKTarget = finalBOOKTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalBOOKAchivePerc = finalBOOKAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalBOOKShortfall = finalBOOKShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalBOOKShortfallPerc = finalBOOKShortfallPerc + Double.parseDouble(tmp);
					}
				}

				if (target.getParamName().equalsIgnoreCase(HOME_VISIT)) {
					if (null != target.getAchievment()) {
						finalHV = finalHV + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalHVTarget = finalHVTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalHVAchivePerc = finalHVAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalHVShortfall = finalHVShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalHVShortfallPerc = finalHVShortfallPerc + Double.parseDouble(tmp);
					}

				}

				if (target.getParamName().equalsIgnoreCase(INSURANCE)) {

					if (null != target.getAchievment()) {
						finalINS = finalINS + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalINSTarget = finalINSTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalINSAchivePerc = finalINSAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalINSShortfall = finalINSShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalINSShortfallPerc = finalINSShortfallPerc + Double.parseDouble(tmp);
					}
				}

				if (target.getParamName().equalsIgnoreCase(INVOICE)) {

					if (null != target.getAchievment()) {
						finalRETAIL = finalRETAIL + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalRETAILTarget = finalRETAILTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalRETAILAchivePerc = finalRETAILAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalRETAILShortfall = finalRETAILShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalRETAILShortfallPerc = finalRETAILShortfallPerc
								+ Double.parseDouble(tmp);
					}
				}

				if (target.getParamName().equalsIgnoreCase(EXCHANGE)) {

					if (null != target.getAchievment()) {
						finalEXC = finalEXC + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalEXCTarget = finalEXCTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalEXCAchivePerc = finalEXCAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalEXCShortfall = finalEXCShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalEXCShortfallPerc = finalEXCShortfallPerc + Double.parseDouble(tmp);
					}
				}

				if (target.getParamName().equalsIgnoreCase(EXCHANGE)) {

					if (null != target.getAchievment()) {
						finalACC = finalACC + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalACCTarget = finalACCTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalACCAchivePerc = finalACCAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalACCShortfall = finalACCShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalACCShortfallPerc = finalACCShortfallPerc + Double.parseDouble(tmp);
					}
				}

			}

		}

		TargetAchivement targetEnq = new TargetAchivement();
		targetEnq.setParamName(ENQUIRY);
		targetEnq.setParamShortName("Enq");
		targetEnq.setAchievment(String.valueOf(finalEnq));
		targetEnq.setTarget(String.valueOf(finalEnqTarget));
		targetEnq.setAchivementPerc(String.valueOf(finalEnqAchivePerc));
		targetEnq.setShortfall(String.valueOf(finalEnqShortfall));
		targetEnq.setShortFallPerc(String.valueOf(finalEnqShortfallPerc));

		TargetAchivement targetTD = new TargetAchivement();
		targetTD.setParamName(TEST_DRIVE);
		targetTD.setParamShortName("Tdr");
		targetTD.setAchievment(String.valueOf(finalTD));
		targetTD.setTarget(String.valueOf(finalTDTarget));
		targetTD.setAchivementPerc(String.valueOf(finalTDAchivePerc));
		targetTD.setShortfall(String.valueOf(finalTDShortfall));
		targetTD.setShortFallPerc(String.valueOf(finalTDShortfallPerc));

		resList.add(targetEnq);
		resList.add(targetTD);

		TargetAchivement targetFIN = new TargetAchivement();
		targetFIN.setParamName(FINANCE);targetTD.setParamShortName("Fin");
		targetFIN.setAchievment(String.valueOf(finalFIN));
		targetFIN.setTarget(String.valueOf(finalFINTarget));
		targetFIN.setAchivementPerc(String.valueOf(finalFINAchivePerc));
		targetFIN.setShortfall(String.valueOf(finalFINShortfall));
		targetFIN.setShortFallPerc(String.valueOf(finalFINShortfallPerc));

		resList.add(targetFIN);

		TargetAchivement targetINS = new TargetAchivement();
		targetINS.setParamName(INSURANCE);targetTD.setParamShortName("Ins");
		targetINS.setAchievment(String.valueOf(finalINS));
		targetINS.setTarget(String.valueOf(finalINSTarget));
		targetINS.setAchivementPerc(String.valueOf(finalINSAchivePerc));
		targetINS.setShortfall(String.valueOf(finalINSShortfall));
		targetINS.setShortFallPerc(String.valueOf(finalINSShortfallPerc));

		resList.add(targetINS);

		TargetAchivement targetHV = new TargetAchivement();
		targetHV.setParamName(HOME_VISIT);targetTD.setParamShortName("Hvt");
		targetHV.setAchievment(String.valueOf(finalHV));
		targetHV.setTarget(String.valueOf(finalHVTarget));
		targetHV.setAchivementPerc(String.valueOf(finalHVAchivePerc));
		targetHV.setShortfall(String.valueOf(finalHVShortfall));
		targetHV.setShortFallPerc(String.valueOf(finalHVShortfallPerc));

		resList.add(targetHV);

		TargetAchivement targetBOOK = new TargetAchivement();
		targetBOOK.setParamName(BOOKING);
		targetTD.setParamShortName("Bkg");
		targetBOOK.setAchievment(String.valueOf(finalBOOK));
		targetBOOK.setTarget(String.valueOf(finalBOOKTarget));
		targetBOOK.setAchivementPerc(String.valueOf(finalBOOKAchivePerc));
		targetBOOK.setShortfall(String.valueOf(finalBOOKShortfall));
		targetBOOK.setShortFallPerc(String.valueOf(finalBOOKShortfallPerc));

		resList.add(targetBOOK);

		TargetAchivement targetEXC = new TargetAchivement();
		targetEXC.setParamName(EXCHANGE);
		targetTD.setParamShortName("Exg");
		targetEXC.setAchievment(String.valueOf(finalEXC));
		targetEXC.setTarget(String.valueOf(finalEXCTarget));
		targetEXC.setAchivementPerc(String.valueOf(finalEXCAchivePerc));
		targetEXC.setShortfall(String.valueOf(finalEXCShortfall));
		targetEXC.setShortFallPerc(String.valueOf(finalEXCShortfallPerc));

		
		resList.add(targetEXC);

		TargetAchivement targetRETAIL = new TargetAchivement();
		targetRETAIL.setParamName(INVOICE);
		targetTD.setParamShortName("Ret");
		targetRETAIL.setAchievment(String.valueOf(finalRETAIL));
		targetRETAIL.setTarget(String.valueOf(finalRETAILTarget));
		targetRETAIL.setAchivementPerc(String.valueOf(finalRETAILAchivePerc));
		targetRETAIL.setShortfall(String.valueOf(finalRETAILShortfall));
		targetRETAIL.setShortFallPerc(String.valueOf(finalRETAILShortfallPerc));

		resList.add(targetRETAIL);

		TargetAchivement targetACC = new TargetAchivement();
		targetACC.setParamName(ACCCESSORIES);
		targetTD.setParamShortName("Acc");
		targetACC.setAchievment(String.valueOf(finalACC));
		targetACC.setTarget(String.valueOf(finalACCTarget));
		targetACC.setAchivementPerc(String.valueOf(finalACCAchivePerc));
		targetACC.setShortfall(String.valueOf(finalACCShortfall));
		targetACC.setShortFallPerc(String.valueOf(finalACCShortfallPerc));

		resList.add(targetACC);

		return resList;
	}
	
	private long salesPercentage(Long totalSalesCount , Long particularSaleCount) {
		
			long percenatge = (long)((particularSaleCount * 100)/totalSalesCount);
			
			return percenatge;
	}
		
	public void getSalesPercentage(Map<String, Long> salesData, Long totalSalesCount) {
		
		for(Long value : salesData.values()) {
			salesData.put("percentage", salesPercentage(totalSalesCount, value));
		}
		
	}



	@Autowired
	DashBoardServiceV3 dashBoardServiceV3;
	
	@Override
	public Map<String, Object> getTodaysPendingUpcomingDataV2(MyTaskReq req) throws DynamicFormsServiceException {
		log.info("Inside getTodaysPendingUpcomingDataV2(){},empId " + req.getLoggedInEmpId() + " and IsOnlyForEmp "
				+ req.isOnlyForEmp());
		Map<String, Object> list = new LinkedHashMap<>();

		try {
	
			boolean isOnlyForEmp =req.isOnlyForEmp();
			List<Integer> empIdList = new ArrayList<>();
			log.debug("isOnlyForEmp::"+isOnlyForEmp);
			if(isOnlyForEmp) {
				empIdList.add(req.getLoggedInEmpId());
				
			}else {
				Long startTime = System.currentTimeMillis();
				//empIdList = getEmployeeHiearachyData(req.getOrgId(),req.getLoggedInEmpId());
				empIdList = getReportingEmployes(req.getLoggedInEmpId());
				log.debug("getReportingEmployes list "+empIdList.size());
				log.debug("Time taken to get employess list "+(System.currentTimeMillis()-startTime));
			}
			Long startTime_1 = System.currentTimeMillis();
			list = getTodaysDataV2(empIdList, req,req.getDataType());
			log.debug("Time taken to get Todays Data "+(System.currentTimeMillis()-startTime_1));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return list;
		

	}
	
	
	




	private List<Integer> getEmployeeHiearachyData(Integer orgId, Integer empId) {
		List<Integer> empIdList = new ArrayList<>();
		try {
			/*String empHierarchyString = dashBoardServiceV3.getEmpHierararchyDataSchedular(empId, orgId);

			if (null != empHierarchyString) {
				log.debug("empHierarchyString is not null " + empId);
				String[] strings = empHierarchyString.substring(1, empHierarchyString.length() - 1).split(",");
				for (int i = 1; i < strings.length; i++) {
					strings[i] = strings[i].substring(1);
					empIdList.add(Integer.parseInt(strings[i]));
				}
			}*/
			//else {
				empIdList = getReportingEmployes(empId);
			//}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in getEmployeeHiearachyData() ",e);
		
		}
		return empIdList;
	}

	private Map<String, Object> getTodaysDataV2(List<Integer> empIdsUnderReporting, MyTaskReq req, String dataType) {

		Map<String, Object> map = new LinkedHashMap<>();
		try {
			log.debug("empIdsUnderReporting in getTodaysData before pagination"+empIdsUnderReporting.size());
			log.debug("dataType::::"+dataType);
			if(dataType.equalsIgnoreCase(DynamicFormConstants.TODAYS_DATA)) {
				map.put("todaysData", processTodaysUpcomingPendingData(req,empIdsUnderReporting,DynamicFormConstants.TODAYS_DATA));
				map.put("rescheduledData", processTodaysUpcomingPendingData(req,empIdsUnderReporting,DynamicFormConstants.RESCHEDULED_DATA));
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.UPCOMING_DATA)) {
				map.put("upcomingData", processTodaysUpcomingPendingData(req,empIdsUnderReporting,DynamicFormConstants.UPCOMING_DATA));
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.PENDING_DATA)) {
				map.put("pendingData", processTodaysUpcomingPendingData(req,empIdsUnderReporting,DynamicFormConstants.PENDING_DATA));
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.RESCHEDULED_DATA)) {
				map.put("rescheduledData", processTodaysUpcomingPendingData(req,empIdsUnderReporting,DynamicFormConstants.RESCHEDULED_DATA));
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.COMPLETED_DATA)) {
				map.put("completedData", processTodaysUpcomingPendingData(req,empIdsUnderReporting,DynamicFormConstants.COMPLETED_DATA));
			}
			
			
			} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in getTodaysDataV2 ",e);			}
		return map;
	
	}




	private List<TodaysTaskRes> processTodaysData(List<Integer> empIdsUnderReporting) {
		List<TodaysTaskRes> todaysRes = new ArrayList<>();
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			log.debug("generating data for empId " + empId + " and empName:" + empName);

			String todaysDate = getTodaysDate();
			log.debug("todaysDate::" + todaysDate);
			List<DmsWFTask> todayWfTaskList = dmsWfTaskDao.getTodaysUpcomingTasks(empId, todaysDate + " 00:00:00",
					todaysDate + " 23:59:59");
			todaysRes.add(buildMyTaskObj(todayWfTaskList, empId, empName));
		}
		return todaysRes;
	}
	

	private List<TodaysTaskRes> processPendingData(MyTaskReq req,  List<Integer> empIdsUnderReporting) {
		log.debug("Inside getUpcomingDataV2::()");
		List<TodaysTaskRes> todaysRes = new ArrayList<>();
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			//log.debug("generating data for empId " + empId + " and empName:" + empName);
			String startDate = req.getStartDate()+" 00:00:00";
			String endDate = req.getEndDate()+" 23:59:59";
			log.debug("processPendingData :startDate:"+startDate+",endDate:"+endDate);
			List<DmsWFTask> wfTaskList = null;
			if(req.isIgnoreDateFilter()) {
				wfTaskList = dmsWfTaskDao.findAllByPendingStatus (String.valueOf(empId));
			}else if(!req.isIgnoreDateFilter() && req.getStartDate()!=null && req.getEndDate()!=null) {
				wfTaskList = dmsWfTaskDao.findAllByPendingData(empId,startDate,endDate);
			}
			else {
				wfTaskList = dmsWfTaskDao.findAllByPendingStatus (String.valueOf(empId));
			}
		
			log.debug("wfTaskList size ingetPendingDataV2 "+wfTaskList.size());
			//wfTaskList = wfTaskList.stream().filter(wfTask->validatePendingTask(wfTask.getTaskUpdatedTime(), wfTask.getTaskCreatedTime())).collect(Collectors.toList());
			todaysRes.add(buildMyTaskObj(wfTaskList,empId,empName));    
		}
		return todaysRes;
	}
	
	private List<TodaysTaskRes> processResechduledData(MyTaskReq req,List<Integer> empIdsUnderReporting) {
		log.debug("Inside getRescheduledDataV2::()");
		List<TodaysTaskRes> todaysRes = new ArrayList<>();
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			log.debug("generating data for empId " + empId + " and empName:" + empName);
			String startDate = req.getStartDate()+" 00:00:00";
			String endDate = req.getEndDate()+" 23:59:59";
			
			List<DmsWFTask> wfTaskList  =null;
			if(req.isIgnoreDateFilter()) {
				wfTaskList = dmsWfTaskDao.findAllByRescheduledStatusWithNoDate (String.valueOf(empId));
			}else if(!req.isIgnoreDateFilter() && req.getStartDate()!=null && req.getEndDate()!=null) {
				wfTaskList =dmsWfTaskDao.findAllByRescheduledStatus(empId,startDate,endDate);
			}
			else {
				wfTaskList = dmsWfTaskDao.findAllByRescheduledStatusWithNoDate (String.valueOf(empId));
			}
		
			
			
			//List<DmsWFTask> wfTaskList = dmsWfTaskDao.findAllByRescheduledStatus(empId,startDate,endDate);
			log.debug("wfTaskList size ingetPendingDataV2 "+wfTaskList.size());
			//wfTaskList = wfTaskList.stream().filter(wfTask->validatePendingTask(wfTask.getTaskUpdatedTime(), wfTask.getTaskCreatedTime())).collect(Collectors.toList());
			todaysRes.add(buildMyTaskObj(wfTaskList,empId,empName));    
		}
		return todaysRes;
	}
	private List<TodaysTaskRes> processCompletededData(MyTaskReq req,List<Integer> empIdsUnderReporting) {
		log.debug("Inside getRescheduledDataV2::()");
		List<TodaysTaskRes> todaysRes = new ArrayList<>();
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			log.debug("generating data for empId " + empId + " and empName:" + empName);
			String startDate = req.getStartDate()+" 00:00:00";
			String endDate = req.getEndDate()+" 23:59:59";
			
			List<DmsWFTask> wfTaskList  =null;
			if(req.isIgnoreDateFilter()) {
				wfTaskList = dmsWfTaskDao.findAllByCompletedStatusWithNoDate (String.valueOf(empId));
			}else if(!req.isIgnoreDateFilter() && req.getStartDate()!=null && req.getEndDate()!=null) {
				wfTaskList =dmsWfTaskDao.findAllByCompletedStatus(empId,startDate,endDate);
			}
			else {
				wfTaskList = dmsWfTaskDao.findAllByCompletedStatusWithNoDate (String.valueOf(empId));
			}
			
			
			
			//List<DmsWFTask> wfTaskList = dmsWfTaskDao.findAllByRescheduledStatus(empId,startDate,endDate);
			log.debug("wfTaskList size ingetPendingDataV2 "+wfTaskList.size());
			//wfTaskList = wfTaskList.stream().filter(wfTask->validatePendingTask(wfTask.getTaskUpdatedTime(), wfTask.getTaskCreatedTime())).collect(Collectors.toList());
			todaysRes.add(buildMyTaskObj(wfTaskList,empId,empName));    
		}
		return todaysRes;
	}




	private List<TodaysTaskRes> processUpcomingData(List<Integer> empIdsUnderReporting) {
		log.debug("Inside getUpcomingDataV2::()");
		List<TodaysTaskRes> todaysRes = new ArrayList<>();
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			log.debug("generating data for empId " + empId + " and empName:" + empName);
			String todaysDate = getTodaysDate();
			log.debug("todaysDate::"+todaysDate);
			List<DmsWFTask> wfTaskList = dmsWfTaskDao.getTodaysUpcomingTasksV2(empId, todaysDate+" 00:00:00");
			List<DmsWFTask> upcomingWfTaskList = wfTaskList.stream().filter(wfTask->validateUpcomingTask(wfTask.getTaskUpdatedTime(), wfTask.getTaskCreatedTime())).collect(Collectors.toList());
			todaysRes.add(buildMyTaskObj(upcomingWfTaskList,empId,empName));          
		}
		return todaysRes;
	}




	private List<TodaysTaskRes> processTodaysUpcomingPendingData(MyTaskReq req, List<Integer> empIdsUnderReporting,String dataType) {

		log.debug("Inside getTodayDataV2::()");
		List<TodaysTaskRes> todaysRes = new ArrayList<>();

		if (empIdsUnderReporting.size() > 0) {
			List<List<Integer>> empIdPartionList = partitionList(empIdsUnderReporting);
			log.debug("empIdPartionList ::" + empIdPartionList.size());
			ExecutorService executor = Executors.newFixedThreadPool(empIdPartionList.size());
			
			List<CompletableFuture<List<TodaysTaskRes>>> futureList  =null;
			if(dataType.equalsIgnoreCase(DynamicFormConstants.TODAYS_DATA)) {
				futureList = empIdPartionList.stream()
						.map(strings -> CompletableFuture.supplyAsync(() -> processTodaysData(strings), executor))
						.collect(Collectors.toList());
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.UPCOMING_DATA)) {
				futureList = empIdPartionList.stream()
						.map(strings -> CompletableFuture.supplyAsync(() -> processUpcomingData(strings), executor))
						.collect(Collectors.toList());
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.PENDING_DATA)) {
				futureList = empIdPartionList.stream()
						.map(strings -> CompletableFuture.supplyAsync(() -> processPendingData(req,strings), executor))
						.collect(Collectors.toList());
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.RESCHEDULED_DATA)) {
				futureList = empIdPartionList.stream()
						.map(strings -> CompletableFuture.supplyAsync(() -> processResechduledData(req,strings), executor))
						.collect(Collectors.toList());
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.COMPLETED_DATA)) {
				futureList = empIdPartionList.stream()
						.map(strings -> CompletableFuture.supplyAsync(() -> processCompletededData(req,strings), executor))
						.collect(Collectors.toList());
			}
			if (null != futureList) {
				CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
				Stream<List<TodaysTaskRes>> dataStream = (Stream<List<TodaysTaskRes>>) futureList.stream().map(CompletableFuture::join);
				dataStream.forEach(x -> {
					todaysRes.addAll(x);
				});

			}
		}
		log.debug("size of todaysRes " + todaysRes.size());
	
		return todaysRes;
	}




	private List<List<Integer>> partitionList(List<Integer> list) {
		final int G = 5;
		final int NG = (list.size() + G - 1) / G;
		List<List<Integer>> result = IntStream.range(0, NG)
			    .mapToObj(i -> list.subList(G * i, Math.min(G * i + G, list.size())))
			    .collect(Collectors.toList());
		return result;
	}
	
	private List<List<DmsEmployee>> partitionListEmp(List<DmsEmployee> list) {
		final int G = 5;
		final int NG = (list.size() + G - 1) / G;
		List<List<DmsEmployee>> result = IntStream.range(0, NG)
			    .mapToObj(i -> list.subList(G * i, Math.min(G * i + G, list.size())))
			    .collect(Collectors.toList());
		return result;
	}
	
	public List<List<EmployeeTargetAchievement>> partitionListEmpTarget(List<EmployeeTargetAchievement> list) {
		final int G = 5;
		final int NG = (list.size() + G - 1) / G;
		List<List<EmployeeTargetAchievement>> result = IntStream.range(0, NG)
			    .mapToObj(i -> list.subList(G * i, Math.min(G * i + G, list.size())))
			    .collect(Collectors.toList());
		return result;
	}


	private TodaysTaskRes buildMyTaskObj(List<DmsWFTask> todayWfTaskList,Integer empId, String empName) {
		TodaysTaskRes todaysRes = new TodaysTaskRes();
		try {
			List<MyTask> myTaskList = new ArrayList<>();
			Set<String> uniqueTastSet = todayWfTaskList.stream().map(x->x.getTaskName()).collect(Collectors.toSet());
			log.debug("uniqueTastSet:::"+uniqueTastSet);
			Integer uniqueTaskcnt = uniqueTastSet.size();
			log.debug("uniqueTaskcnt:::"+uniqueTaskcnt);
			todaysRes.setEmpId(empId);
			todaysRes.setEmpName(empName);
			todaysRes.setTasksAvailable(uniqueTastSet);
			todaysRes.setTaskAvailableCnt(uniqueTaskcnt);
			if (null != todayWfTaskList) {
				for (DmsWFTask wf : todayWfTaskList) {
					MyTask task = new MyTask();
					task.setTaskName(wf.getTaskName());
					task.setTaskStatus(wf.getTaskStatus());
					task.setCreatedOn(wf.getTaskCreatedTime());
					List<DmsLead> tmpList = dmsLeadDao.getLeadByUniversalId(wf.getUniversalId());
					if (tmpList != null && !tmpList.isEmpty()) {
						DmsLead lead = tmpList.get(0);
						task.setCustomerName(lead.getFirstName() + " " + lead.getLastName());
						task.setPhoneNo(lead.getPhone());
					}
					task.setSalesExecutive(dmsEmployeeRepo.getEmpName(wf.getAssigneeId()));
					task.setUniversalId(wf.getUniversalId());
					
					DmsLead l = dmsLeadDao.getDMSLead(wf.getUniversalId());
					task.setModel(l.getModel());
					
					task.setTaskId(wf.getTaskId());
					
					String source = getSource(l.getDmsSourceOfEnquiry().getId());	
					task.setSourceType(source);					
					myTaskList.add(task);
				}
			}
			Map<String,List<MyTask>> map=   myTaskList.stream().collect(Collectors.groupingBy(MyTask::getTaskName));
			
			List<EmpTask> tasksList = new ArrayList<>();
			
			map.forEach((k,v)->{
				EmpTask t = new EmpTask();
				t.setTaskName(k);
				if(v!=null && !v.isEmpty()) {
					t.setTaskCnt(v.size());
					t.setMyTaskList(v);
				}
				tasksList.add(t);
				
			});
			
			todaysRes.setTasksList(tasksList);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return todaysRes;
	}
	
	private String getSource(int source) {
		log.info("Within the getSource method");
		String description = "";
		Optional<DmsSourceOfEnquiry> optional = dmsSourceOfEnquiryDao.findById(source);
		if (optional.isPresent()) {
			DmsSourceOfEnquiry dmsSourceOfEnquiry = optional.get();
			description = dmsSourceOfEnquiry.getDescription();
		}
		log.info("sourceOfEnquiry description:{}", description);
		return description;

	}


	@Override
	public Map<String, Object> getTodaysPendingUpcomingDataDetailV3(MyTaskReq req) throws DynamicFormsServiceException {
		log.info("Inside getTodaysPendingUpcomingDataDetailV2(){},empId " + req.getLoggedInEmpId() + " and IsOnlyForEmp "
				+ req.isOnlyForEmp());
		Map<String, Object> list = new LinkedHashMap<>();

		try {
	
			boolean isOnlyForEmp =req.isOnlyForEmp();
			List<Integer> empIdList = new ArrayList<>();
			log.debug("isOnlyForEmp::"+isOnlyForEmp);
			if(isOnlyForEmp) {
				empIdList.add(req.getLoggedInEmpId());
				
			}else {
				Long startTime = System.currentTimeMillis();
				empIdList = getEmployeeHiearachyData(req.getOrgId(),req.getLoggedInEmpId());
				
				log.debug("getReportingEmployes list "+empIdList.size());
				log.debug("Time taken to get employess list "+(System.currentTimeMillis()-startTime));
			}
			Long startTime_1 = System.currentTimeMillis();
			list = getTodaysDataV2(empIdList, req,req.getDataType());
			log.debug("Time taken to get Todays Data "+(System.currentTimeMillis()-startTime_1));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return list;
	}

	
	
	@Override
	public Map<String, Object> getTodaysPendingUpcomingDataV3(MyTaskReq req) throws DynamicFormsServiceException {
		log.info("Inside getTodaysPendingUpcomingDataV2(){},empId " + req.getLoggedInEmpId() + " and IsOnlyForEmp "
				+ req.isOnlyForEmp());
		Map<String, Object> list = new LinkedHashMap<>();

		try {
			boolean isOnlyForEmp =req.isOnlyForEmp();
			List<Integer> empIdList = new ArrayList<>();
			log.debug("isOnlyForEmp::"+isOnlyForEmp);
			if(isOnlyForEmp) {
				
				empIdList.add(req.getLoggedInEmpId());
				
			}else {
				//Long startTime = System.currentTimeMillis();
				boolean flag = false;
				String viewForEmp = req.getViewForEmp();
				if(null!=viewForEmp && viewForEmp.equalsIgnoreCase("yes")) {
					flag=true;
					req.setDetailView(true);
				//}
				}else {
					req.setDetailView(false);
				}
				//log.info(" :req.isDetailView() "+req.isDetailView());
				if(req.isDetailView()) {
					
					List<Integer> detailedViewEmpIdList = new ArrayList<>();
					detailedViewEmpIdList.add(req.getDetailedViewEmpId());
					log.info("detailedViewEmpIdList::"+detailedViewEmpIdList);
					list = getTodaysDataV3(detailedViewEmpIdList, req,req.getDataType());
				}else {
					empIdList = getEmployeeHiearachyData(req.getOrgId(),req.getLoggedInEmpId());
					list = getTodaysDataV3(empIdList, req,req.getDataType());
				}
				
			
				//log.debug("getReportingEmployes list "+empIdList.size());
				//log.debug("Time taken to get employess list "+(System.currentTimeMillis()-startTime));
			}
			Long startTime_1 = System.currentTimeMillis();
			
			log.debug("Time taken to get Todays Data "+(System.currentTimeMillis()-startTime_1));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return list;
		

	}
	
	
	private Map<String, Object> getTodaysDataV3(List<Integer> empIdsUnderReporting, MyTaskReq req, String dataType) {

		Map<String, Object> map = new LinkedHashMap<>();
		try {
			log.debug("empIdsUnderReporting in getTodaysData before pagination"+empIdsUnderReporting.size());
			log.debug("dataType::::"+dataType);
			if(dataType.equalsIgnoreCase(DynamicFormConstants.TODAYS_DATA)) {
				map.put("todaysData", processTodaysUpcomingPendingDataV3(req,empIdsUnderReporting,DynamicFormConstants.TODAYS_DATA));
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.UPCOMING_DATA)) {
				map.put("upcomingData", processTodaysUpcomingPendingDataV3(req,empIdsUnderReporting,DynamicFormConstants.UPCOMING_DATA));
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.PENDING_DATA)) {
				map.put("pendingData", processTodaysUpcomingPendingDataV3(req,empIdsUnderReporting,DynamicFormConstants.PENDING_DATA));
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.RESCHEDULED_DATA)) {
				map.put("rescheduledData", processTodaysUpcomingPendingDataV3(req,empIdsUnderReporting,DynamicFormConstants.RESCHEDULED_DATA));
			}
			
			
			} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in getTodaysDataV2 ",e);			}
		return map;
	
	}
	
	private List<TodaysTaskRes> processTodaysUpcomingPendingDataV3(MyTaskReq req, List<Integer> empIdsUnderReporting,String dataType) {

		log.debug("Inside getTodayDataV2::()");
		List<TodaysTaskRes> todaysRes = new ArrayList<>();

		if(!req.isDetailView()) {
		if (empIdsUnderReporting.size() > 0) {
			List<List<Integer>> empIdPartionList = partitionList(empIdsUnderReporting);
			log.debug("empIdPartionList ::" + empIdPartionList.size());
			ExecutorService executor = Executors.newFixedThreadPool(empIdPartionList.size());
			
			List<CompletableFuture<List<TodaysTaskRes>>> futureList  =null;
			if(dataType.equalsIgnoreCase(DynamicFormConstants.TODAYS_DATA)) {
				futureList = empIdPartionList.stream()
						.map(strings -> CompletableFuture.supplyAsync(() -> processTodaysDataV3(strings,req), executor))
						.collect(Collectors.toList());
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.UPCOMING_DATA)) {
				futureList = empIdPartionList.stream()
						.map(strings -> CompletableFuture.supplyAsync(() -> processUpcomingDataV3(strings,req), executor))
						.collect(Collectors.toList());
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.PENDING_DATA)) {
				futureList = empIdPartionList.stream()
						.map(strings -> CompletableFuture.supplyAsync(() -> processPendingDataV3(req,strings), executor))
						.collect(Collectors.toList());
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.RESCHEDULED_DATA)) {
				futureList = empIdPartionList.stream()
						.map(strings -> CompletableFuture.supplyAsync(() -> processResechduledDataV3(req,strings), executor))
						.collect(Collectors.toList());
			}
			if (null != futureList) {
				CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
				Stream<List<TodaysTaskRes>> dataStream = (Stream<List<TodaysTaskRes>>) futureList.stream().map(CompletableFuture::join);
				dataStream.forEach(x -> {
					todaysRes.addAll(x);
				});

			}
		}
		
		}else {
			
			if(dataType.equalsIgnoreCase(DynamicFormConstants.TODAYS_DATA)) {
				todaysRes.addAll(processTodaysDataV3(empIdsUnderReporting,req));
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.UPCOMING_DATA)) {
				todaysRes.addAll(processUpcomingDataV3(empIdsUnderReporting,req));
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.PENDING_DATA)) {
				todaysRes.addAll(processPendingDataV3(req,empIdsUnderReporting));
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.PENDING_DATA)) {
				todaysRes.addAll(processResechduledDataV3(req,empIdsUnderReporting));
			}
			
		}
		log.debug("size of todaysRes " + todaysRes.size());
	
		return todaysRes;
	}


	
	private List<TodaysTaskRes> processTodaysDataV3(List<Integer> empIdsUnderReporting,MyTaskReq req) {
		List<TodaysTaskRes> todaysRes = new ArrayList<>();
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			log.debug("generating data for empId " + empId + " and empName:" + empName);

			String todaysDate = getTodaysDate();
			log.debug("todaysDate::" + todaysDate);
			List<DmsWFTask> todayWfTaskList = dmsWfTaskDao.getTodaysUpcomingTasks(empId, todaysDate + " 00:00:00",
					todaysDate + " 23:59:59");
			todaysRes.add(buildMyTaskObjV3(todayWfTaskList, empId, empName,req));
		}
		return todaysRes;
	}
	

	private List<TodaysTaskRes> processPendingDataV3(MyTaskReq req,  List<Integer> empIdsUnderReporting) {
		log.debug("Inside getUpcomingDataV2::()");
		List<TodaysTaskRes> todaysRes = new ArrayList<>();
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			//log.debug("generating data for empId " + empId + " and empName:" + empName);
			String startDate = req.getStartDate()+" 00:00:00";
			String endDate = req.getEndDate()+" 23:59:59";
			log.debug("processPendingData :startDate:"+startDate+",endDate:"+endDate);
			List<DmsWFTask> wfTaskList = null;
			if(req.isIgnoreDateFilter()) {
				wfTaskList = dmsWfTaskDao.findAllByPendingStatus (String.valueOf(empId));
			}else if(!req.isIgnoreDateFilter() && req.getStartDate()!=null && req.getEndDate()!=null) {
				wfTaskList = dmsWfTaskDao.findAllByPendingData(empId,startDate,endDate);
			}
			else {
				wfTaskList = dmsWfTaskDao.findAllByPendingStatus (String.valueOf(empId));
			}
		
			log.debug("wfTaskList size ingetPendingDataV2 "+wfTaskList.size());
			//wfTaskList = wfTaskList.stream().filter(wfTask->validatePendingTask(wfTask.getTaskUpdatedTime(), wfTask.getTaskCreatedTime())).collect(Collectors.toList());
			todaysRes.add(buildMyTaskObjV3(wfTaskList,empId,empName,req));    
		}
		return todaysRes;
	}
	
	private List<TodaysTaskRes> processResechduledDataV3(MyTaskReq req,List<Integer> empIdsUnderReporting) {
		log.debug("Inside getRescheduledDataV2::()");
		List<TodaysTaskRes> todaysRes = new ArrayList<>();
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			log.debug("generating data for empId " + empId + " and empName:" + empName);
			String startDate = req.getStartDate()+" 00:00:00";
			String endDate = req.getEndDate()+" 23:59:59";
			
			List<DmsWFTask> wfTaskList  =null;
			if(req.isIgnoreDateFilter()) {
				wfTaskList = dmsWfTaskDao.findAllByRescheduledStatusWithNoDate (String.valueOf(empId));
			}else if(!req.isIgnoreDateFilter() && req.getStartDate()!=null && req.getEndDate()!=null) {
				wfTaskList =dmsWfTaskDao.findAllByRescheduledStatus(empId,startDate,endDate);
			}
			else {
				wfTaskList = dmsWfTaskDao.findAllByRescheduledStatusWithNoDate (String.valueOf(empId));
			}
		
			
			
			//List<DmsWFTask> wfTaskList = dmsWfTaskDao.findAllByRescheduledStatus(empId,startDate,endDate);
			log.debug("wfTaskList size ingetPendingDataV2 "+wfTaskList.size());
			//wfTaskList = wfTaskList.stream().filter(wfTask->validatePendingTask(wfTask.getTaskUpdatedTime(), wfTask.getTaskCreatedTime())).collect(Collectors.toList());
			todaysRes.add(buildMyTaskObjV3(wfTaskList,empId,empName,req));    
		}
		return todaysRes;
	}




	private List<TodaysTaskRes> processUpcomingDataV3(List<Integer> empIdsUnderReporting,MyTaskReq req) {
		log.debug("Inside getUpcomingDataV2::()");
		List<TodaysTaskRes> todaysRes = new ArrayList<>();
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			log.debug("generating data for empId " + empId + " and empName:" + empName);
			String todaysDate = getTodaysDate();
			log.debug("todaysDate::"+todaysDate);
			List<DmsWFTask> wfTaskList = dmsWfTaskDao.getTodaysUpcomingTasksV2(empId, todaysDate+" 00:00:00");
			List<DmsWFTask> upcomingWfTaskList = wfTaskList.stream().filter(wfTask->validateUpcomingTask(wfTask.getTaskUpdatedTime(), wfTask.getTaskCreatedTime())).collect(Collectors.toList());
			todaysRes.add(buildMyTaskObjV3(upcomingWfTaskList,empId,empName,req));          
		}
		return todaysRes;
	}

	
	private TodaysTaskRes buildMyTaskObjV3(List<DmsWFTask> todayWfTaskList,Integer empId, String empName, MyTaskReq req) {
		log.info("buildMyTaskObjV3::");	
		TodaysTaskRes todaysRes = new TodaysTaskRes();
		try {
			List<MyTask> myTaskList = new ArrayList<>();
			Set<String> uniqueTastSet = todayWfTaskList.stream().map(x->x.getTaskName()).collect(Collectors.toSet());
			//log.debug("uniqueTastSet:::"+uniqueTastSet);
			Integer uniqueTaskcnt = uniqueTastSet.size();
			//log.debug("uniqueTaskcnt:::"+uniqueTaskcnt);
			todaysRes.setEmpId(empId);
			todaysRes.setEmpName(empName);
			todaysRes.setTasksAvailable(uniqueTastSet);
			todaysRes.setTaskAvailableCnt(uniqueTaskcnt);
			log.info("::todaysRes:"+todaysRes);		
			
			if(req.isDetailView()) {
			if (null != todayWfTaskList) {
				for (DmsWFTask wf : todayWfTaskList) {
					MyTask task = new MyTask();
					task.setTaskName(wf.getTaskName());
					task.setTaskStatus(wf.getTaskStatus());
					task.setCreatedOn(wf.getTaskCreatedTime());
					List<DmsLead> tmpList = dmsLeadDao.getLeadByUniversalId(wf.getUniversalId());
					if (tmpList != null && !tmpList.isEmpty()) {
						DmsLead lead = tmpList.get(0);
						task.setCustomerName(lead.getFirstName() + " " + lead.getLastName());
						task.setPhoneNo(lead.getPhone());
					}
					task.setSalesExecutive(dmsEmployeeRepo.getEmpName(wf.getAssigneeId()));
					task.setUniversalId(wf.getUniversalId());
					
					DmsLead l = dmsLeadDao.getDMSLead(wf.getUniversalId());
					task.setModel(l.getModel());
					
					task.setTaskId(wf.getTaskId());
					
					String source = getSource(l.getDmsSourceOfEnquiry().getId());	
					task.setSourceType(source);					
					myTaskList.add(task);
				}
			}
			}
			Map<String,List<MyTask>> map=   myTaskList.stream().collect(Collectors.groupingBy(MyTask::getTaskName));
			
			List<EmpTask> tasksList = new ArrayList<>();
			
			map.forEach((k,v)->{
				EmpTask t = new EmpTask();
				t.setTaskName(k);
				if(v!=null && !v.isEmpty()) {
					t.setTaskCnt(v.size());
					t.setMyTaskList(v);
				}
				tasksList.add(t);
				
			});
			
			todaysRes.setTasksList(tasksList);
			log.info("todaysRes "+todaysRes);
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return todaysRes;
	}
	
	@Override
	public List<TargetAchivementModelandSource> getTargetAchivementParamsModelAndSource(DashBoardReqV2 req) throws DynamicFormsServiceException {
		log.info("Inside getTargetAchivementParams(){}");
		List<TargetAchivementModelandSource> resList = new ArrayList<>();
		try {
			long startTime = System.currentTimeMillis();
			List<List<TargetAchivementModelandSource>> allTargets = new ArrayList<>();
			Integer empId = req.getLoggedInEmpId();
			log.debug("Getting Target Data, LoggedIn emp id " + empId);

			List<Integer> selectedEmpIdList = req.getEmpSelected();
			List<Integer> selectedNodeList = req.getLevelSelected();
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(empId);
			String orgId = tRole.getOrgId();
			String branchId = tRole.getBranchId();
			req.setOrgId(orgId);
			log.debug("tRole getTargetAchivementParams " + tRole);
			Map<Integer, String> vehicleDataMap = dashBoardUtil.getVehilceDetails(orgId).get("main");
			List<String> vehicleModelList = new ArrayList<>();
			vehicleDataMap.forEach((k, v) -> {
				vehicleModelList.add(v);
			});
			
			
			 List<VehicleModelRes> vehicleModelData = getVehicleModelDataModelandSource(getEmpReportingList(empId,selectedEmpIdList,selectedNodeList,orgId,branchId), req, orgId, branchId, vehicleModelList,empId);
			 List<LeadSourceRes> leadSourceData = getLeadSourceData(req);
			 
			 //System.out.println("vehical model data size is  "+vehicleModelData.size());
			 //System.out.println("vehocal mode data details"+vehicleModelData);
			
			if (null != selectedEmpIdList && !selectedEmpIdList.isEmpty()) {
				log.debug("Fetching empReportingIdList for selected employees,selectedEmpIdList" + selectedEmpIdList);
				for (Integer eId : selectedEmpIdList) {
					List<Integer> empReportingIdList = new ArrayList<>();
					empReportingIdList.add(eId);
					//empIdList = getEmployeeHiearachyData(orgId,req.getLoggedInEmpId());
					empReportingIdList.addAll(getEmployeeHiearachyData(Integer.parseInt(orgId),eId));
					log.debug("empReportingIdList for given selectedEmpIdList " + empReportingIdList);
					resList = getTargetAchivementParamsForMultipleEmpmodelandSource(empReportingIdList, req,
							orgId,vehicleModelData,  leadSourceData);
					//log.debug("targetList::::::" + targetList);
					//allTargets.add(targetList);
				}

				//return resList;
				//resList = buildFinalTargetsModelandSorce(allTargets);
				
			} else if (null != selectedNodeList && !selectedNodeList.isEmpty()) {
				log.debug("Fetching empReportingIdList for selected LEVEL NODES");

				for (Integer node : selectedNodeList) {
					List<Integer> empReportingIdList = new ArrayList<>();
					empReportingIdList.add(req.getLoggedInEmpId());
					List<Integer> nodeList = new ArrayList<>();
					nodeList.add(node);

					Map<String, Object> datamap = ohServiceImpl.getActiveDropdownsV2(nodeList,
							Integer.parseInt(tRole.getOrgId()), empId);
					datamap.forEach((k, v) -> {
						Map<String, Object> innerMap = (Map<String, Object>) v;
						innerMap.forEach((x, y) -> {
							List<TargetDropDownV2> dd = (List<TargetDropDownV2>) y;
							empReportingIdList.addAll(dd.stream().map(z -> z.getCode()).map(Integer::parseInt)
									.collect(Collectors.toList()));
						});
					});
					resList = getTargetAchivementParamsForMultipleEmpmodelandSource(empReportingIdList, req,
							orgId,vehicleModelData,leadSourceData);
					//allTargets.add(targetList);

				}
				//resList = buildFinalTargetsModelandSorce(allTargets);
			} else {
				log.debug("Fetching empReportingIdList for logged in emp in else :" + req.getLoggedInEmpId());
				List<Integer> empReportingIdList = getEmployeeHiearachyData(Integer.parseInt(orgId),req.getLoggedInEmpId());
				empReportingIdList.add(req.getLoggedInEmpId());
				List<VehicleModelRes> vehicleModelData1 = getVehicleModelDataModelandSource(empReportingIdList, req, orgId, branchId, vehicleModelList,empId);
				 List<LeadSourceRes> leadSourceData1 = getLeadSourceData(req);
				log.debug("empReportingIdList for emp " + req.getLoggedInEmpId());
				log.debug("Calling getTargetAchivemetns in else" + empReportingIdList);
				resList = getTargetAchivementParamsForMultipleEmpmodelandSource(empReportingIdList, req, orgId,vehicleModelData1, leadSourceData1);
			}
			log.debug("Total time taken for getTargetparams "+(System.currentTimeMillis()-startTime));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
//		Map<Integer, String> vehicleDataMap = dashBoardUtil.getVehilceDetails(req.getOrgId()).get("main");
//		List<String> vehicleModelList = new ArrayList<>();
//		vehicleDataMap.forEach((k, v) -> {
//			vehicleModelList.add(v);
//		});
		
	
		return resList;
	}

	public List<TargetAchivementModelandSource> buildFinalTargetsModelandSorce(List<List<TargetAchivementModelandSource>> allTargets) {

		List<TargetAchivementModelandSource> resList = new ArrayList<>();
		Integer finalEnq = 0;
		Integer finalEnqTarget = 0;
		Double finalEnqAchivePerc = 0D;
		Integer finalEnqShortfall = 0;
		Double finalEnqShortfallPerc = 0D;

		Integer finalTD = 0;
		Integer finalTDTarget = 0;
		Double finalTDAchivePerc = 0D;
		Integer finalTDShortfall = 0;
		Double finalTDShortfallPerc = 0D;

		Integer finalFIN = 0;
		Integer finalFINTarget = 0;
		Double finalFINAchivePerc = 0D;
		Integer finalFINShortfall = 0;
		Double finalFINShortfallPerc = 0D;

		Integer finalINS = 0;
		Integer finalINSTarget = 0;
		Double finalINSAchivePerc = 0D;
		Integer finalINSShortfall = 0;
		Double finalINSShortfallPerc = 0D;

		Integer finalHV = 0;
		Integer finalHVTarget = 0;
		Double finalHVAchivePerc = 0D;
		Integer finalHVShortfall = 0;
		Double finalHVShortfallPerc = 0D;

		Integer finalBOOK = 0;
		Integer finalBOOKTarget = 0;
		Double finalBOOKAchivePerc = 0D;
		Integer finalBOOKShortfall = 0;
		Double finalBOOKShortfallPerc = 0D;

		Integer finalEXC = 0;
		Integer finalEXCTarget = 0;
		Double finalEXCAchivePerc = 0D;
		Integer finalEXCShortfall = 0;
		Double finalEXCShortfallPerc = 0D;

		Integer finalRETAIL = 0;
		Integer finalRETAILTarget = 0;
		Double finalRETAILAchivePerc = 0D;
		Integer finalRETAILShortfall = 0;
		Double finalRETAILShortfallPerc = 0D;

		Integer finalACC = 0;
		Integer finalACCTarget = 0;
		Double finalACCAchivePerc = 0D;
		Integer finalACCShortfall = 0;
		Double finalACCShortfallPerc = 0D;
		String model=null;
		String source=null;

		for (List<TargetAchivementModelandSource> targetList : allTargets) {

			for (TargetAchivementModelandSource target : targetList) {
				
				

				if (target.getParamName().equalsIgnoreCase(ENQUIRY)) {

					if (null != target.getAchievment()) {
						finalEnq = finalEnq + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						
						finalEnqTarget = finalEnqTarget + Integer.parseInt(target.getTarget());
					
					}
					////System.out.println("finalEnqAchivePerc::"+finalEnqAchivePerc);
					////System.out.println("target.getAchivementPerc()::"+target.getAchivementPerc());
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
					    finalEnqAchivePerc = finalEnqAchivePerc + Double.parseDouble(tmp);
					}
				
					if (null != target.getShortfall()) {
						log.debug("Integer.parseInt(target.getShortfall())  "+Integer.parseInt(target.getShortfall()));
						finalEnqShortfall = finalEnqShortfall + Integer.parseInt(target.getShortfall());
						log.debug("finalEnqShortfall::"+finalEnqShortfall);
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalEnqShortfallPerc = finalEnqShortfallPerc + Double.parseDouble(tmp);
					}
				}
				if (target.getParamName().equalsIgnoreCase(TEST_DRIVE)) {
					if (null != target.getAchievment()) {
						finalTD = finalTD + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalTDTarget = finalTDTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalTDAchivePerc = finalTDAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalTDShortfall = finalTDShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalTDShortfallPerc = finalTDShortfallPerc + Double.parseDouble(tmp);
					}
				}

				if (target.getParamName().equalsIgnoreCase(FINANCE)) {
					if (null != target.getAchievment()) {
						finalFIN = finalFIN + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalFINTarget = finalFINTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalFINAchivePerc = finalFINAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalFINShortfall = finalFINShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalFINShortfallPerc = finalFINShortfallPerc + Double.parseDouble(tmp);
					}

				}

				if (target.getParamName().equalsIgnoreCase(BOOKING)) {

					if (null != target.getAchievment()) {
						finalBOOK = finalBOOK + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalBOOKTarget = finalBOOKTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalBOOKAchivePerc = finalBOOKAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalBOOKShortfall = finalBOOKShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalBOOKShortfallPerc = finalBOOKShortfallPerc + Double.parseDouble(tmp);
					}
				}

				if (target.getParamName().equalsIgnoreCase(HOME_VISIT)) {
					if (null != target.getAchievment()) {
						finalHV = finalHV + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalHVTarget = finalHVTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalHVAchivePerc = finalHVAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalHVShortfall = finalHVShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalHVShortfallPerc = finalHVShortfallPerc + Double.parseDouble(tmp);
					}

				}

				if (target.getParamName().equalsIgnoreCase(INSURANCE)) {

					if (null != target.getAchievment()) {
						finalINS = finalINS + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalINSTarget = finalINSTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalINSAchivePerc = finalINSAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalINSShortfall = finalINSShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalINSShortfallPerc = finalINSShortfallPerc + Double.parseDouble(tmp);
					}
				}

				if (target.getParamName().equalsIgnoreCase(INVOICE)) {

					if (null != target.getAchievment()) {
						finalRETAIL = finalRETAIL + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalRETAILTarget = finalRETAILTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalRETAILAchivePerc = finalRETAILAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalRETAILShortfall = finalRETAILShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalRETAILShortfallPerc = finalRETAILShortfallPerc
								+ Double.parseDouble(tmp);
					}
				}

				if (target.getParamName().equalsIgnoreCase(EXCHANGE)) {

					if (null != target.getAchievment()) {
						finalEXC = finalEXC + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalEXCTarget = finalEXCTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalEXCAchivePerc = finalEXCAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalEXCShortfall = finalEXCShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalEXCShortfallPerc = finalEXCShortfallPerc + Double.parseDouble(tmp);
					}
				}

				if (target.getParamName().equalsIgnoreCase(EXCHANGE)) {

					if (null != target.getAchievment()) {
						finalACC = finalACC + Integer.parseInt(target.getAchievment());
					}
					if (null != target.getTarget()) {
						finalACCTarget = finalACCTarget + Integer.parseInt(target.getTarget());
					}
					if (null != target.getAchivementPerc()) {
						String tmp = target.getAchivementPerc().replaceAll("%","").trim();
						finalACCAchivePerc = finalACCAchivePerc + Double.parseDouble(tmp);
					}
					if (null != target.getShortfall()) {
						finalACCShortfall = finalACCShortfall + Integer.parseInt(target.getShortfall());
					}
					if (null != target.getShortFallPerc()) {
						String tmp = target.getShortFallPerc().replaceAll("%","").trim();
						finalACCShortfallPerc = finalACCShortfallPerc + Double.parseDouble(tmp);
					}
				}

			}

		}

		TargetAchivementModelandSource targetEnq = new TargetAchivementModelandSource();
		targetEnq.setParamName(ENQUIRY);
		targetEnq.setParamShortName("Enq");
		targetEnq.setAchievment(String.valueOf(finalEnq));
		targetEnq.setTarget(String.valueOf(finalEnqTarget));
		targetEnq.setAchivementPerc(String.valueOf(finalEnqAchivePerc));
		targetEnq.setShortfall(String.valueOf(finalEnqShortfall));
		targetEnq.setShortFallPerc(String.valueOf(finalEnqShortfallPerc));

		TargetAchivementModelandSource targetTD = new TargetAchivementModelandSource();
		targetTD.setParamName(TEST_DRIVE);
		targetTD.setParamShortName("Tdr");
		targetTD.setAchievment(String.valueOf(finalTD));
		targetTD.setTarget(String.valueOf(finalTDTarget));
		targetTD.setAchivementPerc(String.valueOf(finalTDAchivePerc));
		targetTD.setShortfall(String.valueOf(finalTDShortfall));
		targetTD.setShortFallPerc(String.valueOf(finalTDShortfallPerc));

		resList.add(targetEnq);
		resList.add(targetTD);

		TargetAchivementModelandSource targetFIN = new TargetAchivementModelandSource();
		targetFIN.setParamName(FINANCE);targetTD.setParamShortName("Fin");
		targetFIN.setAchievment(String.valueOf(finalFIN));
		targetFIN.setTarget(String.valueOf(finalFINTarget));
		targetFIN.setAchivementPerc(String.valueOf(finalFINAchivePerc));
		targetFIN.setShortfall(String.valueOf(finalFINShortfall));
		targetFIN.setShortFallPerc(String.valueOf(finalFINShortfallPerc));

		resList.add(targetFIN);

		TargetAchivementModelandSource targetINS = new TargetAchivementModelandSource();
		targetINS.setParamName(INSURANCE);targetTD.setParamShortName("Ins");
		targetINS.setAchievment(String.valueOf(finalINS));
		targetINS.setTarget(String.valueOf(finalINSTarget));
		targetINS.setAchivementPerc(String.valueOf(finalINSAchivePerc));
		targetINS.setShortfall(String.valueOf(finalINSShortfall));
		targetINS.setShortFallPerc(String.valueOf(finalINSShortfallPerc));

		resList.add(targetINS);

		TargetAchivementModelandSource targetHV = new TargetAchivementModelandSource();
		targetHV.setParamName(HOME_VISIT);targetTD.setParamShortName("Hvt");
		targetHV.setAchievment(String.valueOf(finalHV));
		targetHV.setTarget(String.valueOf(finalHVTarget));
		targetHV.setAchivementPerc(String.valueOf(finalHVAchivePerc));
		targetHV.setShortfall(String.valueOf(finalHVShortfall));
		targetHV.setShortFallPerc(String.valueOf(finalHVShortfallPerc));

		resList.add(targetHV);

		TargetAchivementModelandSource targetBOOK = new TargetAchivementModelandSource();
		targetBOOK.setParamName(BOOKING);
		targetTD.setParamShortName("Bkg");
		targetBOOK.setAchievment(String.valueOf(finalBOOK));
		targetBOOK.setTarget(String.valueOf(finalBOOKTarget));
		targetBOOK.setAchivementPerc(String.valueOf(finalBOOKAchivePerc));
		targetBOOK.setShortfall(String.valueOf(finalBOOKShortfall));
		targetBOOK.setShortFallPerc(String.valueOf(finalBOOKShortfallPerc));

		resList.add(targetBOOK);

		TargetAchivementModelandSource targetEXC = new TargetAchivementModelandSource();
		targetEXC.setParamName(EXCHANGE);
		targetTD.setParamShortName("Exg");
		targetEXC.setAchievment(String.valueOf(finalEXC));
		targetEXC.setTarget(String.valueOf(finalEXCTarget));
		targetEXC.setAchivementPerc(String.valueOf(finalEXCAchivePerc));
		targetEXC.setShortfall(String.valueOf(finalEXCShortfall));
		targetEXC.setShortFallPerc(String.valueOf(finalEXCShortfallPerc));

		
		resList.add(targetEXC);

		TargetAchivementModelandSource targetRETAIL = new TargetAchivementModelandSource();
		targetRETAIL.setParamName(INVOICE);
		targetTD.setParamShortName("Ret");
		targetRETAIL.setAchievment(String.valueOf(finalRETAIL));
		targetRETAIL.setTarget(String.valueOf(finalRETAILTarget));
		targetRETAIL.setAchivementPerc(String.valueOf(finalRETAILAchivePerc));
		targetRETAIL.setShortfall(String.valueOf(finalRETAILShortfall));
		targetRETAIL.setShortFallPerc(String.valueOf(finalRETAILShortfallPerc));

		resList.add(targetRETAIL);

		TargetAchivementModelandSource targetACC = new TargetAchivementModelandSource();
		targetACC.setParamName(ACCCESSORIES);
		targetTD.setParamShortName("Acc");
		targetACC.setAchievment(String.valueOf(finalACC));
		targetACC.setTarget(String.valueOf(finalACCTarget));
		targetACC.setAchivementPerc(String.valueOf(finalACCAchivePerc));
		targetACC.setShortfall(String.valueOf(finalACCShortfall));
		targetACC.setShortFallPerc(String.valueOf(finalACCShortfallPerc));

		resList.add(targetACC);

		return resList;
	}

	public List<TargetAchivementModelandSource> getTargetAchivementParamsForMultipleEmpmodelandSource(
			List<Integer> empIdsUnderReporting, DashBoardReqV2 req,String orgId,List<VehicleModelRes> vehicleModelData, List<LeadSourceRes> leadSourceData) throws ParseException, DynamicFormsServiceException {
		List<TargetAchivementModelandSource> resList = new ArrayList<>();
		List<String> empNamesList = dmsEmployeeRepo.findEmpNamesById(empIdsUnderReporting);
		int empId = req.getLoggedInEmpId();
		log.debug("empNamesList::" + empNamesList);
		log.debug("Calling getTargetAchivementParamsForMultipleEmp");
		final String startDate;
		final String endDate;
		if (null == req.getStartDate() && null == req.getEndDate()) {
			startDate = getFirstDayOfMonth();
			endDate = getLastDayOfMonth();
		} else {
			startDate = req.getStartDate()+" 00:00:00";
			endDate = req.getEndDate()+" 23:59:59";
		}

		
		log.info("StartDate " + startDate + ", EndDate " + endDate);
		Map<String, Integer> map = new ConcurrentHashMap<>();
		Map<String, Integer> finalMap = new ConcurrentHashMap<>();
		
		if(empIdsUnderReporting.size()>0) {
			List<List<Integer>> empIdPartionList = partitionList(empIdsUnderReporting);
			log.debug("empIdPartionList ::" + empIdPartionList.size());
			ExecutorService executor = Executors.newFixedThreadPool(empIdPartionList.size());
			
			List<CompletableFuture<Map<String, Integer>>> futureList = empIdPartionList.stream()
					.map(strings -> CompletableFuture.supplyAsync(() -> processTargetMap(strings,map,startDate,endDate), executor))
					.collect(Collectors.toList());
			
			CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
			Stream<Map<String, Integer>> dataStream = (Stream<Map<String, Integer>>) futureList.stream().map(CompletableFuture::join);
			dataStream.forEach(x -> {
				finalMap.putAll(map);
			});
		}
		
		/*
		for(Integer empId : empIdsUnderReporting) {
			log.debug("Getting target params for user "+empId);
			Map<String, Integer> innerMap = getTargetParams(String.valueOf(empId), startDate, endDate);
			log.debug("innerMap::"+innerMap);
			map = validateAndUpdateMapData(ENQUIRY,innerMap,map);
			map = validateAndUpdateMapData(TEST_DRIVE,innerMap,map);
			map = validateAndUpdateMapData(HOME_VISIT,innerMap,map);
			map = validateAndUpdateMapData(VIDEO_CONFERENCE,innerMap,map);
			map = validateAndUpdateMapData(BOOKING,innerMap,map);
			map = validateAndUpdateMapData(EXCHANGE,innerMap,map);
			map = validateAndUpdateMapData(FINANCE,innerMap,map);
			map = validateAndUpdateMapData(INSURANCE,innerMap,map);
			map = validateAndUpdateMapData(ACCCESSORIES,innerMap,map);
			map = validateAndUpdateMapData(EVENTS,innerMap,map);
			map = validateAndUpdateMapData(INVOICE,innerMap,map);
			
		}
		*/
		//List<DmsLead> dmsLeadList = dmsLeadDao.getAllEmployeeLeads(empNamesList, startDate, endDate, ENQUIRY);
	
		
		
		return getTaskAndBuildTargetAchievementsmodelandSource(empIdsUnderReporting, orgId, resList, empNamesList, startDate, endDate,map,vehicleModelData, leadSourceData, empId);
	}
	
	/**
	 * @param empIdsUnderReporting
	 * @param orgId
	 * @param resList
	 * @param empNamesList
	 * @param startDate
	 * @param endDate
	 * @param map
	 * @param vehicleModelData
	 * @param leadSourceData
	 * @return
	 */
	private List<TargetAchivementModelandSource> getTaskAndBuildTargetAchievementsmodelandSource(List<Integer> empIdsUnderReporting, String orgId,
			List<TargetAchivementModelandSource> resList, List<String> empNamesList, String startDate, String endDate,
			Map<String, Integer> map,List<VehicleModelRes> vehicleModelData, List<LeadSourceRes> leadSourceData, int empId1) {
		Long dropLeadCnt = 0L;
		Long enqLeadCnt = 0L;
		Long preBookCount = 0L;
		Long bookCount = 0L;
		Long invCount = 0L;
		Long preDeliveryCnt = 0L;
		Long delCnt = 0L;
		Map<String,List<TargetAchivement>> targetMapSource=new LinkedHashMap<>();
		Map<String,List<TargetAchivement>> targetMapModel=new LinkedHashMap<>();
		TargetAchivementResponseDto targetAchivementResponse=new TargetAchivementResponseDto();
		//System.out.println(orgId);
		
		
		
	/**	
		
		List<DmsEmployeeAllocation> dmsEmployeeAllocations = employeeAllocation.findByEmployeeId(empId1);
		
		
		
		List<Integer> dmsLeadList = dmsLeadDao.getLeadIdsByEmpNamesWithOutDrop(empNamesList);
        //System.out.println("dmsLeadList Before Adding"+dmsLeadList.size());

        dmsLeadList.addAll(dmsEmployeeAllocations.stream().filter(res -> !res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")
				&& empNamesList.equals(res.getDmsLead().getSalesConsultant())).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
		//dmsLeadList.addAll(dmsEmployeeAllocations.stream().filter(res -> !res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
		
		//System.out.println("dmsLeadList After Adding"+dmsLeadList.size());	
		
		List<Integer> dmsLeadListDropped = dmsLeadDao.getLeadIdsByEmpNamesWithDrop(empNamesList);
		
		//System.out.println("dmsLeadListDropped Before Adding"+dmsLeadListDropped.size());
		
		dmsLeadListDropped.addAll(dmsEmployeeAllocations.stream().filter(res -> res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")
				&& empNamesList.equals(res.getDmsLead().getSalesConsultant())).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
		
		//dmsLeadListDropped.addAll(dmsEmployeeAllocations.stream().filter(res -> res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
		
		//System.out.println("dmsLeadListDropped After Adding"+dmsLeadListDropped.size());
		
		dmsLeadList = dmsLeadList.stream().distinct().collect(Collectors.toList());
		
		//System.out.println("dmsLeadList After Deleting Duplicates"+dmsLeadList.size());
		
		dmsLeadListDropped = dmsLeadListDropped.stream().distinct().collect(Collectors.toList());
		
		//System.out.println("dmsLeadListDropped After Deleting Duplicates"+dmsLeadListDropped.size());
		
		List<LeadStageRefEntity> leadRefList  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadList,startDate,endDate);
		
		//System.out.println("leadRefList Before Duplicates"+leadRefList.size());
		
		leadRefList = leadRefList.stream().distinct().collect(Collectors.toList());
		
		//System.out.println("leadRefList After Duplicates"+leadRefList.size());
		
		List<LeadStageRefEntity> leadRefListDropped  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadListDropped,startDate,endDate);
		
		if(null!=leadRefList && !leadRefList.isEmpty()) {
			
			log.debug("Total leads in LeadReF table is ::"+leadRefList.size());
			enqLeadCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).distinct().count();
			//enqLeadCnt = leadRefList.stream().filter(x-> x.getLeadStatus()!=null &&  x.getLeadStatus().equalsIgnoreCase(preenqCompStatus)).count();
			preBookCount =leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("PREBOOKING")).count();
			bookCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("BOOKING")).distinct().count();
			//bookCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("BOOKING") && (x.getStageName().equalsIgnoreCase("INVOICE") && x.getLeadStatus().equalsIgnoreCase("BOOKINGCOMPLETED"))).count();
			//bookCount = leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(preBookCompStatus)).count();
			//invCount = leadRefList.stream().filter(x->(x.getStageName().equalsIgnoreCase("INVOICE") && x.getLeadStatus().equalsIgnoreCase("INVOICECOMPLETED")) && (x.getStageName().equalsIgnoreCase("PREDELIVERY")) && (x.getStageName().equalsIgnoreCase("DELIVERY") && x.getLeadStatus().equalsIgnoreCase("PREDELIVERYCOMPLETED"))).count();
			//invCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("INVOICE") 
			//		&& x.getLeadStatus().equalsIgnoreCase("INVOICECOMPLETED")).count();
			//invCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("PREDELIVERY") 
			//		&& x.getLeadStatus().equalsIgnoreCase("INVOICECOMPLETED") && x.getLeadStatus().equalsIgnoreCase("PREDELIVERYCOMPLETED")).count();
			invCount = leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(invCompStatus)).distinct().count();
			preDeliveryCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("PREDELIVERY")).count();
			//delCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("DELIVERY") && x.getLeadStatus().equalsIgnoreCase("DELIVERYCOMPLETED")).count();
			delCnt = leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(delCompStatus)).count();
			
			if(null!=leadRefListDropped && !leadRefListDropped.isEmpty()) {
							
							dropLeadCnt = leadRefListDropped.stream().distinct().count();
						}
			
			System.out.println("@@@@@@@@@#############leadRefList:::::::::"+leadRefList.stream().map(res->res.getLeadId()).distinct().collect(Collectors.toList()));
			/*
			 * enqLeadCnt =
			 * leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")
			 * && x.getStageName().equalsIgnoreCase("PREBOOKING")).count(); //enqLeadCnt =
			 * leadRefList.stream().filter(x-> x.getLeadStatus()!=null &&
			 * x.getLeadStatus().equalsIgnoreCase(preenqCompStatus)).count(); preBookCount
			 * =leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase(
			 * "PREBOOKING")).count(); bookCount =
			 * leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("BOOKING")
			 * && (x.getStageName().equalsIgnoreCase("INVOICE") &&
			 * x.getLeadStatus().equalsIgnoreCase("BOOKINGCOMPLETED"))).count(); //bookCount
			 * = leadRefList.stream().filter(x->x.getLeadStatus()!=null &&
			 * x.getLeadStatus().equalsIgnoreCase(preBookCompStatus)).count(); invCount =
			 * leadRefList.stream().filter(x->(x.getStageName().equalsIgnoreCase("INVOICE")
			 * && x.getLeadStatus().equalsIgnoreCase("INVOICECOMPLETED")) &&
			 * (x.getStageName().equalsIgnoreCase("PREDELIVERY")) &&
			 * (x.getStageName().equalsIgnoreCase("DELIVERY") &&
			 * x.getLeadStatus().equalsIgnoreCase("PREDELIVERYCOMPLETED"))).count();
			 * //invCount = leadRefList.stream().filter(x->x.getLeadStatus()!=null &&
			 * x.getLeadStatus().equalsIgnoreCase(invCompStatus)).count(); preDeliveryCnt =
			 * leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase(
			 * "PREDELIVERY")).count(); delCnt =
			 * leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("DELIVERY")
			 * && x.getLeadStatus().equalsIgnoreCase("DELIVERYCOMPLETED")).count();
			 */
			//delCnt = leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(delCompStatus)).count();
			
		
			
	/*	} */
		
	
		
		
		
		
		
		
		
		
		
	
		List<DmsWFTask> wfTaskList = dmsWfTaskDao.getWfTaskByAssigneeIdList(empIdsUnderReporting, startDate, endDate);
		//return buildTargetAchivements(resList, map, finalEnqLeadCnt,finalBookCnt, finalInvCount,wfTaskList);
		List<TargetAchivementModelandSource> buildTargetAchivementsModelAndSource =null;
		
		//System.out.println("----- model -------------"+vehicleModelData.size());
		
		for(VehicleModelRes vehicalmodelresult : vehicleModelData) {
			String source=null;
		/*	
			List<DmsLead> dmsLeadList1 = dmsLeadDao.getAllEmployeeLeadsWithModel(orgId,empNamesList,startDate, endDate,vehicalmodelresult.getModel());
				List<Integer> dmsLeadIdList = dmsLeadList1.stream().map(DmsLead::getId).collect(Collectors.toList());
				log.debug("dmsLeadList::"+dmsLeadList1);
			    leadRefList  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadIdList,startDate,endDate); */
		//New Code 
			List<DmsEmployeeAllocation> dmsEmployeeAllocations = employeeAllocation.findByEmployeeId(empId1);
			List<Integer> dmsLeadList = dmsLeadDao.getAllEmployeeLeadsWithModel1(orgId,empNamesList,startDate, endDate, vehicalmodelresult.getModel());
			dmsLeadList.addAll(dmsEmployeeAllocations.stream().filter(res -> !res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")
			&& empNamesList.equals(res.getDmsLead().getSalesConsultant()) && res.getDmsLead().getModel().equalsIgnoreCase(vehicalmodelresult.getModel())).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
			dmsLeadList = dmsLeadList.stream().distinct().collect(Collectors.toList());
			List<LeadStageRefEntity> leadRefList  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadList,startDate,endDate);
			leadRefList = leadRefList.stream().distinct().collect(Collectors.toList());
			
				if(null!=leadRefList && !leadRefList.isEmpty()) {
					log.debug("Total leads in LeadReF table is ::"+leadRefList.size());
//					//System.out.println("Total leads in LeadReF table is ------ ::"+leadRefList.size());
//					for(LeadStageRefEntity refentity : leadRefList) {
//						//System.out.println("-------------"+refentity.getStageName());
//					}
				
				}
				
			
			enqLeadCnt=vehicalmodelresult.getE();
			//System.out.println("enqLeadCnt::::::"+enqLeadCnt);
			bookCount=vehicalmodelresult.getB();
		long testDriveCnt=vehicalmodelresult.getT();
		long homeVistCnt=vehicalmodelresult.getV();
		invCount=vehicalmodelresult.getR();
		dropLeadCnt=vehicalmodelresult.getL();
		
		System.out.println("@@@@@@@@@#############leadRefList1:::::::::"+leadRefList.stream().map(res->res.getLeadId()).distinct().collect(Collectors.toList()));
		resList = buildTargetAchivementsModelAndSource(resList, map,dropLeadCnt, enqLeadCnt,preBookCount, bookCount,invCount,preDeliveryCnt,delCnt,wfTaskList,leadRefList,testDriveCnt,homeVistCnt,vehicalmodelresult.getModel(),source);
		//targetMapModel.put(vehicalmodelresult.getModel(), buildTargetAchivementsModelAndSource);
		}
		
		//targetAchivementResponse.setTargetMapModel(targetMapModel);
		
		Map<String, Integer> leadTypes = getLeadTypes(orgId);
			
	//System.out.println("------------------ enetered into --------"+leadSourceData.size());	
		
		for( LeadSourceRes  leadSourceRes : leadSourceData) {
			
			String model=null;
			
			
		/*	
			List<DmsLead> dmsAllLeadList = dmsLeadDao.getAllEmployeeLeadsBasedOnEnquiry(orgId, empNamesList, startDate, endDate, v);
			List<Integer> dmsLeadList2 = dmsAllLeadList.stream().map(DmsLead::getId).collect(Collectors.toList());
			log.debug("dmsLeadList::"+dmsLeadList);
				
			 leadRefList  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadList,startDate,endDate);
		*/
			
		//New Code 
			//Map<Integer, String> vehicleDataMap = dashBoardUtil.getVehilceDetails(orgId).get("main");
			List<String> vehicleModelList = new ArrayList<>();
			Map<Integer, String> vehicleDataMap = dashBoardUtil.getVehilceDetails(orgId).get("main");

			vehicleDataMap.forEach((k, v) -> {
				vehicleModelList.add(v);
			});
			Integer v = leadTypes.get(leadSourceRes.getLead());
			List<DmsEmployeeAllocation> dmsEmployeeAllocations = employeeAllocation.findByEmployeeId(empId1);
			List<Integer> dmsLeadList = dmsLeadDao.getAllEmployeeLeadsBasedOnEnquiry1(orgId,empNamesList,startDate, endDate, v,vehicleModelList);
			dmsLeadList.addAll(dmsEmployeeAllocations.stream().filter(res -> !res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")
			&& empNamesList.equals(res.getDmsLead().getSalesConsultant()) && res.getDmsLead().getDmsSourceOfEnquiry().getId()==v).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
			dmsLeadList = dmsLeadList.stream().distinct().collect(Collectors.toList());
			List<LeadStageRefEntity> leadRefList  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadList,startDate,endDate);
			leadRefList = leadRefList.stream().distinct().collect(Collectors.toList());
			
			enqLeadCnt=leadSourceRes.getE();
			bookCount=leadSourceRes.getB();
			long testDriveCnt=leadSourceRes.getT();
			long homeVistCnt=leadSourceRes.getV();
			invCount=leadSourceRes.getR();
			dropLeadCnt=leadSourceRes.getL();
			System.out.println("@@@@@@@@@#############leadRefList2:::::::::"+leadRefList.stream().map(res->res.getLeadId()).distinct().collect(Collectors.toList()));

			resList = buildTargetAchivementsModelAndSource(resList, map, dropLeadCnt, enqLeadCnt,preBookCount, bookCount,invCount,preDeliveryCnt,delCnt,wfTaskList,leadRefList,testDriveCnt,homeVistCnt,model,leadSourceRes.getLead());
		
			//targetMapSource.put(leadSourceRes.getLead(), buildTargetAchivementsModelAndSource);
		}
		
		//System.out.println("size of the target builded data---- "+resList.size());
		
		//targetAchivementResponse.setTargetMapSource(targetMapSource);
		 return resList;
		 
	}
	
	private List<TargetAchivementModelandSource> buildTargetAchivementsModelAndSource(List<TargetAchivementModelandSource> resList,
			Map<String, Integer> targetParamMap, Long dropLeadCnt, Long enqLeadCnt,Long preBookCount, Long bookCount, Long invCount, Long preDeliveryCnt, Long delCnt, List<DmsWFTask> wfTaskList, List<LeadStageRefEntity> leadRefList,Long testDriveCnt,Long homeVistCnt,String model,String source) {
		
	

		// Getting Test Drive Cnt
		//Long testDriveCnt = getTestDriveCount(wfTaskList);
		//Long financeCnt = getFinanceCount(wfTaskList);
		//Long insuranceCnt =getInsuranceCount(wfTaskList);
		
		//Long bookingCnt = getBookingCount(wfTaskList);
		//Long homeVistCnt = getHomeVisitCount(wfTaskList);
		//Long videoConfCnt = wfTaskList.stream().filter(x->x.getTaskName().equalsIgnoreCase(VIDEO_CONFERENCE)).count();
		//Long exchangeCnt = getExchangeCount(wfTaskList);
		//Long invoceCnt = getInvoiceCountTarget(wfTaskList);
		//Long retailCnt = 0L;
		Long bookingCnt = bookCount;
		Long invoceCnt = invCount;
		
		
		Long exchangeCnt  = 0L;
		Long insuranceCnt = 0L;
		Long accessoriesCnt = 0L;
		Long extendedWarntyCnt  =0L;
		Long financeCnt = 0L;
		
		List<Integer> leadIdList = leadRefList.stream().map(x->x.getLeadId()).collect(Collectors.toList());

		List<Integer> leadIdListV1 = leadRefList.stream().filter(x->null!=x.getLeadStatus() && x.getLeadStatus().equals("INVOICECOMPLETED")).map(x->x.getLeadId()).collect(Collectors.toList());

		if(leadIdListV1!=null && !leadIdListV1.isEmpty()) {
			exchangeCnt  = getExchangeCntSupportParam(leadIdListV1);
			insuranceCnt = getInsuranceCntSupportParam(leadIdListV1);
			accessoriesCnt = getAccessoriesCount(leadIdListV1);
			if(accessoriesCnt==null || leadIdListV1.isEmpty())
			{
				accessoriesCnt = 0L;
			}
			financeCnt = getFinanceCntSupportParam(leadIdListV1);	
		}
		
		extendedWarntyCnt  =getExtendedWarrntySupportParam(leadIdList);
		
		TargetAchivementModelandSource enqTargetAchivement = new TargetAchivementModelandSource();
		enqTargetAchivement.setParamName(ENQUIRY);
		enqTargetAchivement.setParamShortName("Enq");
		//System.out.println("------------model data data"+model);
		//System.out.println("------------source data data"+source);
		enqTargetAchivement.setModel(model);
		enqTargetAchivement.setSource(source);;
		enqTargetAchivement.setAchievment(String.valueOf(enqLeadCnt));;
		if(targetParamMap.containsKey(ENQUIRY)) {
			enqTargetAchivement.setTarget(String.valueOf(targetParamMap.get(ENQUIRY)));
			
			enqTargetAchivement.setAchivementPerc(getAchievmentPercentage(enqLeadCnt,targetParamMap.get(ENQUIRY)));
			enqTargetAchivement.setShortfall(getShortFallCount(enqLeadCnt,targetParamMap.get(ENQUIRY)));
			enqTargetAchivement.setShortFallPerc(getShortFallPercentage(enqLeadCnt,targetParamMap.get(ENQUIRY)));;
		}else {
			enqTargetAchivement.setTarget(String.valueOf("0"));
			enqTargetAchivement.setAchivementPerc(String.valueOf("0"));
			enqTargetAchivement.setShortfall(String.valueOf("0"));
			enqTargetAchivement.setShortFallPerc(String.valueOf("0"));
		}
		//enqTargetAchivement.setData(buildEnqDataList(leadRefList,ENQUIRY));
	
		resList.add(enqTargetAchivement);
		
		TargetAchivementModelandSource lostDropTargetAchivement = new TargetAchivementModelandSource();
		lostDropTargetAchivement.setParamName(DROPPED);
		lostDropTargetAchivement.setParamShortName("Enq");
		//System.out.println("------------model data data"+model);
		//System.out.println("------------source data data"+source);
		lostDropTargetAchivement.setModel(model);
		lostDropTargetAchivement.setSource(source);;
		lostDropTargetAchivement.setAchievment(String.valueOf(dropLeadCnt));;
		if(targetParamMap.containsKey(DROPPED)) {
			lostDropTargetAchivement.setTarget(String.valueOf(targetParamMap.get(DROPPED)));
			
			lostDropTargetAchivement.setAchivementPerc(getAchievmentPercentage(dropLeadCnt,targetParamMap.get(DROPPED)));
			lostDropTargetAchivement.setShortfall(getShortFallCount(dropLeadCnt,targetParamMap.get(DROPPED)));
			lostDropTargetAchivement.setShortFallPerc(getShortFallPercentage(dropLeadCnt,targetParamMap.get(DROPPED)));;
		}else {
			lostDropTargetAchivement.setTarget(String.valueOf("0"));
			lostDropTargetAchivement.setAchivementPerc(String.valueOf("0"));
			lostDropTargetAchivement.setShortfall(String.valueOf("0"));
			lostDropTargetAchivement.setShortFallPerc(String.valueOf("0"));
		}
		//enqTargetAchivement.setData(buildEnqDataList(leadRefList,ENQUIRY));
	
		resList.add(lostDropTargetAchivement);
		
		
		
		
		TargetAchivementModelandSource testDriveTA = new TargetAchivementModelandSource();
	
		testDriveTA.setParamName(TEST_DRIVE);
		testDriveTA.setParamShortName("Tdr");
		testDriveTA.setModel(model);
		testDriveTA.setSource(source);;
		testDriveTA.setAchievment(String.valueOf(testDriveCnt));
		if(targetParamMap.containsKey(TEST_DRIVE)) {
			testDriveTA.setTarget(String.valueOf(targetParamMap.get(TEST_DRIVE)));
			testDriveTA.setAchivementPerc(getAchievmentPercentage(testDriveCnt,targetParamMap.get(TEST_DRIVE)));
			testDriveTA.setShortfall(getShortFallCount(testDriveCnt,targetParamMap.get(TEST_DRIVE)));
			testDriveTA.setShortFallPerc(getShortFallPercentage(testDriveCnt,targetParamMap.get(TEST_DRIVE)));
		}else {
			testDriveTA.setTarget(String.valueOf("0"));
			testDriveTA.setAchivementPerc(String.valueOf("0"));
			testDriveTA.setShortfall(String.valueOf("0"));
			testDriveTA.setShortFallPerc(String.valueOf("0"));
		}
		//testDriveTA.setData(buildDataList(leadRefList,TEST_DRIVE));
		resList.add(testDriveTA);
		
		
		TargetAchivementModelandSource financeTA = new TargetAchivementModelandSource();
		
		financeTA.setParamName(FINANCE);
		financeTA.setParamShortName("Fin");
		financeTA.setModel(model);
		financeTA.setSource(source);
		financeTA.setAchievment(String.valueOf(financeCnt));
		if(targetParamMap.containsKey(FINANCE)) {
			financeTA.setTarget(String.valueOf(targetParamMap.get(FINANCE)));
			financeTA.setAchivementPerc(getAchievmentPercentage(financeCnt,targetParamMap.get(FINANCE)));
			financeTA.setShortfall(getShortFallCount(financeCnt,targetParamMap.get(FINANCE)));
			financeTA.setShortFallPerc(getShortFallPercentage(financeCnt,targetParamMap.get(FINANCE)));
		}else {
			financeTA.setTarget(String.valueOf("0"));
			financeTA.setAchivementPerc(String.valueOf("0"));
			financeTA.setShortfall(String.valueOf("0"));
			financeTA.setShortFallPerc(String.valueOf("0"));
		}
		//financeTA.setData(buildDataList(leadRefList,FINANCE));
		resList.add(financeTA);
		
		TargetAchivementModelandSource insuranceTA = new TargetAchivementModelandSource();
		
		insuranceTA.setParamName(INSURANCE);
		insuranceTA.setParamShortName("Ins");
		insuranceTA.setModel(model);
		insuranceTA.setSource(source);;
		insuranceTA.setAchievment(String.valueOf(insuranceCnt));
		if(targetParamMap.containsKey(INSURANCE)) {
			insuranceTA.setTarget(String.valueOf(targetParamMap.get(INSURANCE)));
			insuranceTA.setAchivementPerc(getAchievmentPercentage(insuranceCnt,targetParamMap.get(INSURANCE)));
			insuranceTA.setShortfall(getShortFallCount(insuranceCnt,targetParamMap.get(INSURANCE)));
			insuranceTA.setShortFallPerc(getShortFallPercentage(insuranceCnt,targetParamMap.get(INSURANCE)));
		}else {
			insuranceTA.setTarget(String.valueOf("0"));
			insuranceTA.setAchivementPerc(String.valueOf("0"));
			insuranceTA.setShortfall(String.valueOf("0"));
			insuranceTA.setShortFallPerc(String.valueOf("0"));
		}
		//insuranceTA.setData(buildDataList(leadRefList,INSURANCE));
		resList.add(insuranceTA);
		
		
		TargetAchivementModelandSource accessoriesTA = new TargetAchivementModelandSource();
		
		accessoriesTA.setParamName(ACCCESSORIES);
		accessoriesTA.setParamShortName("Acc");
		accessoriesTA.setModel(model);
		accessoriesTA.setSource(source);;
		accessoriesTA.setAchievment(String.valueOf(accessoriesCnt));
		if(targetParamMap.containsKey(ACCCESSORIES)) {
			accessoriesTA.setTarget(String.valueOf(targetParamMap.get(ACCCESSORIES)));
			accessoriesTA.setAchivementPerc(getAchievmentPercentage(accessoriesCnt,targetParamMap.get(ACCCESSORIES)));
			accessoriesTA.setShortfall(getShortFallCount(accessoriesCnt,targetParamMap.get(ACCCESSORIES)));
			accessoriesTA.setShortFallPerc(getShortFallPercentage(accessoriesCnt,targetParamMap.get(ACCCESSORIES)));
		}else {
			accessoriesTA.setTarget(String.valueOf("0"));
			accessoriesTA.setAchivementPerc(String.valueOf("0"));
			accessoriesTA.setShortfall(String.valueOf("0"));
			accessoriesTA.setShortFallPerc(String.valueOf("0"));
		}
		//accessoriesTA.setData(buildDataList(leadRefList,ACCCESSORIES));
		resList.add(accessoriesTA);
		
		
		TargetAchivementModelandSource bookingTA = new TargetAchivementModelandSource();

		bookingTA.setParamName(BOOKING);
		bookingTA.setParamShortName("Bkg");
		bookingTA.setModel(model);
		bookingTA.setSource(source);;
		bookingTA.setAchievment(String.valueOf(bookingCnt));
		if(targetParamMap.containsKey(BOOKING)) {
			bookingTA.setTarget(String.valueOf(targetParamMap.get(BOOKING)));
			bookingTA.setAchivementPerc(getAchievmentPercentage(bookingCnt,targetParamMap.get(BOOKING)));
			bookingTA.setShortfall(getShortFallCount(bookingCnt,targetParamMap.get(BOOKING)));
			bookingTA.setShortFallPerc(getShortFallPercentage(bookingCnt,targetParamMap.get(BOOKING)));
		}else {
			bookingTA.setTarget(String.valueOf("0"));
			bookingTA.setAchivementPerc(String.valueOf("0"));
			bookingTA.setShortfall(String.valueOf("0"));
			bookingTA.setShortFallPerc(String.valueOf("0"));
		}
		//bookingTA.setData(buildBkgDataList(leadRefList,BOOKING));
		resList.add(bookingTA);
		
		TargetAchivementModelandSource homeVisitTA = new TargetAchivementModelandSource();
		
		homeVisitTA.setParamName(HOME_VISIT);
		homeVisitTA.setParamShortName("Hvt");
		homeVisitTA.setModel(model);
		homeVisitTA.setSource(source);;
		homeVisitTA.setAchievment(String.valueOf(homeVistCnt));
		if(targetParamMap.containsKey(BOOKING)) {
			homeVisitTA.setTarget(String.valueOf(targetParamMap.get(HOME_VISIT)));
			homeVisitTA.setAchivementPerc(getAchievmentPercentage(homeVistCnt,targetParamMap.get(HOME_VISIT)));
			homeVisitTA.setShortfall(getShortFallCount(homeVistCnt,targetParamMap.get(HOME_VISIT)));
			homeVisitTA.setShortFallPerc(getShortFallPercentage(homeVistCnt,targetParamMap.get(HOME_VISIT)));
		}else {
			homeVisitTA.setTarget(String.valueOf("0"));
			homeVisitTA.setAchivementPerc(String.valueOf("0"));
			homeVisitTA.setShortfall(String.valueOf("0"));
			homeVisitTA.setShortFallPerc(String.valueOf("0"));
		}
		//homeVisitTA.setData(buildDataList(leadRefList,HOME_VISIT));
		resList.add(homeVisitTA);
		
		TargetAchivementModelandSource exchangeTA = new TargetAchivementModelandSource();
		exchangeTA.setParamName(EXCHANGE);
		exchangeTA.setParamShortName("Exg");
		exchangeTA.setModel(model);
		exchangeTA.setSource(source);
		exchangeTA.setAchievment(String.valueOf(exchangeCnt));
		if(targetParamMap.containsKey(EXCHANGE)) {
			exchangeTA.setTarget(String.valueOf(targetParamMap.get(EXCHANGE)));
			exchangeTA.setAchivementPerc(getAchievmentPercentage(exchangeCnt,targetParamMap.get(EXCHANGE)));
			exchangeTA.setShortfall(getShortFallCount(exchangeCnt,targetParamMap.get(EXCHANGE)));
			exchangeTA.setShortFallPerc(getShortFallPercentage(exchangeCnt,targetParamMap.get(EXCHANGE)));
		}else {
			exchangeTA.setTarget(String.valueOf("0"));
			exchangeTA.setAchivementPerc(String.valueOf("0"));
			exchangeTA.setShortfall(String.valueOf("0"));
			exchangeTA.setShortFallPerc(String.valueOf("0"));
		}
		//exchangeTA.setData(buildDataList(leadRefList,EXCHANGE));
		resList.add(exchangeTA);
		
		/*
		TargetAchivement vcTA = new TargetAchivement();
		vcTA.setTarget(String.valueOf(targetParamMap.get(VIDEO_CONFERENCE)));
		vcTA.setParamName(VIDEO_CONFERENCE);
		vcTA.setParamShortName("VC");
		vcTA.setAchievment(String.valueOf(0));
		vcTA.setAchivementPerc(String.valueOf(0));
		vcTA.setShortfall(String.valueOf(0));
		vcTA.setShortFallPerc(String.valueOf(0));
		resList.add(vcTA);*/
		
		TargetAchivementModelandSource rTa = new TargetAchivementModelandSource();
		rTa.setParamName(INVOICE);
		rTa.setParamShortName("Ret");
		rTa.setModel(model);
		rTa.setSource(source);
		rTa.setAchievment(String.valueOf(invoceCnt));
		if(targetParamMap.containsKey(INVOICE)) {
			rTa.setTarget(String.valueOf(targetParamMap.get(INVOICE)));
			rTa.setAchivementPerc(getAchievmentPercentage(invoceCnt,targetParamMap.get(INVOICE)));
			rTa.setShortfall(getShortFallCount(invoceCnt,targetParamMap.get(INVOICE)));
			rTa.setShortFallPerc(getShortFallPercentage(invoceCnt,targetParamMap.get(INVOICE)));
		}else {
			rTa.setTarget(String.valueOf("0"));
			rTa.setAchivementPerc(String.valueOf("0"));
			rTa.setShortfall(String.valueOf("0"));
			rTa.setShortFallPerc(String.valueOf("0"));
		}
		//rTa.setData(buildInvDataList(leadRefList,INVOICE));
		resList.add(rTa);
		
		
		
		
		
		TargetAchivementModelandSource extendedWarantyTA = new TargetAchivementModelandSource();
		extendedWarantyTA.setParamName(EXTENDED_WARRANTY);
		extendedWarantyTA.setParamShortName("ExW");
		extendedWarantyTA.setModel(model);
		extendedWarantyTA.setSource(source);;
		extendedWarantyTA.setAchievment(String.valueOf(extendedWarntyCnt));
		if(targetParamMap.containsKey(EXTENDED_WARRANTY)) {
			extendedWarantyTA.setTarget(String.valueOf(targetParamMap.get(EXTENDED_WARRANTY)));
			extendedWarantyTA.setAchivementPerc(getAchievmentPercentage(invoceCnt,targetParamMap.get(EXTENDED_WARRANTY)));
			extendedWarantyTA.setShortfall(getShortFallCount(invoceCnt,targetParamMap.get(EXTENDED_WARRANTY)));
			extendedWarantyTA.setShortFallPerc(getShortFallPercentage(invoceCnt,targetParamMap.get(EXTENDED_WARRANTY)));
		}else {
			extendedWarantyTA.setTarget(String.valueOf("0"));
			extendedWarantyTA.setAchivementPerc(String.valueOf("0"));
			extendedWarantyTA.setShortfall(String.valueOf("0"));
			extendedWarantyTA.setShortFallPerc(String.valueOf("0"));
		}
		//extendedWarantyTA.setData(buildDataList(leadRefList,EXTENDED_WARRANTY));
		resList.add(extendedWarantyTA);
		return resList;
	}
	
	
	private List<VehicleModelRes> getVehicleModelDataModelandSource(List<Integer> empIdsUnderReporting, DashBoardReqV2 req,String orgId, String branchId,
			List<String> vehicleModelList,Integer empId) {
		List<VehicleModelRes> resList = new ArrayList<>();
		//System.out.println(empIdsUnderReporting);
		//System.out.println("employee Id for reporting data "+empId);
		//empIdsUnderReporting.add(empId);
		List<String> empNamesList = dmsEmployeeRepo.findEmpNamesById(empIdsUnderReporting);
		log.info("empNamesList::" + empNamesList);
		String startDate = getStartDate(req.getStartDate());
		String endDate = getEndDate(req.getEndDate());
		log.info("StartDate " + startDate + ", EndDate " + endDate);
		log.info("vehicleModelList ::" + vehicleModelList);
		for (String model : vehicleModelList) {
			if (null != model) {
				VehicleModelRes vehicleRes = new VehicleModelRes();
				log.info("Generating data for model " + model);
				
				//New Code
				List<DmsEmployeeAllocation> dmsEmployeeAllocations = employeeAllocation.findByEmployeeId(empId);
				
				
				
				//List<Integer> dmsLeadList = dmsLeadDao.getLeadIdsByEmpNamesWithOutDrop(empNamesList);
				List<Integer> dmsLeadList = dmsLeadDao.getAllEmployeeLeadsWithModel1(orgId,empNamesList,startDate, endDate, model);
		        //System.out.println("dmsLeadList Before Adding"+dmsLeadList.size());

		        dmsLeadList.addAll(dmsEmployeeAllocations.stream().filter(res -> !res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")
						&& empNamesList.equals(res.getDmsLead().getSalesConsultant()) && res.getDmsLead().getModel().equalsIgnoreCase(model)).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
				//dmsLeadList.addAll(dmsEmployeeAllocations.stream().filter(res -> !res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
				
				//System.out.println("dmsLeadList After Adding"+dmsLeadList.size());	
				
				List<Integer> dmsLeadListDropped = dmsLeadDao.getAllEmployeeLeadsWithModel1(orgId,empNamesList,startDate, endDate, model);
				
				//System.out.println("dmsLeadListDropped Before Adding"+dmsLeadListDropped.size());
				
				dmsLeadListDropped.addAll(dmsEmployeeAllocations.stream().filter(res -> res.getDmsLead().getLeadStage().equalsIgnoreCase("DROPPED")
						&& empNamesList.equals(res.getDmsLead().getSalesConsultant()) && res.getDmsLead().getModel().equalsIgnoreCase(model)).map(res -> res.getDmsLead().getId()).collect(Collectors.toList()));
				
				System.out.println("dmsLeadListDropped After Adding"+dmsLeadListDropped.size());
				
				dmsLeadList = dmsLeadList.stream().distinct().collect(Collectors.toList());
				
				//System.out.println("dmsLeadList After Deleting Duplicates"+dmsLeadList.size());
				
				dmsLeadListDropped = dmsLeadListDropped.stream().distinct().collect(Collectors.toList());
				
				//System.out.println("dmsLeadListDropped After Deleting Duplicates"+dmsLeadListDropped.size());
				
				List<LeadStageRefEntity> leadRefList  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadList,startDate,endDate);
				
				//System.out.println("leadRefList Before Duplicates"+leadRefList.size());
				
				leadRefList = leadRefList.stream().distinct().collect(Collectors.toList());
				
				//System.out.println("leadRefList After Duplicates"+leadRefList.size());
				
				//List<LeadStageRefEntity> leadRefListDropped  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadListDropped,startDate,endDate);
				
				//New Code Ends
				
				
				
				//List<DmsLead> dmsLeadList = dmsLeadDao.getAllEmployeeLeadsWithModel(orgId,empNamesList,startDate, endDate, model);
				
				
				Long enqLeadCnt = 0L;
				Long bookCount = 0L;
				Long invCount = 0L;
				Long droppedCnt = 0L;
			
				/*
				 * List<Integer> dmsLeadIdList =
				 * dmsLeadList.stream().map(DmsLead::getId).collect(Collectors.toList());
				 * log.debug("dmsLeadList::"+dmsLeadList); List<LeadStageRefEntity> leadRefList
				 * =
				 * leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadIdList,startDate,endDate)
				 * ;
				 */
				if(null!=leadRefList && !leadRefList.isEmpty()) {
					log.debug("Total leads in LeadReF table is ::"+leadRefList.size());
//					//System.out.println("Total leads in LeadReF table is ------ ::"+leadRefList.size());
//					for(LeadStageRefEntity refentity : leadRefList) {
//						//System.out.println("-------------"+refentity.getStageName());
//					}
					enqLeadCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).distinct().count();
					bookCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("BOOKING")).distinct().count();
					invCount = leadRefList.stream().filter(x->x.getLeadStatus() !=null && x.getLeadStatus().equalsIgnoreCase("INVOICECOMPLETED")).distinct().count();

				}
				
				//System.out.println("@@@@@@@EnqMod List"+leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).map(res -> res.getLeadId()).distinct().collect(Collectors.toList()));
				//System.out.println("@@@@@@@@@BookingMod List"+leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("BOOKING")).map(res -> res.getLeadId()).distinct().collect(Collectors.toList()));
				//System.out.println("@@@@@@@@@@InvMod List"+leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(invCompStatus)).map(res -> res.getLeadId()).distinct().collect(Collectors.toList()));
		
				droppedCnt = 0L;
				if (null != dmsLeadList) {
					log.info("size of dmsLeadList " + dmsLeadList.size());
					enqLeadCnt = enqLeadCnt;
					if(dmsLeadListDropped!=null && dmsLeadListDropped.size() > 0)
					{
					droppedCnt = dmsLeadListDropped.stream().distinct().count();
					}
					

					log.info("enqLeadCnt: " + enqLeadCnt + " ,droppedCnt : " + droppedCnt);
				}
				vehicleRes.setModel(model);
				vehicleRes.setE(enqLeadCnt);
				vehicleRes.setL(droppedCnt);
				vehicleRes.setB(bookCount);
				vehicleRes.setR(invCount);
				List<String> leadUniversalIdList = leadRefList.stream().map(x->x.getUniversalId()).distinct().collect(Collectors.toList());
				/*
				 * dmsLeadList.stream().map(DmsLead::getCrmUniversalId)
				 * .collect(Collectors.toList());
				 */
				log.info("leadUniversalIdList " + leadUniversalIdList);

				List<DmsWFTask> wfTaskList = dmsWfTaskDao.getWfTaskByAssigneeIdListByModel(empIdsUnderReporting,
						leadUniversalIdList, startDate, endDate);

				vehicleRes.setT(getTestDriveCount(wfTaskList));
				vehicleRes.setV(getHomeVisitCount(wfTaskList));
			//	vehicleRes.setB(getBookingCount(wfTaskList));
				
				resList.add(vehicleRes);
			}
		}
		return resList;
	}
	
	
	public List<TargetAchivementModelandSource>  getTargetAchivementParamsForMultipleEmpAndEmpsModelAndSourceV4(
			List<Integer> empIdsUnderReporting, DashBoardReqV2 req,String orgId,List<EmployeeTargetAchievementModelAndView> empTargetAchievements,String startDate,String endDate,List<VehicleModelRes> vehicleModelDataModelAndSource,List<LeadSourceRes> leadSourceData) throws ParseException, DynamicFormsServiceException {
		log.debug("Calling getTargetAchivementParamsForMultipleEmp");

		List<TargetAchivementModelandSource> resList = new ArrayList<>();
		List<DmsEmployee> employees = dmsEmployeeRepo.findAllById(empIdsUnderReporting);
		List<String> empNamesList = employees.stream().map(x->x.getEmpName()).collect(Collectors.toList());
		int empId1 = req.getLoggedInEmpId();
		
		Map<String, Integer> map = new LinkedHashMap<>();
				
		List<List<DmsEmployee>> empIdPartionList = partitionListEmp(employees);
		log.debug("empIdPartionList ::" + empIdPartionList.size());
		ExecutorService executor = Executors.newFixedThreadPool(empIdPartionList.size());
		
		List<CompletableFuture<List<EmployeeTargetAchievementModelAndView>>> futureList = empIdPartionList.stream()
				.map(strings -> CompletableFuture.supplyAsync(() -> processTargetAchivementFormMultipleEmpModelAndSource(strings,map,startDate, endDate), executor))
				.collect(Collectors.toList());
		if (null != futureList) {
			CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
			Stream<List<EmployeeTargetAchievementModelAndView>> dataStream = (Stream<List<EmployeeTargetAchievementModelAndView>>) futureList.stream().map(CompletableFuture::join);
			dataStream.forEach(x -> {
				empTargetAchievements.addAll(x);
			});

		}
	
		/*for(DmsEmployee employee : employees) {
			EmployeeTargetAchievement employeeTargetAchievement = new EmployeeTargetAchievement();
			log.debug("Getting target params for user "+employee.getEmp_id());
			Map<String, Integer> innerMap = getTargetParams(String.valueOf(employee.getEmp_id()), startDate, endDate);
			log.debug("innerMap::"+innerMap);
			employeeTargetAchievement.setEmpId(employee.getEmp_id());
			employeeTargetAchievement.setEmpName(employee.getEmpName());
			employeeTargetAchievement.setBranchId(employee.getBranch());
			employeeTargetAchievement.setOrgId(employee.getOrg());
			employeeTargetAchievement.setTargetAchievementsMap(innerMap);
			empTargetAchievements.add(employeeTargetAchievement);
			/*map = validateAndUpdateMapData(ENQUIRY,innerMap,map);
			map = validateAndUpdateMapData(TEST_DRIVE,innerMap,map);
			map = validateAndUpdateMapData(HOME_VISIT,innerMap,map);
			map = validateAndUpdateMapData(VIDEO_CONFERENCE,innerMap,map);
			map = validateAndUpdateMapData(BOOKING,innerMap,map);
			map = validateAndUpdateMapData(EXCHANGE,innerMap,map);
			map = validateAndUpdateMapData(FINANCE,innerMap,map);
			map = validateAndUpdateMapData(INSURANCE,innerMap,map);
			map = validateAndUpdateMapData(ACCCESSORIES,innerMap,map);
			map = validateAndUpdateMapData(EVENTS,innerMap,map);
			map = validateAndUpdateMapData(INVOICE,innerMap,map);
		}*/
		return getTaskAndBuildTargetAchievementsmodelandSource(empIdsUnderReporting, orgId, resList, empNamesList, startDate, endDate,map,vehicleModelDataModelAndSource,leadSourceData, empId1);
	}
	
	
	public List<List<EmployeeTargetAchievementModelAndView>> partitionListEmpTargetModelAndSource(List<EmployeeTargetAchievementModelAndView> list) {
		final int G = 5;
		final int NG = (list.size() + G - 1) / G;
		List<List<EmployeeTargetAchievementModelAndView>> result = IntStream.range(0, NG)
			    .mapToObj(i -> list.subList(G * i, Math.min(G * i + G, list.size())))
			    .collect(Collectors.toList());
		return result;
	}
	
	private List<EmployeeTargetAchievementModelAndView>  processTargetAchivementFormMultipleEmpModelAndSource(List<DmsEmployee> employees,Map<String, Integer> map,
			 String startDate, String endDate) {
		List<EmployeeTargetAchievementModelAndView> empTargetAchievements = new ArrayList<>();
		try {
			for (DmsEmployee employee : employees) {
				EmployeeTargetAchievementModelAndView employeeTargetAchievement = new EmployeeTargetAchievementModelAndView();
				log.debug("Getting target params for user " + employee.getEmp_id());
				Map<String, Integer> innerMap = getTargetParams(String.valueOf(employee.getEmp_id()), startDate,
						endDate);
				log.debug("innerMap::" + innerMap);
				employeeTargetAchievement.setEmpId(employee.getEmp_id());
				employeeTargetAchievement.setEmpName(employee.getEmpName());
				employeeTargetAchievement.setBranchId(employee.getBranch());
				employeeTargetAchievement.setOrgId(employee.getOrg());
				employeeTargetAchievement.setTargetAchievementsMap(innerMap);
				empTargetAchievements.add(employeeTargetAchievement);
				
				map = validateAndUpdateMapData(ENQUIRY,innerMap,map);
				map = validateAndUpdateMapData(TEST_DRIVE,innerMap,map);
				map = validateAndUpdateMapData(HOME_VISIT,innerMap,map);
				map = validateAndUpdateMapData(VIDEO_CONFERENCE,innerMap,map);
				map = validateAndUpdateMapData(BOOKING,innerMap,map);
				map = validateAndUpdateMapData(EXCHANGE,innerMap,map);
				map = validateAndUpdateMapData(FINANCE,innerMap,map);
				map = validateAndUpdateMapData(INSURANCE,innerMap,map);
				map = validateAndUpdateMapData(ACCCESSORIES,innerMap,map);
				map = validateAndUpdateMapData(EVENTS,innerMap,map);
				map = validateAndUpdateMapData(INVOICE,innerMap,map);
				map = validateAndUpdateMapData(EXTENDED_WARRANTY,innerMap,map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in processTargetAchivementFormMultipleEmp ", e);
		}
		return empTargetAchievements;
	}


//	public List<EmployeeTargetAchievementModelAndView> processEmployeeTargetAchiveListModelAndView(List<EmployeeTargetAchievementModelAndView> empTargetAchievements,
//			List<TargetAchivementModelandSource> resList, String startDt, String endDt) {
//		List<EmployeeTargetAchievementModelAndView> res = new ArrayList<>();
//		try {
//			
//				empTargetAchievements.stream().forEach(employeeTarget->{
//					List<TargetAchivement> responseList = new ArrayList();
//					employeeTarget.setTargetAchievements(getTaskAndBuildTargetAchievements(Arrays.asList(employeeTarget.getEmpId()), employeeTarget.getOrgId(), responseList, Arrays.asList(employeeTarget.getEmpName()), startDt,endDt, employeeTarget.getTargetAchievementsMap()));
//					res.add(employeeTarget);
//				});
//				
//			
//		}catch(Exception e) {
//			e.printStackTrace();
//			log.error("Exception ",e);
//			
//		}
//		return res;
//	}
//	
	
	public List<EmployeeTargetAchievementModelAndView> processEmployeeTargetAchiveListModelAndSource(List<EmployeeTargetAchievementModelAndView> empTargetAchievements,
			List<TargetAchivementModelandSource> resList, String startDt, String endDt,List<VehicleModelRes> vehicleModelDataModelAndSourceFinal,List<LeadSourceRes> leadSourceDataFinal, int empId1) {
		List<EmployeeTargetAchievementModelAndView> res = new ArrayList<>();
		try {
			
				empTargetAchievements.stream().forEach(employeeTarget->{
					List<TargetAchivementModelandSource> responseList = new ArrayList();
					employeeTarget.setTargetAchievements(getTaskAndBuildTargetAchievementsmodelandSource(Arrays.asList(employeeTarget.getEmpId()), employeeTarget.getOrgId(), responseList, Arrays.asList(employeeTarget.getEmpName()), startDt,endDt, employeeTarget.getTargetAchievementsMap(),vehicleModelDataModelAndSourceFinal,leadSourceDataFinal, empId1));
					res.add(employeeTarget);
				});
				
			
		}catch(Exception e) {
			e.printStackTrace();
			log.error("Exception ",e);
			
		}
		return res;
	}
	
	
	@Override
	public List<TargetAchivementModelandSource> getTargetAchivementParamsForSingleEmpModelAndSource3(DashBoardReqV2 req) throws DynamicFormsServiceException {
		log.info("Inside getTargetAchivementParams(){}");
		List<TargetAchivementModelandSource> resList = new ArrayList<>();
		try {
			List<List<TargetAchivementModelandSource>> allTargets = new ArrayList<>();
			Integer empId = req.getLoggedInEmpId();
			log.debug("Getting Target Data, LoggedIn emp id "+empId );
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(empId);
			String orgId = tRole.getOrgId();
			String branchId = tRole.getBranchId();
			log.debug("Fetching empReportingIdList for logged in emp in else :"+req.getLoggedInEmpId());
			log.debug("empReportingIdList for emp "+req.getLoggedInEmpId());
			resList = getTargetAchivementParamsForEmpModelAndSource3(req.getLoggedInEmpId(),req,orgId,branchId);
			}catch(Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resList;
	}
	
	
	private List<TargetAchivementModelandSource> getTargetAchivementParamsForEmpModelAndSource3(
			Integer empId, DashBoardReqV2 req,String orgId,String branchId) throws ParseException, DynamicFormsServiceException {
		List<TargetAchivementModelandSource> resList = new ArrayList<>();
		Optional<DmsEmployee> dmsEmployee = dmsEmployeeRepo.findById(empId);
		int empId1=req.getLoggedInEmpId();
		String empName="";
		if(dmsEmployee.isPresent()) {
			empName = dmsEmployee.get().getEmpName();
		}
		log.info("Calling getTargetAchivementParamsForEmp");
		String startDate = null;
		String endDate = null;
		if (null == req.getStartDate() && null == req.getEndDate()) {
			startDate = getFirstDayOfMonth();
			endDate = getLastDayOfMonth();
		} else {
			startDate = req.getStartDate()+" 00:00:00";
			endDate = req.getEndDate()+" 23:59:59";
		}
		log.info("StartDate " + startDate + ", EndDate " + endDate);
		Map<String, Integer> map = new LinkedHashMap<>();
		log.debug("Getting target params for user "+empId);
		Map<String, Integer> innerMap = getTargetParams(String.valueOf(empId), startDate, endDate);
		log.debug("innerMap::"+innerMap);
		map = validateAndUpdateMapData(ENQUIRY,innerMap,map);
		map = validateAndUpdateMapData(TEST_DRIVE,innerMap,map);
		map = validateAndUpdateMapData(HOME_VISIT,innerMap,map);
		map = validateAndUpdateMapData(VIDEO_CONFERENCE,innerMap,map);
		map = validateAndUpdateMapData(BOOKING,innerMap,map);
		map = validateAndUpdateMapData(EXCHANGE,innerMap,map);
		map = validateAndUpdateMapData(FINANCE,innerMap,map);
		map = validateAndUpdateMapData(INSURANCE,innerMap,map);
		map = validateAndUpdateMapData(ACCCESSORIES,innerMap,map);
		map = validateAndUpdateMapData(EVENTS,innerMap,map);
		map = validateAndUpdateMapData(INVOICE,innerMap,map);
		map = validateAndUpdateMapData(EXTENDED_WARRANTY,innerMap,map);
		Map<Integer, String> vehicleDataMap = dashBoardUtil.getVehilceDetails(orgId).get("main");
		List<String> vehicleModelList = new ArrayList<>();
		vehicleDataMap.forEach((k, v) -> {
			vehicleModelList.add(v);
		});
		
		
		 List<VehicleModelRes> vehicleModelData = getVehicleModelDataModelandSource(Arrays.asList(empId), req, orgId, branchId, vehicleModelList,empId);
		 List<LeadSourceRes> leadSourceData = getLeadSourceData(req); 
		return getTaskAndBuildTargetAchievementsmodelandSource(Arrays.asList(empId), orgId, resList, Arrays.asList(empName), startDate, endDate,
				map,vehicleModelData,leadSourceData, empId1);
	}

	
	
	
	
	
	@Override
	public Map<String, Object> getTodaysPendingUpcomingDataV2Filter(MyTaskReq req) throws DynamicFormsServiceException {
		log.info("Inside getTodaysPendingUpcomingDataV2Filter(){},empId " + req.getLoggedInEmpId() + " and IsOnlyForEmp "
				+ req.isOnlyForEmp());
		Map<String, Object> list = new LinkedHashMap<>();

		try {

			boolean isOnlyForEmp =req.isOnlyForEmp();
			List<Integer> empIdList = new ArrayList<>();
			log.debug("isOnlyForEmp::"+isOnlyForEmp);
			if(isOnlyForEmp) {
				empIdList.add(req.getLoggedInEmpId());

			}else {
				if(req.getSalesConsultantId() != null && req.getSalesConsultantId().size()>0){
					req.getSalesConsultantId().remove(req.getLoggedInEmpId());
					empIdList = req.getSalesConsultantId();
				}else{
					Long startTime = System.currentTimeMillis();
					empIdList = getReportingEmployesFilter(req.getLoggedInEmpId());
					log.debug("getReportingEmployes list "+empIdList.size());
					log.debug("Time taken to get employess list "+(System.currentTimeMillis()-startTime));
				}
			}
			Long startTime_1 = System.currentTimeMillis();
			list = getTodaysDataV2Filter(empIdList, req,req.getDataType());
			log.debug("Time taken to get Todays Data "+(System.currentTimeMillis()-startTime_1));

		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return list;
	}

	public List<Integer> getReportingEmployesFilter(Integer empId) throws DynamicFormsServiceException {
		List<String> empReportingIdList = new ArrayList<>();
		List<Integer> empReportingIdList_1 = new ArrayList<>();
		Optional<DmsEmployee> empOpt = dmsEmployeeRepo.findEmpById(empId);
		if(empOpt.isPresent()) {
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV3(empId);
			log.debug("tRole::"+tRole);
			log.debug("tRole.getOrgMapBranches():::"+tRole.getOrgMapBranches());

			for(String orgMapBranchId : tRole.getOrgMapBranches()) {
				Map<String, Object> hMap = ohServiceImpl.getReportingHierarchyV2(empOpt.get(),Integer.parseInt(orgMapBranchId),Integer.parseInt((tRole.getOrgId())));
				if(null!=hMap) {
					for(Map.Entry<String, Object> mapentry : hMap.entrySet()) {
						Map<String, Object> map2 = (Map<String, Object>)mapentry.getValue();
						for(Map.Entry<String, Object> mapentry_1 :map2.entrySet()) {
							List<TargetDropDownV2> ddList = (List<TargetDropDownV2>)mapentry_1.getValue();
							empReportingIdList.addAll(ddList.stream().map(x->x.getCode()).collect(Collectors.toList()));
						}
					}
				}
			}
			Set<String> s = new HashSet<>();
			s.addAll(empReportingIdList);
			empReportingIdList = new ArrayList<>(s);
		}else {
			throw new DynamicFormsServiceException("Logged in emp is not valid employee,no record found in dms_employee", HttpStatus.BAD_REQUEST);
		}
		empReportingIdList_1 = empReportingIdList.stream().map(Integer::parseInt).collect(Collectors.toList());

		return empReportingIdList_1;
	}

	private Map<String, Object> getTodaysDataV2Filter(List<Integer> empIdsUnderReporting, MyTaskReq req, String dataType) {

		Map<String, Object> map = new LinkedHashMap<>();
		try {
			log.debug("empIdsUnderReporting in getTodaysData before pagination"+empIdsUnderReporting.size());
			log.debug("dataType::::"+dataType);
			if(dataType.equalsIgnoreCase(DynamicFormConstants.TODAYS_DATA)) {
				map.put("todaysData", processTodaysUpcomingPendingDataFilter(req,empIdsUnderReporting,DynamicFormConstants.TODAYS_DATA));
				map.put("rescheduledData", processTodaysUpcomingPendingDataFilter(req,empIdsUnderReporting,DynamicFormConstants.RESCHEDULED_DATA));
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.PENDING_DATA)) {
				map.put("pendingData", processTodaysUpcomingPendingDataFilter(req,empIdsUnderReporting,DynamicFormConstants.PENDING_DATA));
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.RESCHEDULED_DATA)) {
				map.put("rescheduledData", processTodaysUpcomingPendingDataFilter(req,empIdsUnderReporting,DynamicFormConstants.RESCHEDULED_DATA));
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.COMPLETED_DATA)) {
				map.put("completedData", processTodaysUpcomingPendingDataFilter(req,empIdsUnderReporting,DynamicFormConstants.COMPLETED_DATA));
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in getTodaysDataV2 ",e);			}
		return map;

	}

	private List<TodaysTaskRes> processTodaysUpcomingPendingDataFilter(MyTaskReq req, List<Integer> empIdsUnderReporting,String dataType) {

		log.debug("Inside getTodayDataV2::()");
		List<TodaysTaskRes> todaysRes = new ArrayList<>();

		if (empIdsUnderReporting.size() > 0) {
			List<List<Integer>> empIdPartionList = partitionList(empIdsUnderReporting);
			log.debug("empIdPartionList ::" + empIdPartionList.size());
			ExecutorService executor = Executors.newFixedThreadPool(empIdPartionList.size());

			List<CompletableFuture<List<TodaysTaskRes>>> futureList  =null;
			if(dataType.equalsIgnoreCase(DynamicFormConstants.TODAYS_DATA)) {
				futureList = empIdPartionList.stream()
						.map(strings -> CompletableFuture.supplyAsync(() -> processTodaysDataFilter(req,strings), executor))
						.collect(Collectors.toList());
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.PENDING_DATA)) {
				futureList = empIdPartionList.stream()
						.map(strings -> CompletableFuture.supplyAsync(() -> processPendingDataFilter(req,strings), executor))
						.collect(Collectors.toList());
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.RESCHEDULED_DATA)) {
				futureList = empIdPartionList.stream()
						.map(strings -> CompletableFuture.supplyAsync(() -> processResechduledDataFilter(req,strings), executor))
						.collect(Collectors.toList());
			}
			else if(dataType.equalsIgnoreCase(DynamicFormConstants.COMPLETED_DATA)) {
				futureList = empIdPartionList.stream()
						.map(strings -> CompletableFuture.supplyAsync(() -> processCompletededDataFilter(req,strings), executor))
						.collect(Collectors.toList());
			}
			if (null != futureList) {
				CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
				Stream<List<TodaysTaskRes>> dataStream = (Stream<List<TodaysTaskRes>>) futureList.stream().map(CompletableFuture::join);
				dataStream.forEach(x -> {
					todaysRes.addAll(x);
				});

			}
		}
		log.debug("size of todaysRes " + todaysRes.size());

		return todaysRes;
	}

	private List<TodaysTaskRes> processTodaysDataFilter(MyTaskReq req,  List<Integer> empIdsUnderReporting) {
		List<TodaysTaskRes> todaysRes = new ArrayList<>();
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			log.debug("generating data for empId " + empId + " and empName:" + empName);

			String todaysDate = getTodaysDate();
			log.debug("todaysDate::" + todaysDate);

			List<DmsWFTask> todayWfTaskList = null;
			List<String> dealerId = req.getBranchCodes();

			if(req.isIgnoreDateFilter() && (req.getBranchCodes()!=null && req.getBranchCodes().size()>0)){
				todayWfTaskList =dmsWfTaskDao.getTodaysUpcomingTasksWithDealer(empId, todaysDate + " 00:00:00", todaysDate + " 23:59:59",dealerId);
			} else if((!req.isIgnoreDateFilter() && req.getStartDate()!=null && req.getEndDate()!=null) && (req.getBranchCodes()!=null && req.getBranchCodes().size()>0)){
				todayWfTaskList = dmsWfTaskDao.getTodaysUpcomingTasksWithDateAndDealer(empId,req.getStartDate() + " 00:00:00", req.getEndDate() + " 23:59:59",dealerId);
			} else if((!req.isIgnoreDateFilter() && req.getStartDate()!=null && req.getEndDate()!=null) && (req.getBranchCodes()==null || req.getBranchCodes().size()==0)){
				todayWfTaskList = dmsWfTaskDao.getTodaysUpcomingTasks(empId,req.getStartDate()+ " 00:00:00", req.getEndDate()+" 23:59:59");
			} else{
				todayWfTaskList = dmsWfTaskDao.getTodaysUpcomingTasks(empId, todaysDate + " 00:00:00", todaysDate + " 23:59:59");
			}
			todaysRes.add(buildMyTaskObj(todayWfTaskList, empId, empName));
		}
		return todaysRes;
	}


	private List<TodaysTaskRes> processPendingDataFilter(MyTaskReq req,  List<Integer> empIdsUnderReporting) {
		log.debug("Inside getUpcomingDataV2::()");
		List<TodaysTaskRes> todaysRes = new ArrayList<>();
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			String startDate = req.getStartDate()+" 00:00:00";
			String endDate = req.getEndDate()+" 23:59:59";
			log.debug("processPendingData :startDate:"+startDate+",endDate:"+endDate);
			List<DmsWFTask> wfTaskList = null;
			List<String> dealerId = req.getBranchCodes();

			if(req.isIgnoreDateFilter() && (req.getBranchCodes()!=null && req.getBranchCodes().size()>0)){
				wfTaskList =dmsWfTaskDao.findAllByPendingStatusFilterWithDealer(empId,dealerId);
			} else if((!req.isIgnoreDateFilter() && req.getStartDate()!=null && req.getEndDate()!=null) && (req.getBranchCodes()!=null && req.getBranchCodes().size()>0)){
				wfTaskList =dmsWfTaskDao.findAllByPendingStatusFilterWithDateAndDealer(empId,startDate,endDate,dealerId);
			} else if((!req.isIgnoreDateFilter() && req.getStartDate()!=null && req.getEndDate()!=null) && (req.getBranchCodes()==null || req.getBranchCodes().size()==0)){
				wfTaskList = dmsWfTaskDao.findAllByPendingData(empId,startDate,endDate);
			} else{
				wfTaskList = dmsWfTaskDao.findAllByPendingStatus (String.valueOf(empId));
			}

			log.debug("wfTaskList size ingetPendingDataV2 "+wfTaskList.size());
			todaysRes.add(buildMyTaskObj(wfTaskList,empId,empName));
		}
		return todaysRes;
	}

	private List<TodaysTaskRes> processResechduledDataFilter(MyTaskReq req,List<Integer> empIdsUnderReporting) {
		log.debug("Inside getRescheduledDataV2::()");
		List<TodaysTaskRes> todaysRes = new ArrayList<>();
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			log.debug("generating data for empId " + empId + " and empName:" + empName);
			String startDate = req.getStartDate()+" 00:00:00";
			String endDate = req.getEndDate()+" 23:59:59";
			List<String> dealerId = req.getBranchCodes();

			List<DmsWFTask> wfTaskList  =null;

			if(req.isIgnoreDateFilter() && (req.getBranchCodes()!=null && req.getBranchCodes().size()>0)){
				wfTaskList =dmsWfTaskDao.findAllByRescheduledStatusFilterWithDealer(empId,dealerId);
			} else if((!req.isIgnoreDateFilter() && req.getStartDate()!=null && req.getEndDate()!=null) && (req.getBranchCodes()!=null && req.getBranchCodes().size()>0)){
				wfTaskList =dmsWfTaskDao.findAllByRescheduledStatusFilterWithDateAndDealer(empId,startDate,endDate,dealerId);
			} else if((!req.isIgnoreDateFilter() && req.getStartDate()!=null && req.getEndDate()!=null) && (req.getBranchCodes()==null || req.getBranchCodes().size()==0)){
				wfTaskList =dmsWfTaskDao.findAllByRescheduledStatus(empId,startDate,endDate);
			} else{
				wfTaskList = dmsWfTaskDao.findAllByRescheduledStatusWithNoDate (String.valueOf(empId));
			}

			log.debug("wfTaskList size ingetPendingDataV2 "+wfTaskList.size());
			todaysRes.add(buildMyTaskObj(wfTaskList,empId,empName));
		}
		return todaysRes;
	}
	private List<TodaysTaskRes> processCompletededDataFilter(MyTaskReq req,List<Integer> empIdsUnderReporting) {
		log.debug("Inside getRescheduledDataV2::()");
		List<TodaysTaskRes> todaysRes = new ArrayList<>();
		for (Integer empId : empIdsUnderReporting) {
			String empName = salesGapServiceImpl.getEmpName(String.valueOf(empId));
			log.debug("generating data for empId " + empId + " and empName:" + empName);
			String startDate = req.getStartDate()+" 00:00:00";
			String endDate = req.getEndDate()+" 23:59:59";
			List<String> dealerId = req.getBranchCodes();

			List<DmsWFTask> wfTaskList  =null;

			if(req.isIgnoreDateFilter() && (req.getBranchCodes()!=null && req.getBranchCodes().size()>0)){
				wfTaskList =dmsWfTaskDao.findAllByCompletedStatusFilterWithDealer(empId,dealerId);
			} else if((!req.isIgnoreDateFilter() && req.getStartDate()!=null && req.getEndDate()!=null) && (req.getBranchCodes()!=null && req.getBranchCodes().size()>0)){
				wfTaskList =dmsWfTaskDao.findAllByCompletedStatusFilterWithDateAndDealer(empId,startDate,endDate,dealerId);
			} else if((!req.isIgnoreDateFilter() && req.getStartDate()!=null && req.getEndDate()!=null) && (req.getBranchCodes()==null || req.getBranchCodes().size()==0)){
				wfTaskList =dmsWfTaskDao.findAllByCompletedStatus(empId,startDate,endDate);
			} else{
				wfTaskList = dmsWfTaskDao.findAllByCompletedStatusWithNoDate (String.valueOf(empId));
			}

			log.debug("wfTaskList size ingetPendingDataV2 "+wfTaskList.size());
			todaysRes.add(buildMyTaskObj(wfTaskList,empId,empName));
		}
		return todaysRes;
	}
}



