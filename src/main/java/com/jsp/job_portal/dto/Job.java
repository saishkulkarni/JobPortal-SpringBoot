package com.jsp.job_portal.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Data
@Entity
public class Job {
	@Id
	@GeneratedValue(generator = "job_id")
	@SequenceGenerator(name = "job_id", initialValue = 100121001, allocationSize = 1)
	private int id;
	private String role;
	private String description;
	private String skills;
	private String location;
	private int ctc;
	private int experience;
	private boolean approved;

	@ManyToOne
	private Recruiter recruiter;

}
