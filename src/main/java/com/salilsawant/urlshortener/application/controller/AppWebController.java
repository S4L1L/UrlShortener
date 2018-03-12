package com.salilsawant.urlshortener.application.controller;

import com.salilsawant.urlshortener.domain.service.UrlShorteningService;
import com.salilsawant.urlshortener.infrastructure.model.AccountShortenedUrl;
import com.salilsawant.urlshortener.infrastructure.service.DatabaseService;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
class AppWebController {

    private static final Logger logger = LoggerFactory.getLogger(AppWebController.class);

    private static final String ERROR_VIEW = "404";

    private final DatabaseService databaseService;

    private AppWebController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @RequestMapping(value = "/help", method = RequestMethod.GET)
    private String displayHelp(Model model) {
        return "help";
    }

    @RequestMapping(value = {"/{shortUrl}"}, method = RequestMethod.GET)
    public ModelAndView pageRedirect(
            @Nullable @PathVariable String shortUrl,
            HttpServletRequest request,
            HttpServletResponse response) {

        long urlId;
        ModelAndView mav = new ModelAndView();

        try {
            urlId = UrlShorteningService.decode(shortUrl);
        } catch (Exception e) {
            logger.debug("Could not find urlId for " + shortUrl);
            mav.setViewName(ERROR_VIEW);
            return mav;
        }

        AccountShortenedUrl shortenedUrlObj = databaseService.findUrl(urlId);

        if (shortenedUrlObj == null) {
            logger.debug("Redirect to error page (" + ERROR_VIEW + ")");
            mav.setViewName(ERROR_VIEW);
            return mav;
        }

        databaseService.incrementHits(urlId);

        mav.setStatus(HttpStatus.valueOf(Integer.parseInt(shortenedUrlObj.getRedirectType())));
        mav.setViewName(shortenedUrlObj.getNormalUrl());
        return new ModelAndView("redirect:" + shortenedUrlObj.getNormalUrl());
    }

    private String getServerUrl(HttpServletRequest request) {
        StringBuilder result = new StringBuilder();
        result.append(request.getScheme())
                .append("://")
                .append(request.getServerName());

        if ( (request.getScheme().equals("http") && request.getServerPort() != 80) ||
                (request.getScheme().equals("https") && request.getServerPort() != 443) ) {
            result.append(':')
                    .append(request.getServerPort());
        }

        return result.toString();
    }
}
