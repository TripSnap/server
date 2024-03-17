package com.tripsnap.api.auth;

import com.tripsnap.api.auth.exception.AccessTokenExpiredException;
import com.tripsnap.api.auth.login.JWTAuthenticationToken;
import com.tripsnap.api.auth.vo.DecryptedToken;
import com.tripsnap.api.auth.vo.TokenData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class JWTFilter extends OncePerRequestFilter {

    private TokenService tokenService;

    @Autowired
    private void init(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(hasAccessToken(request)) {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            String token = header.replaceFirst(TokenService.TOKEN_TYPE, "");

            DecryptedToken decryptedToken = tokenService.verifyAccessToken(token);

            if(decryptedToken.expired()) {
                try {
                    if(hasRefreshToken(request)) {
                        String refreshToken = request.getParameter("token");
                        if(tokenService.verifyRefreshToken(refreshToken, decryptedToken.email()) ) {
                            String accessToken = tokenService.createAccessToken(decryptedToken.email(), decryptedToken.role());
                            tokenService.setAccessTokenToResponse(accessToken, response);
                        }
                    } else {
                        throw new AccessTokenExpiredException();
                    }
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            setAuthentication(decryptedToken);
        }

        filterChain.doFilter(request, response);
    }

    private boolean hasRefreshToken(HttpServletRequest request) throws URISyntaxException {
        String grantType = request.getParameter("grant_type");
        String token = request.getParameter("token");
        URI uri = new URI(request.getRequestURI());
        return "/refresh".equals(uri.getPath())
                && "refresh_token".equals(grantType) && StringUtils.hasText(token);
    }

    private boolean hasAccessToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return StringUtils.hasText(header) && StringUtils.startsWithIgnoreCase(header, TokenService.TOKEN_TYPE);
    }

    private void setAuthentication(DecryptedToken tokenData) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new JWTAuthenticationToken(new TokenData(tokenData.email(), tokenData.role())));
        SecurityContextHolder.setContext(context);
    }
}
