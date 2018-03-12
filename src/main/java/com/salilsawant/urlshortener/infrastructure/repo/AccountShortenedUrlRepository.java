package com.salilsawant.urlshortener.infrastructure.repo;


import com.salilsawant.urlshortener.infrastructure.model.AccountShortenedUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

@Repository
@Transactional
public interface AccountShortenedUrlRepository extends JpaRepository<AccountShortenedUrl, String> {

    @NotNull
    AccountShortenedUrl findByUrlId(
            Long urlId);

    @NotNull
    AccountShortenedUrl[] findByAccountId(
            String accountId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE shortened_url SET hits = hits + 1 WHERE id = :urlId", nativeQuery = true)
    int updateHitsByUrlId(@Param("urlId") long urlId);
}
