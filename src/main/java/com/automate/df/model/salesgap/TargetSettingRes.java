package com.automate.df.model.salesgap;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@NoArgsConstructor
@ToString
public class TargetSettingRes {

	Integer id;
	String branch;
	String branchName;
	
	//String location;
	String locationName;
	String department;
	String departmentName;
	String designation;
	String designationName;
	String experience;
	String salrayRange;
	String retailTarget;
	String enquiry;
	String testDrive;
	String homeVisit;
	String videoConference;
	String booking;
	String exchange;
	String finance;
	String insurance;
	String exWarranty;
	String accessories;
	String events;
	String invoice;
	String other;
	
	
	String startDate;
	String endDate;
	
	
	//DSE
	String empName;
	String employeeId;
	
	
	String teamLeadId;
	String teamLead;
	
	//Branch manager
	String managerId;
	String manager;
	
	//General manager
	String branchManagerId;
	String branchmanger;
	
	String generalManagerId;
	String generalManager;
	String userRole;
	
	String targetName;
	String targetType;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getDesignationName() {
		return designationName;
	}
	public void setDesignationName(String designationName) {
		this.designationName = designationName;
	}
	public String getExperience() {
		return experience;
	}
	public void setExperience(String experience) {
		this.experience = experience;
	}
	public String getSalrayRange() {
		return salrayRange;
	}
	public void setSalrayRange(String salrayRange) {
		this.salrayRange = salrayRange;
	}
	public String getRetailTarget() {
		return retailTarget;
	}
	public void setRetailTarget(String retailTarget) {
		this.retailTarget = retailTarget;
	}
	public String getEnquiry() {
		return enquiry;
	}
	public void setEnquiry(String enquiry) {
		this.enquiry = enquiry;
	}
	public String getTestDrive() {
		return testDrive;
	}
	public void setTestDrive(String testDrive) {
		this.testDrive = testDrive;
	}
	public String getHomeVisit() {
		return homeVisit;
	}
	public void setHomeVisit(String homeVisit) {
		this.homeVisit = homeVisit;
	}
	public String getVideoConference() {
		return videoConference;
	}
	public void setVideoConference(String videoConference) {
		this.videoConference = videoConference;
	}
	public String getBooking() {
		return booking;
	}
	public void setBooking(String booking) {
		this.booking = booking;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public String getFinance() {
		return finance;
	}
	public void setFinance(String finance) {
		this.finance = finance;
	}
	public String getInsurance() {
		return insurance;
	}
	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}
	public String getExWarranty() {
		return exWarranty;
	}
	public void setExWarranty(String exWarranty) {
		this.exWarranty = exWarranty;
	}
	public String getAccessories() {
		return accessories;
	}
	public void setAccessories(String accessories) {
		this.accessories = accessories;
	}
	public String getEvents() {
		return events;
	}
	public void setEvents(String events) {
		this.events = events;
	}
	public String getInvoice() {
		return invoice;
	}
	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getTeamLeadId() {
		return teamLeadId;
	}
	public void setTeamLeadId(String teamLeadId) {
		this.teamLeadId = teamLeadId;
	}
	public String getTeamLead() {
		return teamLead;
	}
	public void setTeamLead(String teamLead) {
		this.teamLead = teamLead;
	}
	public String getManagerId() {
		return managerId;
	}
	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getBranchManagerId() {
		return branchManagerId;
	}
	public void setBranchManagerId(String branchManagerId) {
		this.branchManagerId = branchManagerId;
	}
	public String getBranchmanger() {
		return branchmanger;
	}
	public void setBranchmanger(String branchmanger) {
		this.branchmanger = branchmanger;
	}
	public String getGeneralManagerId() {
		return generalManagerId;
	}
	public void setGeneralManagerId(String generalManagerId) {
		this.generalManagerId = generalManagerId;
	}
	public String getGeneralManager() {
		return generalManager;
	}
	public void setGeneralManager(String generalManager) {
		this.generalManager = generalManager;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	public String getTargetName() {
		return targetName;
	}
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	public String getTargetType() {
		return targetType;
	}
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	
	
	
	
}
