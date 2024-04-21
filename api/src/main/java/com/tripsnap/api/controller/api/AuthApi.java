package com.tripsnap.api.controller.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name="auth", description = "로그인, 로그아웃, 토큰 갱신")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid status value",content = @Content()),
        @ApiResponse(responseCode = "401", description = "access token 만료",content = @Content(), headers = @Header(name="WWW-Authenticate", description = "Refresh-Token", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "401 ", description = "로그인 필요",content = @Content(), headers = @Header(name="WWW-Authenticate", description = "Bearer", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "403", description = "권한에 맞지 않는 접근",content = @Content())
})
public interface AuthApi {
    @Operation(summary = "토큰 갱신", security = @SecurityRequirement(name = "Authorization"))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(),
            headers = {
                @Header(name = "Authorization", description = "access token", schema = @Schema(implementation = String.class, example = "bearer {access token}"))
            }
    )
    @RequestBody(
            content = {@Content(schemaProperties = {
                    @SchemaProperty(name = "grant_type", schema = @Schema(implementation = String.class)),
                    @SchemaProperty(name = "token", schema = @Schema(implementation = String.class)),
            }, examples = @ExampleObject("""
                    {"grant_type":"refresh_token", "token":"{token}"}
                    """))}
    )
    ResponseEntity<?> refresh();
}
