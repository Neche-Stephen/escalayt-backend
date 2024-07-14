package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.auth.model.ConfirmationToken;
import com.sq022groupA.escalayt.auth.model.JwtToken;
import com.sq022groupA.escalayt.auth.model.Role;
import com.sq022groupA.escalayt.auth.repository.ConfirmationTokenRepository;
import com.sq022groupA.escalayt.auth.repository.JwtTokenRepository;
import com.sq022groupA.escalayt.auth.repository.RoleRepository;
import com.sq022groupA.escalayt.auth.service.JwtService;
import com.sq022groupA.escalayt.entity.model.User;
import com.sq022groupA.escalayt.payload.request.LoginRequestDto;
import com.sq022groupA.escalayt.payload.request.PasswordResetDto;
import com.sq022groupA.escalayt.payload.request.UserDetailsDto;
import com.sq022groupA.escalayt.payload.request.UserRequest;
import com.sq022groupA.escalayt.payload.response.EmailDetails;
import com.sq022groupA.escalayt.payload.response.LoginInfo;
import com.sq022groupA.escalayt.payload.response.LoginResponse;
import com.sq022groupA.escalayt.repository.UserRepository;
import com.sq022groupA.escalayt.service.EmailService;
import com.sq022groupA.escalayt.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenRepository jwtTokenRepository ;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${baseUrl}")
    private String baseUrl;

    @Override
    public String registerUser(UserRequest registrationRequest) throws MessagingException {

        //Optional<User> existingUser = userRepository.findByEmail(registrationRequest.getEmail());
        Optional<User> existingUser = userRepository.findByUsername(registrationRequest.getUserName());


        if(existingUser.isPresent()){
            throw new RuntimeException("Email already exists. Login to your account");
        }

        Optional<Role> userRole = roleRepository.findByName("USER");
        if (userRole.isEmpty()) {
            throw new RuntimeException("Default role USER not found in the database.");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(userRole.get());


        User newUser = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .username(registrationRequest.getUserName())
                .email(registrationRequest.getEmail())
                .phoneNumber(registrationRequest.getPhoneNumber())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .roles(roles)
                .build();

        User savedUser = userRepository.save(newUser);

        ConfirmationToken confirmationToken = new ConfirmationToken(savedUser);
        confirmationTokenRepository.save(confirmationToken);
        System.out.println(confirmationToken.getToken());

//        String confirmationUrl = EmailTemplate.getVerificationUrl(baseUrl, confirmationToken.getToken());

//        String confirmationUrl = baseUrl + "/confirmation/confirm-token-sucess.html?token=" + confirmationToken.getToken();
        String confirmationUrl = "http://localhost:8080/api/v1/auth/confirm?token=" + confirmationToken.getToken();

//        send email alert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION SUCCESSFUL")
                .build();
        emailService.sendSimpleMailMessage(emailDetails, savedUser.getFirstName(), savedUser.getLastName(), confirmationUrl);
        return "Confirmed Email";

    }

}
