package com.smartnotification.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smartnotification.entity.FcmToken;
import com.smartnotification.entity.User;
import com.smartnotification.repository.FcmTokenRepository;
import com.smartnotification.service.FcmTokenService;

@Service
public class FcmTokenServiceImpl implements FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    public FcmTokenServiceImpl(FcmTokenRepository fcmTokenRepository) {
        this.fcmTokenRepository = fcmTokenRepository;
    }

    @Override
    public void saveToken(
            User user,
            String token,
            String deviceName,
            String platform) {

        FcmToken fcmToken = fcmTokenRepository.findByToken(token)
                .orElse(new FcmToken());

        fcmToken.setUser(user);
        fcmToken.setToken(token);
        fcmToken.setDeviceName(deviceName);
        fcmToken.setPlatform(platform);
        fcmToken.setActive(true);

        fcmTokenRepository.save(fcmToken);
    }

    @Override
    public List<FcmToken> getActiveTokens(User user) {
        return fcmTokenRepository.findByUserAndActiveTrue(user);
    }

    @Override
    public void deactivateToken(String token) {

        fcmTokenRepository.findByToken(token)
                .ifPresent(fcmToken -> {
                    fcmToken.setActive(false);
                    fcmTokenRepository.save(fcmToken);
                });
    }
}