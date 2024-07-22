package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.entity.model.NotificationToken;
import com.sq022groupA.escalayt.payload.request.NotificationRequest;
import com.sq022groupA.escalayt.repository.NotificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationService {

    @Autowired
    private NotificationTokenRepository tokenRepository;

    @Autowired
    private FCMService fcmService;

    public void sendNotificationToAll(NotificationRequest request) throws ExecutionException, InterruptedException {
        List<NotificationToken> tokens = tokenRepository.findAll();
        for (NotificationToken token : tokens) {
            request.setToken(token.getToken());
            fcmService.sendMessageToToken(request);
        }
    }

}
