package com.jsp.job_portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.job_portal.dto.Recruiter;

public interface RecruiterRepository extends JpaRepository<Recruiter, Integer> {

	boolean existsByEmail(String email);

	boolean existsByMobile(Long mobile);

	Recruiter findByMobile(Long mobile);

	Recruiter findByEmail(String email);

}
