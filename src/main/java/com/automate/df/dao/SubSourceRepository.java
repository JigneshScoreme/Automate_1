package com.automate.df.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.automate.df.entity.SubSource;

public interface SubSourceRepository extends CrudRepository<SubSource, Integer> {
	 @Query(value = "SELECT * FROM sub_source WHERE org_id =?1 and status='Active'", nativeQuery = true)
	    List<SubSource> getAllSubsource(String orgId);
}
