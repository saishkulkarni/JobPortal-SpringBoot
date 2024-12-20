package com.jsp.job_portal.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.jsp.job_portal.dto.JobSeeker;
import com.jsp.job_portal.helper.AES;
import com.jsp.job_portal.helper.MyEmailSender;
import com.jsp.job_portal.repository.JobSeekerRepository;
import com.jsp.job_portal.repository.RecruiterRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class JobSeekerService {

	@Autowired
	JobSeekerRepository seekerRepository;

	@Autowired
	RecruiterRepository recruiterRepository;
	
	@Autowired
	MyEmailSender emailSender;

	public String register(JobSeeker jobSeeker, ModelMap map) {
		map.put("jobSeeker", jobSeeker);
		return "jobseeker-register.html";
	}

	public String register(JobSeeker jobSeeker, BindingResult result, HttpSession session) {
		if (!jobSeeker.getPassword().equals(jobSeeker.getConfirmPassword()))
			result.rejectValue("confirmPassword", "error.confirmPassword",
					"* Password and Comfirm Password Should be Matching");
		if (seekerRepository.existsByEmail(jobSeeker.getEmail())
				|| recruiterRepository.existsByEmail(jobSeeker.getEmail()))
			result.rejectValue("email", "error.email", "* Email Should be Unique");
		if (seekerRepository.existsByMobile(jobSeeker.getMobile())
				|| recruiterRepository.existsByMobile(jobSeeker.getMobile()))
			result.rejectValue("mobile", "error.mobile", "* Mobile Number Should be Unique");

		if (result.hasErrors())
			return "jobseeker-register.html";
		else {
			jobSeeker.setOtp(new Random().nextInt(1000, 10000));
			jobSeeker.setVerified(false);
			jobSeeker.setPassword(AES.encrypt(jobSeeker.getPassword()));
			seekerRepository.save(jobSeeker);
			System.err.println(jobSeeker.getOtp());
			//emailSender.sendOtp(jobSeeker);
			session.setAttribute("success", "Otp Sent Success!!!");
			return "redirect:/jobseeker/otp/" + jobSeeker.getId();
		}
	}

	public String otp(int otp, int id, HttpSession session) {
		JobSeeker jobSeeker = seekerRepository.findById(id).orElseThrow();
		if (jobSeeker.getOtp() == otp) {
			jobSeeker.setVerified(true);
			seekerRepository.save(jobSeeker);
			session.setAttribute("success", "Account Created Successfully");
			return "redirect:/";
		} else {
			session.setAttribute("error", "OTP Missmatch, Try Again");
			return "redirect:/jobseeker/otp/" + jobSeeker.getId();
		}
	}

	public String resendOtp(Integer id, HttpSession session) {
		JobSeeker jobSeeker = seekerRepository.findById(id).orElseThrow();
		jobSeeker.setOtp(new Random().nextInt(1000, 10000));
		jobSeeker.setVerified(false);
		seekerRepository.save(jobSeeker);
		System.err.println(jobSeeker.getOtp());
		//emailSender.sendOtp(jobSeeker);
		session.setAttribute("success", "OTP Resent Success");
		return "redirect:/jobseeker/otp/" + jobSeeker.getId();
	}

}
