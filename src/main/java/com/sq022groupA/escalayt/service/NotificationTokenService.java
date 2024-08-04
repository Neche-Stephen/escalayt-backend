package com.sq022groupA.escalayt.service;

import com.sq022groupA.escalayt.entity.model.NotificationToken;

import java.util.List;

public interface NotificationTokenService {

//    void saveToken(String token);
//    void deleteToken(String token);
    List<NotificationToken> getTokensByUserId(Long userId);
    NotificationToken saveToken(Long userId, String token);
    void deleteToken(Long userId, String token);
    boolean tokenExists(String token);
}
