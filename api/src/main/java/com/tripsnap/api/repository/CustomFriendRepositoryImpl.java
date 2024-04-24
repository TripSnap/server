package com.tripsnap.api.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tripsnap.api.domain.entity.*;
import com.tripsnap.api.domain.entity.key.MemberFriendId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CustomFriendRepositoryImpl implements CustomFriendRepository {
    @PersistenceContext
    private EntityManager em;

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

        Friend friend1 = Friend.builder().id(id1).build();
        Friend friend2 = Friend.builder().id(id2).build();

        em.persist(friend1);
        em.persist(friend2);

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

    @Override
    public void removeFriendAndRequestAll(long memberId) {
        QFriend friend = QFriend.friend;
        QFriendRequest friendRequest = QFriendRequest.friendRequest;

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        queryFactory.delete(friend).where(
                // 1) friend_id column 이 {memberId}인 데이터 (상대방 데이터)
                friend.id.memberId.in(
                        // member의 friend_id 가져옴
                        JPAExpressions
                                .select(friend.id.friendId).from(friend)
                                .where(friend.id.memberId.eq(memberId))
                        )
                        .and(friend.id.friendId.eq(memberId))

//                        2) member_id column 이 {memberId}인 데이터
                    .or(friend.id.memberId.eq(memberId))
        );

    }

    @Transactional
    @Override
    public List<Friend> getFriendsByMemberId(long offset, long limit, Long memberId) {
        QFriend friend = QFriend.friend;
        JPAQuery<Friend> query = new JPAQuery<>(em);
        List<Friend> friends = query.select(friend).from(friend).where(friend.id.memberId.eq(memberId))
                .offset(offset).limit(limit).fetch();

        return friends;
    }

    @Transactional
    @Override
    public Page<FriendRequest> getFriendSendRequestsByMemberId(Pageable pageable, Long memberId) {
        QFriendRequest friendRequest = QFriendRequest.friendRequest;

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<FriendRequest> friendRequests = queryFactory.select(friendRequest).from(friendRequest).where(friendRequest.id.memberId.eq(memberId))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
        Long requestAllCount = queryFactory.select(friendRequest.count()).from(friendRequest).where(friendRequest.id.memberId.eq(memberId)).fetchOne();

        return new PageImpl<>(friendRequests, pageable, requestAllCount);
    }

    @Transactional
    @Override
    public Page<Member> getFriendReceiveRequestsByMemberId(Pageable pageable, Long memberId) {
        QFriendRequest friendRequest = QFriendRequest.friendRequest;
        QMember member = QMember.member;

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        Long requestAllCount = queryFactory.select(friendRequest.count()).from(friendRequest).where(friendRequest.id.friendId.eq(memberId)).fetchOne();
        // memberId에게 온 친구 신청 리스트
        List<FriendRequest> friendRequests = queryFactory.select(friendRequest).from(friendRequest).where(friendRequest.id.friendId.eq(memberId))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        // memberId에게 친구 신청 보낸 id 리스트
        List<Long> userIds = friendRequests.stream().map(request -> request.getId().getMemberId()).toList();

        List<Member> receiveMembers = queryFactory.select(member).from(member).where(member.id.in(userIds)).fetch();

        return new PageImpl<>(receiveMembers, pageable, requestAllCount);
    }
}
