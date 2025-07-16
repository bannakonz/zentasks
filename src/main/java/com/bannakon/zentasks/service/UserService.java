package com.bannakon.zentasks.service;

import com.bannakon.zentasks.entity.User;
import com.bannakon.zentasks.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    public UserService(
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public User getCurrentUser(String accessToken) {
        String email = jwtService.extractEmail(accessToken);
        return userRepository.findByEmail(email).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
