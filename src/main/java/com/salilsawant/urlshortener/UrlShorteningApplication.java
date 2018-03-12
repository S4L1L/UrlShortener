package com.salilsawant.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
public class UrlShorteningApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlShorteningApplication.class, args);
	}
}
