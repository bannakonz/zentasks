package com.bannakon.zentasks.service;

import com.bannakon.zentasks.dto.*;
import com.bannakon.zentasks.entity.RefreshToken;
import com.bannakon.zentasks.entity.User;
import com.bannakon.zentasks.repository.RefreshTokenRepository;
import com.bannakon.zentasks.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(
            UserRepository userRepository,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
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
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        Long expiration = jwtService.getExpirationMs() / 1000;

        return new LoginResponse("Login successfully", token, refreshToken.getToken(), expiration);

    }

    public LogoutResponse logout(String email, String refreshToken) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found"));

        if (!token.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token does not belong to authenticated user");
        }

        refreshTokenRepository.delete(token);
        return new LogoutResponse("Logout successful");
    }
}
