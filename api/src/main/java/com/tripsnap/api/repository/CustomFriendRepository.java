package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.Friend;

import java.util.List;

public interface CustomFriendRepository {
    List<Friend> getFriendList(long memberId, List<String> emails);
}
