package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, CustomNotificationRepository {
    void deleteByMemberIdAndIdIn(Long memberId, List<Long> ids);
}
