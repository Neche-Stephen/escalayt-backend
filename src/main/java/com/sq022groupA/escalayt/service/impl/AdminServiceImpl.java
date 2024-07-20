package com.sq022groupA.escalayt.service.impl;

import com.sq022groupA.escalayt.entity.model.*;
import com.sq022groupA.escalayt.repository.AdminRepository;
import com.sq022groupA.escalayt.repository.ConfirmationTokenRepository;
import com.sq022groupA.escalayt.repository.JwtTokenRepository;
import com.sq022groupA.escalayt.repository.RoleRepository;
import com.sq022groupA.escalayt.exception.PasswordsDoNotMatchException;
import com.sq022groupA.escalayt.exception.UserNotFoundException;
import com.sq022groupA.escalayt.exception.UsernameAlreadyExistsException;
import com.sq022groupA.escalayt.payload.request.*;
import com.sq022groupA.escalayt.payload.response.EmailDetails;
import com.sq022groupA.escalayt.payload.response.LoginInfo;
import com.sq022groupA.escalayt.payload.response.LoginResponse;
import com.sq022groupA.escalayt.service.EmailService;
import com.sq022groupA.escalayt.service.AdminService;
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
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
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


        Admin newUser = Admin.builder()
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
        Admin admin = adminRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + loginRequestDto.getUsername()));

        if (!admin.isEnabled()) {
            throw new RuntimeException("User account is not enabled. Please check your email to confirm your account.");
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

        Optional<Admin> checkUser = adminRepository.findByEmail(forgetPasswordDto.getEmail());

        // check if user exist with that email
        if(!checkUser.isPresent()) throw new RuntimeException("No such user with this email.");

        Admin forgettingUser = checkUser.get();

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

    @Service
    public static class JwtService {
        private final static String SECRET_KEY =
                "JpLx8hyycP9RwoEJ+0sSj3p4xsIBmfYe4vVbequytgVfTqXN93NcaTlAVo9y3fpC" +
                        "" +
                        "DstegCKTDKFcU30iPKiRbQ==";

        // Extract all claims
        private Claims extractAllClaims(String token){

            return Jwts
                    .parser()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        }


        private Key getSignInKey() {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

            return Keys.hmacShaKeyFor(keyBytes);
        }

        // extract single claims
        public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
            final Claims claims = extractAllClaims(token);

            return claimsResolver.apply(claims);
        }

        public String extractUsername(String token){
            return extractClaim(token, Claims::getSubject);
        }

        // method to generate token

        public String generateToken(Map<String, Object> extractClaims, UserDetails userDetails){
            extractClaims.put("roles", userDetails.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .collect(Collectors.toList()));

            return Jwts
                    .builder()
                    .setClaims(extractClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() +
                            1000 * 60 * 60 * 24
                    ))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        }

        public String generateToken(UserDetails userDetails){
            return (generateToken(new HashMap<>(), userDetails));
        }

        // Check if the token is valid

        public Boolean isTokenValid(String token, UserDetails userDetails){
            final String userName = extractUsername(token);

            return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
        }

        private boolean isTokenExpired(String token) {
            return  extractExpiration(token).before(new Date());
        }

        private Date extractExpiration(String token){
            return extractClaim(token, Claims::getExpiration);
        }

        // Extract roles from the token
        public List<String> extractRoles(String token) {
            Claims claims = extractAllClaims(token);
            return claims.get("roles", List.class);
        }

    }
}
