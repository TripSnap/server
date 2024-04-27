package com.tripsnap.api.controller.api;


import com.tripsnap.api.domain.dto.PageDTO;
import com.tripsnap.api.domain.dto.ResultDTO;
import com.tripsnap.api.domain.dto.SearchMemberDTO;
import com.tripsnap.api.domain.dto.option.FriendListOption;
import com.tripsnap.api.domain.dto.option.ProcessOption;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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

@Tag(name="friend", description = "친구 기능")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid status value",content = @Content()),
        @ApiResponse(responseCode = "401", description = "access token 만료",content = @Content(), headers = @Header(name="WWW-Authenticate", description = "Refresh-Token", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "401 ", description = "로그인 필요",content = @Content(), headers = @Header(name="WWW-Authenticate", description = "Bearer", schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "403", description = "권한에 맞지 않는 접근",content = @Content())
})
public interface FriendApi {
    @Operation(summary = "친구 목록", security = @SecurityRequirement(name = "Authorization"))
    @Parameters({
            @Parameter(name = "page", schema = @Schema(implementation = Long.class)),
            @Parameter(name = "pagePerCnt", schema = @Schema(implementation = Long.class)),
            @Parameter(name = "option", schema = @Schema(implementation = FriendListOption.class))
    })
    @ApiResponse(responseCode = "200", description = "successful operation",useReturnTypeSchema = true)
    ResponseEntity<?> friendList(User user, @Parameter(hidden = true) Map<String, Object> param);

    @Operation(summary = "보낸 친구 신청 목록", security = @SecurityRequirement(name = "Authorization"))
    @Parameters({
            @Parameter(name = "page", schema = @Schema(implementation = Long.class)),
            @Parameter(name = "pagePerCnt", schema = @Schema(implementation = Long.class)),
    })
    @ApiResponse(responseCode = "200", description = "successful operation",useReturnTypeSchema = true)
    ResponseEntity<?> friendSendRequestList(User user, @Parameter(hidden = true) PageDTO param);

    @Operation(summary = "친구 검색", security = @SecurityRequirement(name = "Authorization"))
    @RequestBody(content = @Content( schemaProperties = @SchemaProperty(name="email", schema = @Schema(implementation = String.class) )))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schemaProperties = @SchemaProperty(name="data", schema = @Schema(implementation = SearchMemberDTO.class)))
    )
    ResponseEntity<?> search(User user, Map<String, Object> param);


    @Operation(summary = "친구 신청", security = @SecurityRequirement(name = "Authorization"))
    @RequestBody(content = @Content( schemaProperties = @SchemaProperty(name="email", schema = @Schema(implementation = String.class) )))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class))
    )
    ResponseEntity<?> sendRequest(User user, Map<String, Object> param);

    @Operation(summary = "친구 신청 삭제", security = @SecurityRequirement(name = "Authorization"))
    @RequestBody(content = @Content( schemaProperties = @SchemaProperty(name="email", schema = @Schema(implementation = String.class) )))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class))
    )
    ResponseEntity<?> removeSendRequest(User user, Map<String, Object> param);

    @Operation(summary = "친구 수락 또는 거절", security = @SecurityRequirement(name = "Authorization"))
    @RequestBody(content = @Content( schemaProperties = @SchemaProperty(name="email", schema = @Schema(implementation = String.class) )))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SuccessOrNot.class))
    )
    ResponseEntity<?> processRequest(User user, Map<String, Object> param, ProcessOption option);

    @Operation(summary = "친구 삭제", security = @SecurityRequirement(name = "Authorization"))
    @RequestBody(content = @Content( schemaProperties = @SchemaProperty(name="email", schema = @Schema(implementation = String.class) )))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class))
    )
    ResponseEntity<?> remove(User user, Map<String, Object> param);
}
