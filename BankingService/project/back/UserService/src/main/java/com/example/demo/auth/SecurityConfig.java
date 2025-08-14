package com.example.demo.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity // Enables Spring Security web support
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService; // Service to load user details from DB

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter; // Custom filter for JWT validation

    // Bean for AuthenticationManager (used for authentication process)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Bean for AuthenticationProvider (handles user authentication using UserDetailsService and password encoder)
    @Bean
    public AuthenticationProvider authenticationProvider() {
        System.out.println("AuthenticationProvider");
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService); // Load user by username
        authenticationProvider.setPasswordEncoder(passwordEncoder()); // Encode & verify password
        return authenticationProvider;
    }

    // Bean for SecurityFilterChain (main Spring Security configuration)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("securityFilterChain");
        return http

                // Enable CORS with default configuration
                .cors(withDefaults())

                // Disable CSRF because we're using stateless JWT
                .csrf(csrf -> csrf.disable())

                // Define public and protected routes
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/rest/auth/**", "/verify",
                                "/regenerateOtp", "/validation",
                                "/swagger-ui/**", "/v3/api-docs/**"
                        ).permitAll() // Allow without authentication
                        .anyRequest().authenticated() // All other routes require authentication
                )

                // Set session to stateless (no HTTP session will be used)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Use custom authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT filter before username/password filter
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    // Bean for password encoding using BCrypt
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        System.out.println("BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    // Bean for CORS configuration (Cross-Origin Resource Sharing)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow frontend origin (Angular localhost:4200)
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Allowed HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow sending cookies/auth headers
        configuration.setAllowCredentials(true);

        // Allow client to read Authorization header
        configuration.setExposedHeaders(List.of("Authorization"));

        // Apply configuration to all routes
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
