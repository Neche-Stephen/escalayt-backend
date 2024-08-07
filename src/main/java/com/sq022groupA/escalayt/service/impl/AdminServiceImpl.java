package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.config.JwtService;
import com.sq022groupA.escalayt.entity.model.*;
import com.sq022groupA.escalayt.exception.*;
import com.sq022groupA.escalayt.payload.response.*;
import com.sq022groupA.escalayt.repository.*;
import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.service.DepartmentService;
import com.sq022groupA.escalayt.service.EmailService;
import com.sq022groupA.escalayt.service.AdminService;
import com.sq022groupA.escalayt.utils.ForgetPasswordEmailBody;
import com.sq022groupA.escalayt.utils.UniqueIdGenerator;
import com.sq022groupA.escalayt.utils.UserRegistrationEmailBody;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final JwtTokenRepository jwtTokenRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    private final DepartmentRepository departmentRepository;


    @Value("${baseUrl}")
    private String baseUrl;


    @Override
    public String register(AdminRequest registrationRequest) throws MessagingException {

        //Optional<User> existingUser = userRepository.findByEmail(registrationRequest.getEmail());
        Optional<Admin> existingUser = adminRepository.findByUsername(registrationRequest.getUserName());


        if(existingUser.isPresent()){
            throw new RuntimeException("Email already exists. Login to your account");
        }

        // check if username already exists
        Optional<Admin> existingUserByUsername = adminRepository.findByUsername(registrationRequest.getUserName());
        if (existingUserByUsername.isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists. Please choose another username.");
        }

        Optional<Role> userRole = roleRepository.findByName("ADMIN");
        if (userRole.isEmpty()) {
            throw new RuntimeException("Default role ADMIN not found in the database.");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(userRole.get());

        // Generate a unique Long ID
        Long uniqueId;
        do {
            uniqueId = UniqueIdGenerator.generateUniqueLongId();
        } while (adminRepository.existsById(uniqueId));  // Check if Long ID exists in the database

        Admin newUser = Admin.builder()
                .id(uniqueId)  // Set the unique Long ID
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .username(registrationRequest.getUserName())
                .email(registrationRequest.getEmail())
                .phoneNumber(registrationRequest.getPhoneNumber())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .roles(roles)
                .build();

        Admin savedUser = adminRepository.save(newUser);

        ConfirmationToken confirmationToken = new ConfirmationToken(savedUser);
        confirmationTokenRepository.save(confirmationToken);
        System.out.println(confirmationToken.getToken());

//        String confirmationUrl = EmailTemplate.getVerificationUrl(baseUrl, confirmationToken.getToken());

//        String confirmationUrl = baseUrl + "/confirmation/confirm-token-sucess.html?token=" + confirmationToken.getToken();
        //String confirmationUrl = "http://localhost:8080/api/v1/auth/confirm?token=" + confirmationToken.getToken();
        String confirmationUrl = "http://localhost:5173/confirm-email?token=" + confirmationToken.getToken();

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

        try{
            Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                            loginRequestDto.getUsername(),
                                            loginRequestDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Admin admin = adminRepository.findByUsername(loginRequestDto.getUsername())
                    .orElseThrow(() -> new CustomException("User not found with username: " + loginRequestDto.getUsername()));


            if (!admin.isEnabled()) {
                throw new CustomException("User account is not enabled. Please check your email to confirm your account.");
            }

            var jwtToken = jwtService.generateToken(admin);
            revokeAllUserTokens(admin);
            saveUserToken(admin, jwtToken);

            return LoginResponse.builder()
                    .responseCode("002")
                    .responseMessage("Login Successfully")
                    .loginInfo(LoginInfo.builder()
                            .username(admin.getUsername())
                            .token(jwtToken)
                            .build())
                    .build();

        }catch (AuthenticationException e) {
            throw new CustomException("Invalid username or password!!", e);
        }

    }

    private void saveUserToken(Admin userModel, String jwtToken) {
        var token = JwtToken.builder()
                .admin(userModel)
                .token(jwtToken)
                .tokenType("BEARER")
                .expired(false)
                .revoked(false)
                .build();
        jwtTokenRepository.save(token);
    }

    private void revokeAllUserTokens(Admin adminModel) {
        var validUserTokens = jwtTokenRepository.findAllValidTokenByUser(adminModel.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        jwtTokenRepository.saveAll(validUserTokens);
    }

    public void resetPassword(PasswordResetDto passwordResetDto) {

        if (!passwordResetDto.getNewPassword().equals(passwordResetDto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException("New password and confirm password do not match.");
        }

        Admin user = adminRepository.findByEmail(passwordResetDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + passwordResetDto.getEmail()));

        if(user.getResetToken() != null){
            return;
        }

        user.setPassword(passwordEncoder.encode(passwordResetDto.getNewPassword()));
        adminRepository.save(user);
    }

    @Override
    public void newResetPassword(PasswordResetDto passwordResetDto) {
        Admin admin = adminRepository.findByEmail(passwordResetDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + passwordResetDto.getEmail()));

        if(admin.getResetToken() != null){
            return;
        }

        admin.setPassword(passwordEncoder.encode(passwordResetDto.getNewPassword()));
        adminRepository.save(admin);
    }

    @Override
    public String editUserDetails(String username, UserDetailsDto userDetailsDto) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        //Update user details
        admin.setFirstName(userDetailsDto.getFirstName());
        admin.setLastName(userDetailsDto.getLastName());
        admin.setEmail(userDetailsDto.getEmail());
        admin.setPhoneNumber(userDetailsDto.getPhoneNumber());

        //save the updated user
        adminRepository.save(admin);

        return "User details updated successfully";
    }

//    @Override
//    public String forgotPassword(ForgetPasswordDto forgetPasswordDto) {
//
//
//        /*
//        steps
//        1- check if email exist (settled)
//        2- create a random token (done)
//        3- Hash the token and add it to the db under the user (done)
//        4- set expiration time for the token in the db (done)
//        5- generate a reset url using the token (done)
//        6- send email with reset url link
//         */
//
//        Optional<Admin> checkUser = adminRepository.findByEmail(forgetPasswordDto.getEmail());
//
//        // check if user exist with that email
//        if(!checkUser.isPresent()) throw new RuntimeException("No such user with this email.");
//
//        Admin forgettingUser = checkUser.get();
//
//        // generate a hashed token
//        ConfirmationToken forgetPassWordToken = new ConfirmationToken(forgettingUser);
//
//        // saved the token.
//        // the token has an expiration date
//        confirmationTokenRepository.save(forgetPassWordToken);
//        // System.out.println("the token "+forgetPassWordToken.getToken());
//
//        // generate a password reset url
//        String resetPasswordUrl = "http://localhost:8080/api/v1/auth/confirm?token=" + forgetPassWordToken.getToken();
//
//
//
//        // click this link to reset password;
//        EmailDetails emailDetails = EmailDetails.builder()
//                .recipient(forgettingUser.getEmail())
//                .subject("FORGET PASSWORD")
//                .messageBody(ForgetPasswordEmailBody.buildEmail(forgettingUser.getFirstName(),
//                        forgettingUser.getLastName(), resetPasswordUrl))
//                .build();
//
//        //send the reset password link
//        emailService.mimeMailMessage(emailDetails);
//
//        return "A reset password link has been sent to your account." + resetPasswordUrl;
//    }

    @Override
    public String forgotPassword(String email) {

        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

        String token = UUID.randomUUID().toString();
        admin.setResetToken(token);
        admin.setTokenCreationDate(LocalDateTime.now());
        adminRepository.save(admin);

        String resetUrl = "http://localhost:5173/reset-password?token=" + token;

        // click this link to reset password;
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(admin.getEmail())
                .subject("FORGET PASSWORD")
                .messageBody(ForgetPasswordEmailBody.buildEmail(admin.getLastName(), admin.getFirstName(), resetUrl ))
                .build();

        //send the reset password link
        emailService.mimeMailMessage(emailDetails);

        return "A reset password link has been sent to your account email address:          " + resetUrl;
    }

    public boolean existsByEmail(String email) {
        return adminRepository.existsByEmail(email);
    }

    public Admin findByResetToken(String token) {
        return adminRepository.findByResetToken(token).orElse(null);
    }

    public void updatePassword(String currentUsername, String newPassword) {

        // GET ADMIN ID BY USERNAME
        Optional<Admin> currentAdmin = adminRepository.findByUsername(currentUsername);

        // Check if admin is present
        if (currentAdmin.isEmpty()) {
            throw new RuntimeException("Admin user not found");
        }

        Admin admin = currentAdmin.get();

        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
    }

    @Override
    public String createToken(Admin admin) {

        var jwtToken = jwtService.generateToken(admin);
        revokeAllUserTokens(admin);
        saveUserToken(admin, jwtToken);

        return jwtToken;
    }


    /////------ USER/EMPLOYEE RELATED SERVICE IMPLEMENTATIONS -----\\\\\


    // USER/EMPLOYEE REGISTRATION
    @Override
    public UserRegistrationResponse registerUser(String currentUsername, UserRegistrationDto userRegistrationDto) throws MessagingException {
        // GET ADMIN ID BY USERNAME
        Optional<Admin> loggedInAdmin = adminRepository.findByUsername(currentUsername);

        // Check if admin is present
        if (loggedInAdmin.isEmpty()) {
            throw new RuntimeException("Admin user not found");
        }

        Department currentDepartment = departmentRepository.findById(userRegistrationDto.getDepartmentId()).orElse(null);

        if(currentDepartment == null){
            throw new DoesNotExistException("Department does not exist");
        }

        // Check if the user email already exists
        Optional<User> existingUser = userRepository.findByEmail(userRegistrationDto.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User email already exists");
        }

        Optional<Role> userRole = roleRepository.findByName("USER");
        if (userRole.isEmpty()) {
            throw new RuntimeException("Default role USER not found in the database.");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(userRole.get());

        // Generate username and password
        String generatedUsername = generateUserName(userRegistrationDto.getFullName());
        String generatedPassword = generatePassword();

        // Build new User entity
        User newUser = User.builder()
                .fullName(userRegistrationDto.getFullName())
                .email(userRegistrationDto.getEmail())
                .phoneNumber(userRegistrationDto.getPhoneNumber())
                .jobTitle(userRegistrationDto.getJobTitle())
                //.department(userRegistrationDto.getDepartment())
                .employeeDepartment(currentDepartment)
                .username(generatedUsername)
                .password(passwordEncoder.encode(generatedPassword))
                .createdUnder(loggedInAdmin.get().getId())
                .roles(roles)
                .build();

        // Save new user to the repository
        User savedUser = userRepository.save(newUser);

        // Set up email message for the registered user/employee
        String userLoginUrl = baseUrl + "/user/login";

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ESCALAYT LOGIN DETAILS")
                .messageBody(UserRegistrationEmailBody.buildEmail(savedUser.getFullName(),
                        savedUser.getUsername(), generatedPassword, userLoginUrl))
                .build();

        // Send email message to the registered user/employee
        emailService.mimeMailMessage(emailDetails);

        // Method response
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
                //.department(savedUser.getDepartment())
                .createdUnder(savedUser.getCreatedUnder())
                .build();
    }



    private static String generateUserName(String fullName) {
        String firstFourLetters = fullName.replaceAll("\\s+", "").substring(0, Math.min(fullName.length(), 4)).toLowerCase();
        int randomNumbers = new Random().nextInt(900) + 100; // 3-digit random number
        return firstFourLetters + randomNumbers;
    }

    private static String generatePassword() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }


    // new endpoint to get user detail
    @Override
    public AdminUserDetailsDto getAdminDetails(String username) {

        Admin admin = adminRepository.findByUsername(username).orElse(null);

        if(admin == null){
            throw new UserNotFoundException("admin not found");
        }
        return AdminUserDetailsDto.builder()
                .id(admin.getId())
                .fullName(admin.getFirstName() +" "+ admin.getLastName())
                .username(admin.getUsername())
                .phoneNumber(admin.getPhoneNumber())
                .email(admin.getEmail())
                .build();
    }


    // get the list of users
    @Override
    public List<AdminUserDetailsDto> getAllEmployee(String username) {

        Admin admin = adminRepository.findByUsername(username).orElse(null);

        List<User> userList = userRepository.findAllByCreatedUnder(admin.getId());
        if(admin == null){
            throw new UserNotFoundException("admin not found");
        }

        List<AdminUserDetailsDto> userDetailsDto = userList.stream()
                .map(user ->
                   new AdminUserDetailsDto(
                           user.getId(),
                            user.getUsername(),
                            user.getFullName(),
                            user.getEmail(),
                            user.getPictureUrl(),
                            user.getJobTitle(),
                           user.getEmployeeDepartment().getDepartment(),
                           user.getPhoneNumber()
                    ))
                .collect(Collectors.toList());

        return userDetailsDto;
    }

    @Override
    public AdminUserDetailsDto editUserDetailsByAdmin( Long departmentId, AdminEditUserRequestDto adminUserDetailsDto) {

        User user = userRepository.findById(adminUserDetailsDto.getId()).orElse(null);
        Department department = departmentRepository.findById(departmentId).orElse(null);

        if(user == null){
            throw new UserNotFoundException("user not found for real");
        }
        if(department == null){
            throw new DoesNotExistException("Department does not exist");
        }

        user.setEmail(adminUserDetailsDto.getEmail());
        user.setUsername(adminUserDetailsDto.getUsername());
        user.setJobTitle(adminUserDetailsDto.getJobTitle());
        user.setEmployeeDepartment(department);
        user.setPassword(passwordEncoder.encode(adminUserDetailsDto.getPassword()));

        User updatedUser = userRepository.save(user);

        return AdminUserDetailsDto.builder()
                .username(updatedUser.getUsername())
                .build();
    }

    @Override
    public AdminUserDetailsDto editAdminDetails(String username, AdminDetailsRequestDto requestDto) {

        Admin admin = adminRepository.findByUsername(username).orElse(null);

        if(admin == null){
            throw new UserNotFoundException("Admin not found");
        }


        admin.setFirstName(requestDto.getFirstName());
        admin.setLastName(requestDto.getLastName());
        admin.setEmail(requestDto.getEmail());
        admin.setPhoneNumber(requestDto.getPhoneNumber());
        admin.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        Admin newAdmin = adminRepository.save(admin);

        return AdminUserDetailsDto.builder()
                .fullName(newAdmin.getFirstName()+" "+newAdmin.getLastName())
                .build();
    }

}
/*
 employeeId: employeeId,
      email: emailRef.current.value,
      username: usernameRef.current.value,
      jobTitle: jobTitleRef.current.value,
      departmentId: departmentId,
      password: passwordRef.current.value,
 */