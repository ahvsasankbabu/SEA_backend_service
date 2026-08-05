package com.smartnotification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartnotification.entity.OAuthToken;

public interface OAuthTokenRepository extends JpaRepository<OAuthToken, Long> {

    Optional<OAuthToken> findByEmail(String email);

    List<OAuthToken> findAll();
}