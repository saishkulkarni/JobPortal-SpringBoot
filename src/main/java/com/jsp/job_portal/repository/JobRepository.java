package com.jsp.job_portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.job_portal.dto.Job;

public interface JobRepository extends JpaRepository<Job, Integer> {

}
