package com.tripsnap.api.service;


import com.tripsnap.api.domain.dto.GroupDTO;
import com.tripsnap.api.domain.dto.GroupInsDTO;
import com.tripsnap.api.domain.dto.PageDTO;
import com.tripsnap.api.domain.dto.ResultDTO;
import com.tripsnap.api.domain.entity.Friend;
import com.tripsnap.api.domain.entity.Group;
import com.tripsnap.api.domain.entity.GroupMemberRequest;
import com.tripsnap.api.domain.entity.Member;
import com.tripsnap.api.domain.entity.key.GroupMemberId;
import com.tripsnap.api.domain.mapstruct.GroupMapper;
import com.tripsnap.api.exception.ServiceException;
import com.tripsnap.api.repository.FriendRepository;
import com.tripsnap.api.repository.GroupMemberRepository;
import com.tripsnap.api.repository.GroupRepository;
import com.tripsnap.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    // 회원이 가입한 그룹 리스트
    public ResultDTO.SimpleWithPageData<List<GroupDTO>> getGroupList(String email, PageDTO param) {
        Optional<Member> optMember = memberRepository.findByEmail(email);
        if(optMember.isPresent()) {
            Member member = optMember.get();
            Pageable pageable = Pageable.ofSize(param.pagePerCnt()).withPage(param.page());
            List<Group> groups = groupMemberRepository.getGroupsByMemberId(pageable,member.getId());

            return ResultDTO.WithPageData(pageable, groupMapper.toDTOList(groups));
        }
        throw ServiceException.BadRequestException();
    }

    @Transactional
    public ResultDTO.SimpleSuccessOrNot createGroup(String email, GroupInsDTO groupInsDTO) {
        Optional<Member> optMember = memberRepository.findByEmail(email);
        if(optMember.isPresent()) {
            Member member = optMember.get();
            // 이메일을 통해 member의 친구 목록애서 회원 정보를 가져옴
            List<Friend> friends = friendRepository.getFriendList(member.getId(), groupInsDTO.memberEmails());

            // group 생성 및 insert
            Group group = Group.builder().owner(member)
                    .title(groupInsDTO.title()).build();
            groupRepository.save(group);

            // groupMemberRequest insert
            List<GroupMemberRequest> groupMemberRequests = friends.stream().map(friend -> GroupMemberRequest.builder()
                            .id(GroupMemberId.builder()
                                    .groupId(group.getId())
                                    .memberId(friend.getId().getFriendId())
                                    .build()
                            ).build())
                    .toList();
            group.setMemberRequests(groupMemberRequests);
            groupRepository.save(group);

            return ResultDTO.SuccessOrNot(true);
        }
        throw ServiceException.BadRequestException();
    }

    @Transactional
    public ResultDTO.SimpleSuccessOrNot deleteGroup(String email, Long groupId) {
        Optional<Member> optMember = memberRepository.findByEmail(email);
        if(optMember.isPresent()) {
            Member member = optMember.get();
            groupRepository.removeGroupByOwnerIdAndId(member.getId(), groupId);
            return ResultDTO.SuccessOrNot(true);
        }
        throw ServiceException.BadRequestException();
    }
}
