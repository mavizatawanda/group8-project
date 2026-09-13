package com.devops.qvs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth ->
                auth
                    // Public UI pages and static assets
                    .requestMatchers("/", "/verify", "/login", "/dashboard", "/audit", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                    // H2 console & Swagger docs & Actuator
                    .requestMatchers("/h2-console/**", "/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/actuator/**").permitAll()
                    // Public verification and bonus API endpoints
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/verify").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/verify/**").permitAll()
                    .requestMatchers("/api/blockchain/**").permitAll()
                    .requestMatchers("/api/ai/**").permitAll()
                    .requestMatchers("/api/metrics/**").permitAll()
                    // Protected endpoints
                    .requestMatchers(HttpMethod.GET, "/api/v1/qualifications/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/qualifications/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_INSTITUTION")
                    .requestMatchers(HttpMethod.PUT, "/api/v1/qualifications/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_INSTITUTION")
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/qualifications/**").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/api/v1/audit-logs/**").permitAll()
                    .anyRequest().authenticated()
            );

        // Required for H2 Console frame display
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
