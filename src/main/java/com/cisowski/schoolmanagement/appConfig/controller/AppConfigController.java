package com.cisowski.schoolmanagement.appConfig.controller;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigDetailedResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigSummaryResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigUpdateRequest;
import com.cisowski.schoolmanagement.appConfig.service.AppConfigService;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/config")
@RestController
@RequiredArgsConstructor
public class AppConfigController {

    private final AppConfigService appConfigService;

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PatchMapping("/{key}")
    ResponseEntity<AppConfigDetailedResponse> updateConfig(@RequestBody @Valid AppConfigUpdateRequest updateRequest, @PathVariable String key) {
        DbLogger.info(String.format("Received PATCH request for AppConfig with key: %s, with request: %s", key, updateRequest.toString()));
        AppConfigDetailedResponse response = appConfigService.updateConfigValue(updateRequest, key);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/{key}")
    ResponseEntity<AppConfigDetailedResponse> getConfigByKey(@PathVariable String key) {
        DbLogger.info("Received GET request for AppConfig with key: " + key);
        AppConfigDetailedResponse response = appConfigService.getConfigByKey(key);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping()
    ResponseEntity<List<AppConfigSummaryResponse>> getAllEditableConfigs() {
        DbLogger.info("Received GET request for all editable AppConfigs");
        List<AppConfigSummaryResponse> response = appConfigService.getAllConfigValues();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
