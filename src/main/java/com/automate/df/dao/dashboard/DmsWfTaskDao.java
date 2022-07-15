package com.automate.df.dao.dashboard;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.automate.df.entity.dashboard.DmsWFTask;


/**
 * 
 * @author sruja
 *
 */
public interface DmsWfTaskDao extends JpaRepository<DmsWFTask, Integer> {
	
	@Query(value = "SELECT * FROM dms_workflow_task where assignee_id=:assigneeId and \r\n"
			+ " task_created_time >= :startTime and task_created_time <= :endTime", nativeQuery = true)
	List<DmsWFTask> getWfTaskByAssigneeId(
			@Param(value = "assigneeId") String assigneeId,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime);
	
	
	@Query(value = "SELECT * FROM dms_workflow_task where assignee_id in (:assigneeIdList) and \r\n"
			+ " task_created_time >= :startTime and task_created_time <= :endTime and task_status='CLOSED'", nativeQuery = true)
	List<DmsWFTask> getWfTaskByAssigneeIdList(
			@Param(value = "assigneeIdList") List<Integer> assigneeIdList,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime);
	
	
	@Query(value = "SELECT * FROM dms_workflow_task where assignee_id in (:assigneeIdList) and \r\n"
			+ " task_created_time >= :startTime and task_created_time <= :endTime and universal_id in (:universalIdList)", nativeQuery = true)
	List<DmsWFTask> getWfTaskByAssigneeIdListByModel(
			@Param(value = "assigneeIdList") List<Integer> assigneeIdList,
			@Param(value = "universalIdList") List<String> universalIdList,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime);
		
	
	

	@Query(value = "SELECT * FROM dms_workflow_task where assignee_id =:assigneeId \r\n"
			+ "	and task_status != 'CLOSED' \r\n"
			+ "	and task_status != 'RESCHEDULED' \r\n"
			+ "	and task_updated_time>= :startTime  and task_updated_time <= :endTime order by "+ " task_updated_time desc", nativeQuery = true)
	List<DmsWFTask> getTodaysUpcomingTasks(
			@Param(value = "assigneeId") Integer assigneeId,
			//@Param(value = "universalIdList") List<String> universalIdList,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime);
	
	@Query(value = "SELECT * FROM dms_workflow_task where assignee_id =:assigneeId \r\n"
			+ "	and task_status != 'CLOSED' \r\n"
			+ "	and task_created_time>= :startTime  order by "+ " task_created_time desc", nativeQuery = true)
	List<DmsWFTask> getTodaysUpcomingTasksV2(
			@Param(value = "assigneeId") Integer assigneeId,
			//@Param(value = "universalIdList") List<String> universalIdList,
			@Param(value = "startTime") String startTime);

	@Query(value = "SELECT * FROM dms_workflow_task where assignee_id =:assigneeId \r\n"
			+ "	and task_status != 'CLOSED' \r\n"
			+ "	and task_created_time<= :startTime", nativeQuery = true)
	List<DmsWFTask> getTodaysUpcomingTasksV3(
			@Param(value = "assigneeId") Integer assigneeId,
			//@Param(value = "universalIdList") List<String> universalIdList,
			@Param(value = "startTime") String startTime);
	
	@Query(value = "SELECT * FROM dms_workflow_task where dms_workflow_task.assignee_id=?1 and  DATE" +
            "(`task_updated_time`) != CURDATE() and dms_workflow_task.task_status != 'CLOSED' and dms_workflow_task.task_status != 'RESCHEDULED'  and dms_workflow_task.task_name NOT IN ('Proceed to Pre Booking','Proceed to Booking','Proceed to Invoice','Proceed to Predelivery','Proceed to Delivery') order by " +
            "task_created_time desc", nativeQuery = true)
	  List<DmsWFTask> findAllByPendingStatus(String empId);
	
	
	@Query(value = "SELECT * FROM dms_workflow_task where assignee_id =:assigneeId \r\n"
			+ "	and task_status != 'CLOSED' \r\n"
			+ "	and task_updated_time>= :startTime  and task_updated_time<=:endTime and task_name NOT IN ('Proceed to Pre Booking','Proceed to Booking','Proceed to Invoice','Proceed to Predelivery','Proceed to Delivery')", nativeQuery = true)
	
	List<DmsWFTask> findAllByPendingData(@Param(value = "assigneeId") Integer assigneeId,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime);
	
	

	   @Query(value = "SELECT * FROM dms_workflow_task where dms_workflow_task.assignee_id=?1 and dms_workflow_task.task_status != 'ASSIGNED'and dms_workflow_task.task_status != 'CLOSED'and dms_workflow_task.task_status != 'IN_PROGRESS' and dms_workflow_task.task_status != 'CANCELLED'and dms_workflow_task.task_status != 'SYSTEM_ERROR' and dms_workflow_task.task_status != 'SENT_FOR_APPROVAL' and dms_workflow_task.task_status != 'APPROVED' order by "+ " task_created_time desc", nativeQuery = true)
	    List<DmsWFTask> findAllByRescheduledStatusWithNoDate(String empId);
	
	  @Query(value = "SELECT * FROM dms_workflow_task where dms_workflow_task.assignee_id=:assigneeId and task_updated_time>= :startTime  and task_updated_time<=:endTime"
	  		+ " and dms_workflow_task.task_status != 'ASSIGNED'and dms_workflow_task.task_status != 'CLOSED'and dms_workflow_task.task_status != 'IN_PROGRESS' and dms_workflow_task.task_status != 'CANCELLED'and dms_workflow_task.task_status != 'SYSTEM_ERROR' and dms_workflow_task.task_status != 'SENT_FOR_APPROVAL' and dms_workflow_task.task_status != 'APPROVED' order by "+ " task_created_time desc", nativeQuery = true)
	    List<DmsWFTask> findAllByRescheduledStatus(@Param(value = "assigneeId") Integer assigneeId,
				@Param(value = "startTime") String startTime,
				@Param(value = "endTime") String endTime);
	  
	  
	  
	@Query(value = "SELECT * FROM dms_workflow_task  where universal_id=:universalId and task_name=:taskName", nativeQuery = true)
	List<DmsWFTask> getWfTaskByUniversalIdandTask(@Param(value = "universalId") String crmUniversalId, 
			@Param(value = "taskName") String hOME_VISIT);
	
	@Query(value = "SELECT * FROM dms_workflow_task where task_name=:taskName and \r\n"
			+ " task_created_time >= :startTime and task_created_time <= :endTime", nativeQuery = true)
	List<DmsWFTask> getWfTaskByTaskName(
			@Param(value = "taskName") String taskName,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime);

}
