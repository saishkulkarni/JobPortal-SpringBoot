package com.jsp.job_portal.service;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public String handle() {
        return "404.html";
    }
}
