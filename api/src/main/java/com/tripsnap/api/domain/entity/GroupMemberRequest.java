package com.tripsnap.api.domain.entity;

import com.tripsnap.api.domain.entity.base.BaseEntity;
import com.tripsnap.api.domain.entity.key.GroupMemberId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class GroupMemberRequest extends BaseEntity {
    @EmbeddedId
    GroupMemberId id;
    @OneToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="group_id",insertable=false, updatable=false)
    private Group group;

    public LocalDateTime getExpireDate() {
        return createdAt.plusDays(7);
    }
}
