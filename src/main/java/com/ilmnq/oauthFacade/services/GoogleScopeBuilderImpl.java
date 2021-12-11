package com.ilmnq.oauthFacade.services;

import java.util.ArrayList;
import java.util.Arrays;

public class GoogleScopeBuilderImpl implements GoogleScopeBuilder{

    @Override
    public ArrayList<String> buildScope(String scopeString){
        ArrayList<String> stringList = new ArrayList<>(
                Arrays.asList(scopeString
                        .toUpperCase()
                        .split(",", 0)));
        ArrayList<String> scopeList = new ArrayList<>();
        stringList.forEach(entry -> {
            if (GoogleScope.valueOf(entry) != null){
                scopeList.add(GoogleScope.valueOf(entry).getApiLink());
            }
        });
        return scopeList;
    }

}
