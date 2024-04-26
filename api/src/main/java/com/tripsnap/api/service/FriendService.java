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
import com.tripsnap.api.repository.FriendRepository;
import com.tripsnap.api.repository.FriendRequestRepository;
import com.tripsnap.api.repository.MemberRepository;
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
public class FriendService {
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final PermissionCheckService permissionCheckService;


    /**
     * 친구 목록을 가져와 DTO로 변환하여 리턴한다.
     * @param email
     * @param pageDTO
     * @return
     */
    public ResultDTO.SimpleWithPageData<List<MemberDTO>> getFriendList(String email, PageDTO pageDTO) {
        Member member = permissionCheckService.getMember(email);
        Pageable pageable = Pageable.ofSize(pageDTO.pagePerCnt()).withPage(pageDTO.page());
        List<Friend> friends = friendRepository.getFriendsByMemberId(pageable, member.getId());
        List<MemberDTO> dtoList = memberMapper.toMemberDTOList(friends.stream().map(Friend::getMember).toList());
        return ResultDTO.WithPageData(pageable, dtoList);
    }

    /**
     * getAllFriendList 의 결과를 DTO로 변환하여 리턴한다.
     * @param email
     * @param pageDTO
     * @return
     */

    public ResultDTO.SimpleWithPageData<List<MemberDTO>> getAllFriendList(String email, PageDTO pageDTO) {
        Member member = permissionCheckService.getMember(email);
        Pageable pageable = Pageable.ofSize(pageDTO.pagePerCnt()).withPage(pageDTO.page());
        return ResultDTO.WithPageData(pageable, getAllFriendList(pageable, member.getId()));
    }

    /**
     * 친구 신청 받은 리스트와 친구 리스트를 합쳐서 가져온다.
     * @param pageable
     * @param memberId
     * @return
     */
    private List<MemberDTO> getAllFriendList(Pageable pageable, long memberId) {
        List<MemberDTO> friendMemberDTOs = new ArrayList<>();

        Page<Member> friendRequestsPage = friendRepository.getFriendReceiveRequestsByMemberId(pageable, memberId);
        var friendRequests = friendRequestsPage.getContent();

        friendMemberDTOs.addAll(memberMapper.toWatingMemberDTOList(friendRequests));

        // 리스트 앞부분의 친구 신청 때문에 값 보정
        long page = pageable.getPageNumber() - friendRequestsPage.getTotalElements() / pageable.getPageSize();
        if(page >= 0) {
            long limit = friendRequests.isEmpty() ? pageable.getPageSize() : pageable.getPageSize() - friendRequests.size();
            long offset = page == 0 ? 0 : (page*pageable.getPageSize()) - friendRequestsPage.getTotalElements() % pageable.getPageSize();

            List<Friend> friends = friendRepository.getFriendsByMemberId(offset, limit, memberId);
            friendMemberDTOs.addAll(memberMapper.toMemberDTOList(friends.stream().map(Friend::getMember).toList()));
        }

        return friendMemberDTOs;
    }

    public ResultDTO.SimpleWithPageData<List<MemberDTO>> getFriendRequestSendList(String email, PageDTO pageDTO) {
        Member member = permissionCheckService.getMember(email);
        Pageable pageable = Pageable.ofSize(pageDTO.pagePerCnt()).withPage(pageDTO.page());
        Page<FriendRequest> requests = friendRepository.getFriendSendRequestsByMemberId(pageable, member.getId());
        List<Member> members = requests.getContent().stream().map(FriendRequest::getMember).toList();
        return ResultDTO.WithPageData(pageable, memberMapper.toMemberDTOList(members));
    }

    // 친구 검색
    public ResultDTO.SimpleWithData<SearchMemberDTO> searchMember(String email, String friendEmail) {
        SearchMemberDTO searchMemberDTO = null;

        if(!email.equals(friendEmail)) {
            Member member = permissionCheckService.getMember(email);
            Optional<Member> optSearchMember = memberRepository.findByEmail(friendEmail);

            if(optSearchMember.isPresent()) {
                Member searchMember = optSearchMember.get();
                MemberFriendId memberFriendId = MemberFriendId.builder().memberId(member.getId()).friendId(searchMember.getId()).build();
                searchMemberDTO = memberMapper.toSearchMemberDTO(searchMember);


                boolean friendOption = false;

                Optional<Friend> optFriend = friendRepository.findFriendById(memberFriendId);
                if(optFriend.isPresent()) {
                    searchMemberDTO.setIsFriend(true);
                    friendOption = true;
                }

                if(!friendOption) {
                    Optional<FriendRequest> sendRequest = friendRequestRepository.findFriendRequestById(memberFriendId);
                    if(sendRequest.isPresent()) {
                        searchMemberDTO.setIsSendRequest(true);
                        friendOption = true;
                    }
                }

                if(!friendOption) {
                    MemberFriendId receiveRequestId = MemberFriendId.builder().memberId(searchMember.getId()).friendId(member.getId()).build();
                    Optional<FriendRequest> receiveRequest = friendRequestRepository.findFriendRequestById(receiveRequestId);
                    if(receiveRequest.isPresent()) {
                        searchMemberDTO.setIsReceiveRequest(true);
                    }
                }
            }
        }
        return ResultDTO.WithData(searchMemberDTO);
    }

    // 친구 신청
    public ResultDTO.SimpleSuccessOrNot sendRequest(String email, String friendEmail) {
        Member member = permissionCheckService.getMember(email);
        Member searchMember = permissionCheckService.getMember(friendEmail);

        MemberFriendId memberFriendId = MemberFriendId.builder().memberId(member.getId()).friendId(searchMember.getId()).build();
        FriendRequest request = FriendRequest.builder().id(memberFriendId).build();

        friendRequestRepository.save(request);
        return ResultDTO.SuccessOrNot(true);
    }

    
    // 친구 요청 승인 또는 거절
    @Transactional
    public ResultDTO.SuccessOrNot processFriendRequest(String email, String friendEmail, boolean isAllow) {
        Member member = permissionCheckService.getMember(email);
        Member friend = permissionCheckService.getMember(friendEmail);

        MemberFriendId memberFriendId = MemberFriendId.builder().memberId(friend.getId()).friendId(member.getId()).build();

        Optional<FriendRequest> optionalFriendRequest = friendRequestRepository.findFriendRequestById(memberFriendId);
        if(optionalFriendRequest.isPresent()) {
            if(isAllow) {
                friendRepository.createFriend(member.getId(), friend.getId());
            }
            friendRequestRepository.deleteById(memberFriendId);

            return ResultDTO.SuccessOrNot(true, null);
        }
        return ResultDTO.SuccessOrNot(false, "친구 요청 내역이 존재하지 않습니다.");
    }

    // 친구 삭제
    public ResultDTO.SimpleSuccessOrNot removeFriend(String email, String friendEmail) {
        Member member = permissionCheckService.getMember(email);
        Member friend = permissionCheckService.getMember(friendEmail);
        friendRepository.removeFriend(member.getId(), friend.getId());
        return ResultDTO.SuccessOrNot(true);
    }
}
