package com.tripsnap.api.repository;


import com.tripsnap.api.domain.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    void removeGroupByOwnerIdAndId(Long ownerId, Long groupId);
}
