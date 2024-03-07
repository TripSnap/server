package com.tripsnap.api.auth;

public record DecryptedToken(boolean valid, boolean expired, TokenData tokenData) {

    public DecryptedToken(boolean valid) {
        this(valid, false, null);
    }
}
