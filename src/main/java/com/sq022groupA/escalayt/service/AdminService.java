package com.sq022groupA.escalayt.service;

import com.sq022groupA.escalayt.entity.model.Admin;
import com.sq022groupA.escalayt.entity.model.User;
import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.AdminUserDetailsDto;
import com.sq022groupA.escalayt.payload.response.LoginResponse;
import com.sq022groupA.escalayt.payload.response.UserRegistrationResponse;
import jakarta.mail.MessagingException;

import java.util.List;

public interface AdminService {

    String register(AdminRequest registrationRequest) throws MessagingException;
    LoginResponse loginUser(LoginRequestDto loginRequestDto);
    void resetPassword(PasswordResetDto passwordResetDto);
    void newResetPassword(PasswordResetDto passwordResetDto);
    String editUserDetails(String username, UserDetailsDto userDetailsDto);
    String forgotPassword (String email);

    boolean existsByEmail(String email);
    Admin findByResetToken(String token);
    void updatePassword(String username, String newPassword);

    String createToken(Admin admin);


    // USER/EMPLOYEE RELATED METHODS
    UserRegistrationResponse registerUser(String currentUsername, UserRegistrationDto userRegistrationDto) throws MessagingException;


    AdminUserDetailsDto getAdminDetails(String username);

    List<AdminUserDetailsDto> getAllEmployee(String username);


    AdminUserDetailsDto editUserDetailsByAdmin(Long departmentId, AdminEditUserRequestDto requestDto);

    AdminUserDetailsDto editAdminDetails(String username, AdminDetailsRequestDto requestDto);


}
