package com.tripsnap.api.repository;


import com.tripsnap.api.domain.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long>, CustomGroupRepository {
    void removeGroupByIdAndOwnerId(Long groupId,Long ownerId);
    Optional<Group> findGroupById(Long groupId);
}
