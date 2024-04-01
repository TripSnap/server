package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.Friend;
import com.tripsnap.api.domain.entity.key.MemberFriendId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, MemberFriendId>,CustomFriendRepository {
}
