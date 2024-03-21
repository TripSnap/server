package com.tripsnap.api.domain.entity;


import com.tripsnap.api.domain.entity.base.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="travel_group")
public class Group extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="group_id")
    private Long id;
    @OneToOne
    @JoinColumn(name="member_id")
    private Member ownerId;
    @OneToMany
    @JoinColumn(name="album_id")
    List<GroupAlbum> groupAlbums = new ArrayList<>();
}
