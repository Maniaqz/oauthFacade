package com.ilmnq.oauthFacade.data.entities.google;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class GoogleUser {
    @Id
    @Getter @Setter
    private UUID id;
    @Getter @Setter
    private Object data;
}
