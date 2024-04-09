package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.GroupAlbum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupAlbumRepository extends JpaRepository<GroupAlbum, Long> {
    void deleteById(Long id);
    Optional<GroupAlbum> getGroupAlbumById(Long id);
}
