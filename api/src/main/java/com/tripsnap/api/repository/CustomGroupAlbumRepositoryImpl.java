package com.tripsnap.api.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tripsnap.api.domain.dto.AlbumPhotoInsDTO;
import com.tripsnap.api.domain.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomGroupAlbumRepositoryImpl implements CustomGroupAlbumRepository{
    @PersistenceContext
    private EntityManager em;

    // 앨범 목록을 가져온다
    @Override
    public List<GroupAlbum> getGroupAlbumsByGroupId(Pageable pageable, Long groupId) {
        QGroupAlbum groupAlbum = QGroupAlbum.groupAlbum;
        QMember member = QMember.member;

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<GroupAlbum> groupAlbums = queryFactory.selectFrom(groupAlbum).where(groupAlbum.groupId.eq(groupId))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        List<Long> memberIds = groupAlbums.stream().map(GroupAlbum::getMemberId).toList();

        List<Member> members = queryFactory.selectFrom(member).where(member.id.in(memberIds)).fetch();
        Map<Long, Member> memberMap = members.stream().collect(Collectors.toMap(Member::getId, (m) -> m));

        return groupAlbums.stream().peek((album -> album.setMember(memberMap.get(album.getMemberId())))).toList();
    }

    // 앨범 사진 가져오기
    @Override
    public List<AlbumPhoto> getPhotosByAlbumId(Pageable pageable, GroupAlbum album) {
        QAlbumPhoto albumPhoto = QAlbumPhoto.albumPhoto;

        JPAQuery<AlbumPhoto> query = new JPAQuery<>(em);
        List<AlbumPhoto> photos = query.select(albumPhoto).from(albumPhoto)
                .where(albumPhoto.groupAlbum.eq(album))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        return photos;
    }

    @Transactional
    @Override
    public void insertPhotosToAlbum(GroupAlbum album, List<AlbumPhotoInsDTO> photos) {
        QAlbumPhoto albumPhoto = QAlbumPhoto.albumPhoto;

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        // TODO: 개선이 필요
        photos.forEach(photoDTO -> {
            queryFactory.insert(albumPhoto).set(albumPhoto.groupAlbum, album).set(albumPhoto.photo, photoDTO.photo()).execute();
        });
    }


    @Transactional
    @Override
    public void updateAlbumAndPhotoForLeave(Long groupId, Long memberId) {
        QGroupAlbum groupAlbum = QGroupAlbum.groupAlbum;
        QAlbumPhoto albumPhoto = QAlbumPhoto.albumPhoto;

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        // TODO: update join 으로 수정

        List<Long> albumIds = queryFactory.select(groupAlbum.id).from(groupAlbum)
                .where(groupAlbum.groupId.eq(groupId), groupAlbum.memberId.eq(memberId)).fetch();

        queryFactory.update(albumPhoto).setNull(albumPhoto.memberId)
                .where(albumPhoto.groupAlbum.id.in(albumIds)).execute();

        queryFactory.update(groupAlbum).setNull(groupAlbum.memberId)
                .where(groupAlbum.id.in(albumIds)).execute();
    }
}
