package com.automate.df.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.automate.df.entity.sales.lead.DmsDelivery;



public interface DmsDeliveryDao extends JpaRepository<DmsDelivery, Integer>{
	
	
	@Query(value="select  * from dms_delivery where lead_id = :id",nativeQuery = true)
	public List<DmsDelivery> getDeliveriesWithLeadId(@Param(value="id") int leadId);

	@Query(value="SELECT insurance_taken FROM dms_delivery where lead_id in (:leadList) and insurance_taken=:type",nativeQuery = true)
	public List<String> getInsuranceTakenLeads(@Param(value="leadList") List<Integer> collect, @Param(value="type") String insuranceType);

	@Query(value="SELECT warranty_taken FROM dms_delivery where lead_id in (:leadList) and warranty_taken='YES'",nativeQuery = true)
	public List<String> getWarrantyTakenLeads(@Param(value="leadList")  List<Integer> leadIdList);

	@Query(value="SELECT sum(amount) FROM dms_accessories where lead_id in (:leadList) and dms_accessories_type='MRP'",nativeQuery = true)
	public Long getAccessoriesAmt(@Param(value="leadList")  List<Integer> leadIdList);


}
