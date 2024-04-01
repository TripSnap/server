package com.tripsnap.api.controller;


import com.tripsnap.api.controller.api.GroupApi;
import com.tripsnap.api.controller.response.ResponseDTO;
import com.tripsnap.api.domain.dto.GroupDTO;
import com.tripsnap.api.domain.dto.GroupInsDTO;
import com.tripsnap.api.domain.dto.PageDTO;
import com.tripsnap.api.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController implements GroupApi {
    private final GroupService groupService;

    @Override
    @GetMapping("/list")
    public ResponseEntity<ResponseDTO.SimpleWithPageData<List<GroupDTO>>> groups(@AuthenticationPrincipal User user, @Valid @RequestParam PageDTO param) {
        return ResponseEntity.ok(groupService.getGroupList(user.getUsername(), param));
    }

    @Override
    @PostMapping
    public ResponseEntity<?> addGroup(@AuthenticationPrincipal User user, @Valid @RequestBody GroupInsDTO param) {
        return ResponseEntity.ok(groupService.createGroup(user.getUsername(), param));
    }

    @Override
    @DeleteMapping("/{group-id}")
    public ResponseEntity<?> removeGroup(@AuthenticationPrincipal User user, @PathVariable("group-id") Long groupId) {
        return ResponseEntity.ok(groupService.deleteGroup(user.getUsername(), groupId));
    }


}
