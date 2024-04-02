package com.tripsnap.api.service;

import com.tripsnap.api.domain.dto.MemberDTO;
import com.tripsnap.api.domain.dto.PageDTO;
import com.tripsnap.api.domain.dto.ResultDTO;
import com.tripsnap.api.domain.dto.SearchMemberDTO;
import com.tripsnap.api.domain.entity.Friend;
import com.tripsnap.api.domain.entity.FriendRequest;
import com.tripsnap.api.domain.entity.Member;
import com.tripsnap.api.domain.entity.key.MemberFriendId;
import com.tripsnap.api.domain.mapstruct.MemberMapper;
import com.tripsnap.api.exception.ServiceException;
import com.tripsnap.api.repository.FriendRepository;
import com.tripsnap.api.repository.FriendRequestRepository;
import com.tripsnap.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final MemberMapper memberMapper;

    // 친구 목록
    public ResultDTO.SimpleWithPageData<List<MemberDTO>> getFriendList(String email, PageDTO pageDTO) {
        Optional<Member> optMember = memberRepository.findByEmail(email);
        if(optMember.isPresent()) {
            Member member = optMember.get();
            Pageable pageable = Pageable.ofSize(pageDTO.pagePerCnt()).withPage(pageDTO.page());
            List<Friend> friendEntities = friendRepository.findFriendsByMemberId(pageable, member.getId());
            List<MemberDTO> friends = memberMapper.toMemberDTOList(friendEntities.stream().map(Friend::getMember).toList());
            return ResultDTO.WithPageData(pageable, friends);
        }
        throw ServiceException.BadRequestException();
    }

    // 친구 검색
    public ResultDTO.SimpleWithData<SearchMemberDTO> searchMember(String email, String friendEmail) {
        Optional<Member> optMember = memberRepository.findByEmail(email);
        SearchMemberDTO searchMemberDTO = null;
        if(optMember.isPresent()) {
            Optional<Member> optSearchMember = memberRepository.findByEmail(friendEmail);
            if(optSearchMember.isPresent()) {
                Member member = optMember.get();
                Member searchMember = optSearchMember.get();
                MemberFriendId memberFriendId = MemberFriendId.builder().memberId(member.getId()).friendId(searchMember.getId()).build();
                searchMemberDTO = memberMapper.toSearchMemberDTO(searchMember);

                Optional<Friend> optFriend = friendRepository.findFriendById(memberFriendId);
                searchMemberDTO.setIsFriend(optFriend.isPresent());
            }
            return ResultDTO.WithData(searchMemberDTO);
        }
        throw ServiceException.BadRequestException();
    }

    // 친구 신청
    public ResultDTO.SimpleSuccessOrNot sendRequest(String email, String friendEmail) {
        Optional<Member> optMember = memberRepository.findByEmail(email);
        Optional<Member> optSearchMember = memberRepository.findByEmail(friendEmail);

        if(optMember.isPresent() && optSearchMember.isPresent()) {
            Member member = optMember.get();
            Member searchMember = optSearchMember.get();

            MemberFriendId memberFriendId = MemberFriendId.builder().memberId(member.getId()).friendId(searchMember.getId()).build();
            FriendRequest request = FriendRequest.builder().id(memberFriendId).build();

            friendRequestRepository.save(request);
            return ResultDTO.SuccessOrNot(true);
        }
        throw ServiceException.BadRequestException();
    }

    
    // 친구 요청 승인 또는 거절
    @Transactional
    public ResultDTO.SuccessOrNot processFriendRequest(String email, String friendEmail, boolean isAllow) {
        Optional<Member> optMember = memberRepository.findByEmail(email);
        Optional<Member> optFriend = memberRepository.findByEmail(friendEmail);
        if(optMember.isPresent() && optFriend.isPresent()) {
            Member member = optMember.get();
            Member friend = optFriend.get();
            MemberFriendId memberFriendId = MemberFriendId.builder().memberId(member.getId()).friendId(friend.getId()).build();

            Optional<FriendRequest> optionalFriendRequest = friendRequestRepository.findFriendRequestById(memberFriendId);
            if(optionalFriendRequest.isPresent()) {
                MemberFriendId requestId = MemberFriendId.builder().memberId(friend.getId()).friendId(member.getId()).build();
                if(isAllow) {
                    friendRepository.createFriend(member.getId(), friend.getId());
                }
                friendRequestRepository.deleteById(requestId);

                return ResultDTO.SuccessOrNot(true, null);
            }
            return ResultDTO.SuccessOrNot(false, "친구 요청 내역이 존재하지 않습니다.");
        }
        throw ServiceException.BadRequestException();
    }

    // 친구 삭제
    public ResultDTO.SimpleSuccessOrNot removeFriend(String email, String friendEmail) {
        Optional<Member> optMember = memberRepository.findByEmail(email);
        Optional<Member> optFriend = memberRepository.findByEmail(friendEmail);
        if(optMember.isPresent() && optFriend.isPresent()) {
            Member member = optMember.get();
            Member friend = optFriend.get();
            friendRepository.removeFriend(member.getId(), friend.getId());
            return ResultDTO.SuccessOrNot(true);
        }
        throw ServiceException.BadRequestException();
    }
}
