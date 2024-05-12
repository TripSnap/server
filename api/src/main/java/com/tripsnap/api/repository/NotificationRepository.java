package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>, CustomNotificationRepository {
    void deleteByMemberIdAndId(Long memberId, Long id);
    void deleteAllByMemberId(Long memberId);
}
