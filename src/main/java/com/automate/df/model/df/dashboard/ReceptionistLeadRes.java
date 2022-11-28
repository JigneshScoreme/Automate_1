package com.automate.df.model.df.dashboard;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReceptionistLeadRes {
	
	private String  name;
	
	private String model;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createddatetime;

	private String firstName;

	private String lastName;

	private String leadStage;

	private String salesConsultant;

	private String phone;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date droppedTime;

	public ReceptionistLeadRes(String name, String model, Date createddatetime, String firstName, String lastName,
			String leadStage, String salesConsultant, String phone) {
		super();
		this.name = name;
		this.model = model;
		this.createddatetime = createddatetime;
		this.firstName = firstName;
		this.lastName = lastName;
		this.leadStage = leadStage;
		this.salesConsultant = salesConsultant;
		this.phone = phone;
	}

	public ReceptionistLeadRes(String name, String model, Date createddatetime, String firstName, String lastName,
			String leadStage, String salesConsultant, String phone, Date droppedTime) {
		super();
		this.name = name;
		this.model = model;
		this.createddatetime = createddatetime;
		this.firstName = firstName;
		this.lastName = lastName;
		this.leadStage = leadStage;
		this.salesConsultant = salesConsultant;
		this.phone = phone;
		this.droppedTime = droppedTime;
	}

	

	
	
}
