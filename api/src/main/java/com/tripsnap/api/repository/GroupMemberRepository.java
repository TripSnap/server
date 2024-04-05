package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.GroupMember;
import com.tripsnap.api.domain.entity.key.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId>, CustomGroupMemberRepository {
    Optional<GroupMember> findGroupMemberById(GroupMemberId id);
}
