package com.sq022groupA.escalayt.service;

import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.LoginResponse;
import jakarta.mail.MessagingException;

public interface UserService {

    String registerUser(UserRequest registrationRequest) throws MessagingException;
    LoginResponse loginUser(LoginRequestDto loginRequestDto);
    void resetPassword(PasswordResetDto passwordResetDto);
    String editUserDetails(Long userId, UserRequest userRequest);


    String forgotPassword (ForgetPasswordDto forgetPasswordDto);

}
