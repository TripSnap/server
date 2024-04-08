package com.tripsnap.api.repository;

import com.tripsnap.api.domain.dto.AlbumPhotoInsDTO;
import com.tripsnap.api.domain.entity.AlbumPhoto;
import com.tripsnap.api.domain.entity.Group;
import com.tripsnap.api.domain.entity.GroupAlbum;
import com.tripsnap.api.domain.entity.Member;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomGroupRepository {
    List<Group> getGroupsByMemberId(Pageable pageable, Long memberId);
    List<Member> getGroupMembersByGroupId(Pageable pageable, Long groupId);
    List<Member> getGroupMemberWaitingListByGroupId(Pageable pageable, Long groupId);

    List<GroupAlbum> getGroupAlbumsByGroupId(Pageable pageable, Long groupId);
    List<AlbumPhoto> getPhotosByAlbumId(Pageable pageable, Long albumId);
    void insertPhotosToAlbum(GroupAlbum album, List<AlbumPhotoInsDTO> photos);
}
