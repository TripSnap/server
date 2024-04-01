package com.tripsnap.api.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.tripsnap.api.domain.entity.Friend;
import com.tripsnap.api.domain.entity.QFriend;
import com.tripsnap.api.domain.entity.QMember;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class CustomFriendRepositoryImpl implements CustomFriendRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Friend> getFriendList(long memberId, List<String> emails) {
        QMember member = QMember.member;
        QFriend friend = QFriend.friend;

        JPAQuery<Friend> query = new JPAQuery<>(em);

        List<Friend> friends = query.select(friend).from(member).join(friend)
                .on(member.email.in(emails), friend.id.memberId.eq(memberId), friend.id.friendId.eq(member.id))
                .fetch();

        return friends;
    }
}
