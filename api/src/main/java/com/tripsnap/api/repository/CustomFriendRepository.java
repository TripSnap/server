package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.Friend;
import com.tripsnap.api.domain.entity.FriendRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomFriendRepository {
    List<Friend> getFriendListByEmail(long memberId, List<String> emails);
    Boolean createFriend(long memberId, long friendId);
    Boolean removeFriend(long memberId, long friendId);
    void removeFriendAndRequestAll(long memberId);
    List<Friend> getFriendsByMemberId(long offset, long limit, Long memberId);

    /**
     * 내가 친구 신청한 리스트를 가져온다.
     */
    Page<FriendRequest> getFriendRequestsByMemberId(Pageable pageable, Long memberId);
}
