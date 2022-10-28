package com.automate.df.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.automate.df.entity.sales.allocation.DmsEmployeeAllocation;

public interface EmployeeAllocation extends JpaRepository<DmsEmployeeAllocation, Integer>,
JpaSpecificationExecutor<DmsEmployeeAllocation> {

@Query(value = "select * from dms_employee_allocation where lead_id=?1", nativeQuery = true)
List<DmsEmployeeAllocation> findByLeadId(int id);

List<DmsEmployeeAllocation> findByEmployeeId(int empId);

List<DmsEmployeeAllocation> findByEmployeeIdIn(List<Integer> empId);

}
