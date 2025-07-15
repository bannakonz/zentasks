package com.bannakon.zentasks.controller;


import com.bannakon.zentasks.dto.RegisterRequest;
import com.bannakon.zentasks.dto.RegisterResponse;
import com.bannakon.zentasks.service.AuthService;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/auth")
@RestController
@NoArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok().body(authService.createUser(registerRequest));
    }



}
