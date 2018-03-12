package com.salilsawant.urlshortener.infrastructure.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.salilsawant.urlshortener.infrastructure.model.AccountShortenedUrl;
import com.salilsawant.urlshortener.infrastructure.model.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.salilsawant.urlshortener.infrastructure.repo.AccountShortenedUrlRepository;
import com.salilsawant.urlshortener.infrastructure.repo.UserAccountRepository;

@Component
public class DatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    private final AccountShortenedUrlRepository accountShortenedUrlRepository;

    private final UserAccountRepository userAccountRepository;

    @Autowired
    public DatabaseService(
            AccountShortenedUrlRepository accountShortenedUrlRepository,
            UserAccountRepository userAccountRepository) {
        this.accountShortenedUrlRepository = accountShortenedUrlRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public boolean checkIfAccountExists(String accountId) {
        UserAccount userAccountObj = userAccountRepository.findByAccountId(accountId);
        logger.debug(new Gson().toJson(userAccountObj, new TypeToken<UserAccount>() {}.getType()));

        return userAccountObj != null;
    }

    public UserAccount findAccountId(String accountId) {
        return userAccountRepository.findByAccountId(accountId);
    }

    public UserAccount saveUserAccount(UserAccount inputUserAccountObj) {
        UserAccount userAccountObj = userAccountRepository.save(inputUserAccountObj);
        logger.debug(new Gson().toJson(userAccountObj, new TypeToken<UserAccount>() {}.getType()));
        return userAccountObj;
    }

    public AccountShortenedUrl registerUrl(AccountShortenedUrl inputAccountShortenedUrlObj) {
        AccountShortenedUrl accountShortenedUrlObj = accountShortenedUrlRepository.save(inputAccountShortenedUrlObj);
        logger.debug(new Gson().toJson(accountShortenedUrlObj, new TypeToken<AccountShortenedUrl>() {}.getType()));
        return accountShortenedUrlObj;
    }

    public AccountShortenedUrl findUrl(long urlId) {
        AccountShortenedUrl accountShortenedUrlObj = accountShortenedUrlRepository.findByUrlId(urlId);
        logger.debug(new Gson().toJson(accountShortenedUrlObj, new TypeToken<AccountShortenedUrl>() {}.getType()));
        return accountShortenedUrlObj;
    }

    public int incrementHits(long urlId) {
        int updateReturn = accountShortenedUrlRepository.updateHitsByUrlId(urlId);
        logger.debug(String.valueOf(updateReturn));
        return updateReturn;
    }

    public AccountShortenedUrl[] urlsByAccountId(String accountId) {
        AccountShortenedUrl[] urlObj = accountShortenedUrlRepository.findByAccountId(accountId);
        logger.debug(new Gson().toJson(urlObj, new TypeToken<AccountShortenedUrl[]>() {}.getType()));
        return urlObj;
    }
}
