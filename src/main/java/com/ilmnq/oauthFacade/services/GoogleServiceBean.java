package com.ilmnq.oauthFacade.services;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.ilmnq.oauthFacade.config.GoogleConfig;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Log
public class GoogleServiceBean implements GoogleService{

    private final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/user.phonenumbers.read",
            "https://www.googleapis.com/auth/user.gender.read",
            "https://www.googleapis.com/auth/user.birthday.read");


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
    public String getLoginUrl(List<String> scopes) {
        return getFlow(scopes)
                .newAuthorizationUrl()
                .setRedirectUri(googleConfig.getCurrentAppUrl())
                .build();
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
    public HashMap<String, Object> getUserDataMap(String code) {
        Person person;
        try {
            PeopleService peopleService = getPeopleService(getAccessToken(code));
            //TODO:разобраться, есть ли смысл динамически вставлять поля PersonFields
            // или  же можно сразу сделать общий запрос, и поля без доступа просто проигнорируются
            person = peopleService.people().get("people/me").setPersonFields("addresses,ageRanges,biographies,birthdays," +
                    "braggingRights,coverPhotos,emailAddresses," +
                    "events,genders,imClients,interests,locales," +
                    "memberships,metadata,names,nicknames,occupations," +
                    "organizations,phoneNumbers,photos,relations,relationshipInterests," +
                    "relationshipStatuses,residences,skills,taglines,urls")
                    .execute();
        } catch (Exception e){
            log.severe("Can't get user data");
            return null;
        }

        HashMap<String, Object> returnHashMap = new HashMap<>();

        //TODO: возможно стоит разбить метод на дефолтный и кастомный,
        // и в кастомный передавать строку с запросом
        String dynamicTestString = "names,emailAddresses,birthdays";

        person.getNames().get(0).getDisplayName();
        ArrayList<String> fetchingAttributes = new ArrayList<>(Arrays.asList(dynamicTestString.split(",", 0)));
        fetchingAttributes.forEach(entry ->{
            try {
                Method methodToCall = person.getClass().getDeclaredMethod("get"+ StringUtils.capitalize(entry));
                List<Object> objectList = (List<Object>) methodToCall.invoke(person);
                if (!objectList.isEmpty()){
                    Method additionalMethod = defineGetValueMethod(entry);
                    if (additionalMethod != null){
                        returnHashMap.put(entry, additionalMethod.invoke(objectList.get(0)));
                    }
                }
            } catch (Exception e){
                log.severe("sneed");
            }
        });

        return returnHashMap;
    }

    //TODO: закончить работу со всеми возможными пунктами скоупа
    private Method defineGetValueMethod(String attributeName) throws NoSuchMethodException {
        Method returnMethod = null;
        switch (attributeName){
            case "names":
                returnMethod = Name.class.getDeclaredMethod("getDisplayName");
                break;
            case "emailAddresses":
                returnMethod = EmailAddress.class.getDeclaredMethod("getValue");
                break;
            case "birthdays":
                //можно использовать getText()
                returnMethod = Birthday.class.getDeclaredMethod("getDate");
                break;
        }
        return returnMethod;
    }

    protected PeopleService getPeopleService(String accessToken) {
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
                    .build();
        } catch (Exception e) {
            log.severe("Failed to prepare Google Authorization workflow");
            throw new RuntimeException("Google authorization workflow failed");
        }
    }

}
