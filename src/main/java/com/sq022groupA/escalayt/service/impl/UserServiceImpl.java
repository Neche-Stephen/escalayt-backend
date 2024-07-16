package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.auth.model.ConfirmationToken;
import com.sq022groupA.escalayt.auth.model.JwtToken;
import com.sq022groupA.escalayt.auth.model.Role;
import com.sq022groupA.escalayt.auth.repository.ConfirmationTokenRepository;
import com.sq022groupA.escalayt.auth.repository.JwtTokenRepository;
import com.sq022groupA.escalayt.auth.repository.RoleRepository;
import com.sq022groupA.escalayt.auth.service.JwtService;
import com.sq022groupA.escalayt.entity.model.User;
import com.sq022groupA.escalayt.exception.UserNotFoundException;
import com.sq022groupA.escalayt.payload.request.ForgetPasswordDto;
import com.sq022groupA.escalayt.payload.request.LoginRequestDto;
import com.sq022groupA.escalayt.payload.request.PasswordResetDto;
import com.sq022groupA.escalayt.payload.request.UserRequest;
import com.sq022groupA.escalayt.payload.response.EmailDetails;
import com.sq022groupA.escalayt.payload.response.LoginInfo;
import com.sq022groupA.escalayt.payload.response.LoginResponse;
import com.sq022groupA.escalayt.repository.UserRepository;
import com.sq022groupA.escalayt.service.EmailService;
import com.sq022groupA.escalayt.service.UserService;
import com.sq022groupA.escalayt.utils.ForgetPasswordEmailBody;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenRepository jwtTokenRepository ;
    private final RoleRepository roleRepository;
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

    @Override
    public LoginResponse loginUser(LoginRequestDto loginRequestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword()
                )
        );
        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + loginRequestDto.getUsername()));

        if (!user.isEnabled()) {
            throw new RuntimeException("User account is not enabled. Please check your email to confirm your account.");
        }

        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return LoginResponse.builder()
                .responseCode("002")
                .responseMessage("Login Successfully")
                .loginInfo(LoginInfo.builder()
                        .username(user.getUsername())
                        .token(jwtToken)
                        .build())
                .build();
    }

    private void saveUserToken(User userModel, String jwtToken) {
        var token = JwtToken.builder()
                .user(userModel)
                .token(jwtToken)
                .tokenType("BEARER")
                .expired(false)
                .revoked(false)
                .build();
        jwtTokenRepository.save(token);
    }

    private void revokeAllUserTokens(User userModel) {
        var validUserTokens = jwtTokenRepository.findAllValidTokenByUser(userModel.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        jwtTokenRepository.saveAll(validUserTokens);
    }

    public void resetPassword(PasswordResetDto passwordResetDto) {
        User user = userRepository.findByEmail(passwordResetDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + passwordResetDto.getEmail()));

        if(user.getResetToken() != null){
            return;
        }

        user.setPassword(passwordEncoder.encode(passwordResetDto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public String editUserDetails(String username, UserRequest userRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        //Update user details
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setPhoneNumber(userRequest.getPhoneNumber());

        //save the updated user
        userRepository.save(user);

        return "User details updated successfully";
    }

    @Override
    public String forgotPassword(ForgetPasswordDto forgetPasswordDto) {


        /*
        steps
        1- check if email exist (settled)
        2- create a random token (done)
        3- Hash the token and add it to the db under the user (done)
        4- set expiration time for the token in the db (done)
        5- generate a reset url using the token (done)
        6- send email with reset url link
         */

        Optional<User> checkUser = userRepository.findByEmail(forgetPasswordDto.getEmail());

        // check if user exist with that email
        if(!checkUser.isPresent()) throw new RuntimeException("No such user with this email.");

        User forgettingUser = checkUser.get();

        // generate a hashed token
        ConfirmationToken forgetPassWordToken = new ConfirmationToken(forgettingUser);

        // saved the token.
        // the token has an expiration date
        confirmationTokenRepository.save(forgetPassWordToken);
        // System.out.println("the token "+forgetPassWordToken.getToken());

        // generate a password reset url
        String resetPasswordUrl = "http://localhost:8080/api/v1/auth/confirm?token=" + forgetPassWordToken.getToken();



        // click this link to reset password;
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(forgettingUser.getEmail())
                .subject("FORGET PASSWORD")
                .messageBody(ForgetPasswordEmailBody.buildEmail(forgettingUser.getFirstName(),
                        forgettingUser.getLastName(), resetPasswordUrl))
                .build();

        //send the reset password link
        emailService.mimeMailMessage(emailDetails);

        return "A reset password link has been sent to your account." + resetPasswordUrl;
    }
}
