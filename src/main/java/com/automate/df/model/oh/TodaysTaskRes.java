package com.automate.df.model.oh;

import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TodaysTaskRes {

	String empName;
	Integer empId;
	Set<String> tasksAvilable;
	Integer taskCnt;
	List<MyTask> myTaskList;
	
}
