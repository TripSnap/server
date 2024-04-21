package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.Friend;
import com.tripsnap.api.domain.entity.key.MemberFriendId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, MemberFriendId>,CustomFriendRepository {
    List<Friend> findFriendsByMemberId(Pageable pageable, Long MemberId);
    Optional<Friend> findFriendById(MemberFriendId id);
}
