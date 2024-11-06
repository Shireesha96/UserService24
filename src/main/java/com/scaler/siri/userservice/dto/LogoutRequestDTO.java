package com.scaler.siri.userservice.dto;

import com.scaler.siri.userservice.models.Token;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDTO {
    private String token;
}
