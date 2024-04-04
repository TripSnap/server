package com.tripsnap.api.controller.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name="auth", description = "로그인, 로그아웃, 토큰 갱신")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid status value",content = @Content())
})
public interface AuthApi {
    @Operation(summary = "토큰 갱신")
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content()
    )
    ResponseEntity<?> refresh();
}
