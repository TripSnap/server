package com.tripsnap.api.controller;

import com.tripsnap.api.controller.api.NotificationApi;
import com.tripsnap.api.domain.dto.NotificationDTO;
import com.tripsnap.api.domain.dto.PageDTO;
import com.tripsnap.api.domain.dto.ResultDTO;
import com.tripsnap.api.service.NotificationService;
import com.tripsnap.api.utils.ParameterUtil;
import com.tripsnap.api.utils.ValidationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/notification", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {
    private final NotificationService notificationService;

    @GetMapping("/list")
    @Override
    public ResponseEntity<ResultDTO.SimpleWithPageData<List<NotificationDTO>>> notificationList(
            @AuthenticationPrincipal User user, @Valid PageDTO pageDTO
    ) {
        return ResponseEntity.ok(notificationService.notificationList(user.getUsername(), pageDTO));
    }

    @DeleteMapping("/{notification-id:\\d+}")
    @Override
    public ResponseEntity<?> remove(@AuthenticationPrincipal User user, @PathVariable("notification-id") Long id) {
        ParameterUtil.validation(id, ValidationType.PrimitiveWrapper.EntityId);
        return ResponseEntity.ok(notificationService.remove(user.getUsername(), id));
    }

    @GetMapping("/read")
    @Override
    public ResponseEntity<?> read(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.read(user.getUsername()));
    }

    @GetMapping("/check-new")
    @Override
    public ResponseEntity<ResultDTO.SimpleWithData<Map<String, Boolean>>> checkNewNotification(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(notificationService.checkNewNotification(user.getUsername()));
    }
}
