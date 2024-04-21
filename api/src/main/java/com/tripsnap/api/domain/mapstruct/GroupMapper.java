package com.tripsnap.api.domain.mapstruct;

import com.tripsnap.api.domain.dto.GroupDTO;
import com.tripsnap.api.domain.entity.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class GroupMapper {
    public abstract List<GroupDTO> toDTOList(List<Group> source);

    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy/MM/dd")
    public abstract GroupDTO toDTO(Group source);
}
