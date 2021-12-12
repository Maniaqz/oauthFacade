package com.ilmnq.oauthFacade.services;

import com.ilmnq.oauthFacade.data.entities.google.GoogleUser;
import com.ilmnq.oauthFacade.data.repositories.GoogleUserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class DataManagingServiceBean implements DataManagingService {

    @Autowired
    GoogleUserRepository googleUserRepository;

    @Override
    public void saveGoogleUser(Object dataObject) {
        GoogleUser newGoogleUser = new GoogleUser();
        newGoogleUser.setId(UUID.randomUUID());
        newGoogleUser.setData(dataObject);
        googleUserRepository.save(newGoogleUser);
    }
}
