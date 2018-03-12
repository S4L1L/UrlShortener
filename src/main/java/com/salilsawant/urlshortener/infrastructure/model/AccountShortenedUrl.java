package com.salilsawant.urlshortener.infrastructure.model;

import org.springframework.data.annotation.PersistenceConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "shortened_url",
        indexes = {
                @Index(name = "IDX_ACCOUNTID",
                        columnList = "account_id")},
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"account_id", "normal_url"})})
public class AccountShortenedUrl implements Serializable  {

    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(name = "id")
    @GeneratedValue
    private long urlId;

    @NotNull
    @Column(name = "account_id")
    private String accountId;

    @NotNull
    @Column(name = "normal_url")
    private String normalUrl;

    @NotNull
    @Column(name = "redirect_type")
    private String redirectType;

    @NotNull
    @Column(name = "hits")
    private int hits;

    @PersistenceConstructor
    private AccountShortenedUrl() {

    }

    public AccountShortenedUrl(String accountId, String normalUrl, String redirectType) {
        super();
        this.accountId = accountId;
        this.normalUrl = normalUrl;
        this.redirectType = redirectType;
        this.hits = 0;
    }

    public long getUrlId() {
        return this.urlId;
    }

    public String getNormalUrl() {
        return normalUrl;
    }

    public String getRedirectType() {
        return redirectType;
    }

    public int getHits() {
        return hits;
    }

    public String getAccountId() {
        return accountId;
    }
}
