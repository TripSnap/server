package com.tripsnap.api.domain.entity;

import com.tripsnap.api.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AlbumPhoto extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="album_photo_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "album_id")
    private GroupAlbum groupAlbum;
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;
    @Column(nullable = false)
    private String photo;
}
