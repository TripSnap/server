package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.Notification;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomNotificationRepository {
    List<Notification> getNotificationList(Pageable pageable, Long memberId);
    void readNotification(Long memberId);
    boolean existNewNotification(Long memberId);
}
