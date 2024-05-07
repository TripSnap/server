package com.tripsnap.api.domain.mapstruct;

import com.tripsnap.api.domain.dto.GroupDTO;
import com.tripsnap.api.domain.dto.GroupMemberRequestDTO;
import com.tripsnap.api.domain.entity.Group;
import com.tripsnap.api.domain.entity.GroupMemberRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class GroupMapper {
    public List<GroupDTO> toDTOList(List<Group> source, String email) {
        return source.stream().map((group) -> toDTO(group, email)).toList();
    }

    @Mapping(target = "createdAt", source = "source.createdAt", dateFormat = "yyyy/MM/dd")
    @Mapping(target="isOwner", expression = "java(email.equals(source.getOwner().getEmail()))")
    public abstract GroupDTO toDTO(Group source, String email);



    @Named(value="GroupMemberRequestDTO")
    public abstract List<GroupMemberRequestDTO> toRequestDTOList(List<GroupMemberRequest> source);

    @IterableMapping(qualifiedByName = "GroupMemberRequestDTO")
    @Mapping(target = "expiredAt", source = "expireDate", dateFormat = "yyyy/MM/dd HH:mm")
    @Mapping(target = "id", source = "id.groupId")
    @Mapping(target = "title", source = "group.title")
    @Mapping(target = "owner", source = "member")
    public abstract GroupMemberRequestDTO toRequestDTO(GroupMemberRequest source);
}
