package com.tripsnap.api.service;

import com.tripsnap.api.domain.entity.Group;
import com.tripsnap.api.domain.entity.GroupAlbum;
import com.tripsnap.api.domain.entity.GroupMember;
import com.tripsnap.api.domain.entity.Member;
import com.tripsnap.api.domain.entity.key.GroupMemberId;
import com.tripsnap.api.exception.ServiceException;
import com.tripsnap.api.repository.GroupAlbumRepository;
import com.tripsnap.api.repository.GroupMemberRepository;
import com.tripsnap.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionCheckService {
    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupAlbumRepository groupAlbumRepository;

    public void memberCheck(String email) {
        getMember(email);
    }

    public Member getMember(String email) {
        Optional<Member> optMember = memberRepository.findByEmail(email);
        return optMember.orElseThrow(ServiceException::BadRequestException);
    }

    public void checkGroupMember(Long groupId, Long memberId) {
        getGroupMember(groupId, memberId);
    }

    public GroupMember getGroupMember(Long groupId, Long memberId) {
        GroupMemberId id = GroupMemberId.builder().groupId(groupId).memberId(memberId).build();
        Optional<GroupMember> optGroupMember = groupMemberRepository.findGroupMemberById(id);
        return optGroupMember.orElseThrow(ServiceException::BadRequestException);
    }

    public void checkGroupAlbum(Long groupId, Long albumId) {
        getGroupAlbum(groupId, albumId);
    }

    public GroupAlbum getGroupAlbum(Long groupId, Long albumId) {
        Optional<GroupAlbum> optGroupAlbum = groupAlbumRepository.getGroupAlbumById(albumId);
        if(optGroupAlbum.isPresent() && optGroupAlbum.get().getGroupId().equals(groupId)) {
            return optGroupAlbum.get();
        }
        throw ServiceException.BadRequestException();
    }

    public Boolean isGroupOwner(Group group, Member member) {
        return member.getId().equals(group.getOwner().getId());
    }

    public Boolean isAlbumOwner(GroupAlbum album, Member member) {
        return member.getId().equals(album.getMemberId());
    }
}
