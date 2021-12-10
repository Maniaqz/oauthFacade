package com.ilmnq.oauthFacade.services;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.ilmnq.oauthFacade.config.GoogleConfig;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Log
public class GoogleServiceBean implements GoogleService{

    private final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email");


    @Autowired
    GoogleConfig googleConfig;

    @Override
    public String getLoginUrl() {
        return getFlow()
                .newAuthorizationUrl()
                .setRedirectUri(googleConfig.getCurrentAppUrl())
                .build();
    }

    //TODO: работа с кастомным скоуп
    @Override
    public String getLoginUrl(String appUrl, List<String> scopes) {
        return null;
    }

    public String getAccessToken(String code) {
        try {
            TokenResponse tokenResponse = getFlow()
                    .newTokenRequest(code)
                    .setRedirectUri(googleConfig.getCurrentAppUrl())
                    .execute();
            return tokenResponse.getAccessToken();
        } catch (Exception e) {
            log.severe("Can't get user data");
            return null;
        }
    }


    @Override
    public HashMap<String, String> getUserDataMap(String code) {
        Person person;
        try {
            PeopleService peopleService = getUserService(getAccessToken(code));
            person = peopleService.people().get("people/me").setPersonFields("names,emailAddresses").execute();
        } catch (Exception e){
            log.severe("Can't get user data");
            return null;
        }

        List<EmailAddress> emails = person.getEmailAddresses();
        if (emails.size() <= 0) {
            log.severe("Emails size 0");
            return null;
        }

        HashMap<String, String> returnHashMap = new HashMap<>();

        Name names = person.getNames().get(0);

        returnHashMap.put("email", emails.get(0).getValue());
        returnHashMap.put("id", "");
        returnHashMap.put("name", names.getDisplayName());
        returnHashMap.put("firstName", names.getDisplayName() == null ? null : names.getGivenName());
        returnHashMap.put("middleName", names.getDisplayName() == null ? null : names.getMiddleName());
        returnHashMap.put("lastName", names.getDisplayName() == null ? null : names.getFamilyName());

        return returnHashMap;
    }
//
    protected PeopleService getUserService(String accessToken) {
        try {
            GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(accessToken, null));
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
            return new PeopleService.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    requestInitializer)
                    .build();
        } catch (Exception e) {
            log.severe("Failed to prepare connection to Google Plus");
            throw new RuntimeException("Google service connection failed");
        }
    }


    public AuthorizationCodeFlow getFlow(){
        try {
            return new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    googleConfig.getClientId(),
                    googleConfig.getSecret(),
                    SCOPES)
                    .build();
        } catch (Exception e){
            log.severe("Failed to prepare Google Authorization workflow");
            throw new RuntimeException("Google Authorization workflow failed");
        }
    }

    //TODO: Допилить кастомный скоуп
    public AuthorizationCodeFlow getFlow(List<String> scopes) {
        try {
            return new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    //TODO: Придумать, как выводить не через геттеры,
                    // а через обращение к константам типа SECRET / CLIENT_ID,
                    // учитывая что они определяются через properties
                    googleConfig.getClientId(),
                    googleConfig.getSecret(),
                    scopes)
                    .setAccessType("offline")
                    .setApprovalPrompt("force")
                    .build();
        } catch (Exception e) {
            log.severe("Failed to prepare Google Authorization workflow");
            throw new RuntimeException("Google authorization workflow failed");
        }
    }

}
