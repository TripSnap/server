package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.GroupMemberRequest;
import com.tripsnap.api.domain.entity.key.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRequestRepository extends JpaRepository<GroupMemberRequest, GroupMemberId> {
}
