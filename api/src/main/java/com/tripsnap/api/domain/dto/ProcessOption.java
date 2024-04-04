package com.tripsnap.api.domain.dto;

public enum ProcessOption {
    allow, deny;

    public boolean isAllow() {
        return this == ProcessOption.allow;
    }
}
