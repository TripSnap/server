package com.tripsnap.api.domain.dto;

public record MemberPasswordEditDTO(
        String password,
        String newPassword
) {
}
