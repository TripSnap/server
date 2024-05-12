package com.tripsnap.api.domain.dto;

public record NotificationDTO(
        Long id,
        String title,
        Boolean isRead,
        Boolean isBroadCast,
        String date
) {
}
