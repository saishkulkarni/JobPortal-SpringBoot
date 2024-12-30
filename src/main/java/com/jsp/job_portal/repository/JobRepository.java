package com.jsp.job_portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.job_portal.dto.Job;
import com.jsp.job_portal.dto.Recruiter;

public interface JobRepository extends JpaRepository<Job, Integer> {

	List<Job> findByRecruiter(Recruiter recruiter);

	List<Job> findByApprovedTrue();

	List<Job> findByRoleLikeAndApprovedTrue(String string);

	List<Job> findBySkillsLikeAndApprovedTrue(String string);

}
