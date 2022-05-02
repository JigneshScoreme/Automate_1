package com.automate.df.model.oh;

import java.util.List;
import java.util.Set;

import com.automate.df.model.Task;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TodaysTaskRes {

	String empName;
	Integer empId;
	Set<String> tasksAvailable;
	Integer taskAvailableCnt;
	List<EmpTask> tasksList;
	
}
