package com.tripsnap.api.domain.entity;

import com.tripsnap.api.domain.entity.base.BaseEntity;
import com.tripsnap.api.domain.entity.key.GroupMemberId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class GroupMember extends BaseEntity {
    @EmbeddedId
    private GroupMemberId id;
    @OneToOne
    @PrimaryKeyJoinColumn(name = "member_id")
    private Member member;
    @ManyToOne
    @JoinColumn(name="group_id",insertable=false, updatable=false)
    private Group group;
}
