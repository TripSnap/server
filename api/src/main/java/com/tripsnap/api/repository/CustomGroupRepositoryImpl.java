package com.tripsnap.api.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tripsnap.api.domain.dto.AlbumPhotoInsDTO;
import com.tripsnap.api.domain.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    // 앨범 목록을 가져온다
    @Override
    public List<GroupAlbum> getGroupAlbumsByGroupId(Pageable pageable, Long groupId) {
        QGroupAlbum groupAlbum = QGroupAlbum.groupAlbum;
        QMember member = QMember.member;

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Tuple> tupleList = queryFactory.select(groupAlbum, member).from(groupAlbum)
                .leftJoin(member).on(groupAlbum.groupId.eq(groupId), groupAlbum.memberId.eq(member.id))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        return tupleList.stream().map(tuple -> {
            GroupAlbum album = tuple.get(groupAlbum);
            album.setMember(tuple.get(member));
            return album;
        }).toList();
    }

    @Override
    public List<AlbumPhoto> getPhotosByAlbumId(Pageable pageable, Long albumId) {
        return null;
    }

    @Override
    public void insertPhotosToAlbum(GroupAlbum album, List<AlbumPhotoInsDTO> photos) {
        QAlbumPhoto albumPhoto = QAlbumPhoto.albumPhoto;

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        // TODO: 개선이 필요
        photos.forEach(photoDTO -> {
            queryFactory.insert(albumPhoto).set(albumPhoto.groupAlbum, album).set(albumPhoto.photo, photoDTO.photo()).execute();
        });
    }
}
