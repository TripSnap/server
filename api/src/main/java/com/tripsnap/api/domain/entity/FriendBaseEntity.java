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
    @OneToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "friend_id")
    private Member member;
}
