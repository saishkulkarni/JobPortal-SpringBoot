package com.jsp.job_portal.controller;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jsp.job_portal.dto.Job;
import com.jsp.job_portal.dto.JobSeeker;
import com.jsp.job_portal.dto.Recruiter;
import com.jsp.job_portal.helper.AES;
import com.jsp.job_portal.repository.JobRepository;
import com.jsp.job_portal.repository.JobSeekerRepository;
import com.jsp.job_portal.repository.RecruiterRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class GeneralController {

	@Value("${portal.admin.email}")
	private String adminEmail;
	@Value("${portal.admin.password}")
	private String adminPassword;

	@Autowired
	JobRepository jobRepository;

	@Autowired
	JobSeekerRepository jobSeekerRepository;

	@Autowired
	RecruiterRepository recruiterRepository;

	@GetMapping("/")
	public String loadHome() {
		return "home.html";
	}

	@GetMapping("/about-us")
	public String loadAbout() {
		return "about-us.html";
	}

	@GetMapping("/privacy-policy")
	public String services() {
		return "privacy-policy.html";
	}

	@GetMapping("/terms")
	public String loadTerms() {
		return "terms.html";
	}

	@GetMapping("/login")
	public String loadLogin() {
		return "login.html";
	}

	@PostMapping("/login")
	public String login(@RequestParam("emph") String emph, @RequestParam("password") String password,
			HttpSession session) {

		if (emph.equals(adminEmail) && password.equals(adminPassword)) {
			session.setAttribute("success", "Login Success as Admin");
			session.setAttribute("admin", "admin");
			return "redirect:/admin/home";
		} else {
			Long mobile = null;
			String email = null;

			try {
				mobile = Long.parseLong(emph);
				Recruiter recruiter = recruiterRepository.findByMobile(mobile);
				JobSeeker jobSeeker = jobSeekerRepository.findByMobile(mobile);
				if (recruiter == null && jobSeeker == null) {
					session.setAttribute("error", "Invalid Mobile Number");
					return "redirect:/login";
				} else {
					if (recruiter != null) {
						if (AES.decrypt(recruiter.getPassword()).equals(password)) {
							session.setAttribute("success", "Login Success as Recruiter");
							session.setAttribute("recruiter", recruiter);
							return "redirect:/recruiter/home";
						} else {
							session.setAttribute("error", "Invalid Password");
							return "redirect:/login";
						}
					} else {
						if (AES.decrypt(jobSeeker.getPassword()).equals(password)) {
							session.setAttribute("success", "Login Success as JobSeeker");
							session.setAttribute("jobSeeker", jobSeeker);
							return "redirect:/jobseeker/home";
						} else {
							session.setAttribute("error", "Invalid Password");
							return "redirect:/login";
						}
					}
				}

			} catch (NumberFormatException e) {
				email = emph;
				Recruiter recruiter = recruiterRepository.findByEmail(email);
				JobSeeker jobSeeker = jobSeekerRepository.findByEmail(email);
				if (recruiter == null && jobSeeker == null) {
					session.setAttribute("error", "Invalid Email");
					return "redirect:/login";
				} else {
					if (recruiter != null) {
						if (AES.decrypt(recruiter.getPassword()).equals(password)) {
							session.setAttribute("success", "Login Success as Recruiter");
							session.setAttribute("recruiter", recruiter);
							return "redirect:/recruiter/home";
						} else {
							session.setAttribute("error", "Invalid Password");
							return "redirect:/login";
						}
					} else {
						if (AES.decrypt(jobSeeker.getPassword()).equals(password)) {
							session.setAttribute("success", "Login Success as JobSeeker");
							session.setAttribute("jobSeeker", jobSeeker);
							return "redirect:/jobseeker/home";
						} else {
							session.setAttribute("error", "Invalid Password");
							return "redirect:/login";
						}
					}
				}

			}
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("jobSeeker");
		session.removeAttribute("recruiter");
		session.removeAttribute("admin");
		session.setAttribute("success", "Logged Out Successfully");
		return "redirect:/";
	}

	@GetMapping("/admin/home")
	public String adminHome(HttpSession session) {
		if (session.getAttribute("admin") != null)
			return "admin-home.html";
		else {
			session.setAttribute("error", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@GetMapping("/admin/manage-jobs")
	public String manageJobs(HttpSession session, ModelMap map) {
		if (session.getAttribute("admin") != null) {
			List<Job> jobs = jobRepository.findAll();
			if (jobs.isEmpty()) {
				session.setAttribute("error", "No Jobs Added Yet");
				return "redirect:/admin/home";
			} else {
				map.put("jobs", jobs);
				return "admin-jobs.html";
			}
		} else {
			session.setAttribute("error", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@GetMapping("/admin/change/{id}")
	public String changeStatus(@PathVariable("id") int id, HttpSession session) {
		if (session.getAttribute("admin") != null) {
			Job job = jobRepository.findById(id).orElseThrow();
			if (job.isApproved())
				job.setApproved(false);
			else
				job.setApproved(true);

			jobRepository.save(job);
			session.setAttribute("success", "Status Changed Success");
			return "redirect:/admin/manage-jobs";
		} else {
			session.setAttribute("error", "Invalid Session, Login Again");
			return "redirect:/login";
		}
	}

	@PostMapping("/search-jobs")
	public String viewJobs(@RequestParam("query") String query, HttpSession session, ModelMap map) {
		List<Job> roleJobs = jobRepository.findByRoleLikeAndApprovedTrue("%" + query + "%");
		List<Job> skillJobs = jobRepository.findBySkillsLikeAndApprovedTrue("%" + query + "%");
		HashSet<Job> jobs = new HashSet<Job>();
		jobs.addAll(skillJobs);
		jobs.addAll(roleJobs);

		if (jobs.isEmpty()) {
			session.setAttribute("error", "No Jobs Added Yet");
			return "redirect:/jobseeker/home";
		} else {
			map.put("jobs", jobs);
			return "search-jobs-result.html";
		}

	}
}
