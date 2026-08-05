package com.smartnotification.service;

import java.util.List;

import com.smartnotification.entity.FcmToken;
import com.smartnotification.entity.User;

public interface FcmTokenService {

    void saveToken(User user,
                   String token,
                   String deviceName,
                   String platform);

    List<FcmToken> getActiveTokens(User user);

    void deactivateToken(String token);
}