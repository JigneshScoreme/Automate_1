package com.automate.df.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="emp_hierarchy_tmp")
@Entity
@Data
@NoArgsConstructor
public class TmpEmpHierarchy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;


	@Column(name = "emp_id")
	private Integer empId;
	
	@Column(name = "org_id")
	private Integer orgId;
	
	@Column(name = "data")
	private String data;
	
	@Column(name = "created_on")
	private String createdAt;
}
