package com.automate.df.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.automate.df.entity.SourceAndId;

public interface SourceAndIddao extends CrudRepository<SourceAndId, Integer> {
	 @Query(value = "SELECT name,id FROM dms_source_of_enquiries WHERE org_id =?1  and status='Active'", nativeQuery = true)
	 List<SourceAndId> getSources(String orgId);
}
