package com.bannakon.zentasks.repository;

import com.bannakon.zentasks.entity.RefreshToken;
import com.bannakon.zentasks.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
