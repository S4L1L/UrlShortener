package com.salilsawant.urlshortener.application.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.salilsawant.urlshortener.domain.service.UrlShorteningService;
import com.salilsawant.urlshortener.infrastructure.model.AccountShortenedUrl;
import com.salilsawant.urlshortener.infrastructure.model.UserAccount;
import com.salilsawant.urlshortener.infrastructure.service.DatabaseService;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
class AppRestController {

    private static final Logger logger = LoggerFactory.getLogger(AppRestController.class);

    private static final String TOKEN_PREFIX = "BASIC";

    private final DatabaseService databaseService;

    private AppRestController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @RequestMapping(value = "/account", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> accountRegistration(
            @Nullable @RequestParam(value="accountId", required = false) String accountId) {

        Map<String, String> response = new HashMap<>();

        if ((accountId == null) || !accountId.matches("[A-Za-z0-9]+")) {
            response.put("success", "false");
            response.put("description", "Invalid accountId " + accountId);
            return generateResponse(response, HttpStatus.BAD_REQUEST);
        }

        boolean accountExists = databaseService.checkIfAccountExists(accountId);

        if (accountExists) {
            response.put("success", "false");
            response.put("description", "Account with id " + accountId + " already exists");
            return generateResponse(response, HttpStatus.CONFLICT);
        }

        UserAccount userAccountObj = databaseService.saveUserAccount(
                new UserAccount(
                        accountId,
                        new RandomStringGenerator.Builder()
                            .withinRange('0', 'z')
                            .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                            .build()
                            .generate(8)
                )
        );

        if (userAccountObj == null) {
            response.put("success", "false");
            response.put("description", "Could not create accountId " + accountId);
            return generateResponse(response, HttpStatus.FORBIDDEN);
        }

        response.put("success", "true");
        response.put("description", "Account with id "+accountId+" successfully created");
        response.put("password", userAccountObj.getPassword());
        return generateResponse(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> registerUrl(
            @RequestHeader("Authorization") String authToken,
            @Nullable @RequestParam(value="url", required = false) String normalUrl,
            @Nullable @RequestParam(value="redirectType", required = false, defaultValue = "302") String redirectType) {

        Map<String, String> response = new HashMap<>();

        if (authToken == null || !authToken.startsWith("Basic")) {
            response.put("success", "false");
            response.put("description", "Unauthorized Access");
            return generateResponse(response, HttpStatus.FORBIDDEN);
        }

        String[] credentials = convertAuthTokenToCrendentials(authToken);

        logger.debug(new Gson().toJson(
                credentials,
                new TypeToken<String[]>() {
                }.getType()
        ));

        UserAccount userAccountObj = databaseService.findAccountId(credentials[0]);

        if (userAccountObj == null || !credentials[1].equals(userAccountObj.getPassword())) {
            response.put("success", "false");
            response.put("description", "Invalid Auth Token");
            return generateResponse(response, HttpStatus.UNAUTHORIZED);
        }

        if (!new UrlValidator(new String[]{"http", "https"}).isValid(normalUrl)) {
            response.put("success", "false");
            response.put("description", "Invalid Url");
            return generateResponse(response, HttpStatus.BAD_REQUEST);
        }

        if ((redirectType != null) && !redirectType.equals("301") && !redirectType.equals("302")) {
            response.put("success", "false");
            response.put("description", "Invalid RedirectType");
            return generateResponse(response, HttpStatus.BAD_REQUEST);
        }

        try {
            AccountShortenedUrl shortenedUrlObj = databaseService.registerUrl(
                    new AccountShortenedUrl(
                            userAccountObj.getAccountId(),
                            normalUrl,
                            redirectType
                    )
            );

            logger.debug(new Gson().toJson(
                    shortenedUrlObj,
                    new TypeToken<AccountShortenedUrl>() {
                    }.getType()
            ));

            response.put("shortUrl", UrlShorteningService.encode(shortenedUrlObj.getUrlId()));

            return generateResponse(response, HttpStatus.OK);
        } catch (Exception e) {
            if (e.getClass().getName().contains("DataIntegrityViolationException")) {
                response.put("success", "false");
                response.put("description", "Url already registered");
                return generateResponse(response, HttpStatus.CONFLICT);
            }
            response.put("success", "false");
            response.put("description", "Unable to register the url; " + e.getClass());
            return generateResponse(response, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/statistic/{accountId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> fetchAccountStatistics(
            @RequestHeader("Authorization") String authToken,
            @PathVariable(value="accountId", required = false) String accountId) {

        Map<String, String> response = new HashMap<>();

        if (authToken == null || !authToken.startsWith("Basic")) {
            response.put("success", "false");
            response.put("description", "Unauthorized Access");
            return generateResponse(response, HttpStatus.FORBIDDEN);
        }

        String[] credentials = convertAuthTokenToCrendentials(authToken);

        logger.debug(new Gson().toJson(
                credentials,
                new TypeToken<String[]>() {
                }.getType()
        ));

        UserAccount userAccountObj = databaseService.findAccountId(credentials[0]);

        if (userAccountObj == null || !credentials[1].equals(userAccountObj.getPassword())) {
            response.put("success", "false");
            response.put("description", "Invalid Auth Token");
            return generateResponse(response, HttpStatus.UNAUTHORIZED);
        }

        if (!credentials[0].equals(accountId)) {
            response.put("success", "false");
            response.put("description", "You are not authorized to access this account");
            return generateResponse(response, HttpStatus.UNAUTHORIZED);
        }

        AccountShortenedUrl urlList[] = databaseService.urlsByAccountId(accountId);

        for (AccountShortenedUrl urlObj : urlList) {
            response.put(urlObj.getNormalUrl(), String.valueOf(urlObj.getHits()));
        }

        return generateResponse(response, HttpStatus.OK);
    }

    private ResponseEntity<String> generateResponse(Map<String, String> response, HttpStatus httpStatus) {
        return new ResponseEntity<>(
                new Gson().toJson(
                        response,
                        new TypeToken<Map<String, String>>() {
                        }.getType()
                ),
                httpStatus);
    }

    private String[] convertAuthTokenToCrendentials(String authToken) {
        String token = authToken.replaceAll("^(?i)" + TOKEN_PREFIX,"").trim();
        String authString = StringUtils.newStringUtf8(Base64.getDecoder().decode(token));
        return authString.split(":", 2);
    }
}
