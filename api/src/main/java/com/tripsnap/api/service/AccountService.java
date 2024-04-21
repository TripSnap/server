package com.tripsnap.api.service;

import com.tripsnap.api.domain.dto.MemberDTO;
import com.tripsnap.api.domain.dto.MemberEditDTO;
import com.tripsnap.api.domain.dto.MemberPasswordEditDTO;
import com.tripsnap.api.domain.dto.ResultDTO;
import com.tripsnap.api.domain.entity.Group;
import com.tripsnap.api.domain.entity.Member;
import com.tripsnap.api.domain.mapstruct.MemberMapper;
import com.tripsnap.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final PermissionCheckService permissionCheckService;
    private final GroupRepository groupRepository;
    private final FriendRepository friendRepository;
    private final NotificationRepository notificationRepository;
    private final GroupMemberRequestRepository groupMemberRequestRepository;
    private final GroupService groupService;
    private final MemberRepository memberRepository;

    private final MemberMapper memberMapper;

    private final PasswordEncoder passwordEncoder;


    // 회원 탈퇴
    @Transactional
    public ResultDTO.SimpleSuccessOrNot leave(String email) {
        Member member = permissionCheckService.getMember(email);
        // 알람 삭제
        // 친구 삭제
        // 그룹 탈퇴
        // 그룹 초대 테이블 삭제
        notificationRepository.deleteAllByMemberId(member.getId());
        friendRepository.removeFriendAndRequestAll(member.getId());
        List<Group> groups = groupRepository.getGroupsByMemberId(member.getId());
        groups.forEach(group -> groupService.leaveGroup(member, group));
        groupMemberRequestRepository.deleteAllByMemberId(member.getId());

        return ResultDTO.SuccessOrNot(true);
    }

    // 유저 데이터 가져오기
    public ResultDTO.SimpleWithData<MemberDTO> getUserData(String email) {
        Member member = permissionCheckService.getMember(email);
        return ResultDTO.WithData(memberMapper.toMemberDTO(member));
    }

    // 유저 데이터 수정
    public ResultDTO.SimpleSuccessOrNot editUserData(String email, MemberEditDTO param) {
        Member member = permissionCheckService.getMember(email);
        memberRepository.updateMember(member.getId(), param);
        return ResultDTO.SuccessOrNot(true);
    }

    // 유저 비밀번호 수정
    public ResultDTO.SuccessOrNot editUserPassword(String email, MemberPasswordEditDTO param) {
        Member member = permissionCheckService.getMember(email);

        if(passwordEncoder.matches(param.password(), member.getPassword())) {
            memberRepository.updateMemberPassword(member.getId(), passwordEncoder.encode(param.newPassword()));
        } else {
            return ResultDTO.SuccessOrNot(false, "비밀번호가 틀립니다.");
        }
        return ResultDTO.SuccessOrNot(true,null);
    }

    public ResultDTO.SuccessOrNot findUser(String email) {
        return null;
    }
}
