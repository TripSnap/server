package com.tripsnap.api.controller;

import com.tripsnap.api.controller.api.AccountApi;
import com.tripsnap.api.domain.dto.MemberEditDTO;
import com.tripsnap.api.domain.dto.MemberPasswordEditDTO;
import com.tripsnap.api.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/account", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AccountController implements AccountApi {
    private final AccountService accountService;

    @GetMapping("/leave")
    @Override
    public ResponseEntity<?> leave(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.leave(user.getUsername()));
    }

    @GetMapping("/user")
    @Override
    public ResponseEntity<?> getUserData(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getUserData(user.getUsername()));
    }

    @PatchMapping("/user")
    @Override
    public ResponseEntity<?> updateData(@AuthenticationPrincipal User user, @Valid @RequestBody MemberEditDTO param) {
        return ResponseEntity.ok(accountService.editUserData(user.getUsername(), param));
    }

    @PatchMapping("/user/password")
    @Override
    public ResponseEntity<?> updatePassword(@AuthenticationPrincipal User user, @Valid @RequestBody MemberPasswordEditDTO param) {
        return ResponseEntity.ok(accountService.editUserPassword(user.getUsername(), param));
    }

    @PostMapping("/find")
    @Override
    public ResponseEntity<?> find(@RequestBody  Map<String, Object> param) {
        return null;
    }
}
