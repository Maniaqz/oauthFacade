package com.ilmnq.oauthFacade;

import com.ilmnq.oauthFacade.services.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.ilmnq.oauthFacade")
public class OauthFacadeFactoryConfiguration {
    @Bean
    GoogleService googleService(){
        return new GoogleServiceBean();
    }
    @Bean
    GoogleScopeBuilder googleScopeBuilder(){
        return new GoogleScopeBuilderImpl();
    }
    @Bean
    DataManagingService dataManagingService(){
        return new DataManagingServiceBean();
    }
}
