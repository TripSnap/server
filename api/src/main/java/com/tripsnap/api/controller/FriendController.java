package com.tripsnap.api.controller;


import com.tripsnap.api.controller.api.FriendApi;
import com.tripsnap.api.domain.dto.MemberDTO;
import com.tripsnap.api.domain.dto.PageDTO;
import com.tripsnap.api.domain.dto.ResultDTO;
import com.tripsnap.api.domain.dto.option.FriendListOption;
import com.tripsnap.api.domain.dto.option.ProcessOption;
import com.tripsnap.api.service.FriendService;
import com.tripsnap.api.utils.ParameterUtil;
import com.tripsnap.api.utils.ValidationType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/friend", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class FriendController implements FriendApi {
    private final FriendService friendService;

    @GetMapping("/list")
    @Override
    public ResponseEntity<ResultDTO.SimpleWithPageData<List<MemberDTO>>> friendList(@AuthenticationPrincipal User user, @RequestParam Map<String, Object> param) {
        PageDTO pageDTO = ParameterUtil.validationAndConvert(param, PageDTO.class);
        FriendListOption option = ParameterUtil.validationAndConvert(param.get("option"), ValidationType.PrimitiveWrapper.FriendListOptionType, FriendListOption.class);
        if(option == FriendListOption.all) {
            return ResponseEntity.ok(friendService.getAllFriendList(user.getUsername(), pageDTO));
        }
        if(option == FriendListOption.active) {
            return ResponseEntity.ok(friendService.getFriendList(user.getUsername(), pageDTO));
        }
        return null;
    }

    @GetMapping("/send-request/list")
    @Override
    public ResponseEntity<ResultDTO.SimpleWithPageData<List<MemberDTO>>> friendSendRequestList(@AuthenticationPrincipal User user, PageDTO param) {
        return ResponseEntity.ok(friendService.getFriendRequestSendList(user.getUsername(), param));
    }

    @PostMapping("/search")
    @Override
    public ResponseEntity<?> search(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> param) {
        String email = ParameterUtil.validationAndConvert(param.get("email"), ValidationType.PrimitiveWrapper.Email);
        return ResponseEntity.ok(friendService.searchMember(user.getUsername(), email));
    }

    @PostMapping("/send-request")
    @Override
    public ResponseEntity<?> sendRequest(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> param) {
        String email = ParameterUtil.validationAndConvert(param.get("email"), ValidationType.PrimitiveWrapper.Email);
        return ResponseEntity.ok(friendService.sendRequest(user.getUsername(), email));
    }

    @PostMapping("/send-request/remove")
    @Override
    public ResponseEntity<?> removeSendRequest(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> param) {
        String email = ParameterUtil.validationAndConvert(param.get("email"), ValidationType.PrimitiveWrapper.Email);
        return ResponseEntity.ok(friendService.removeSendRequest(user.getUsername(), email));
    }

    @PostMapping("/{option:allow|deny}-request")
    @Override
    public ResponseEntity<?> processRequest(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> param, @PathVariable("option") ProcessOption option) {
        String email = ParameterUtil.validationAndConvert(param.get("email"), ValidationType.PrimitiveWrapper.Email);
        return ResponseEntity.ok(friendService.processFriendRequest(user.getUsername(), email, option.isAllow()));
    }

    @PostMapping("/remove")
    @Override
    public ResponseEntity<?> remove(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> param) {
        String email = ParameterUtil.validationAndConvert(param.get("email"), ValidationType.PrimitiveWrapper.Email);
        return ResponseEntity.ok(friendService.removeFriend(user.getUsername(), email));
    }
}
