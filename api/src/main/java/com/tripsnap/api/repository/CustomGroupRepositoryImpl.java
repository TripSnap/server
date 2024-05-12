package com.tripsnap.api.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tripsnap.api.domain.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public class CustomGroupRepositoryImpl implements CustomGroupRepository {
    @PersistenceContext
    private EntityManager em;

    // 회원이 가입한 그룹 리스트를 가져온다
    @Transactional(readOnly = true)
    @Override
    public List<Group> getGroupsByMemberId(Pageable pageable, Long memberId) {
        QGroupMember groupMember = QGroupMember.groupMember;
        QGroup group = QGroup.group;

        JPAQuery<GroupMember> query = new JPAQuery<>(em);
        JPAQuery<Group> selectQuery = query.select(group).from(groupMember)
                .innerJoin(group)
                .on(groupMember.id.memberId.eq(memberId), groupMember.id.groupId.eq(group.id));

        if(pageable != null) {
            selectQuery = selectQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }
        List<Group> groups = selectQuery.fetch();

        return groups;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Group> getGroupsByMemberId(Long memberId) {
        return getGroupsByMemberId(null, memberId);
    }


    @Transactional(readOnly = true)
    @Override
    public List<Member> getGroupMembersByGroupId(Pageable pageable, Long groupId) {
        return getGroupMembersByGroupId(pageable.getOffset(), pageable.getPageSize(), groupId);
    }

    // 그룹의 멤버들을 가져온다
    @Transactional(readOnly = true)
    @Override
    public List<Member> getGroupMembersByGroupId(long offset, long limit, Long groupId) {
        QGroupMember groupMember = QGroupMember.groupMember;
        QMember member = QMember.member;

        // TODO: select 부분 Projections로 변경하기
        JPAQuery<GroupMember> query = new JPAQuery<>(em);
        List<Member> members = query.select(member).from(groupMember)
                .innerJoin(member)
                .on(groupMember.id.groupId.eq(groupId), groupMember.id.memberId.eq(member.id))
                .offset(offset).limit(limit)
                .fetch();
        return members;
    }
    
    // 초대 대기중인 회원들을 가져온다
    @Transactional(readOnly = true)
    @Override
    public Page<Member> getGroupMemberWaitingListByGroupId(Pageable pageable, Long groupId) {
        QGroupMemberRequest groupMemberRequest = QGroupMemberRequest.groupMemberRequest;
        QMember member = QMember.member;

        // TODO: select 부분 Projections로 변경하기
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Member> members = queryFactory.select(member).from(groupMemberRequest)
                .innerJoin(member)
                .on(groupMemberRequest.id.groupId.eq(groupId), groupMemberRequest.id.memberId.eq(member.id))
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .fetch();
        Long count = queryFactory.select(groupMemberRequest.count()).from(groupMemberRequest)
                .where(groupMemberRequest.id.groupId.eq(groupId))
                .fetchFirst();
        return new PageImpl<>(members, pageable, count);
    }

    @Transactional
    @Override
    public void updateGroupOwner(Group group) {
        Optional<GroupMember> optionalGroupMember = getSuccessor(group);
        if(optionalGroupMember.isPresent()) {
            GroupMember successor = optionalGroupMember.get();
            if(!successor.getMember().equals(group.getOwner())) {
                QGroup qGroup = QGroup.group;
                JPAQueryFactory queryFactory = new JPAQueryFactory(em);
                queryFactory.update(qGroup).set(qGroup.owner, successor.getMember())
                        .where(qGroup.id.eq(group.getId())).execute();
            }
        }
    }

    /**
     * 그룹에서 그룹장을 제외 한 제일 빨리 가입한 회원을 가져온다.
     * @param group
     * @return
     */
    private Optional<GroupMember> getSuccessor(Group group) {
        QGroupMember groupMember = QGroupMember.groupMember;
        JPAQuery<GroupMember> query = new JPAQuery<>(em);
        GroupMember member = query.select(groupMember).from(groupMember)
                .where(groupMember.group.eq(group)).orderBy(groupMember.createdAt.asc())
                .offset(1).limit(1).fetchFirst();
        return Optional.ofNullable(member);
    }

    @Override
    public List<GroupMemberRequest> getGroupInviteListByMemberId(Pageable pageable, long memberId) {
        // TODO: 쿼리 개선해야 함..
        QGroupMemberRequest groupMemberRequest = QGroupMemberRequest.groupMemberRequest;
        JPAQuery<GroupMemberRequest> query = new JPAQuery<>(em);
        List<GroupMemberRequest> groupMemberRequests = query.select(groupMemberRequest).from(groupMemberRequest)
                .where(groupMemberRequest.id.memberId.eq(memberId)).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
        return groupMemberRequests;
    }
}
