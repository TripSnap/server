package com.tripsnap.api.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class AccessTokenExpiredException extends AuthenticationException {
    public AccessTokenExpiredException() {
        super("access token is expired");
    }
}
