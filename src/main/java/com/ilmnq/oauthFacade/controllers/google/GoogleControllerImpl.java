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
    @Autowired
    GoogleUserRepository googleUserRepository;

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

    //TODO: подумать над тем, чтобы вне зависимости от скоупа всегда запрашивало
    // какие-то поля типа Имени, Почты, Телефона, мб всего вместе
    @GetMapping("/requestUserInfo")
    public ResponseEntity<ScopeResponseDTO> requestUserInfo(@RequestParam String code,
                                                            @RequestParam String scope,
                                                            @RequestParam Boolean saveData){
        HashMap <String, Object> returnMap = googleService.getUserDataMap(code);

        if (!returnMap.isEmpty() & Boolean.TRUE.equals(saveData)){
            dataManagingService.saveGoogleUserByMap(returnMap);
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

    @GetMapping("/findGoogleUserByEmail")
    public ResponseEntity<GoogleUser> findGoogleUserByEmail(@RequestParam String email){
        GoogleUser googleUser = googleUserRepository.findByEmail(email);
        return googleUser != null ?
                new ResponseEntity<>(googleUser,HttpStatus.OK) :
                new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @AllArgsConstructor
    private static class ScopeResponseDTO{
        @Getter @Setter
        private HashMap<String, Object> scopeValues;
        @Getter @Setter
        private String statusMessage;
    }

}
