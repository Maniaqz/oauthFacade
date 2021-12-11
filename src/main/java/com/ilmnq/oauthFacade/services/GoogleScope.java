package com.ilmnq.oauthFacade.services;

public enum GoogleScope {
    PROFILE("https://www.googleapis.com/auth/userinfo.profile"),
    EMAIL("https://www.googleapis.com/auth/userinfo.email"),
    PHONE("https://www.googleapis.com/auth/user.phonenumbers.read"),
    GENDER("https://www.googleapis.com/auth/user.gender.read"),
    BIRTHDAY("https://www.googleapis.com/auth/user.birthday.read");

    private final String apiLink;

    GoogleScope(String apiLink) {
        this.apiLink = apiLink;
    }

    public String getApiLink() {
        return apiLink;
    }

}
