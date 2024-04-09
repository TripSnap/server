package com.tripsnap.api.controller.api;


import com.tripsnap.api.domain.dto.PageDTO;
import com.tripsnap.api.domain.dto.ProcessOption;
import com.tripsnap.api.domain.dto.ResultDTO;
import com.tripsnap.api.domain.dto.SearchMemberDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;

import java.util.Map;

@Tag(name="friend", description = "친구 기능")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid status value",content = @Content())
})
public interface FriendApi {
    @Operation(summary = "친구 목록")
    @ApiResponse(responseCode = "200", description = "successful operation",useReturnTypeSchema = true)
    ResponseEntity<?> friendList(User user, PageDTO pageDTO);

    @Operation(summary = "친구 검색")
    @RequestBody(content = @Content( schemaProperties = @SchemaProperty(name="email", schema = @Schema(implementation = String.class) )))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schemaProperties = @SchemaProperty(name="data", schema = @Schema(implementation = SearchMemberDTO.class)))
    )
    ResponseEntity<?> search(User user, Map<String, Object> param);


    @Operation(summary = "친구 신청")
    @RequestBody(content = @Content( schemaProperties = @SchemaProperty(name="email", schema = @Schema(implementation = String.class) )))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class))
    )
    ResponseEntity<?> sendRequest(User user, Map<String, Object> param);

    @Operation(summary = "친구 수락 또는 거절")
    @RequestBody(content = @Content( schemaProperties = @SchemaProperty(name="email", schema = @Schema(implementation = String.class) )))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SuccessOrNot.class))
    )
    ResponseEntity<?> processRequest(User user, Map<String, Object> param, ProcessOption option);

    @Operation(summary = "친구 삭제")
    @RequestBody(content = @Content( schemaProperties = @SchemaProperty(name="email", schema = @Schema(implementation = String.class) )))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class))
    )
    ResponseEntity<?> remove(User user, Map<String, Object> param);
}
