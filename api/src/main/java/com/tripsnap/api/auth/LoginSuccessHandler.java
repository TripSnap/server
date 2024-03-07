package com.tripsnap.api.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        response.setHeader(HttpHeaders.AUTHORIZATION,
                JWTUtil.TOKEN_TYPE +
                JWTUtil.createToken(
                        new TokenData(
                                authentication.getName(),
                                authentication.getAuthorities().stream().findFirst().get().getAuthority()
                        )
                )
        );
    }
}
