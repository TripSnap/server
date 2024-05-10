package com.tripsnap.api.repository;

import com.tripsnap.api.domain.dto.AlbumPhotoInsDTO;
import com.tripsnap.api.domain.entity.AlbumPhoto;
import com.tripsnap.api.domain.entity.GroupAlbum;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomGroupAlbumRepository {
    List<GroupAlbum> getGroupAlbumsByGroupId(Pageable pageable, Long groupId);
    List<AlbumPhoto> getPhotosByAlbumId(Pageable pageable, GroupAlbum album);
    void insertPhotosToAlbum(Long memberId, GroupAlbum album, List<AlbumPhotoInsDTO> photos);
    void updateAlbumAndPhotoForLeave(Long groupId, Long memberId);
}
