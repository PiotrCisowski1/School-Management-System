package com.cisowski.schoolmanagement.common.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Operation(security = @SecurityRequirement(name = "bearerAuth"))
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content),
        @ApiResponse(responseCode = "403", description = "User lacks permission to access this resource", content = @Content)
})
public @interface SecurityResponses {
}
