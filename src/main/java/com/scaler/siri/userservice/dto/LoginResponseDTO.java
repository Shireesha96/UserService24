package com.scaler.siri.userservice.dto;

import com.scaler.siri.userservice.models.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String token;
    private ResponseStatus responseStatus;
}
