package com.tripsnap.api.service;

import com.tripsnap.api.domain.dto.NotificationDTO;
import com.tripsnap.api.domain.dto.PageDTO;
import com.tripsnap.api.domain.dto.ResultDTO;
import com.tripsnap.api.domain.entity.Member;
import com.tripsnap.api.domain.entity.Notification;
import com.tripsnap.api.domain.mapstruct.NotificationMapper;
import com.tripsnap.api.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final PermissionCheckService permissionCheckService;

    // 알림 리스트
    public ResultDTO.SimpleWithPageData<List<NotificationDTO>> notificationList(String email, PageDTO pageDTO) {
        Member member = permissionCheckService.getMember(email);
        Pageable pageable = Pageable.ofSize(pageDTO.pagePerCnt()).withPage(pageDTO.page());
        List<Notification> notifications = notificationRepository.getNotificationList(pageable,member.getId());

        return ResultDTO.WithPageData(pageable, notificationMapper.toDTOList(notifications));
    }

    // 알림 삭제
    @Transactional
    public ResultDTO.SimpleSuccessOrNot remove(String email, Long id) {
        Member member = permissionCheckService.getMember(email);
        notificationRepository.deleteByMemberIdAndId(member.getId(), id);
        return ResultDTO.SuccessOrNot(true);
    }

    // 알람 읽기
    public ResultDTO.SimpleSuccessOrNot read(String email) {
        Member member = permissionCheckService.getMember(email);
        notificationRepository.readNotification(member.getId());
        return ResultDTO.SuccessOrNot(true);
    }

    // 새로운 알림 있는지 확인
    public ResultDTO.SimpleWithData<Map<String, Boolean>> checkNewNotification(String email) {
        Member member = permissionCheckService.getMember(email);
        boolean existed = notificationRepository.existNewNotification(member.getId());
        return ResultDTO.WithData(Map.of("existed", existed));
    }

    // 알람 추가
    public void sendNotification(long memberId, String content) {
        Notification notification = Notification.builder().title(content).memberId(memberId).build();
        notificationRepository.save(notification);

    }
}
