package com.tripsnap.api.controller;


import com.tripsnap.api.auth.TokenService;
import com.tripsnap.api.controller.api.AuthApi;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private TokenService tokenService;

    /**
     * {@link com.tripsnap.api.auth.JWTFilter#hasRefreshToken(HttpServletRequest)}
     * @return
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh() {
        return ResponseEntity.ok(null);
    }
}

