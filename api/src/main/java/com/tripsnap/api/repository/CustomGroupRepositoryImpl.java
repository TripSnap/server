package com.tripsnap.api.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tripsnap.api.domain.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
        List<Group> groups = query.select(group).from(groupMember)
                .innerJoin(group)
                .on(groupMember.id.memberId.eq(memberId), groupMember.id.groupId.eq(group.id))
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .fetch();
        return groups;
    }

    // 그룹의 멤버들을 가져온다
    @Transactional(readOnly = true)
    @Override
    public List<Member> getGroupMembersByGroupId(Pageable pageable, Long groupId) {
        QGroupMember groupMember = QGroupMember.groupMember;
        QMember member = QMember.member;

        // TODO: select 부분 Projections로 변경하기
        JPAQuery<GroupMember> query = new JPAQuery<>(em);
        List<Member> members = query.select(member).from(groupMember)
                .innerJoin(groupMember)
                .on(groupMember.id.groupId.eq(groupId), groupMember.id.memberId.eq(member.id))
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .fetch();
        return members;
    }
    
    // 초대 대기중인 회원들을 가져온다
    @Transactional(readOnly = true)
    @Override
    public List<Member> getGroupMemberWaitingListByGroupId(Pageable pageable, Long groupId) {
        QGroupMemberRequest groupMemberRequest = QGroupMemberRequest.groupMemberRequest;
        QMember member = QMember.member;

        // TODO: select 부분 Projections로 변경하기
        JPAQuery<GroupMemberRequest> query = new JPAQuery<>(em);
        List<Member> members = query.select(member).from(groupMemberRequest)
                .innerJoin(groupMemberRequest)
                .on(groupMemberRequest.id.groupId.eq(groupId), groupMemberRequest.id.memberId.eq(member.id))
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .fetch();
        return members;
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

    private Optional<GroupMember> getSuccessor(Group group) {
        QGroupMember groupMember = QGroupMember.groupMember;
        JPAQuery<GroupMember> query = new JPAQuery<>(em);
        GroupMember member = query.select(groupMember).from(groupMember)
                .where(groupMember.group.eq(group)).orderBy(groupMember.createdAt.asc())
                .offset(1).limit(1).fetchFirst();
        return Optional.ofNullable(member);
    }
}
