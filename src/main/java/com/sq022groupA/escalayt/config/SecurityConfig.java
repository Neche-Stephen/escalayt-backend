package com.sq022groupA.escalayt.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        security.csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(
                        requests -> requests
                                .requestMatchers(antMatcher(HttpMethod.POST, "/api/v1/auth/**"),
                                        antMatcher(HttpMethod.POST, "/api/v1/auth/password-reset"),
                                        antMatcher(HttpMethod.GET, "/api/v1/auth/**"),
                                        antMatcher(HttpMethod.GET, "/swagger-ui.html"),
                                        antMatcher(HttpMethod.GET, "/swagger-ui/**"),
                                        antMatcher(HttpMethod.GET, "/v3/api-docs/**"),
                                        antMatcher(HttpMethod.GET, "/swagger-resources/**"),
                                        antMatcher(HttpMethod.POST, "/token/**")
                                ).permitAll()
                                .requestMatchers("/api/v1/ticket/**").hasAnyAuthority("USER", "ADMIN")
                                .requestMatchers("/api/v1/admin/**").hasAnyAuthority("ADMIN")
                                .requestMatchers("/api/v1/users/**").hasAnyAuthority("USER")
                                .anyRequest().authenticated()

                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider)
                .formLogin(form -> form
                        .failureHandler(customAuthenticationFailureHandler))
                .cors(customizer -> customizer.configurationSource(corsConfigurationSource()));

        return security.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Set your allowed origins here
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // Set your allowed HTTP methods
        configuration.setAllowedHeaders(Arrays.asList("*")); // Set your allowed headers (e.g., Content-Type, Authorization)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply CORS configuration to all paths

        return source;
    }

}
