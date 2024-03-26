package com.tripsnap.api.controller;

import com.tripsnap.api.domain.entity.Member;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name="account", description = "회원과 관련된 API")
public interface AccountApi {
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Member.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid status value") })
    public String test();
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Member.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid status value") })

    @GetMapping("/test3")
    public String test2();
}
