package com.jsp.job_portal.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.jsp.job_portal.dto.Recruiter;
import com.jsp.job_portal.helper.AES;
import com.jsp.job_portal.helper.MyEmailSender;
import com.jsp.job_portal.repository.JobSeekerRepository;
import com.jsp.job_portal.repository.RecruiterRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class RecruiterService {

	@Autowired
	JobSeekerRepository jobSeekerRepository;
	@Autowired
	RecruiterRepository recruiterRepository;
	@Autowired
	MyEmailSender emailSender;

	public String register(Recruiter recruiter, BindingResult result, HttpSession session) {
		if (!recruiter.getPassword().equals(recruiter.getConfirmPassword()))
			result.rejectValue("confirmPassword", "error.confirmPassword", "* Password Missmatch");
		if (jobSeekerRepository.existsByEmail(recruiter.getEmail())
				|| recruiterRepository.existsByEmail(recruiter.getEmail()))
			result.rejectValue("email", "error.email", "* Email Already Exists");
		if (jobSeekerRepository.existsByMobile(recruiter.getMobile())
				|| recruiterRepository.existsByMobile(recruiter.getMobile()))
			result.rejectValue("mobile", "error.mobile", "* Mobile Already Exists");

		if (result.hasErrors())
			return "recruiter-register.html";
		else {
			recruiter.setOtp(new Random().nextInt(1000, 10000));
			recruiter.setVerified(false);
			recruiterRepository.save(recruiter);
			System.err.println(recruiter.getOtp());
			emailSender.sendOtp(recruiter);
			session.setAttribute("success", "Otp Sent Success");
			return "redirect:/recruiter/otp/" + recruiter.getId();
		}

	}

	public String otp(int id, int otp, HttpSession session) {
		Recruiter recruiter=recruiterRepository.findById(id).orElseThrow();
		if(recruiter.getOtp()==otp) {
			recruiter.setVerified(true);
			recruiter.setPassword(AES.encrypt(recruiter.getPassword()));
			recruiterRepository.save(recruiter);
			session.setAttribute("success", "Account Created Success");
			return "redirect:/";
		}else {
			session.setAttribute("error", "Otp Missmatch");
			return "redirect:/recruiter/otp/" + recruiter.getId();
		}
	}

	public String resendOtp(int id, HttpSession session) {
		Recruiter recruiter=recruiterRepository.findById(id).orElseThrow();
		recruiter.setOtp(new Random().nextInt(1000, 10000));
		recruiter.setVerified(false);
		recruiterRepository.save(recruiter);
		System.err.println(recruiter.getOtp());
		emailSender.sendOtp(recruiter);
		session.setAttribute("success", "Otp Re-Sent Success");
		return "redirect:/recruiter/otp/" + recruiter.getId();
	}

}
