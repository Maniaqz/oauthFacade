package com.ilmnq.oauthFacade.controllers.google;

import com.ilmnq.oauthFacade.data.entities.google.GoogleUser;
import com.ilmnq.oauthFacade.data.repositories.GoogleUserRepository;
import com.ilmnq.oauthFacade.services.DataManagingService;
import com.ilmnq.oauthFacade.services.GoogleScopeBuilder;
import com.ilmnq.oauthFacade.services.GoogleService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/google")
public class GoogleControllerImpl {

    @Autowired
    GoogleService googleService;
    @Autowired
    GoogleScopeBuilder googleScopeBuilder;
    @Autowired
    DataManagingService dataManagingService;

    /**
     * Builds an authorization link with default info scope (name, phone, email, birthday)
     *
     * @return Google authorization link
     */
    @GetMapping("requestDefaultAuthorizeUrl")
    public ResponseEntity<String> requestDefaultAuthorizeUrl(){
        return new ResponseEntity<>(googleService.getLoginUrl(), HttpStatus.OK);
    }

    @GetMapping("requestCustomAuthorizeUrl")
    public ResponseEntity<String> requestCustomAuthorizeUrl(@RequestParam String scopes){
        return new ResponseEntity<>(
                googleService.getLoginUrl(
                        googleScopeBuilder.buildScope(scopes)), HttpStatus.OK);
    }

    //получение информации через код аутентификации
    @GetMapping("/requestUserInfo")
    public ResponseEntity<ScopeResponseDTO> requestUserInfo(@RequestParam String code,
                                                            @RequestParam String scope,
                                                            @RequestParam Boolean saveData){
        HashMap <String, Object> returnMap = googleService.getUserDataMap(code);

        if (!returnMap.isEmpty() & Boolean.TRUE.equals(saveData)){
            dataManagingService.saveGoogleUser(returnMap);
        }

        return !returnMap.isEmpty() ?
                new ResponseEntity<>(new ScopeResponseDTO(returnMap, "success"), HttpStatus.OK) :
                new ResponseEntity<>(new ScopeResponseDTO(null, "failed to acquire user info"), HttpStatus.NOT_FOUND);
    }

    @GetMapping("/requestCustomUserInfo")
    public ResponseEntity<ScopeResponseDTO> requestUserInfo(
            @RequestParam String code,
            @RequestParam String scope){
        return new ResponseEntity<>(new ScopeResponseDTO(null, "failed to acquire user info"), HttpStatus.NOT_FOUND);
    }

    @AllArgsConstructor
    private static class ScopeResponseDTO{
        @Getter @Setter
        private HashMap<String, Object> scopeValues;
        @Getter @Setter
        private String statusMessage;
    }

}
