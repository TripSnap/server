package com.tripsnap.api.controller.api;


import com.tripsnap.api.domain.dto.GroupInsDTO;
import com.tripsnap.api.domain.dto.PageDTO;
import com.tripsnap.api.domain.dto.ProcessOption;
import com.tripsnap.api.domain.dto.ResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;

import java.util.Map;

@Tag(name="group", description = "그룹과 관련된 API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid status value",content = @Content())
})
public interface GroupApi {
    @Operation(summary = "회원이 가입한 그룹 리스트")
    @ApiResponse(responseCode = "200", description = "successful operation",useReturnTypeSchema = true)
    ResponseEntity<?> groups(User user, PageDTO param);
    @Operation(summary = "그룹 생성")
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class)))
    ResponseEntity<?> addGroup(User user, GroupInsDTO param);

    @Operation(summary = "그룹 삭제")
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class)))
    ResponseEntity<?> removeGroup(User user, Long groupId);

    @Operation(summary = "그룹 멤버 리스트")
    @ApiResponse(responseCode = "200", description = "successful operation",useReturnTypeSchema = true)
    ResponseEntity<?> groupMembers(User user, Map<String, Object> param);

    @Operation(summary = "그룹 탈퇴")
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class)))
    ResponseEntity<?> leaveGroup(User user, Map<String, Object> param);

    @Operation(summary = "그룹 초대 취소")
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class)))
    ResponseEntity<?> cancelInvite(User user, Map<String, Object> param);

    @Operation(summary = "그룹 초대 수락 및 거절")
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SuccessOrNot.class)))
    ResponseEntity<?> processInvite(User user, ProcessOption invite, Map<String, Object> param);
}
