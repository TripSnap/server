package com.tripsnap.api.service;


import com.tripsnap.api.domain.dto.*;
import com.tripsnap.api.domain.entity.*;
import com.tripsnap.api.domain.entity.key.GroupMemberId;
import com.tripsnap.api.domain.mapstruct.GroupMapper;
import com.tripsnap.api.domain.mapstruct.MemberMapper;
import com.tripsnap.api.exception.ServiceException;
import com.tripsnap.api.repository.*;
import com.tripsnap.api.utils.CombinedPageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupMemberRepository groupMemberRepository;
    private final FriendRepository friendRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRequestRepository groupMemberRequestRepository;
    private final GroupAlbumRepository groupAlbumRepository;

    private final PermissionCheckService permissionCheckService;

    private final GroupMapper groupMapper;
    private final MemberMapper memberMapper;

    // 회원이 가입한 그룹 리스트
    public ResultDTO.SimpleWithPageData<List<GroupDTO>> getGroupList(String email, PageDTO param) {
        Member member = permissionCheckService.getMember(email);
        Pageable pageable = Pageable.ofSize(param.pagePerCnt()).withPage(param.page());
        List<Group> groups = groupRepository.getGroupsByMemberId(pageable,member.getId());

        return ResultDTO.WithPageData(pageable, groupMapper.toDTOList(groups, email));
    }

    // 그룹 한개만 가져오기
    public ResultDTO.SimpleWithData<GroupDTO> getGroup(String email, Long groupId) {
        Member member = permissionCheckService.getMember(email);
        Optional<Group> optionalGroup = groupRepository.findGroupById(groupId);
        Group group = optionalGroup.orElseThrow(ServiceException::BadRequestException);

        return ResultDTO.WithData(groupMapper.toDTO(group, email));
    }

    // 그룹 만들기
    @Transactional
    public ResultDTO.SimpleSuccessOrNot createGroup(String email, GroupInsDTO groupInsDTO) {
        Member member = permissionCheckService.getMember(email);

        // group 생성 및 insert
        Group group = Group.builder().owner(member)
                .title(groupInsDTO.title()).build();
        groupRepository.save(group);
        
        // 그룹 장은 바로 그룹 멤버에 추가
        GroupMember owner = GroupMember.builder()
                .id(GroupMemberId.builder().groupId(group.getId()).memberId(member.getId()).build())
                .group(group)
                .member(member)
                .build();
        groupMemberRepository.save(owner);

        // 이메일을 통해 member의 친구 목록애서 회원 정보를 가져옴
        List<Friend> friends = friendRepository.getFriendListByEmail(member.getId(), groupInsDTO.memberEmails());

        // groupMemberRequest insert
        List<GroupMemberRequest> groupMemberRequests = friends.stream().map(friend -> GroupMemberRequest.builder()
                        .id(GroupMemberId.builder()
                                .groupId(group.getId())
                                .memberId(friend.getId().getFriendId())
                                .build()
                        )
                        .member(friend.getMember())
                        .group(group)
                        .build())
                .toList();
        group.setMemberRequests(groupMemberRequests);

        return ResultDTO.SuccessOrNot(true);
    }

    // 그룹 삭제
    @Transactional
    public ResultDTO.SimpleSuccessOrNot deleteGroup(String email, Long groupId) {
        // TODO: cascade 확인 필요
        Member member = permissionCheckService.getMember(email);
        groupRepository.removeGroupByIdAndOwnerId(groupId, member.getId());
        return ResultDTO.SuccessOrNot(true);
    }

    // 그룹 탈퇴
    @Transactional
    public ResultDTO.SimpleSuccessOrNot leaveGroup(String email, Long groupId) {
        Member member = permissionCheckService.getMember(email);
        permissionCheckService.checkGroupMember(groupId, member.getId());
        Optional<Group> optionalGroup = groupRepository.findGroupById(groupId);

        if(optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            return leaveGroup(member, group);
        } else {
            throw ServiceException.BadRequestException();
        }
    }

    /**
     * 그룹 인원 검사 후 1명 초과면 그룹장을 넘기고 탈퇴, 1명이면 그룹을 삭제한다.
     * @param member
     * @param group
     * @return
     */
    @Transactional
    public ResultDTO.SimpleSuccessOrNot leaveGroup(Member member, Group group) {
        int memberCount = groupMemberRepository.countByGroupId(group.getId());
        if(memberCount > 1) {
            if(permissionCheckService.isGroupOwner(group, member)) {
                groupRepository.updateGroupOwner(group);
            }
            groupAlbumRepository.updateAlbumAndPhotoForLeave(group.getId(), member.getId());
            groupMemberRepository.removeById(GroupMemberId.builder().groupId(group.getId()).memberId(member.getId()).build());
        } else {
            // 그룹 회원이 한명일 때
            groupRepository.removeGroupByIdAndOwnerId(group.getId(), member.getId());
        }

        return ResultDTO.SuccessOrNot(true);
    }

    public ResultDTO.SimpleWithPageData<List<GroupMemberRequestDTO>> getGroupInviteList(String email, PageDTO pageDTO) {
        Member member = permissionCheckService.getMember(email);

        Pageable pageable = Pageable.ofSize(pageDTO.pagePerCnt()).withPage(pageDTO.page());
        List<GroupMemberRequest> requestEntities = groupRepository.getGroupInviteListByMemberId(pageable, member.getId());
        List<GroupMemberRequestDTO> requestDTOList = groupMapper.toRequestDTOList(requestEntities);
        return ResultDTO.WithPageData(pageable, requestDTOList);
    }


    // 그룹 멤버 리스트
    public ResultDTO.SimpleWithPageData<List<MemberDTO>> getMemberList(String email, Long groupId, PageDTO pageDTO) {
        Member member = permissionCheckService.getMember(email);
        permissionCheckService.checkGroupMember(groupId,member.getId());

        Group group = groupRepository.findGroupById(groupId).get();
        List<MemberDTO> memberDTOS = new ArrayList<>();
        Pageable pageable = Pageable.ofSize(pageDTO.pagePerCnt()).withPage(pageDTO.page());

        // 그룹장 일때는 초대 대기중인 멤버도 보여준다..
        if(member.getId().equals(group.getOwner().getId())) {
            Page<Member> waitingMemberPage = groupRepository.getGroupMemberWaitingListByGroupId(pageable, groupId);
            memberDTOS.addAll( memberMapper.toWatingMemberDTOList(waitingMemberPage.getContent()));

            CombinedPageable combinedPageable = CombinedPageable.get(pageable, waitingMemberPage);
            if(combinedPageable.isNextDataFetch()) {
                List<Member> members = groupRepository.getGroupMembersByGroupId(combinedPageable.getOffset(), combinedPageable.getLimit(), groupId);
                memberDTOS.addAll(memberMapper.toMemberDTOList(members));
            }
        } else {
            List<Member> members = groupRepository.getGroupMembersByGroupId(pageable, groupId);
            memberDTOS.addAll(memberMapper.toMemberDTOList(members));
        }
        return ResultDTO.WithPageData(pageable, memberDTOS);
    }

    // 초대 취소
    @Transactional
    public ResultDTO.SimpleSuccessOrNot cancelInvite(String email, Long groupId, String requestEmail) {
        Member member = permissionCheckService.getMember(email);
        Optional<Group> optionalGroup = groupRepository.findGroupById(groupId);
        Member requestMember = permissionCheckService.getMember(requestEmail);

        optionalGroup.ifPresentOrElse(group -> {
            if(permissionCheckService.isGroupOwner(group, member)) {
                GroupMemberId id = GroupMemberId.builder().groupId(groupId).memberId(requestMember.getId()).build();
                groupMemberRequestRepository.deleteById(id);
            } else {
                throw ServiceException.PermissionDenied();
            }
        }, () -> {
            throw ServiceException.BadRequestException();
        });
        return ResultDTO.SuccessOrNot(true);
    }

    // 초대 수락 및 거절
    @Transactional
    public ResultDTO.SuccessOrNot processInvite(String email, Long groupId, boolean isAllow) {
        Member member = permissionCheckService.getMember(email);
        Group group = groupRepository.findGroupById(groupId).orElseThrow(ServiceException::BadRequestException);
        GroupMemberId id = GroupMemberId.builder().groupId(groupId).memberId(member.getId()).build();
        Optional<GroupMemberRequest> request = groupMemberRequestRepository.findById(id);
        if(request.isPresent()) {
            groupMemberRequestRepository.deleteById(id);
            if(isAllow) {
                GroupMember groupMember = GroupMember.builder().id(id).group(group).member(member).build();
                groupMemberRepository.save(groupMember);
            }
            return ResultDTO.SuccessOrNot(true, null);
        } else {
            return ResultDTO.SuccessOrNot(false, "만료되었거나 취소된 초대장입니다.");
        }
    }
}
