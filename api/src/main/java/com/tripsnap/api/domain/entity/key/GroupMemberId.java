package com.tripsnap.api.domain.entity.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class GroupMemberId implements Serializable {

    @Column(name = "group_id")
    private Long groupId;
    @Column(name = "member_id")
    private Long memberId;

    @Override
    public int hashCode() {
        return Objects.hash(groupId, memberId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupMemberId groupMemberId = (GroupMemberId) o;
        return Objects.equals(groupId, groupMemberId.groupId) && Objects.equals(memberId, groupMemberId.memberId);
    }
}
