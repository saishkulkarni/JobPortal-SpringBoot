package com.jsp.job_portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.job_portal.dto.Job;
import com.jsp.job_portal.dto.JobApplication;

public interface ApplicationRepository extends JpaRepository<JobApplication,Integer>{

    List<JobApplication> findByJob(Job job);

}
