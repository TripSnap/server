package com.tripsnap.api.service;

import com.tripsnap.api.domain.dto.*;
import com.tripsnap.api.domain.entity.AlbumPhoto;
import com.tripsnap.api.domain.entity.GroupAlbum;
import com.tripsnap.api.domain.entity.Member;
import com.tripsnap.api.domain.mapstruct.GroupAlbumMapper;
import com.tripsnap.api.exception.ServiceException;
import com.tripsnap.api.repository.AlbumPhotoRepository;
import com.tripsnap.api.repository.GroupAlbumRepository;
import com.tripsnap.api.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final GroupRepository groupRepository;
    private final GroupAlbumRepository groupAlbumRepository;
    private final AlbumPhotoRepository albumPhotoRepository;

    private final GroupAlbumMapper groupAlbumMapper;

    private final PermissionCheckService permissionCheckService;

    // 기록 리스트 가져오기
    public ResultDTO.SimpleWithPageData<List<GroupAlbumDTO>> getAlbums(String email, PageDTO pageDTO, Long groupId) {
        Member member = permissionCheckService.getMember(email);
        permissionCheckService.checkGroupMember(groupId, member.getId());
        Pageable pageable = Pageable.ofSize(pageDTO.pagePerCnt()).withPage(pageDTO.page());
        List<GroupAlbum> groupAlbums = groupRepository.getGroupAlbumsByGroupId(pageable, groupId);
        return ResultDTO.WithPageData(pageable, groupAlbumMapper.toDTOList(groupAlbums));
    }

    // 기록 추가
    public ResultDTO.SuccessOrNot createAlbum(String email, GroupAlbumInsDTO param) {
        Member member = permissionCheckService.getMember(email);
        permissionCheckService.checkGroupMember(param.groupId(), member.getId());
        // TODO: 사진 갯수 체크 필요
        GroupAlbum groupAlbumEntity = groupAlbumMapper.toGroupAlbumEntity(param, member.getId());
        groupAlbumEntity = groupAlbumRepository.save(groupAlbumEntity);
        groupRepository.insertPhotosToAlbum(groupAlbumEntity, param.albumPhotoList());
        return ResultDTO.SuccessOrNot(true, null);
    }


    // 앨범 삭제
    public ResultDTO.SimpleSuccessOrNot deleteAlbum(String email, GroupAlbumParamDTO paramDTO) {
        Member member = permissionCheckService.getMember(email);
        GroupAlbum groupAlbum = permissionCheckService.getGroupAlbum(paramDTO.getGroupId(), paramDTO.getAlbumId());

        if(permissionCheckService.isAlbumOwner(groupAlbum, member)) {
            groupAlbumRepository.deleteById(paramDTO.getAlbumId());
            return ResultDTO.SuccessOrNot(true);
        } else {
            throw ServiceException.PermissionDenied();
        }
    }


    // 앨범에서 사진 가져오기
    public ResultDTO.SimpleWithPageData<List<AlbumPhotoDTO>> getPhotos(String email, PageDTO pageDTO, GroupAlbumParamDTO paramDTO) {
        Member member = permissionCheckService.getMember(email);
        permissionCheckService.checkGroupMember(paramDTO.getGroupId(), member.getId());
        permissionCheckService.checkGroupAlbum(paramDTO.getGroupId(), paramDTO.getAlbumId());

        Pageable pageable = Pageable.ofSize(pageDTO.pagePerCnt()).withPage(pageDTO.page());

        List<AlbumPhoto> photos = groupRepository.getPhotosByAlbumId(pageable, paramDTO.getAlbumId());
        return ResultDTO.WithPageData(pageable, groupAlbumMapper.toAlbumDTOList(photos));
    }

    // 사진 추가
    public ResultDTO.SimpleSuccessOrNot addPhotos(String email, GroupAlbumParamDTO paramDTO, List<AlbumPhotoInsDTO> photos) {
        Member member = permissionCheckService.getMember(email);
        permissionCheckService.checkGroupMember(paramDTO.getGroupId(), member.getId());
        GroupAlbum groupAlbum = permissionCheckService.getGroupAlbum(paramDTO.getGroupId(), paramDTO.getAlbumId());

        groupRepository.insertPhotosToAlbum(groupAlbum, photos);

        return ResultDTO.SuccessOrNot(true);
    }

    // 사진 삭제
    public ResultDTO.SimpleSuccessOrNot deletePhotos(String email, GroupAlbumParamDTO paramDTO, List<Long> photoIds) {
        Member member = permissionCheckService.getMember(email);
        permissionCheckService.checkGroupMember(paramDTO.getGroupId(), member.getId());
        permissionCheckService.checkGroupAlbum(paramDTO.getGroupId(), paramDTO.getAlbumId());

        albumPhotoRepository.deleteAllByIdIn(photoIds);

        return ResultDTO.SuccessOrNot(true);
    }
}
