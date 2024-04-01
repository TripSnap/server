package com.tripsnap.api.controller.api;


import com.tripsnap.api.domain.dto.GroupInsDTO;
import com.tripsnap.api.domain.dto.PageDTO;
import com.tripsnap.api.domain.dto.ResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;

@Tag(name="group", description = "그룹과 관련된 API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid status value",content = @Content())
})
public interface GroupApi {
    @Operation(summary = "회원이 가입한 그룹 리스트")
    @ApiResponse(responseCode = "200", description = "successful operation",useReturnTypeSchema = true)
//
//    @ApiResponse(responseCode = "200", description = "successful operation",
//            content = @Content(
//                    schema=@Schema(implementation = MemberDTO.class)
//                    , schemaProperties = {@SchemaProperty(name = "", schema = @Schema(implementation = GroupDTO.class)),@SchemaProperty(name = "data", schema = @Schema(implementation = ResponseDTO.SimpleWithPageData.class))}
//            )
//    )
    ResponseEntity<?> groups(User user, PageDTO param);
    @Operation(summary = "그룹 생성")
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class)))
    ResponseEntity<?> addGroup(User user, GroupInsDTO param);

    @Operation(summary = "그룹 삭제")
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class)))
    ResponseEntity<?> removeGroup(User user, Long groupId);

}
