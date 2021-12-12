package com.ilmnq.oauthFacade.services;

import com.ilmnq.oauthFacade.data.entities.google.GoogleUser;
import com.ilmnq.oauthFacade.data.repositories.GoogleUserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.UUID;

public class DataManagingServiceBean implements DataManagingService {

    @Autowired
    GoogleUserRepository googleUserRepository;

    @Override
    public void saveGoogleUserByMap(HashMap<String, Object> map) {
        if (map.get("names") != null &
                map.get("emailAddresses") !=null){
            GoogleUser newGoogleUser = new GoogleUser();
            newGoogleUser.setId(UUID.randomUUID());
            newGoogleUser.setName((String) map.get("names"));
            map.remove("names");
            newGoogleUser.setEmail((String) map.get("emailAddresses"));
            map.remove("emailAddresses");
            newGoogleUser.setData(map);

            googleUserRepository.save(newGoogleUser);
        } else {
            //выбрасить экспешен
            System.out.println("sneed");
        }

    }
}
