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
import org.springframework.web.multipart.MultipartFile;

import com.jsp.job_portal.dto.Education;
import com.jsp.job_portal.dto.Experience;
import com.jsp.job_portal.dto.Job;
import com.jsp.job_portal.dto.JobApplication;
import com.jsp.job_portal.dto.JobSeeker;
import com.jsp.job_portal.helper.CloudinaryHelper;
import com.jsp.job_portal.repository.JobRepository;
import com.jsp.job_portal.repository.JobSeekerRepository;
import com.jsp.job_portal.service.JobSeekerService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/jobseeker")
public class JobSeekerController {
	@Autowired
	JobSeekerService seekerService;

	@Autowired
	JobSeekerRepository jobSeekerRepository;

	@Autowired
	CloudinaryHelper cloudinaryHelper;

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
	public String viewAllJobs(HttpSession session, ModelMap map) {
		if (session.getAttribute("jobSeeker") != null) {
			List<Job> jobs = jobRepository.findByApprovedTrue();
			if (jobs.isEmpty()) {
				session.setAttribute("error", "No Jobs Present Yet");
				return "redirect:/jobseeker/home";
			} else {
				map.put("jobs", jobs);
				return "jobseeker-jobs.html";
			}

		} else {
			session.setAttribute("error", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@GetMapping("/apply/{id}")
	public String apply(@PathVariable("id") Integer id, HttpSession session) {
		return seekerService.apply(id, session);
	}

	@GetMapping("/complete-profile")
	public String completeProfile(HttpSession session) {
		if (session.getAttribute("jobSeeker") != null) {
			return "complete-profile.html";
		} else {
			session.setAttribute("error", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@PostMapping("/complete-profile")
	public String completeProfile(@RequestParam MultipartFile resume, @RequestParam MultipartFile profilePic,
			Experience experience, Education education, HttpSession session) {
		if (session.getAttribute("jobSeeker") != null) {
			JobSeeker jobSeeker = (JobSeeker) session.getAttribute("jobSeeker");
			jobSeeker.setEducation(education);
			jobSeeker.setExperience(experience);
			jobSeeker.setResumeUrl(cloudinaryHelper.savePdf(resume));
			jobSeeker.setProfilePicUrl(cloudinaryHelper.saveImage(profilePic));
			jobSeeker.setCompleted(true);
			jobSeekerRepository.save(jobSeeker);
			session.setAttribute("success", "Profile Completed, You can Apply for Jobs Now");
			return "redirect:/jobseeker/home";
		} else {
			session.setAttribute("error", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@GetMapping("/my-applications")
	public String viewApplications(HttpSession session, ModelMap map) {
		if (session.getAttribute("jobSeeker") != null) {
			JobSeeker jobSeeker = (JobSeeker) session.getAttribute("jobSeeker");
			List<JobApplication> applications=jobSeeker.getJobApplications();
			if (applications.isEmpty()) {
				session.setAttribute("error", "No Applications Found");
				return "redirect:/jobseeker/home";
			} else {
				map.put("applications", applications);
				return "jobseeker-applications.html";
			}
		} else {
			session.setAttribute("error", "Invalid Session, Login Again");
			return "redirect:/login";
		}

	}
}
