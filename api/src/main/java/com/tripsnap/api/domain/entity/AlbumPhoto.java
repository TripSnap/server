package com.tripsnap.api.domain.entity;

import com.tripsnap.api.domain.entity.base.BaseEntity;
import jakarta.persistence.*;

@Entity
public class AlbumPhoto extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="album_photo_id")
    private Long id;
    @ManyToOne
    private GroupAlbum groupAlbum;
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;
    @Column(nullable = false)
    private String photo;
}
