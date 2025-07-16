package com.bannakon.zentasks.controller;


import com.bannakon.zentasks.dto.*;
import com.bannakon.zentasks.service.AuthService;
import com.bannakon.zentasks.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("api/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    public AuthController(
            AuthService authService,
            JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok().body(authService.createUser(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().body(authService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody LogoutRequest logoutRequest
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ") ) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }
        String accessToken = authHeader.substring(7);
        String email = jwtService.extractEmail(accessToken);
        return ResponseEntity.ok().body(authService.logout(email, logoutRequest.getRefreshToken()));
    }


}
