package com.cisowski.schoolmanagement.appConfig.controller;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigDetailedResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigSummaryResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigUpdateRequest;
import com.cisowski.schoolmanagement.appConfig.service.AppConfigService;
import com.cisowski.schoolmanagement.common.annotation.SecurityResponses;
import com.cisowski.schoolmanagement.common.model.PagedResponse;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/config")
@RestController
@RequiredArgsConstructor
@Tag(name = "App Config", description = "Management of global application parameters and environment-specific settings.")
public class AppConfigController {

    private final AppConfigService appConfigService;

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PatchMapping("/{key}")
    @SecurityResponses
    @Operation(
            summary = "Update configuration value",
            description = "Modifies one of global application parameters and settings. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Config value successfully updated")
    @ApiResponse(responseCode = "404", description = "Config with given key not found")
    @ApiResponse(responseCode = "406", description = "Config is uneditable")
    ResponseEntity<AppConfigDetailedResponse> updateConfig(@RequestBody @Valid AppConfigUpdateRequest updateRequest, @PathVariable String key) {
        DbLogger.info(String.format("Received PATCH request for AppConfig with key: %s, with request: %s", key, updateRequest.toString()));
        AppConfigDetailedResponse response = appConfigService.updateConfigValue(updateRequest, key);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/{key}")
    @SecurityResponses
    @Operation(
            summary = "Returns configuration by key",
            description = "Retrieves single Config data based on Config key. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Config value successfully fetched")
    @ApiResponse(responseCode = "404", description = "Config with given key not found")
    ResponseEntity<AppConfigDetailedResponse> getConfigByKey(@PathVariable String key) {
        DbLogger.info("Received GET request for AppConfig with key: " + key);
        AppConfigDetailedResponse response = appConfigService.getConfigByKey(key);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping()
    @SecurityResponses
    @Operation(
            summary = "List editable system configs",
            description = "Retrieves all global configuration data that is editable for the User. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns config values (empty result as well)")
    ResponseEntity<PagedResponse<AppConfigSummaryResponse>> getAllEditableConfigs(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        DbLogger.info("Received GET request for all editable AppConfigs");
        PagedResponse<AppConfigSummaryResponse> response = appConfigService.getAllConfigValues(pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
