package com.tripsnap.api.controller;


import com.tripsnap.api.auth.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private TokenService tokenService;

    @PostMapping("/refresh")
    public ResponseEntity refresh() {
        return ResponseEntity.ok(null);
    }
}
