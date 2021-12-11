package com.ilmnq.oauthFacade.services;

import java.util.ArrayList;

public interface GoogleScopeBuilder {

    public ArrayList<String> buildScope(String scopeString);

}
