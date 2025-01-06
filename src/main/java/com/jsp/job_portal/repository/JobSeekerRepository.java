package com.jsp.job_portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.job_portal.dto.JobApplication;
import com.jsp.job_portal.dto.JobSeeker;

public interface JobSeekerRepository extends JpaRepository<JobSeeker, Integer> {

	boolean existsByEmail(String email);

	boolean existsByMobile(Long mobile);

	JobSeeker findByMobile(Long mobile);

	JobSeeker findByEmail(String email);

    List<JobSeeker> findByJobApplicationsIn(List<JobApplication> applicaitons);

}
