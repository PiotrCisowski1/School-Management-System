package com.cisowski.schoolmanagement.common.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenApiCustomizer globalHeaderOpenApiCustomizer() {
        return openApi -> {
            openApi.getPaths().forEach((path, pathItem) -> {
                if (!path.equals("/login")) {
                    pathItem.readOperations().forEach(operation -> {
                        operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
                        ApiResponses responses = operation.getResponses();
                        if (!responses.containsKey("401")) {
                            responses.addApiResponse("401", new ApiResponse()
                                    .description("Full authentication is required to access this resource"));
                        }
                        if (!responses.containsKey("403")) {
                            responses.addApiResponse("403", new ApiResponse()
                                    .description("You do not have permission to access this resource"));
                        }
                    });
                }
            });
        };
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("School Management API").version("1.0"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
