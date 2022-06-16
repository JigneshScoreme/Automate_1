package com.automate.df.dao.dashboard;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.automate.df.entity.DmsEmployeeTargetRankingOrg;
import com.google.common.base.Optional;
@Repository
public interface DmsEmpTargetRankingOrgDao  extends CrudRepository<DmsEmployeeTargetRankingOrg, Integer> {
	  @Query(value = "SELECT * FROM dms_emp_target_ranking_org WHERE emp_id=?1 and org_id=?2", nativeQuery = true)
			Optional<DmsEmployeeTargetRankingOrg> findByEmpId(Integer empId,Integer orgId);

}
