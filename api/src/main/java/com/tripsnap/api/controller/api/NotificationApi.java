package com.tripsnap.api.controller.api;

import com.tripsnap.api.domain.dto.PageDTO;
import com.tripsnap.api.domain.dto.RemoveNotificationDTO;
import com.tripsnap.api.domain.dto.ResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;

@Tag(name="notification", description = "알람 및 공지 관련 기능")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid status value",content = @Content())
})
public interface NotificationApi {
    @ApiResponse(responseCode = "200", description = "successful operation",useReturnTypeSchema = true)
    @Operation(summary = "알람 리스트")
    ResponseEntity<?> notificationList(User user, PageDTO pageDTO);
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class)))
    @Operation(summary = "알람 삭제")
    ResponseEntity<?> remove(User user, RemoveNotificationDTO param);
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class)))
    @Operation(summary = "알람 읽음처리")
    ResponseEntity<?> read(User user);
    @ApiResponse(responseCode = "200", description = "successful operation",useReturnTypeSchema = true,
            content=@Content(schemaProperties = @SchemaProperty(name = "existed", schema = @Schema(implementation = Boolean.class))))
    @Operation(summary = "새로운 알람 있는지 확인")
    ResponseEntity<?> checkNewNotification(User user);
}
