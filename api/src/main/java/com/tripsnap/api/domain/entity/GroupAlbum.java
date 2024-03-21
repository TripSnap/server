package com.tripsnap.api.domain.entity;

import com.tripsnap.api.domain.entity.base.BaseEntity;
import jakarta.persistence.*;

@Entity
public class GroupAlbum extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="album_id")
    private Long id;
    @Column(nullable = false)
    private Double latitude;
    @Column(nullable = false)
    private Double longitude;
    private String address;
    private String thumbnail;
}
