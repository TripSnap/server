package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.AlbumPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumPhotoRepository extends JpaRepository<AlbumPhoto, Long> {
    void deleteAllByIdIn(List<Long> ids);
}
