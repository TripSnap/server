package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.FriendRequest;
import com.tripsnap.api.domain.entity.key.MemberFriendId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, MemberFriendId> {
    Optional<FriendRequest> findFriendRequestById(MemberFriendId id);
    void deleteById(MemberFriendId id);
}
