package com.automate.df.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.automate.df.entity.sales.lead.DmsFinanceDetail;

public interface DmsFinanceDao extends JpaRepository<DmsFinanceDetail, Integer> {

	
	@Query(value="SELECT finance_type FROM dms_finance_details where lead_id in(:leadIdList) and finance_type='In House';",nativeQuery = true)
	public List<String> getFinanceTypeLeads(@Param(value="leadIdList") List<Integer> list);

}
