package com.ilmnq.oauthFacade.config;

import com.google.api.client.util.Value;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "google-config")
public class GoogleConfig {
    @Getter @Setter
    String clientId;
    @Getter @Setter
    String secret;
    @Getter @Setter
    String redirectValue;
    @Getter @Setter
    String currentAppUrl;
    //TODO: работа со scopes;
}
