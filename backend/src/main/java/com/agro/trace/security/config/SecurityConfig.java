package com.agro.trace.security.config;

import com.agro.trace.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final SecurityProperties securityProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                // Farmer endpoints
                .requestMatchers("/farmer/**").hasRole("FARMER")

                // Agent endpoints
                .requestMatchers("/agents/**").hasAnyRole("COLLECTING_AGENT", "TESTING_AGENT", "NODAL_CENTER_AGENT")

                // Supplier endpoints
                .requestMatchers("/suppliers/**").hasRole("SUPPLIER")

                // Manufacturer endpoints
                .requestMatchers("/manufacturers/**").hasRole("MANUFACTURER_EMPLOYEE")

                // Distributor endpoints
                .requestMatchers("/distributors/**").hasAnyRole("DISTRIBUTOR_EMPLOYEE")

                // Retailer endpoints
                .requestMatchers("/retailers/**").hasRole("RETAILER")

                // Government endpoints
                .requestMatchers("/government/**").hasAnyRole("GOVERNMENT_EMPLOYEE", "GOVERNMENT_INVESTIGATOR")

                // Lot endpoints
                .requestMatchers("/lots/**").authenticated()

                // QR endpoints
                .requestMatchers("/qr/**").authenticated()

                // Testing endpoints
                .requestMatchers("/testing/**").hasAnyRole("TESTING_AGENT", "GOVERNMENT_EMPLOYEE")

                // Organization endpoints
                .requestMatchers("/organizations/**").authenticated()

                // Product endpoints
                .requestMatchers("/products/**").permitAll()

                // Complaints
                .requestMatchers("/complaints/**").authenticated()

                // Actuator
                .requestMatchers("/actuator/**").hasRole("SYSTEM_ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Idempotency-Key", "X-Correlation-Id"));
        configuration.setExposedHeaders(Arrays.asList("X-Request-Id", "X-Correlation-Id"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use Argon2id for PIN hashing
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}