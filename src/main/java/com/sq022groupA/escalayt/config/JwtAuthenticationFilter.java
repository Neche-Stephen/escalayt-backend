package com.sq022groupA.escalayt.config;

import com.sq022groupA.escalayt.repository.JwtTokenRepository;
import com.sq022groupA.escalayt.exception.ExpiredJwtTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    private final JwtTokenRepository jTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        final String username;

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        try{
            // extract token from authHeader
            jwt = authHeader.substring(7);
            // extract the email from the jwt service
            userEmail = jwtService.extractUsername(jwt);
            username = jwtService.extractUsername(jwt);

            if(username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null){

                UserDetails userDetails =
                        this.userDetailsService.loadUserByUsername(username);
                var isTokenValid = jTokenRepository.findByToken(jwt)
                        .map(t -> !t.isExpired() && !t.isRevoked())
                        .orElse(false);

                if(jwtService.isTokenValid(jwt, userDetails) && isTokenValid){
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken
                                    (userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtTokenException e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token expired");
        }
        catch (IOException | ServletException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token is not valid");
        }
    }
}

