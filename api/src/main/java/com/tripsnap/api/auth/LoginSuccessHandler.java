package com.tripsnap.api.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Optional;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private TokenService tokenService;

    @Autowired
    public void init(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        String email = String.valueOf(user.getUsername());
        Optional<? extends GrantedAuthority> authority = authentication.getAuthorities().stream().findFirst();
        String role = authority.map(grantedAuthority -> String.valueOf(grantedAuthority.getAuthority())).orElse(null);

        String accessToken = tokenService.createAccessToken(email, role);
        String refreshToken = tokenService.createRefreshToken(email);
        tokenService.setAccessTokenToResponse(accessToken, response);
        tokenService.setRefreshTokenToResponse(refreshToken, response);
    }
}
