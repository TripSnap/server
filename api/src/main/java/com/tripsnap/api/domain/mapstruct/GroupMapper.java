package com.tripsnap.api.domain.mapstruct;

import com.tripsnap.api.domain.dto.GroupDTO;
import com.tripsnap.api.domain.dto.GroupMemberRequestDTO;
import com.tripsnap.api.domain.entity.Group;
import com.tripsnap.api.domain.entity.GroupMemberRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class GroupMapper {
    public abstract List<GroupDTO> toDTOList(List<Group> source);

    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy/MM/dd")
    public abstract GroupDTO toDTO(Group source);



    @Named(value="GroupMemberRequestDTO")
    public abstract List<GroupMemberRequestDTO> toRequestDTOList(List<GroupMemberRequest> source);

    @IterableMapping(qualifiedByName = "GroupMemberRequestDTO")
    @Mapping(target = "expiredAt", source = "expireDate", dateFormat = "yyyy/MM/dd HH:mm")
    @Mapping(target = "id", source = "id.groupId")
    @Mapping(target = "title", source = "group.title")
    @Mapping(target = "owner", source = "member")
    public abstract GroupMemberRequestDTO toRequestDTO(GroupMemberRequest source);
}
