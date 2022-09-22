package com.automate.df.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.automate.df.dao.DmsSourceOfEnquiryDao;
import com.automate.df.dao.LeadStageRefDao;
import com.automate.df.dao.SourceAndIddao;
import com.automate.df.dao.TmpEmpHierarchyDao;
import com.automate.df.dao.dashboard.DashBoardV3Dao;
import com.automate.df.dao.dashboard.DmsEmpTargetRankingBranchDao;
import com.automate.df.dao.dashboard.DmsEmpTargetRankingOrgDao;
import com.automate.df.dao.dashboard.DmsLeadDao;
import com.automate.df.dao.dashboard.DmsLeadDropDao;
import com.automate.df.dao.dashboard.DmsTargetParamAllEmployeeSchedularDao;
import com.automate.df.dao.dashboard.DmsTargetParamEmployeeSchedularDao;
import com.automate.df.dao.dashboard.DmsWfTaskDao;
import com.automate.df.dao.dashboard.TargetAchivementModelandSource;
import com.automate.df.dao.salesgap.DmsEmployeeRepo;
import com.automate.df.dao.salesgap.TargetSettingRepo;
import com.automate.df.dao.salesgap.TargetUserRepo;
import com.automate.df.entity.LeadStageRefEntity;
import com.automate.df.entity.SourceAndId;
import com.automate.df.entity.dashboard.DmsLead;
import com.automate.df.entity.dashboard.DmsWFTask;
import com.automate.df.entity.salesgap.DmsEmployee;
import com.automate.df.exception.DynamicFormsServiceException;
import com.automate.df.model.df.dashboard.DashBoardReqV2;
import com.automate.df.model.df.dashboard.EmployeeTargetAchievement;
import com.automate.df.model.df.dashboard.EmployeeTargetAchievementModelAndView;
import com.automate.df.model.df.dashboard.LeadSourceRes;
import com.automate.df.model.df.dashboard.OverAllTargetAchivements;
import com.automate.df.model.df.dashboard.OverAllTargetAchivementsModelAndSource;
import com.automate.df.model.df.dashboard.TargetAchivement;
import com.automate.df.model.df.dashboard.VehicleModelRes;
import com.automate.df.model.salesgap.TargetDropDownV2;
import com.automate.df.model.salesgap.TargetRoleRes;
import com.automate.df.service.DashBoardServiceV4;
import com.automate.df.util.DashBoardUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;


/**
 * 
 * @author srujan
 *
 */

@Service
@Slf4j
public class DashBoardServiceImplV4 implements DashBoardServiceV4{
	
	
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
	
	
	
	public static final String enqCompStatus = "ENQUIRYCOMPLETED";
	public static final String preBookCompStatus = "PREBOOKINGCOMPLETED";
	public static final String bookCompStatus = "BOOKINGCOMPLETED";
	public static final String invCompStatus = "INVOICECOMPLETED";
	public static final String preDelCompStatus = "PREDELIVERYCOMPLETED";
	public static final String delCompStatus="DELIVERYCOMPLETED";

	private static final String RETAIL_TARGET = "RETAIL_TARGET";
	
	

	@Autowired
	DmsSourceOfEnquiryDao dmsSourceOfEnquiryDao;
	
	@Autowired
	SourceAndIddao repository; 
	
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
	ObjectMapper om;
	
	@Autowired
	LeadStageRefDao leadStageRefDao;
	
	@Value("${lead.enquiry.url}")
	String leadSourceEnqUrl;
	
	@Autowired
	DashBoardServiceImplV2 dashBoardServiceImplV2;
	
	
	@Override
	public OverAllTargetAchivements getTargetAchivementParamsWithEmps(DashBoardReqV2 req) throws DynamicFormsServiceException {
		log.info("Inside getTargetAchivementParamsWithEmps() of DashBoardServiceImplV{}");
		OverAllTargetAchivements overAllTargetAchivements = new OverAllTargetAchivements();
		List<EmployeeTargetAchievement> empTargetAchievements = new ArrayList<>();
		List<EmployeeTargetAchievement> finalEmpTargetAchievements = new ArrayList<>();
		List<TargetAchivement> resList = new ArrayList<>();
		try {
			List<List<TargetAchivement>> allTargets = new ArrayList<>();
			Integer empId = req.getLoggedInEmpId();
			log.debug("Getting Target Data, LoggedIn emp id "+empId );
			String startDate = null;
			String endDate = null;
			if (null == req.getStartDate() && null == req.getEndDate()) {
				startDate = dashBoardServiceImplV2.getFirstDayOfMonth();
				endDate = dashBoardServiceImplV2.getLastDayOfMonth();
			} else {
				startDate = req.getStartDate()+" 00:00:00";
				endDate = req.getEndDate()+" 23:59:59";
			}

			List<Integer> selectedEmpIdList = req.getEmpSelected();
			List<Integer> selectedNodeList = req.getLevelSelected();
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(empId);
			String orgId = req.getOrgId();
			log.debug("tRole getTargetAchivementParams "+tRole);
			if (null != selectedEmpIdList && !selectedEmpIdList.isEmpty()) {
				log.debug("Fetching empReportingIdList for selected employees,selectedEmpIdList" + selectedEmpIdList);
				for (Integer eId : selectedEmpIdList) {
					List<Integer> empReportingIdList = new ArrayList<>();
					empReportingIdList.add(eId);
					empReportingIdList.addAll(getImmediateReportingEmp(empId,orgId));
					log.debug("empReportingIdList for given selectedEmpIdList " + empReportingIdList);
					List<TargetAchivement> targetList = dashBoardServiceImplV2.getTargetAchivementParamsForMultipleEmpAndEmps(
							empReportingIdList, req, orgId, empTargetAchievements, startDate, endDate);
					
					allTargets.add(targetList);
				}

				resList = dashBoardServiceImplV2.buildFinalTargets(allTargets);
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
					List<TargetAchivement> targetList = dashBoardServiceImplV2.getTargetAchivementParamsForMultipleEmpAndEmps(empReportingIdList,req,orgId,empTargetAchievements,startDate,endDate);
					allTargets.add(targetList);
					
				}
				resList = dashBoardServiceImplV2.buildFinalTargets(allTargets);
			}
			else {
				log.debug("Fetching empReportingIdList for logged in emp in else :"+req.getLoggedInEmpId());
				List<Integer> empReportingIdList =  getImmediateReportingEmp(req.getSelectedEmpId(),orgId);
				log.debug("empReportingIdList::"+empReportingIdList);
				empReportingIdList.add(req.getSelectedEmpId());
				resList = dashBoardServiceImplV2.getTargetAchivementParamsForMultipleEmpAndEmps(empReportingIdList,req,orgId,empTargetAchievements,startDate,endDate);
			}
			final List<TargetAchivement> resListFinal = resList;
			final String startDt = startDate;
			final String endDt = endDate;
			
			if(empTargetAchievements.size()>1) {
			List<List<EmployeeTargetAchievement>> targetAchiPartList = dashBoardServiceImplV2.partitionListEmpTarget(empTargetAchievements);
			ExecutorService executor = Executors.newFixedThreadPool(targetAchiPartList.size());
			
			List<CompletableFuture<List<EmployeeTargetAchievement>>> futureList  = targetAchiPartList.stream()
					.map(strings -> CompletableFuture.supplyAsync(() -> dashBoardServiceImplV2.processEmployeeTargetAchiveList(strings,resListFinal,startDt,endDt), executor))
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

		}catch(Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		overAllTargetAchivements.setOverallTargetAchivements(resList);
		//overAllTargetAchivements.setEmployeeTargetAchievements(empTargetAchievements);
		overAllTargetAchivements.setEmployeeTargetAchievements(finalEmpTargetAchievements);
		return overAllTargetAchivements;
	}
	
	

	@Override
	public List<TargetAchivement> getTargetAchivementParams(DashBoardReqV2 req) throws DynamicFormsServiceException {
		Integer selectedEmpId = req.getSelectedEmpId();
		log.debug("Inside getTargetAchivementParams():::"+selectedEmpId);
		List<TargetAchivement> resList = new ArrayList<>();
		String orgId  = req.getOrgId();
		//List<Integer> empReportingList = getImmediateReportingEmp(selectedEmpId,orgId);
		try {
			long startTime = System.currentTimeMillis();
			List<List<TargetAchivement>> allTargets = new ArrayList<>();
			Integer empId = req.getLoggedInEmpId();
			log.debug("Getting Target Data, LoggedIn emp id " + empId);

			List<Integer> selectedEmpIdList = req.getEmpSelected();
			List<Integer> selectedNodeList = req.getLevelSelected();
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(empId);
			
			log.debug("tRole getTargetAchivementParams " + tRole);
			if (null != selectedEmpIdList && !selectedEmpIdList.isEmpty()) {
				log.debug("Fetching empReportingIdList for selected employees,selectedEmpIdList" + selectedEmpIdList);
				
				
				for (Integer eId : selectedEmpIdList) {
					List<Integer> empReportingIdList = new ArrayList<>();
					empReportingIdList.add(eId);
					//empIdList = getEmployeeHiearachyData(orgId,req.getLoggedInEmpId());
					empReportingIdList.addAll(getImmediateReportingEmp(eId,orgId));
					log.debug("empReportingIdList for given selectedEmpIdList " + empReportingIdList);
					
					List<TargetAchivement> targetList = dashBoardServiceImplV2.getTargetAchivementParamsForMultipleEmp(empReportingIdList, req,
							orgId);
					log.debug("targetList::::::" + targetList);
					allTargets.add(targetList);
				}

				resList = dashBoardServiceImplV2.buildFinalTargets(allTargets);
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
					
					List<TargetAchivement> targetList = dashBoardServiceImplV2.getTargetAchivementParamsForMultipleEmp(empReportingIdList, req,
							orgId);
					allTargets.add(targetList);

				}
				resList = dashBoardServiceImplV2.buildFinalTargets(allTargets);
			} else {
				log.debug("Fetching empReportingIdList for logged in emp in else :" + req.getLoggedInEmpId());
				List<Integer> empReportingIdList = getImmediateReportingEmp(selectedEmpId,orgId);
				empReportingIdList.add(req.getLoggedInEmpId());
				
				
				log.debug("empReportingIdList for emp " + req.getLoggedInEmpId());
				log.debug("Calling getTargetAchivemetns in else" + empReportingIdList);
				
				resList = dashBoardServiceImplV2.getTargetAchivementParamsForMultipleEmp(empReportingIdList, req, orgId);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resList;
	}


	//findEmpByDeptwithActive(orgId,empId);
	public List<Integer> getImmediateReportingEmp(Integer empId, String orgId) throws DynamicFormsServiceException {
		List<String> empReportingIdList = new ArrayList<>();
		log.debug("getImmediateReportingEmp(){} , Empid "+empId+", ORGID "+orgId);
		List<Integer> empReportingIdList_1 = new ArrayList<>();
		Optional<DmsEmployee> empOpt = dmsEmployeeRepo.findEmpById(empId);
		if(empOpt.isPresent()) {
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV3(empId);
			for(String orgMapBranchId : tRole.getOrgMapBranches()) {
				Map<String, Object> hMap = ohServiceImpl.getReportingHierarchyV3(empOpt.get(),Integer.parseInt(orgMapBranchId),Integer.parseInt(orgId));
				log.debug("Emp Hierarchy "+hMap);				
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
		
		
		log.debug("empReportingIdList for org "+orgId+ " is "+empReportingIdList_1);
		List<Integer> salesempReportingIdList = new ArrayList<>();
		for(Integer id : empReportingIdList_1) {
			if(dmsEmployeeRepo.findEmpByDeptwithActive(orgId,id).isPresent()) {
				salesempReportingIdList.add(id);
			}
		}
		
		log.debug("salesempReportingIdList for org "+orgId+ " is "+salesempReportingIdList);
		return salesempReportingIdList;
	}
	
	
	

	@Override
	public OverAllTargetAchivementsModelAndSource getTargetAchivementParamsWithEmpsModelAndSource(DashBoardReqV2 req) throws DynamicFormsServiceException {
		log.info("Inside getTargetAchivementParamsWithEmps() of DashBoardServiceImplV{}");
		OverAllTargetAchivementsModelAndSource overAllTargetAchivements = new OverAllTargetAchivementsModelAndSource();
		List<EmployeeTargetAchievementModelAndView> empTargetAchievements = new ArrayList<>();
		List<EmployeeTargetAchievementModelAndView> finalEmpTargetAchievements = new ArrayList<>();
		List<TargetAchivementModelandSource> resList = new ArrayList<>();
	      List<VehicleModelRes> vehicleModelDataModelAndSource =null;
		  List<LeadSourceRes> leadSourceData = null;
		try {
			List<List<TargetAchivementModelandSource>> allTargets = new ArrayList<>();
			Integer empId = req.getLoggedInEmpId();
			log.debug("Getting Target Data, LoggedIn emp id "+empId );
			String startDate = null;
			String endDate = null;
			if (null == req.getStartDate() && null == req.getEndDate()) {
				startDate = dashBoardServiceImplV2.getFirstDayOfMonth();
				endDate = dashBoardServiceImplV2.getLastDayOfMonth();
			} else {
				startDate = req.getStartDate()+" 00:00:00";
				endDate = req.getEndDate()+" 23:59:59";
			}

			List<Integer> selectedEmpIdList = req.getEmpSelected();
			List<Integer> selectedNodeList = req.getLevelSelected();
			TargetRoleRes tRole = salesGapServiceImpl.getEmpRoleDataV2(empId);
			String orgId = tRole.getOrgId();
			System.out.println("orginization id for the details --"+orgId);
			log.debug("tRole getTargetAchivementParams "+tRole);
			if (null != selectedEmpIdList && !selectedEmpIdList.isEmpty()) {
				log.debug("Fetching empReportingIdList for selected employees,selectedEmpIdList" + selectedEmpIdList);
				for (Integer eId : selectedEmpIdList) {
					List<Integer> empReportingIdList = new ArrayList<>();
					empReportingIdList.add(eId);
					empReportingIdList.addAll(getImmediateReportingEmp(empId,orgId));
					log.debug("empReportingIdList for given selectedEmpIdList " + empReportingIdList);
					 vehicleModelDataModelAndSource = getVehicleModelDataModelAndSource(req,empReportingIdList);
					 leadSourceData = getLeadSourceData(req,empReportingIdList);
			
					
					List<TargetAchivementModelandSource> targetList = dashBoardServiceImplV2.getTargetAchivementParamsForMultipleEmpAndEmpsModelAndSourceV4(
							empReportingIdList, req, orgId, empTargetAchievements, startDate, endDate,vehicleModelDataModelAndSource,leadSourceData);
					
					allTargets.add(targetList);
				}

				resList=allTargets.get(0);
			//	resList = dashBoardServiceImplV2.buildFinalTargets(allTargets);
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
					 vehicleModelDataModelAndSource = getVehicleModelDataModelAndSource(req,empReportingIdList);
					 leadSourceData = getLeadSourceData(req,empReportingIdList);
					List<TargetAchivementModelandSource> targetList = dashBoardServiceImplV2.getTargetAchivementParamsForMultipleEmpAndEmpsModelAndSourceV4(empReportingIdList,req,orgId,empTargetAchievements,startDate,endDate,vehicleModelDataModelAndSource,leadSourceData);
					allTargets.add(targetList);
					 
				}
				//resList = dashBoardServiceImplV2.buildFinalTargets(allTargets);
				resList=allTargets.get(0);
			}
			else {
				log.debug("Fetching empReportingIdList for logged in emp in else :"+req.getLoggedInEmpId());
				List<Integer> empReportingIdList =  getImmediateReportingEmp(req.getSelectedEmpId(),orgId);
				log.debug("empReportingIdList::"+empReportingIdList);
				empReportingIdList.add(req.getSelectedEmpId());
				 vehicleModelDataModelAndSource = getVehicleModelDataModelAndSource(req,empReportingIdList);
			 leadSourceData = getLeadSourceData(req,empReportingIdList);
				resList = dashBoardServiceImplV2.getTargetAchivementParamsForMultipleEmpAndEmpsModelAndSourceV4(empReportingIdList,req,orgId,empTargetAchievements,startDate,endDate,vehicleModelDataModelAndSource,leadSourceData);
			}
			final List<TargetAchivementModelandSource> resListFinal = resList;
			final String startDt = startDate;
			final String endDt = endDate;
			
			final List<VehicleModelRes> vehicleModelDataModelAndSourceFinal =vehicleModelDataModelAndSource;
			 final List<LeadSourceRes> leadSourceDataFinal = leadSourceData;
			
			if(empTargetAchievements.size()>1) {
			List<List<EmployeeTargetAchievementModelAndView>> targetAchiPartList = dashBoardServiceImplV2.partitionListEmpTargetModelAndSource(empTargetAchievements);
			ExecutorService executor = Executors.newFixedThreadPool(targetAchiPartList.size());
			
			
			List<CompletableFuture<List<EmployeeTargetAchievementModelAndView>>> futureList  = targetAchiPartList.stream()
					.map(strings -> CompletableFuture.supplyAsync(() -> dashBoardServiceImplV2.processEmployeeTargetAchiveListModelAndSource(strings,resListFinal,startDt,endDt,vehicleModelDataModelAndSourceFinal,leadSourceDataFinal), executor))
					.collect(Collectors.toList()); 
			
			if (null != futureList) {
				CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
				Stream<List<EmployeeTargetAchievementModelAndView>> dataStream = (Stream<List<EmployeeTargetAchievementModelAndView>>) futureList.stream().map(CompletableFuture::join);
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

		}catch(Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		overAllTargetAchivements.setOverallTargetAchivements(resList);
		//overAllTargetAchivements.setEmployeeTargetAchievements(empTargetAchievements);
		overAllTargetAchivements.setEmployeeTargetAchievements(finalEmpTargetAchievements);
		return overAllTargetAchivements;
	}
	
	
	public List<VehicleModelRes> getVehicleModelDataModelAndSource(DashBoardReqV2 req,List<Integer> empReportingIdList) throws DynamicFormsServiceException {
		log.info("Inside getVehicleModelData(){}");
		List<VehicleModelRes> resList = new ArrayList<>();
		System.out.println("model"+resList);
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
			resList = getVehicleModelDataModelAndSource(empReportingIdList, req, orgId, branchId, vehicleModelList);
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
	
	
	private List<VehicleModelRes> getVehicleModelDataModelAndSource(List<Integer> empIdsUnderReporting, DashBoardReqV2 req,String orgId, String branchId,
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
//					System.out.println("Total leads in LeadReF table is ------ ::"+leadRefList.size());
//					for(LeadStageRefEntity refentity : leadRefList) {
//						System.out.println("-------------"+refentity.getStageName());
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
			return inputStartDate + " 00:00:00";
		}

	}

	public String getEndDate(String inputEndDate) {
		if (null == inputEndDate && null == inputEndDate) {
			return getLastDayOfMonth();
		} else {
			return inputEndDate + " 23:59:59";
		}

	}

	public String getFirstDayOfMonth() {
		return LocalDate.ofEpochDay(System.currentTimeMillis() / (24 * 60 * 60 * 1000)).withDayOfMonth(1).toString()
				+ " 00:00:00";
	}

	public String getLastDayOfMonth() {
		return LocalDate.ofEpochDay(System.currentTimeMillis() / (24 * 60 * 60 * 1000)).plusMonths(1).withDayOfMonth(1)
				.minusDays(1).toString() + " 23:59:59";
	}
	
	private Long getEnqLeadCount(List<DmsLead> dmsLeadList) {
		return dmsLeadList.stream().filter(x->x.getLeadStage().equalsIgnoreCase(ENQUIRY)).count();
	}
	
	private Long getDroppedCount(List<DmsLead> dmsLeadList) {
		return dmsLeadList.stream().filter(x->x.getLeadStage().equalsIgnoreCase(DROPPED)).count();
	}
	
	private Long getInvoiceCount(List<DmsLead> dmsLeadList) {
		/*return wfTaskList.stream().
				filter(x->(x.getTaskName().equalsIgnoreCase(READY_FOR_INVOICE)
						|| x.getTaskName().equalsIgnoreCase(PROCEED_TO_INVOICE)
						|| x.getTaskName().equalsIgnoreCase(INVOICE_FOLLOWUP_DSE))).count();*/
		return dmsLeadList.stream().filter(x->x.getLeadStage().equalsIgnoreCase(INVOICE)).count();
	}
	
	public Long getTestDriveCount(List<DmsWFTask> wfTaskList) {
		//TEST_DRIVE_APPROVAL
		return wfTaskList.stream().filter(x->(x.getTaskName().equalsIgnoreCase(TEST_DRIVE) && x.getTaskStatus().equalsIgnoreCase("CLOSED")) ).count();
	}
	
	private Long getHomeVisitCount(List<DmsWFTask> wfTaskList) {
		return wfTaskList.stream().filter(x->x.getTaskName().equalsIgnoreCase(HOME_VISIT) && x.getTaskStatus().equalsIgnoreCase("CLOSED")).count();
	}
	
	
	public List<LeadSourceRes> getLeadSourceData(DashBoardReqV2 req,List<Integer> empReportingIdList) throws DynamicFormsServiceException {
		log.info("Inside getLeadSourceData(){}");
		List<LeadSourceRes> resList = new ArrayList<>();
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
			String selectedBranch = req.getBranchSelectionInEvents();		
			resList = getLeadSourceDataModelAndSource(empReportingIdList,req,orgId, branchId);
		}catch(Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resList;
	}
	
	private List<LeadSourceRes> getLeadSourceDataModelAndSource(List<Integer> empIdsUnderReporting, DashBoardReqV2 req,String orgId,String branchId) {
		List<LeadSourceRes> resList = new ArrayList<>();

		List<String> empNamesList = dmsEmployeeRepo.findEmpNamesById(empIdsUnderReporting);
		log.info("empNamesList::" + empNamesList);

		String startDate = getStartDate(req.getStartDate());
		String endDate = getEndDate(req.getEndDate());
		log.info("StartDate " + startDate + ", EndDate " + endDate);

		getLeadTypes(orgId).forEach((k, v) -> {
			LeadSourceRes leadSource = new LeadSourceRes();
			log.debug("Generating data for Leadsource " + k + " and enq id " + v);
			List<DmsLead> dmsAllLeadList = dmsLeadDao.getAllEmployeeLeadsBasedOnEnquiry(orgId, empNamesList, startDate, endDate, v);

			//List<DmsLead> dmsAllLeadList = dmsLeadDao.getAllEmployeeLeasForDate(empNamesList, startDate, endDate);
			log.debug("Size of dmsAllLeadList "+dmsAllLeadList.size());
			Long enqLeadCnt = 0L;
			Long preBookCount = 0L;
			Long bookCount = 0L;
			Long invCount = 0L;
			Long preDeliveryCnt = 0L;
			Long delCnt = 0L;
			Long droppedCnt =0L;
		
			
		
			
			//List<Integer> dmsLeadList = dmsLeadDao.getLeadIdsByEmpNames(empNamesList);
			List<Integer> dmsLeadList = dmsAllLeadList.stream().map(DmsLead::getId).collect(Collectors.toList());
			log.debug("dmsLeadList::"+dmsLeadList);
				
			List<LeadStageRefEntity> leadRefList  =  leadStageRefDao.getLeadsByStageandDate(orgId,dmsLeadList,startDate,endDate);
			log.debug("leadRefList size "+leadRefList.size());
			if(null!=leadRefList && !leadRefList.isEmpty()) {
				
				log.debug("Total leads in LeadReF table is ::"+leadRefList.size());
/*				enqLeadCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("ENQUIRY")).count();
				preBookCount =leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("PREBOOKING")).count();
				bookCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("BOOKING")).count();
				invCount = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("INVOICE")).count();
				preDeliveryCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("PREDELIVERY")).count();
				delCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("DELIVERY")).count();*/
				
				enqLeadCnt = leadRefList.stream().filter(x-> x.getLeadStatus()!=null &&  x.getLeadStatus().equalsIgnoreCase(enqCompStatus)).count();
				preBookCount =leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("PREBOOKING")).count();
				bookCount = leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(bookCompStatus)).count();
				invCount = leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(invCompStatus)).count();
				preDeliveryCnt = leadRefList.stream().filter(x->x.getStageName().equalsIgnoreCase("PREDELIVERY")).count();
				delCnt = leadRefList.stream().filter(x->x.getLeadStatus()!=null && x.getLeadStatus().equalsIgnoreCase(delCompStatus)).count();
			}

			log.debug("enqLeadCnt:"+enqLeadCnt);
			log.debug("invCount:"+invCount);
			log.debug("bookCount:"+bookCount);
			//log.debug("enqLeadCnt:"+enqLeadCnt);
			
			leadSource.setE(enqLeadCnt);
			leadSource.setR(invCount);
			leadSource.setB(bookCount);
			
			if (null != dmsAllLeadList) {
				log.info("size of dmsLeadList " + dmsAllLeadList.size());
				//enqLeadCnt = getEnqLeadCount(dmsLeadList);
				droppedCnt = getDroppedCount(dmsAllLeadList);
				///leadSource.setR(getInvoiceCount(dmsLeadList));
				//log.info("enqLeadCnt: " + enqLeadCnt + " ,droppedCnt : " + droppedCnt);
			}
			
			
			leadSource.setLead(k);
			
			leadSource.setL(droppedCnt);

			List<String> leadUniversalIdList = dmsAllLeadList.stream().map(DmsLead::getCrmUniversalId)
					.collect(Collectors.toList());
			log.debug("leadUniversalIdList " + leadUniversalIdList);

			List<DmsWFTask> wfTaskList = dmsWfTaskDao.getWfTaskByAssigneeIdListByModel(empIdsUnderReporting,
					leadUniversalIdList, startDate, endDate);

			leadSource.setT(getTestDriveCount(wfTaskList));
			leadSource.setV(getHomeVisitCount(wfTaskList));
//			leadSource.setB(getBookingCount(wfTaskList));
			resList.add(leadSource);
		});
		return resList;

	}
private Map<String,Integer> getLeadTypes(String orgId){
		
		List<SourceAndId> reslist=repository.getSources(orgId);
		System.out.println("reslist"+reslist);
		Map<String,Integer> map = new LinkedHashMap<>();
		reslist.stream().forEach(res->
		{
			map.put(res.getName(), res.getId());
			
		});
		return map;
	}

		
	


}
