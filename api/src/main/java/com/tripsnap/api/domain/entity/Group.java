package com.tripsnap.api.domain.entity;


import com.tripsnap.api.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name="travel_group")
public class Group extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="group_id")
    private Long id;
    private String title;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="owner_id", referencedColumnName = "member_id")
    private Member owner;
    @OneToMany
    @JoinColumn(name="album_id")
    private List<GroupAlbum> groupAlbums = new ArrayList<>();

    // TODO: 이것이.. 최선인가..?
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
    private List<GroupMember> members = new ArrayList<>();

    @Setter
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
    private List<GroupMemberRequest> memberRequests = new ArrayList<>();
//    @OneToMany(cascade = CascadeType.ALL)
//    @JoinColumn(name="group_id")
//    private List<GroupMemberRequest> memberRequests = new ArrayList<>();
}
