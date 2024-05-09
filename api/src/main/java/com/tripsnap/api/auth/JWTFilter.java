package com.tripsnap.api.auth;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tripsnap.api.auth.exception.AccessTokenExpiredException;
import com.tripsnap.api.auth.login.JWTAuthenticationToken;
import com.tripsnap.api.auth.vo.DecryptedToken;
import com.tripsnap.api.auth.vo.TokenData;
import com.tripsnap.api.exception.ServiceException;
import com.tripsnap.api.filter.MultiReadHttpServletRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class JWTFilter extends OncePerRequestFilter {

    private TokenService tokenService;
    private final Gson gson = new Gson();

    @Autowired
    private void init(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        tokenService.getAccessTokenCookie(request).ifPresent((cookie -> {
            String accessToken = cookie.getValue();
            DecryptedToken decryptedToken = tokenService.verifyAccessToken(accessToken);

            if(decryptedToken.expired()) {
                try {
                    if(isRefreshRequest(request)) {
                        Map<String,String> refreshParam= refreshParameterValidation(request);
                        String refreshToken = refreshParam.get("token");
                        if(tokenService.verifyRefreshToken(refreshToken, decryptedToken.email()) ) {
                            String newAccessToken = tokenService.createAccessToken(decryptedToken.email(), decryptedToken.role());
                            tokenService.setAccessTokenToResponse(newAccessToken, response);
                        }
                    } else {
                        throw new AccessTokenExpiredException();
                    }
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            setAuthentication(decryptedToken);
        }));

        filterChain.doFilter(request, response);
    }

    private boolean isRefreshRequest(HttpServletRequest request) throws URISyntaxException {

        URI uri = new URI(request.getRequestURI());
        return "/refresh".equals(uri.getPath());
    }

    private Map<String, String> refreshParameterValidation(HttpServletRequest request) {
        try {
            MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest(request);
            Map<String, String> map = gson.fromJson(multiReadRequest.getBodyJson(), new TypeToken<Map<String, Object>>(){}.getType());
            return map;
        } catch (NullPointerException e) {
            throw ServiceException.BadRequestException();
        }
    }

    private String getAccessToken(HttpServletRequest request) {
        Optional<Cookie> optionalCookie = Arrays.stream(request.getCookies()).filter((cookie) -> "access-token".equals(cookie.getName())).findFirst();
        Optional<String> token = optionalCookie.map(Cookie::getValue);
        return token.orElse(null);
    }

    private void setAuthentication(DecryptedToken tokenData) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new JWTAuthenticationToken(new TokenData(tokenData.email(), tokenData.role())));
        SecurityContextHolder.setContext(context);
    }
}
