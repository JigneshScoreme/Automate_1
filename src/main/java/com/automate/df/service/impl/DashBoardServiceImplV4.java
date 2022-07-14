package com.automate.df.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
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
import com.automate.df.dao.TmpEmpHierarchyDao;
import com.automate.df.dao.dashboard.DashBoardV3Dao;
import com.automate.df.dao.dashboard.DmsEmpTargetRankingBranchDao;
import com.automate.df.dao.dashboard.DmsEmpTargetRankingOrgDao;
import com.automate.df.dao.dashboard.DmsLeadDao;
import com.automate.df.dao.dashboard.DmsLeadDropDao;
import com.automate.df.dao.dashboard.DmsTargetParamAllEmployeeSchedularDao;
import com.automate.df.dao.dashboard.DmsTargetParamEmployeeSchedularDao;
import com.automate.df.dao.dashboard.DmsWfTaskDao;
import com.automate.df.dao.salesgap.DmsEmployeeRepo;
import com.automate.df.dao.salesgap.TargetSettingRepo;
import com.automate.df.dao.salesgap.TargetUserRepo;
import com.automate.df.entity.salesgap.DmsEmployee;
import com.automate.df.exception.DynamicFormsServiceException;
import com.automate.df.model.df.dashboard.DashBoardReqV2;
import com.automate.df.model.df.dashboard.EmployeeTargetAchievement;
import com.automate.df.model.df.dashboard.OverAllTargetAchivements;
import com.automate.df.model.df.dashboard.TargetAchivement;
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


}
