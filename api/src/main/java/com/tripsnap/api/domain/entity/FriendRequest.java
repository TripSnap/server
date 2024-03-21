package com.tripsnap.api.domain.entity;

import com.tripsnap.api.domain.entity.base.BaseEntity;
import com.tripsnap.api.domain.entity.key.MemberFriendId;
import jakarta.persistence.*;

@Entity
public class FriendRequest extends BaseEntity {
    @EmbeddedId
    private MemberFriendId id;
    @OneToOne
    @JoinColumn(name = "friend_id", referencedColumnName="member_id")
    @PrimaryKeyJoinColumn
    private Member member;
}
