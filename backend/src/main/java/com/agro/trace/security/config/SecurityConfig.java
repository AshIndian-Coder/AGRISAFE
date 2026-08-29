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

                // NOTE: DB stores role names WITH ROLE_ prefix (e.g. ROLE_FARMER).
                // hasRole("X") adds ROLE_ prefix → would look for ROLE_ROLE_X.
                // So we use hasAuthority() to match the exact DB value.

                // Farmer endpoints
                .requestMatchers("/farmer/**").hasAuthority("ROLE_FARMER")

                // Agent endpoints
                .requestMatchers("/agents/**").hasAnyAuthority("ROLE_COLLECTING_AGENT", "ROLE_TESTING_AGENT", "ROLE_NODAL_CENTER_AGENT")

                // Supplier endpoints
                .requestMatchers("/suppliers/**").hasAuthority("ROLE_SUPPLIER")

                // Nodal center endpoints
                .requestMatchers("/nodal-centers/**").hasAuthority("ROLE_NODAL_CENTER_AGENT")

                // Manufacturer endpoints
                .requestMatchers("/manufacturers/**").hasAuthority("ROLE_MANUFACTURER_EMPLOYEE")

                // Distributor endpoints
                .requestMatchers("/distributors/**").hasAnyAuthority("ROLE_DISTRIBUTOR_EMPLOYEE")

                // Retailer endpoints
                .requestMatchers("/retailers/**").hasAuthority("ROLE_RETAILER")

                // Government endpoints
                .requestMatchers("/government/**").hasAnyAuthority("ROLE_GOVERNMENT_EMPLOYEE", "ROLE_GOVERNMENT_INVESTIGATOR")

                // Lot endpoints
                .requestMatchers("/lots/**").authenticated()

                // QR image is public (just a PNG — real security is the rotating code)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/qr/*/image").permitAll()
                // QR management endpoints require auth
                .requestMatchers("/qr/**").authenticated()

                // Testing endpoints
                .requestMatchers("/testing/**").hasAnyAuthority("ROLE_TESTING_AGENT", "ROLE_GOVERNMENT_EMPLOYEE")

                // Organization endpoints
                .requestMatchers("/organizations/**").authenticated()

                // Product endpoints
                .requestMatchers("/products/**").permitAll()

                // Complaints
                .requestMatchers("/complaints/**").authenticated()

                // Actuator
                .requestMatchers("/actuator/**").hasAuthority("ROLE_SYSTEM_ADMIN")

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