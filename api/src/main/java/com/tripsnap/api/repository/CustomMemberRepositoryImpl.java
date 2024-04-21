package com.tripsnap.api.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.tripsnap.api.domain.dto.MemberEditDTO;
import com.tripsnap.api.domain.entity.QMember;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

public class CustomMemberRepositoryImpl implements CustomMemberRepository{
    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public void updateMember(Long memberId, MemberEditDTO member) {
        boolean hasEditField = false;
        QMember qMember = QMember.member;
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        JPAUpdateClause updateClause = queryFactory.update(qMember);

        if(StringUtils.hasText(member.nickname())) {
            hasEditField = true;
            updateClause = updateClause.set(qMember.nickname, member.nickname());
        }

        if(StringUtils.hasText(member.photo())) {
            hasEditField = true;
            updateClause = updateClause.set(qMember.photo, member.photo());
        }

        if(hasEditField) {
            updateClause.where(qMember.id.eq(memberId)).execute();
        }
    }

    @Transactional
    @Override
    public boolean updateMemberPassword(Long memberId, String encodedNewPassword) {
        QMember qMember = QMember.member;
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        long executed = queryFactory.update(qMember).set(qMember.password, encodedNewPassword)
                .where(qMember.id.eq(memberId)).execute();
        em.clear();
        return executed == 1L;
    }
}
