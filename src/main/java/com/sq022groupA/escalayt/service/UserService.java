package com.sq022groupA.escalayt.service;

import com.sq022groupA.escalayt.entity.model.User;
import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.AdminUserDetailsDto;
import com.sq022groupA.escalayt.payload.response.LoginResponse;
import com.sq022groupA.escalayt.payload.response.UserRegistrationResponse;
import jakarta.mail.MessagingException;

public interface UserService {
    LoginResponse loginUser(LoginRequestDto loginRequestDto);

    String forgotPassword(String email);

    boolean existsByEmail(String email);
    User findByResetToken(String token);
    void updatePassword(String username, String newPassword);

    String createToken(User user);

    AdminUserDetailsDto getUserDetails(String username);

    AdminUserDetailsDto editUserInfo(String username, AdminUserDetailsDto adminUserDetailsDto);

}