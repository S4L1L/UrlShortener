package com.salilsawant.urlshortener.infrastructure.repo;


import com.salilsawant.urlshortener.infrastructure.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

@Repository
@Transactional
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {

    @NotNull
    UserAccount findByAccountId(
            String accountId);
}
