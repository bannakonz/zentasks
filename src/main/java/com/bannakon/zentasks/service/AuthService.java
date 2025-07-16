package com.bannakon.zentasks.service;

import com.bannakon.zentasks.dto.*;
import com.bannakon.zentasks.entity.User;
import com.bannakon.zentasks.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public RegisterResponse createUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        userRepository.save(user);

        return new RegisterResponse("Register is successfully") ;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Credential"));

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credential");
        }
        String token = jwtService.generateToken(user.getEmail());
        Long expiration = jwtService.getExpirationMs() / 1000;

        return new LoginResponse("Login successfully", token, expiration);

    }

    public LogoutResponse logout(LogoutRequest logoutRequest) {
        userRepository.findByEmail(logoutRequest.getEmail()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return new LogoutResponse("Logout successfully");
    }
}
