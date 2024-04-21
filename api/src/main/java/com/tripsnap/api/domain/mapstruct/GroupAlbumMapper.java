package com.tripsnap.api.domain.mapstruct;

import com.tripsnap.api.domain.dto.AlbumPhotoDTO;
import com.tripsnap.api.domain.dto.GroupAlbumDTO;
import com.tripsnap.api.domain.dto.GroupAlbumInsDTO;
import com.tripsnap.api.domain.entity.AlbumPhoto;
import com.tripsnap.api.domain.entity.GroupAlbum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = MemberMapper.class
)
public abstract class GroupAlbumMapper {
    public abstract GroupAlbumDTO toDTO(GroupAlbum entity);
    public abstract List<GroupAlbumDTO> toDTOList(List<GroupAlbum> entityList);

    @Mapping(target="albumPhotoList", ignore = true)
    public abstract GroupAlbum toGroupAlbumEntity(GroupAlbumInsDTO dto, Long memberId);


    public abstract AlbumPhotoDTO toAlbumDTO(AlbumPhoto entity);
    public abstract List<AlbumPhotoDTO> toAlbumDTOList(List<AlbumPhoto> entityList);

}
