package com.salilsawant.urlshortener.infrastructure.model;

import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "user_account",
        indexes = {
                @Index(name = "IDX_ACCOUNTID",
                columnList = "account_id",
                unique = true)})
public class UserAccount implements Serializable  {

    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue
    private long userId;

    @NotNull
    @Column(name = "account_id")
    private String accountId;

    @NotNull
    @Column(name = "password")
    private String password;

    @PersistenceConstructor
    private UserAccount() {

    }

    public UserAccount(String accountId, String password) {
        super();
        this.accountId = accountId;
        this.password  = password;
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public String getPassword() {
        return this.password;
    }
}
