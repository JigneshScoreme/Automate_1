package com.automate.df.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="dms_emp_target_ranking_org")
@Entity
@Data
@NoArgsConstructor
public class DmsEmployeeTargetRankingOrg {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	int orgId;
	int empId;
	String data;
	String createdDate;

}
