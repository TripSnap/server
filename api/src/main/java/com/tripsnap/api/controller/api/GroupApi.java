package com.tripsnap.api.controller.api;


import com.tripsnap.api.domain.dto.GroupInsDTO;
import com.tripsnap.api.domain.dto.PageDTO;
import com.tripsnap.api.domain.dto.ProcessOption;
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

@Tag(name="group", description = "그룹과 관련된 API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid status value",content = @Content()),
        @ApiResponse(responseCode = "401", description = "access token 만료",content = @Content(), headers = @Header(name="WWW-Authenticate", description = "Refresh-Token", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "401 ", description = "로그인 필요",content = @Content(), headers = @Header(name="WWW-Authenticate", description = "Bearer", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "403", description = "권한에 맞지 않는 접근",content = @Content())
})
public interface GroupApi {
    @Operation(summary = "회원이 가입한 그룹 리스트", security = @SecurityRequirement(name = "Authorization"))
    @ApiResponse(responseCode = "200", description = "successful operation",useReturnTypeSchema = true)
    ResponseEntity<?> groups(User user, PageDTO param);
    @Operation(summary = "그룹 생성", security = @SecurityRequirement(name = "Authorization"))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class)))
    ResponseEntity<?> addGroup(User user, GroupInsDTO param);

    @Operation(summary = "그룹 삭제", security = @SecurityRequirement(name = "Authorization"))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class)))
    ResponseEntity<?> removeGroup(User user, Long groupId);

    @Operation(summary = "그룹 멤버 리스트", security = @SecurityRequirement(name = "Authorization"))
    @RequestBody(content = @Content(schemaProperties = {
            @SchemaProperty(name="page", schema = @Schema(implementation = Long.class)),
            @SchemaProperty(name="pagePerCnt", schema = @Schema(implementation = Long.class)),
            @SchemaProperty(name="groupId", schema = @Schema(implementation = Long.class))
    }))
    @ApiResponse(responseCode = "200", description = "successful operation",useReturnTypeSchema = true)
    ResponseEntity<?> groupMembers(User user, Map<String, Object> param);

    @Operation(summary = "그룹 탈퇴", security = @SecurityRequirement(name = "Authorization"))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class)))
    ResponseEntity<?> leaveGroup(User user, Long groupId);

    @Operation(summary = "그룹 초대 취소", security = @SecurityRequirement(name = "Authorization"))
    @RequestBody(content = @Content(schemaProperties = {
            @SchemaProperty(name="groupId", schema = @Schema(implementation = Long.class)),
            @SchemaProperty(name="memberId", schema = @Schema(implementation = Long.class))
    }))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class)))
    ResponseEntity<?> cancelInvite(User user, Map<String, Object> param);

    @Operation(summary = "그룹 초대 수락 및 거절", security = @SecurityRequirement(name = "Authorization"))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SuccessOrNot.class)))
    ResponseEntity<?> processInvite(User user, ProcessOption invite, Long groupId);
}
