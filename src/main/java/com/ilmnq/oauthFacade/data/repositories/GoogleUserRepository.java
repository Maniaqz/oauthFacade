package com.ilmnq.oauthFacade.data.repositories;

import com.ilmnq.oauthFacade.data.entities.google.GoogleUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GoogleUserRepository extends MongoRepository<GoogleUser, String> {
}
