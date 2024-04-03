package com.tripsnap.api.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tripsnap.api.domain.entity.Notification;
import com.tripsnap.api.domain.entity.QNotification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class CustomNotificationRepositoryImpl implements CustomNotificationRepository{
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Notification> getNotificationList(Pageable pageable, Long memberId) {
        QNotification notification = QNotification.notification;
        JPAQuery<Notification> query = new JPAQuery<>(em);
        List<Notification> notifications = query.select(notification).from(notification)
                .where(notification.memberId.in(0, memberId))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
        return notifications;
    }

    @Override
    public void readNotification(Long memberId) {
        QNotification notification = QNotification.notification;
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        queryFactory.update(notification).set(notification.isRead, true)
                .where(notification.memberId.eq(memberId),notification.isRead.isFalse())
                .execute();
    }

    @Override
    public boolean existNewNotification(Long memberId) {
        QNotification notification = QNotification.notification;
        JPAQuery<Notification> query = new JPAQuery<>(em);
        List<Notification> notifications = query.select(notification).from(notification)
                .where(notification.memberId.in(0, memberId), notification.isRead.isFalse())
                .limit(1L).fetch();
        return !notifications.isEmpty();
    }
}
