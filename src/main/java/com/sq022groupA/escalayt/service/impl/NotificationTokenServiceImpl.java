package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.entity.model.NotificationToken;
import com.sq022groupA.escalayt.repository.NotificationTokenRepository;
import com.sq022groupA.escalayt.service.NotificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationTokenServiceImpl implements NotificationTokenService {
    @Autowired
    private NotificationTokenRepository tokenRepository;

    public void saveToken(String token) {
        NotificationToken existingToken = tokenRepository.findByToken(token);
        if(existingToken == null){
            NotificationToken newToken = new NotificationToken();
            newToken.setToken(token);
            tokenRepository.save(newToken);
        }
        else {
            System.out.println("Token already exists: " + token);
            // You can update the existing token if needed
            // existingToken.setUpdatedAt(new Date());
            // fcmTokenRepository.save(existingToken);
        }
    }


    public void deleteToken(String token) {
        tokenRepository.deleteByToken(token);
    }
}
