package com.tripsnap.api.service;

import com.tripsnap.api.domain.dto.*;
import com.tripsnap.api.domain.entity.GroupAlbum;
import com.tripsnap.api.domain.entity.Member;
import com.tripsnap.api.domain.mapstruct.GroupAlbumMapper;
import com.tripsnap.api.exception.ServiceException;
import com.tripsnap.api.repository.GroupAlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final GroupAlbumRepository groupAlbumRepository;

    private final GroupAlbumMapper groupAlbumMapper;

    private final PermissionCheckService permissionCheckService;

    // 기록 리스트 가져오기
    public ResultDTO.SimpleWithPageData<List<GroupAlbumDTO>> getAlbums(String email, PageDTO pageDTO, Long groupId) {
        Member member = permissionCheckService.getMember(email);
        permissionCheckService.checkGroupMember(groupId, member.getId());
        Pageable pageable = Pageable.ofSize(pageDTO.pagePerCnt()).withPage(pageDTO.page());
        List<GroupAlbum> groupAlbums = groupAlbumRepository.getGroupAlbumsByGroupId(pageable, groupId);
        return ResultDTO.WithPageData(pageable, groupAlbumMapper.toDTOList(groupAlbums, email));
    }

    // 기록 추가
    public Map<String, Object> createAlbum(String email, GroupAlbumInsDTO param) {
        Member member = permissionCheckService.getMember(email);
        permissionCheckService.checkGroupMember(param.groupId(), member.getId());
        GroupAlbum groupAlbumEntity = groupAlbumMapper.toGroupAlbumEntity(param, member.getId());
        GroupAlbum saved = groupAlbumRepository.save(groupAlbumEntity);
        return Map.of("success", true, "albumId", saved.getId());
    }


    // 앨범 삭제
    public ResultDTO.SimpleSuccessOrNot deleteAlbum(String email, GroupAlbumParamDTO paramDTO) {
        Member member = permissionCheckService.getMember(email);
        GroupAlbum groupAlbum = permissionCheckService.getGroupAlbum(paramDTO.getGroupId(), paramDTO.getAlbumId());

        if(permissionCheckService.isAlbumOwner(groupAlbum, member) || groupAlbum.getMemberId() == null) {
            groupAlbumRepository.deleteById(paramDTO.getAlbumId());
            return ResultDTO.SuccessOrNot(true);
        } else {
            throw ServiceException.PermissionDenied();
        }
    }

}
