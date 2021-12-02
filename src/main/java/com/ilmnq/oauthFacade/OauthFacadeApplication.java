package com.ilmnq.oauthFacade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class OauthFacadeApplication {

	public static void main(String[] args) {
		SpringApplication.run(OauthFacadeApplication.class, args);
	}

}
