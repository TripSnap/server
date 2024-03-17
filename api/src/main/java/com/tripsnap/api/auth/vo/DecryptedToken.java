package com.tripsnap.api.auth.vo;

public record DecryptedToken(String email, String role, boolean expired) {
    public DecryptedToken(String email, String role) {
        this(email,role,false);
    }
}
