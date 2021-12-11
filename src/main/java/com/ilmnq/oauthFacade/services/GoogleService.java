package com.ilmnq.oauthFacade.services;

import java.util.HashMap;
import java.util.List;

public interface GoogleService {

    String NAME = "oauthFacade_GoogleService";

    /**
     * Get default Google login page URL
     *
     * @return Google login URL, which will return authentication code upon
     * successful authorization
     */
    String getLoginUrl();

    /**
     * Get custom Google login page URL
     *
     * @param scopes custom info-fetch scope
     *
     * @return Google login URL, which will return authentication code upon
     * successful authorization
     */
    String getLoginUrl(List<String> scopes);

    String getAccessToken(String code);

//    String getAccessToken(String appUrl, String code);

    HashMap<String, String> getUserDataMap(String accessToken);

}
