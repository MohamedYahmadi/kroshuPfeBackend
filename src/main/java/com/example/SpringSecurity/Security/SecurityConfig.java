    package com.example.SpringSecurity.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailServices;
    private final JwtAuthentificationFilter jwtAuthentificationFilter;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailServices, JwtAuthentificationFilter jwtAuthentificationFilter) {
        this.customUserDetailServices = customUserDetailServices;
        this.jwtAuthentificationFilter = jwtAuthentificationFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(security ->{
                    security.requestMatchers("/api/user/login","/api/user/signup").permitAll();
                    security.anyRequest().permitAll();


                })
                .addFilterBefore(jwtAuthentificationFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(customUserDetailServices);
        return authProvider;
    }

}