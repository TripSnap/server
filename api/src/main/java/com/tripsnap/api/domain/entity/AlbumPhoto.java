package com.tripsnap.api.domain.entity;

import com.tripsnap.api.domain.entity.base.BaseEntity;
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
public class AlbumPhoto extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="album_photo_id")
    private Long id;
    private Long memberId;
    @ManyToOne
    @JoinColumn(name = "album_id")
    private GroupAlbum groupAlbum;
    @Column(nullable = false)
    private String photo;

    @Transient
    private Member member;
}
