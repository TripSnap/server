package com.tripsnap.api.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasLength(header) && StringUtils.startsWithIgnoreCase(header, JWTUtil.TOKEN_TYPE)) {
            String token = header.replaceFirst(JWTUtil.TOKEN_TYPE, "");
            DecryptedToken decryptedToken = JWTUtil.verify(token);
            if(decryptedToken.valid()) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(new JWTAuthenticationToken(decryptedToken.tokenData()));
                SecurityContextHolder.setContext(context);
            } else {
                throw new BadCredentialsException("bad jwt token.");
            }
        }

        filterChain.doFilter(request, response);
    }
}
