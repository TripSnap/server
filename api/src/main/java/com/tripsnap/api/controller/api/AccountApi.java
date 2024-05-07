package com.tripsnap.api.controller.api;

import com.tripsnap.api.domain.dto.MemberDTO;
import com.tripsnap.api.domain.dto.MemberEditDTO;
import com.tripsnap.api.domain.dto.MemberPasswordEditDTO;
import com.tripsnap.api.domain.dto.ResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;

import java.util.Map;

@Tag(name="account", description = "계정과 관련된 API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid status value",content = @Content()),
        @ApiResponse(responseCode = "401", description = "access token 만료",content = @Content(), headers = @Header(name="WWW-Authenticate", description = "Refresh-Token", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "401 ", description = "로그인 필요",content = @Content(), headers = @Header(name="WWW-Authenticate", description = "Bearer", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "403", description = "권한에 맞지 않는 접근",content = @Content())
})
public interface AccountApi {
    @Operation(summary = "회원 탈퇴", security = @SecurityRequirement(name = "access-token"))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class))
    )
    ResponseEntity<?> leave(User user);

    @Operation(summary = "회원 정보 가져오기", security = @SecurityRequirement(name = "access-token"))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = MemberDTO.class))
    )
    ResponseEntity<?> getUserData(User user);

    @Operation(summary = "회원 정보 변경", security = @SecurityRequirement(name = "access-token"))
    @RequestBody(content = @Content(schema = @Schema()))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class))
    )
    ResponseEntity<?> updateData(User user, MemberEditDTO param);

    @Operation(summary = "회원 비밀번호 변경", security = @SecurityRequirement(name = "access-token"))
    @RequestBody(content = @Content(schema = @Schema()))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SuccessOrNot.class))
    )
    ResponseEntity<?> updatePassword(User user, MemberPasswordEditDTO param);


    @Operation(summary = "계정 찾기")
    @RequestBody(content = @Content(schemaProperties = {
            @SchemaProperty(name="email", schema = @Schema(implementation = String.class))
    }))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SuccessOrNot.class))
    )
    ResponseEntity<?> find(Map<String, Object> param);
}
