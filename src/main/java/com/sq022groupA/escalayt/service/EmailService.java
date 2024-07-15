package com.sq022groupA.escalayt.service;


import com.sq022groupA.escalayt.payload.response.EmailDetails;
import jakarta.mail.MessagingException;

public interface EmailService {

    void sendEmailAlert(EmailDetails emailDetails);

    void sendSimpleMailMessage(EmailDetails message, String firstName, String lastName, String link) throws MessagingException;

    void mimeMailMessage(EmailDetails emailDetails);
}
