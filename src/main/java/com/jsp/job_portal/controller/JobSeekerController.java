package com.jsp.job_portal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jsp.job_portal.dto.Job;
import com.jsp.job_portal.dto.JobSeeker;
import com.jsp.job_portal.repository.JobRepository;
import com.jsp.job_portal.service.JobSeekerService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/jobseeker")
public class JobSeekerController {
	@Autowired
	JobSeekerService seekerService;
	
	@Autowired
	JobRepository jobRepository;

	@GetMapping("/register")
	public String loadRegister(JobSeeker jobSeeker, ModelMap map) {
		return seekerService.register(jobSeeker, map);
	}

	@PostMapping("/register")
	public String register(@Valid JobSeeker jobSeeker, BindingResult result, HttpSession session) {
		return seekerService.register(jobSeeker, result, session);
	}

	@GetMapping("/otp/{id}")
	public String otp(@PathVariable("id") Integer id, ModelMap map) {
		map.put("id", id);
		return "seeker-otp.html";
	}

	@PostMapping("/otp")
	public String otp(@RequestParam("otp") int otp, @RequestParam("id") int id, HttpSession session) {
		return seekerService.otp(otp, id, session);
	}

	@GetMapping("/resend-otp/{id}")
	public String resendOtp(@PathVariable("id") Integer id, HttpSession session) {
		return seekerService.resendOtp(id, session);
	}

	@GetMapping("/home")
	public String loadHome(HttpSession session) {
		if (session.getAttribute("jobSeeker") != null) {
			return "jobseeker-home.html";
		} else {
			session.setAttribute("error", "Invalid Session, Login Again");
			return "redirect:/login";
		}

	}
	
	@GetMapping("/view-jobs")
	public String viewAllJobs(HttpSession session,ModelMap map) {
		if (session.getAttribute("jobSeeker") != null) {
			List<Job> jobs=jobRepository.findByApprovedTrue();
			if(jobs.isEmpty()) {
				session.setAttribute("error", "No Jobs Present Yet");
				return "redirect:/jobseeker/home";
			}else {
				map.put("jobs", jobs);
				return "jobseeker-jobs.html";
			}
			
		} else {
			session.setAttribute("error", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}
	
	
}
