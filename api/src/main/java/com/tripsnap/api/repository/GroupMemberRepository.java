package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.GroupMember;
import com.tripsnap.api.domain.entity.key.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId>, CustomGroupMemberRepository {
}
