package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.Friend;

import java.util.List;

public interface CustomFriendRepository {
    List<Friend> getFriendListByEmail(long memberId, List<String> emails);
    Boolean createFriend(long memberId, long friendId);
    Boolean removeFriend(long memberId, long friendId);
    void removeFriendAndRequestAll(long memberId);
}
