package com.ilmnq.oauthFacade.controllers.google;

import com.ilmnq.oauthFacade.services.GoogleService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("api/v1/google")
public class GoogleControllerImpl {

    @Autowired
    GoogleService googleService;

    @GetMapping("requestDefaultAuthorizeUrl")
    public ResponseEntity<String> requestDefaultAuthorizeUrl(){
        return new ResponseEntity<>(googleService.getLoginUrl(), HttpStatus.OK);
    }

    @GetMapping("requestCustomAuthorizeUrl")
    public ResponseEntity<String> requestCustomAuthorizeUrl(@RequestParam String scopes){
        //распарсить строку типа "email;name;phone" на скопы?
        return new ResponseEntity<>("placeholder", HttpStatus.OK);
    }

    //получение информации через код аутентификации
    @GetMapping("/requestUserInfo")
    public ResponseEntity<ScopeResponseDTO> requestUserInfo(@RequestParam String code){
        HashMap <String, String> returnMap = googleService.getUserDataMap(code);
        return !returnMap.isEmpty() ?
                new ResponseEntity<>(new ScopeResponseDTO(returnMap, "success"), HttpStatus.OK) :
                new ResponseEntity<>(new ScopeResponseDTO(null, "failed to acquire user info"), HttpStatus.NOT_FOUND);
    }

    @AllArgsConstructor
    private static class ScopeResponseDTO{
        @Getter @Setter
        private HashMap<String, String> scopeValues;
        private String statusMessage;
    }

}
