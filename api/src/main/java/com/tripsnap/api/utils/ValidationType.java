package com.tripsnap.api.utils;

import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

public enum ValidationType {
    Email(EmailType.class);

    ValidationType(Class<?> clazz) {
        this._class = clazz;
    }

    public final Class<?> _class;

    @RequiredArgsConstructor
    private static class EmailType{
        @Email
        final private String value;
    };
}
