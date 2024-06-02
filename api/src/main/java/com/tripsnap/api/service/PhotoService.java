package com.tripsnap.api.service;

import com.tripsnap.api.domain.dto.*;
import com.tripsnap.api.domain.entity.AlbumPhoto;
import com.tripsnap.api.domain.entity.GroupAlbum;
import com.tripsnap.api.domain.entity.Member;
import com.tripsnap.api.domain.mapstruct.GroupAlbumMapper;
import com.tripsnap.api.exception.ServiceException;
import com.tripsnap.api.repository.AlbumPhotoRepository;
import com.tripsnap.api.repository.GroupAlbumRepository;
import com.tripsnap.api.service.aws.PhotoUploadService;
import com.tripsnap.api.utils.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PhotoService {
    private final GroupAlbumRepository groupAlbumRepository;
    private final AlbumPhotoRepository albumPhotoRepository;

    private final GroupAlbumMapper groupAlbumMapper;

    private final PermissionCheckService permissionCheckService;
    private final PhotoUploadService photoUploadService;

    // 앨범에서 사진 가져오기
    public ResultDTO.SimpleWithPageData<List<AlbumPhotoDTO>> getPhotos(String email, PageDTO pageDTO, GroupAlbumParamDTO paramDTO) {
        Member member = permissionCheckService.getMember(email);
        permissionCheckService.checkGroupMember(paramDTO.getGroupId(), member.getId());
        GroupAlbum groupAlbum = permissionCheckService.getGroupAlbum(paramDTO.getGroupId(), paramDTO.getAlbumId());

        Pageable pageable = Pageable.ofSize(pageDTO.pagePerCnt()).withPage(pageDTO.page());

        List<AlbumPhoto> photos = groupAlbumRepository.getPhotosByAlbumId(pageable, groupAlbum);
        return ResultDTO.WithPageData(pageable, groupAlbumMapper.toAlbumDTOList(photos));
    }

    // 사진 추가
    @Transactional
    public ResultDTO.SimpleSuccessOrNot addPhotos(String email, GroupAlbumParamDTO paramDTO, List<AlbumPhotoInsDTO> photos) {
        Member member = permissionCheckService.getMember(email);
        permissionCheckService.checkGroupMember(paramDTO.getGroupId(), member.getId());
        GroupAlbum groupAlbum = permissionCheckService.getGroupAlbum(paramDTO.getGroupId(), paramDTO.getAlbumId());

        groupAlbumRepository.insertPhotosToAlbum(member.getId(), groupAlbum, photos);

        return ResultDTO.SuccessOrNot(true);
    }

    // 사진 삭제
    @Transactional
    public ResultDTO.SimpleSuccessOrNot deletePhotos(String email, GroupAlbumParamDTO paramDTO, List<Long> photoIds) {
        Member member = permissionCheckService.getMember(email);
        permissionCheckService.checkGroupMember(paramDTO.getGroupId(), member.getId());
        permissionCheckService.checkGroupAlbum(paramDTO.getGroupId(), paramDTO.getAlbumId());

        albumPhotoRepository.deleteAllByIdIn(photoIds);

        return ResultDTO.SuccessOrNot(true);
    }

    public PresignedUrlResultDTO getPresignedURL(String email, Long albumId, RequestPresignedUrlDTO param) {
        Member member = permissionCheckService.getMember(email);
        GroupAlbum album = groupAlbumRepository.getGroupAlbumById(albumId).orElseThrow(ServiceException::BadRequestException);
        permissionCheckService.checkAlbumAndMember(albumId, member.getId());

        String extension = param.type().replace("image/","");
        // 사진 이름: {groupId}/{albumId}/{albumId+timestamp}
        String filename = String.format("%d/%d/%s.%s", album.getGroupId(), albumId, RandomUtil.getRandomString(30), extension);
        String url = photoUploadService.requestPresignedUrl("trip-snap-album", filename);
        return new PresignedUrlResultDTO(url, filename);
    }

}
