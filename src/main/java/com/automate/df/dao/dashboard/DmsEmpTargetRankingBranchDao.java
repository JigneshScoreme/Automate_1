package com.automate.df.dao.dashboard;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.automate.df.entity.DmsEmployeeTargetRankingBranch;
import com.google.common.base.Optional;
@Repository
public interface DmsEmpTargetRankingBranchDao  extends CrudRepository<DmsEmployeeTargetRankingBranch, Integer> {
	  @Query(value = "SELECT * FROM dms_emp_target_ranking_branch WHERE emp_id=?1 and org_id=?2 and branch_id=?3", nativeQuery = true)
			Optional<DmsEmployeeTargetRankingBranch> findByEmpIdAndBranchId(Integer empId,Integer orgId,Integer branchId);

}
