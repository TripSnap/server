package com.tripsnap.api.domain.entity.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MemberFriendId implements Serializable {

    @Column(name = "friend_id")
    private Long friendId;
    @Column(name = "member_id")
    private Long memberId;

    @Override
    public int hashCode() {
        return Objects.hash(friendId, memberId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberFriendId memberFriendId = (MemberFriendId) o;
        return Objects.equals(friendId, memberFriendId.friendId) && Objects.equals(memberId, memberFriendId.memberId);
    }
}
