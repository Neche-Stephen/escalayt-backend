package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.entity.model.*;
import com.sq022groupA.escalayt.payload.response.UserRegistrationResponse;
import com.sq022groupA.escalayt.repository.*;
import com.sq022groupA.escalayt.exception.PasswordsDoNotMatchException;
import com.sq022groupA.escalayt.exception.UserNotFoundException;
import com.sq022groupA.escalayt.exception.UsernameAlreadyExistsException;
import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.EmailDetails;
import com.sq022groupA.escalayt.payload.response.LoginInfo;
import com.sq022groupA.escalayt.payload.response.LoginResponse;
import com.sq022groupA.escalayt.service.EmailService;
import com.sq022groupA.escalayt.service.UserService;
import com.sq022groupA.escalayt.utils.ForgetPasswordEmailBody;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${baseUrl}")
    private String baseUrl;


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
                .build();

        User savedUser = userRepository.save(newUser);

//        send email alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACTIVATE YOUR ACCOUNT")
                .messageBody(ForgetPasswordEmailBody.buildEmail(savedUser.getFullName(),
                        savedUser.getUsername(), userRegistrationDto.getPassword()))
                .build();

        //send the reset password link
        emailService.mimeMailMessage(emailDetails);

        return "A reset password link has been sent to your account." + resetPasswordUrl;

    }

}
