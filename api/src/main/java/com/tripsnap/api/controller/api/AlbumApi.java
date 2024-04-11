package com.tripsnap.api.controller.api;

import com.tripsnap.api.domain.dto.AlbumPhotoInsDTO;
import com.tripsnap.api.domain.dto.GroupAlbumInsDTO;
import com.tripsnap.api.domain.dto.GroupAlbumParamDTO;
import com.tripsnap.api.domain.dto.ResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

@Tag(name="album", description = "그룹 기록 기능")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Invalid status value",content = @Content())
})
public interface AlbumApi {
    @Operation(summary = "기록 목록")
    @RequestBody(content = @Content(schemaProperties = {
            @SchemaProperty(name="page", schema = @Schema(implementation = Long.class)),
            @SchemaProperty(name="pagePerCnt", schema = @Schema(implementation = Long.class)),
            @SchemaProperty(name="albumId", schema = @Schema(implementation = Long.class)),
            @SchemaProperty(name="groupId", schema = @Schema(implementation = Long.class))
    }))
    @ApiResponse(responseCode = "200", description = "successful operation",useReturnTypeSchema = true)
    ResponseEntity<?> getAlbums(User user, Map<String, Object> param);

    @Operation(summary = "기록 추가")
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class))
    )
    ResponseEntity<?> addAlbum(User user, GroupAlbumInsDTO param);

    @Operation(summary = "기록 삭제")
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class))
    )
    ResponseEntity<?> removeAlbum(User user, GroupAlbumParamDTO paramDTO);

    @Operation(summary = "기록 사진 리스트")
    @ApiResponse(responseCode = "200", description = "successful operation", useReturnTypeSchema = true)
    ResponseEntity<?> photos(User user, Map<String, Object> param);

    @Operation(summary = "사진 추가")
    @RequestBody(content = @Content(schemaProperties = {
            @SchemaProperty(name="page", schema = @Schema(implementation = Long.class)),
            @SchemaProperty(name="pagePerCnt", schema = @Schema(implementation = Long.class)),
            @SchemaProperty(name="addPhotos", array = @ArraySchema(schema = @Schema(implementation = AlbumPhotoInsDTO.class)))
    }))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SuccessOrNot.class))
    )
    ResponseEntity<?> addPhotos(User user, Map<String, Object> param);

    @Operation(summary = "사진 삭제")
    @RequestBody(content = @Content(schemaProperties = {
            @SchemaProperty(name="page", schema = @Schema(implementation = Long.class)),
            @SchemaProperty(name="pagePerCnt", schema = @Schema(implementation = Long.class)),
            @SchemaProperty(name="removePhotoIds", array = @ArraySchema(schema = @Schema(implementation = Long.class)))
    }))
    @ApiResponse(responseCode = "200", description = "successful operation",
            content = @Content(schema = @Schema(implementation = ResultDTO.SimpleSuccessOrNot.class))
    )
    ResponseEntity<?> removePhotos(User user, Map<String, Object> param);
}