package com.tripsnap.api.service;

import com.tripsnap.api.domain.dto.NotificationDTO;
import com.tripsnap.api.domain.dto.PageDTO;
import com.tripsnap.api.domain.dto.ResultDTO;
import com.tripsnap.api.domain.entity.Member;
import com.tripsnap.api.domain.entity.Notification;
import com.tripsnap.api.domain.mapstruct.NotificationMapper;
import com.tripsnap.api.exception.ServiceException;
import com.tripsnap.api.repository.MemberRepository;
import com.tripsnap.api.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final NotificationMapper notificationMapper;

    // 알림 리스트
    public ResultDTO.SimpleWithPageData<List<NotificationDTO>> notificationList(String email, PageDTO pageDTO) {
        Optional<Member> optMember = memberRepository.findByEmail(email);
        if(optMember.isPresent()) {
            Member member = optMember.get();
            Pageable pageable = Pageable.ofSize(pageDTO.pagePerCnt()).withPage(pageDTO.page());
            List<Notification> notifications = notificationRepository.getNotificationList(pageable,member.getId());

            return ResultDTO.WithPageData(pageable, notificationMapper.toDTOList(notifications));
        }
        throw ServiceException.BadRequestException();
    }

    // 알림 삭제
    public ResultDTO.SimpleSuccessOrNot remove(String email, List<Long> ids) {
        Optional<Member> optMember = memberRepository.findByEmail(email);
        if(optMember.isPresent()) {
            Member member = optMember.get();
            notificationRepository.deleteByMemberIdAndIdIn(member.getId(), ids);
            return ResultDTO.SuccessOrNot(true);
        }
        throw ServiceException.BadRequestException();
    }

    // 알람 읽기
    public ResultDTO.SimpleSuccessOrNot read(String email) {
        Optional<Member> optMember = memberRepository.findByEmail(email);
        if(optMember.isPresent()) {
            Member member = optMember.get();
            notificationRepository.readNotification(member.getId());
            return ResultDTO.SuccessOrNot(true);
        }
        throw ServiceException.BadRequestException();
    }

    // 새로운 알림 있는지 확인
    public ResultDTO.SimpleWithData<Map<String, Boolean>> checkNewNotification(String email) {
        Optional<Member> optMember = memberRepository.findByEmail(email);
        if(optMember.isPresent()) {
            Member member = optMember.get();
            boolean existed = notificationRepository.existNewNotification(member.getId());
            return ResultDTO.WithData(Map.of("existed", existed));
        }
        throw ServiceException.BadRequestException();
    }

    // 알람 추가
    public void sendNotification(long memberId, String content) {
        Notification notification = Notification.builder().title(content).memberId(memberId).build();
        notificationRepository.save(notification);

    }
}
