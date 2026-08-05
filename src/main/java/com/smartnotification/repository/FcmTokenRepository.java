package com.smartnotification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartnotification.entity.FcmToken;
import com.smartnotification.entity.User;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    List<FcmToken> findByUser(User user);

    List<FcmToken> findByUserAndActiveTrue(User user);

    Optional<FcmToken> findByToken(String token);

    boolean existsByToken(String token);
}