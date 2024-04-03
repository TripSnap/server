package com.tripsnap.api.domain.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record RemoveNotificationDTO (
        @Size(max=20)
        List<Long> ids
) {
}
