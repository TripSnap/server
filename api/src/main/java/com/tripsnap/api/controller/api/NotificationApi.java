package com.tripsnap.api.controller.api;

import com.tripsnap.api.domain.dto.PageDTO;
import com.tripsnap.api.domain.dto.RemoveNotificationDTO;
import com.tripsnap.api.domain.dto.ResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;

@Tag(name="notification", description = "알람 및 공지 관련 기능")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid status value",content = @Content()),
        @ApiResponse(responseCode = "401", description = "access token 만료",content = @Content(), headers = @Header(name="WWW-Authenticate", description = "Refresh-Token", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "401 ", description = "로그인 필요",content = @Content(), headers = @Header(name="WWW-Authenticate", description = "Bearer", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "403", description = "권한에 맞지 않는 접근",content = @Content())
})
public interface NotificationApi {
    @ApiResponse(responseCode = "200", description = "successful operation",useReturnTypeSchema = true)
    @Operation(summary = "알람 리스트", security = @SecurityRequirement(name = "access-token"))
    ResponseEntity<?> notificationList(User user, PageDTO pageDTO);
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class)))
    @Operation(summary = "알람 삭제", security = @SecurityRequirement(name = "access-token"))
    ResponseEntity<?> remove(User user, RemoveNotificationDTO param);
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class)))
    @Operation(summary = "알람 읽음처리", security = @SecurityRequirement(name = "access-token"))
    ResponseEntity<?> read(User user);
    @ApiResponse(responseCode = "200", description = "successful operation",useReturnTypeSchema = true,
            content=@Content(schemaProperties = @SchemaProperty(name = "existed", schema = @Schema(implementation = Boolean.class))))
    @Operation(summary = "새로운 알람 있는지 확인", security = @SecurityRequirement(name = "access-token"))
    ResponseEntity<?> checkNewNotification(User user);
}
