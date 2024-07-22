package com.sq022groupA.escalayt.controller;

import com.sq022groupA.escalayt.payload.request.NotificationRequest;
import com.sq022groupA.escalayt.payload.response.NotificationResponse;
import com.sq022groupA.escalayt.service.impl.FCMService;
import com.sq022groupA.escalayt.service.impl.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class NotificationController {
    @Autowired
    private FCMService fcmService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/notification")
    public ResponseEntity sendNotification(@RequestBody NotificationRequest request) throws ExecutionException, InterruptedException {
        fcmService.sendMessageToToken(request);
        NotificationResponse response = NotificationResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Notification has been sent.")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("token/send-to-all")
    public ResponseEntity<NotificationResponse> sendNotificationToAll(@RequestBody NotificationRequest request) {
        try {
            notificationService.sendNotificationToAll(request);
            NotificationResponse response = NotificationResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Notification sent to all tokens.")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            NotificationResponse response = NotificationResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to send notification.")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}