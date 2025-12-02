package com.cisowski.schoolmanagement.appConfig.service;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigDetailedResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigSummaryResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigUpdateRequest;

import java.util.List;

public interface AppConfigService {
    AppConfigDetailedResponse getConfigByKey(String key);
    List<AppConfigSummaryResponse> getAllConfigValues();
    AppConfigDetailedResponse updateConfigValue(AppConfigUpdateRequest request, String key);
}
