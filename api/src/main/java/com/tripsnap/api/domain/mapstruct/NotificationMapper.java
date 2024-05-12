package com.tripsnap.api.domain.mapstruct;

import com.tripsnap.api.domain.dto.NotificationDTO;
import com.tripsnap.api.domain.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class NotificationMapper {
    public abstract List<NotificationDTO> toDTOList(List<Notification> source);

    @Mapping(target="isBroadCast", expression = "java(source.getMemberId() == null)")
    @Mapping(target="date", source="createdAt", dateFormat = "yyyy/MM/dd HH:mm")
    public abstract NotificationDTO toNotificationDTO(Notification source);
}
