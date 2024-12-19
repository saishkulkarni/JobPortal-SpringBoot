package com.jsp.job_portal.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.jsp.job_portal.dto.JobSeeker;
import com.jsp.job_portal.dto.Recruiter;

import jakarta.mail.internet.MimeMessage;

@Service
public class MyEmailSender {

	@Autowired
	JavaMailSender mailSender;

	@Autowired
	TemplateEngine templateEngine;

	public void sendOtp(JobSeeker jobSeeker) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom("saishkulkarni7@gmail.com", "Job-Portal Application");
			helper.setTo(jobSeeker.getEmail());
			helper.setSubject("Otp for Creating Account with Us");

			Context context = new Context();
			context.setVariable("x", jobSeeker);

			helper.setText(templateEngine.process("otp-template.html", context), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mailSender.send(message);

	}

	public void sendOtp(Recruiter recruiter) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom("saishkulkarni7@gmail.com", "Job-Portal Application");
			helper.setTo(recruiter.getEmail());
			helper.setSubject("Otp for Creating Account with Us");

			Context context = new Context();
			context.setVariable("x", recruiter);

			helper.setText(templateEngine.process("otp-template.html", context), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mailSender.send(message);
	}

}
