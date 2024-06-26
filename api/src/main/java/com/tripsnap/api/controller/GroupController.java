package com.tripsnap.api.controller;


import com.tripsnap.api.controller.api.GroupApi;
import com.tripsnap.api.domain.dto.*;
import com.tripsnap.api.domain.dto.option.ProcessOption;
import com.tripsnap.api.service.GroupService;
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
@RequestMapping(value = "/group", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GroupController implements GroupApi {
    private final GroupService groupService;

    @Override
    @GetMapping(value = "/list", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<ResultDTO.SimpleWithPageData<List<GroupDTO>>> groups(@AuthenticationPrincipal User user, @Valid PageDTO param) {
        return ResponseEntity.ok(groupService.getGroupList(user.getUsername(), param));
    }

    @Override
    @GetMapping(value = "/{group-id:\\d+}")
    public ResponseEntity<?> group(@AuthenticationPrincipal User user, @PathVariable("group-id") Long groupId) {
        ParameterUtil.validation(groupId, ValidationType.PrimitiveWrapper.EntityId);
        return ResponseEntity.ok(groupService.getGroup(user.getUsername(), groupId));
    }

    @Override
    @PostMapping
    public ResponseEntity<?> addGroup(@AuthenticationPrincipal User user, @Valid @RequestBody GroupInsDTO param) {
        return ResponseEntity.ok(groupService.createGroup(user.getUsername(), param));
    }

    @Override
    @DeleteMapping("/{group-id:\\d+}")
    public ResponseEntity<?> removeGroup(@AuthenticationPrincipal User user, @PathVariable("group-id") Long groupId) {
        ParameterUtil.validation(groupId, ValidationType.PrimitiveWrapper.EntityId);
        return ResponseEntity.ok(groupService.deleteGroup(user.getUsername(), groupId));
    }

    @Override
    @GetMapping("/invite/list")
    public ResponseEntity<ResultDTO.SimpleWithPageData<List<GroupMemberRequestDTO>>> inviteList(@AuthenticationPrincipal User user, @Valid PageDTO param) {
        return ResponseEntity.ok(groupService.getGroupInviteList(user.getUsername(), param));
    }

    @PostMapping("/members")
    @Override
    public ResponseEntity<ResultDTO.SimpleWithPageData<List<MemberDTO>>> groupMembers(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> param) {
        Long id = ParameterUtil.validationAndConvert(String.valueOf(param.get("groupId")), ValidationType.PrimitiveWrapper.EntityId, Long.class);
        PageDTO pageDTO = ParameterUtil.validationAndConvert(param, PageDTO.class);

        return ResponseEntity.ok(groupService.getMemberList(user.getUsername(), id, pageDTO));
    }

    @GetMapping("/leave/{group-id:\\d+}")
    @Override
    public ResponseEntity<?> leaveGroup(@AuthenticationPrincipal User user, @PathVariable("group-id") Long groupId) {
        ParameterUtil.validation(groupId, ValidationType.PrimitiveWrapper.EntityId);
        return ResponseEntity.ok(groupService.leaveGroup(user.getUsername(), groupId));
    }

    @PostMapping("/cancel-invite")
    @Override
    public ResponseEntity<?> cancelInvite(@AuthenticationPrincipal User user,@RequestBody Map<String, Object> param) {
        Long id = ParameterUtil.validationAndConvert(param.get("groupId"), ValidationType.PrimitiveWrapper.EntityId, Long.class);
        String email = ParameterUtil.validationAndConvert(param.get("email"), ValidationType.PrimitiveWrapper.Email);
        return ResponseEntity.ok(groupService.cancelInvite(user.getUsername(), id, email));
    }

    @GetMapping("/{option:allow|deny}-invite/{group-id:\\d+}")
    @Override
    public ResponseEntity<?> processInvite(@AuthenticationPrincipal User user, @PathVariable("option") ProcessOption option, @PathVariable("group-id") Long groupId) {
        ParameterUtil.validation(groupId, ValidationType.PrimitiveWrapper.EntityId);
        return ResponseEntity.ok(groupService.processInvite(user.getUsername(), groupId, option.isAllow()));
    }


}
