package com.example.SpringSecurity.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthentificationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailServices;
    @Autowired
    public JwtAuthentificationFilter(JwtService jwtService, CustomUserDetailsService customUserDetailServices) {
        this.jwtService = jwtService;
        this.customUserDetailServices = customUserDetailServices;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String jwt =request.getHeader("Authorization");
        if (jwt !=null){
            String email =jwtService.extractEmail(jwt);
            if (email !=null){
                UserDetails userDetails =customUserDetailServices.loadUserByUsername(email);
                if (jwtService.validateToken(jwt, userDetails.getUsername())){
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request,response);

    }
}
