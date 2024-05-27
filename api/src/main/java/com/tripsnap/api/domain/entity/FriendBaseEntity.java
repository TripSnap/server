package com.tripsnap.api.domain.entity;


import com.tripsnap.api.domain.entity.base.BaseEntity;
import com.tripsnap.api.domain.entity.key.MemberFriendId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@MappedSuperclass
@SuperBuilder(builderMethodName = "friendBuilder")
@NoArgsConstructor
@AllArgsConstructor
public abstract class FriendBaseEntity extends BaseEntity {
    @EmbeddedId
    private MemberFriendId id;

    /**
     * friend_id에 해당하는 Member
     */
    @MapsId("friendId")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "friendId", referencedColumnName = "member_id")
    private Member member;
}
