package com.cisowski.schoolmanagement.appConfig.service;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigDetailedResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigSummaryResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigUpdateRequest;
import com.cisowski.schoolmanagement.common.model.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AppConfigService {
    AppConfigDetailedResponse getConfigByKey(String key);
    PagedResponse<AppConfigSummaryResponse> getAllConfigValues(Pageable pageable);
    AppConfigDetailedResponse updateConfigValue(AppConfigUpdateRequest request, String key);
}
