package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.entity.model.*;
import com.sq022groupA.escalayt.payload.response.*;
import com.sq022groupA.escalayt.repository.*;
import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.service.EmailService;
import com.sq022groupA.escalayt.service.UserService;
import com.sq022groupA.escalayt.utils.ForgetPasswordEmailBody;
import com.sq022groupA.escalayt.utils.UserRegistrationEmailBody;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${baseUrl}")
    private String baseUrl;


    // USER/EMPLOYEE REGISTRATION
    @Override
    public UserRegistrationResponse registerUser(UserRegistrationDto userRegistrationDto) throws MessagingException {

        Optional<User> existingUser = userRepository.findByEmail(userRegistrationDto.getEmail());

        if(existingUser.isPresent()){
            throw new RuntimeException("Email already exists. Login to your account");
        }

        User newUser = User.builder()
                .fullName(userRegistrationDto.getFullName())
                .email(userRegistrationDto.getEmail())
                .phoneNumber(userRegistrationDto.getPhoneNumber())
                .jobTitle(userRegistrationDto.getJobTitle())
                .department(userRegistrationDto.getDepartment())
                .username(userRegistrationDto.getUsername())
                .password(passwordEncoder.encode(userRegistrationDto.getPassword()))
                .createdUnder(userRegistrationDto.getCreatedUnder())
                .build();

        User savedUser = userRepository.save(newUser);

        // SET UP EMAIL MESSAGE FOR REGISTERED USER/EMPLOYEE
        String userLoginUrl = baseUrl + "/user-login";

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACTIVATE YOUR ACCOUNT")
                .messageBody(UserRegistrationEmailBody.buildEmail(savedUser.getFullName(),
                        savedUser.getUsername(), userRegistrationDto.getPassword(), userLoginUrl))
                .build();

        // SEND EMAIL MESSAGE TO REGISTERED USER/EMPLOYEE
        emailService.mimeMailMessage(emailDetails);

        // METHOD RESPONSE
        return UserRegistrationResponse.builder()
                .responseTemplate(ResponseTemplate.builder()
                        .responseCode("007")
                        .responseMessage("User/Employee Created Successfully")
                        .build())
                .fullName(savedUser.getFullName())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .phoneNumber(savedUser.getPhoneNumber())
                .jobTitle(savedUser.getJobTitle())
                .department(savedUser.getDepartment())
                .createdUnder(savedUser.getCreatedUnder())
                .build();

    }


}
