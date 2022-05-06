package com.automate.df.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="dms_target_param_schedular")
@Entity
@Data
@NoArgsConstructor
public class DmsTargetParamSchedular {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	String orgId;
	String empId;
	String data;
	String createdDate;
	
}
