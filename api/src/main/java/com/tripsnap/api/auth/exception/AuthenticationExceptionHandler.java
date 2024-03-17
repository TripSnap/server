package com.tripsnap.api.auth.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if(authException instanceof AccessTokenExpiredException) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Refresh-Token");
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        }
    }
}
