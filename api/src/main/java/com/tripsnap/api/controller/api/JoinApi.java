package com.tripsnap.api.controller.api;

import com.tripsnap.api.domain.dto.JoinDTO;
import com.tripsnap.api.domain.dto.ResultDTO;
import com.tripsnap.api.domain.dto.VerifyCodeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Tag(name="join", description = "회원가입, 계정찾기 등")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid status value",content = @Content()),
        @ApiResponse(responseCode = "403", description = "권한에 맞지 않는 접근",content = @Content())
})
public interface JoinApi {
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SuccessOrNot.class)))
    @Operation(summary = "회원가입")
    ResponseEntity<?> join(JoinDTO param);
    
    @RequestBody(
            content = @Content(
                    schemaProperties = {@SchemaProperty(name="email",schema = @Schema(implementation = String.class))}
            ))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SuccessOrNot.class)))
    @Operation(summary = "이메일 중복체크")
    ResponseEntity<?> checkEmail( Map<String, Object> param);
    
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class)))
    @Operation(summary = "이메일 코드 인증")
    ResponseEntity<?> verifyCode(VerifyCodeDTO param);
}
