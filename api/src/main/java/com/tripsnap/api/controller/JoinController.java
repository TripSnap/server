package com.tripsnap.api.controller;


import com.tripsnap.api.controller.api.JoinApi;
import com.tripsnap.api.controller.response.ResponseDTO;
import com.tripsnap.api.domain.dto.JoinDTO;
import com.tripsnap.api.domain.dto.VerifyCodeDTO;
import com.tripsnap.api.service.JoinService;
import com.tripsnap.api.utils.ParameterUtil;
import com.tripsnap.api.utils.ValidationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/join")
@RestController
@RequiredArgsConstructor
public class JoinController implements JoinApi {
    private final JoinService joinService;

    @PostMapping
    @Override
    public ResponseEntity<?> join(@Valid @RequestBody JoinDTO param) {
        return ResponseEntity.ok(ResponseDTO.SuccessOrNot(joinService.join(param)));
    }

    @PostMapping("/check-email")
    @Override
    public ResponseEntity<?> checkEmail(@RequestBody Map<String, String> param) {
        String email = String.valueOf(param.get("email"));
        ParameterUtil.validation(email, ValidationType.Email);
        return ResponseEntity.ok(ResponseDTO.SuccessOrNot(joinService.checkEmail(email)));
    }

    @PostMapping("/verify-code")
    @Override
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyCodeDTO param) {
        return ResponseEntity.ok(ResponseDTO.SuccessOrNot(joinService.verifyEmailCode(param.email(), param.code())));
    }
}
