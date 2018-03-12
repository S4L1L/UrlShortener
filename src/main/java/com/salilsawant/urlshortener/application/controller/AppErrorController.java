package com.salilsawant.urlshortener.application.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Enumeration;
import java.util.Map;

@Controller
class AppErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(AppErrorController.class);

    private final static String ERROR_PATH = "/error";

    @GetMapping(value = ERROR_PATH)
    public ModelAndView error(
            HttpServletRequest request) {
        logger.debug(request.getHeader("ErrorUrl"));
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("", "");
        return mav;
    }

    @NotNull
    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }


}
