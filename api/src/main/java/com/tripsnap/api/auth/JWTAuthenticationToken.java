package com.tripsnap.api.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;


public class JWTAuthenticationToken extends AbstractAuthenticationToken {

    private String email;

    public JWTAuthenticationToken(TokenData tokenData) {
        super(List.of(new SimpleGrantedAuthority(tokenData.role())));
        this.email = tokenData.email();
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return email;
    }
}
