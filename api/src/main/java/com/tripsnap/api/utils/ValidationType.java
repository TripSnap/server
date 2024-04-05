package com.tripsnap.api.utils;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

public enum ValidationType {
    Email(EmailType.class), EntityId(EntityIdType.class);

    ValidationType(Class<?> clazz) {
        this._class = clazz;
    }

    public final Class<?> _class;

    @RequiredArgsConstructor
    private static class EmailType{
        @Email
        final private String value;
    };

    @RequiredArgsConstructor
    private static class EntityIdType {
        @Positive @NotNull
        final private Long id;
    }
}
