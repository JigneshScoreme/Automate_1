package com.automate.df.model.oh;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyTask {
	
	String taskName;
	String taskStatus;
	String customerName;
	String createdOn;
	String phoneNo;

}
