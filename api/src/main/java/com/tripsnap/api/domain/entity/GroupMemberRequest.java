package com.tripsnap.api.domain.entity;

import com.tripsnap.api.domain.entity.base.BaseEntity;
import com.tripsnap.api.domain.entity.key.GroupMemberId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class GroupMemberRequest extends BaseEntity {
    @EmbeddedId
    GroupMemberId id;
}
