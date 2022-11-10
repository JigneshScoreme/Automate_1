package com.automate.df.controller;

import java.util.List;

public class DashBoardReqImmediateHierarchyV2 {
	String orgId;
	List<Integer> loggedInEmpId;
	List<Integer> empSelected;
	List<Integer> levelSelected;
	
	/*
	List<String> employeeIds;
	List<String> branchIds;
	List<String> vpIds;
	List<String> generalMgrId;
	List<String> managerIds;
	List<String> teamLeadIds;
	*/
	
	
	String startDate;
	String endDate;
	
	String branchSelectionInEvents;
	int pageNo;
	int size;
	
	List<Integer> selectedEmpId;
}
