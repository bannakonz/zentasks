package com.bannakon.zentasks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
//    private String refreshToken;
    private String message;
    private String accessToken;
    private Long expiration;

}
