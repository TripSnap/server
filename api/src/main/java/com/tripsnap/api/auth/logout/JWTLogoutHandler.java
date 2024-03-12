package com.tripsnap.api.auth.logout;

import com.tripsnap.api.auth.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@RequiredArgsConstructor
@Component
public class JWTLogoutHandler implements LogoutHandler {

    private final TokenService tokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if(authentication != null) {
            String email = String.valueOf(authentication.getPrincipal());
            if(StringUtils.hasText(email)) {
                String expireAccessToken = tokenService.expireAccessToken(email);
                tokenService.setAccessTokenToResponse(expireAccessToken, response);
                tokenService.removeRefreshToken(email);
            }
        }
    }
}
