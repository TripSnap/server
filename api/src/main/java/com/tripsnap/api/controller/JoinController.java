package com.tripsnap.api.controller;


import com.tripsnap.api.controller.api.JoinApi;
import com.tripsnap.api.domain.dto.JoinDTO;
import com.tripsnap.api.domain.dto.ResultDTO;
import com.tripsnap.api.domain.dto.VerifyCodeDTO;
import com.tripsnap.api.service.JoinService;
import com.tripsnap.api.utils.ParameterUtil;
import com.tripsnap.api.utils.ValidationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping(value = "/join", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class JoinController implements JoinApi {
    private final JoinService joinService;

    @PostMapping
    @Override
    public ResponseEntity<?> join(@Valid @RequestBody JoinDTO param) {
        return ResponseEntity.ok(ResultDTO.SuccessOrNot(joinService.join(param)));
    }

    @PostMapping("/check-email")
    @Override
    public ResponseEntity<?> checkEmail(@RequestBody Map<String, Object> param) {
        String email = ParameterUtil.validationAndConvert(param.get("email"), ValidationType.PrimitiveWrapper.Email);
        return ResponseEntity.ok(ResultDTO.SuccessOrNot(joinService.checkEmail(email)));
    }

    @PostMapping("/verify-code")
    @Override
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyCodeDTO param) {
        return ResponseEntity.ok(ResultDTO.SuccessOrNot(joinService.verifyEmailCode(param.email(), param.code())));
    }
}
