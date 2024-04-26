package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.Group;
import com.tripsnap.api.domain.entity.GroupMemberRequest;
import com.tripsnap.api.domain.entity.Member;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomGroupRepository {
    List<Group> getGroupsByMemberId(Pageable pageable, Long memberId);
    List<Group> getGroupsByMemberId(Long memberId);
    List<Member> getGroupMembersByGroupId(Pageable pageable, Long groupId);
    List<Member> getGroupMemberWaitingListByGroupId(Pageable pageable, Long groupId);

    void updateGroupOwner(Group group);
    List<GroupMemberRequest> getGroupInviteListByMemberId(Pageable pageable, long memberId);
}
