package com.tripsnap.api.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tripsnap.api.domain.entity.Friend;
import com.tripsnap.api.domain.entity.QFriend;
import com.tripsnap.api.domain.entity.QMember;
import com.tripsnap.api.domain.entity.key.MemberFriendId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class CustomFriendRepositoryImpl implements CustomFriendRepository {
    @PersistenceContext
    private EntityManager em;

    private final FriendRepository friendRepository = (FriendRepository) this;

    @Transactional(readOnly = true)
    @Override
    public List<Friend> getFriendListByEmail(long memberId, List<String> emails) {
        QMember member = QMember.member;
        QFriend friend = QFriend.friend;

        JPAQuery<Friend> query = new JPAQuery<>(em);

        List<Friend> friends = query.select(friend).from(member).join(friend)
                .on(member.email.in(emails), friend.id.memberId.eq(memberId), friend.id.friendId.eq(member.id))
                .fetch();

        return friends;
    }

    @Transactional
    @Override
    public Boolean createFriend(long memberId, long friendId) {

        MemberFriendId id1 = MemberFriendId.builder().memberId(memberId).friendId(friendId).build();
        MemberFriendId id2 = MemberFriendId.builder().memberId(friendId).friendId(memberId).build();

        friendRepository.save(Friend.builder().id(id1).build());
        friendRepository.save(Friend.builder().id(id2).build());

        return true;
    }

    @Transactional
    @Override
    public Boolean removeFriend(long memberId, long friendId) {
        QFriend friend = QFriend.friend;

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        queryFactory.delete(friend).where(friend.id.memberId.eq(memberId), friend.id.friendId.eq(friendId)).execute();
        queryFactory.delete(friend).where(friend.id.memberId.eq(friendId), friend.id.friendId.eq(memberId)).execute();

        return true;
    }
}
