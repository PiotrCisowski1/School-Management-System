package com.cisowski.schoolmanagement.appConfig.service;

import com.cisowski.schoolmanagement.appConfig.mapper.AppConfigMapper;
import com.cisowski.schoolmanagement.appConfig.model.*;
import com.cisowski.schoolmanagement.appConfig.repository.AppConfigRepository;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.users.common.model.AuthorityEntity;
import com.cisowski.schoolmanagement.users.common.model.UserDetailsEntity;
import com.cisowski.schoolmanagement.users.common.repository.AuthorityRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Data
@RequiredArgsConstructor
public class AppConfigServiceImpl implements AppConfigService {

    private final AppConfigRepository configRepository;
    private final AuthorityRepository authorityRepository;
    private final AppConfigMapper configMapper;

    @Override
    public List<AppConfigSummaryResponse> getAllConfigValues() {
        DbLogger.info("Searching for all editable configuration values");
        List<AppConfigEntity> configs = configRepository.findAllByIsEditable(true);
        DbLogger.info(String.format("Found %s editable configs in db", configs.size()));
        return configMapper.toSummaryResponseList(configs);
    }

    @Override
    public AppConfigDetailedResponse getConfigByKey(String key) {
        DbLogger.info("Searching for config with key: " + key);
        Optional<AppConfigEntity> existingConfig = configRepository.findByKey(key);
        if(existingConfig.isEmpty())
            throw new EntityNotFoundException(AppConfigEntity.class, "KEY", key);
        DbLogger.info(String.format("Found config with key: %s, entity: %s", key, existingConfig.get().toString()));
        return configMapper.toDetailedResponse(existingConfig.get());
    }

    @Override
    public AppConfigDetailedResponse updateConfigValue(AppConfigUpdateRequest request, String key) {
        DbLogger.info(String.format("Updating config value with key: %s, for request: %s", key, request.toString()));
        Optional<AppConfigEntity> existingConfig = configRepository.findByKey(key);
        if(existingConfig.isEmpty())
            throw new EntityNotFoundException(AppConfigEntity.class, "KEY", key);
        AppConfigEntity config = existingConfig.get();
        if(!config.isEditable())
            throw new SpecificationBrokenException(String.format("Cannot update configuration with key: %s, because it is uneditable", key));

        checkIfValidConfigValue(request.getValue(), config.getValueType(), key);
        updateAuthorities(request.getAuthoritiesToAddAsEditableBy(), request.getAuthoritiesToRemoveAsEditableBy(), config);
        AppConfigEntity requestConfigEntity = configMapper.toEntity(request);
        configMapper.patchConfig(config, requestConfigEntity);
        config.setModifiedAt(LocalDateTime.now());
        UserDetailsEntity authenticatedUser = (UserDetailsEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        config.setModifiedBy(authenticatedUser.getUser());

        AppConfigEntity savedConfig = configRepository.save(config);
        DbLogger.info(String.format("Config with key: %s, was successfully updated: %s", key, savedConfig.toString()));

        return configMapper.toDetailedResponse(savedConfig);
    }

    private void checkIfValidConfigValue(String newValue, AppConfigValueType valueType, String key) {
        if(StringUtils.isNotEmpty(newValue)) {
            if(!isProperValueType(newValue, valueType)) {
                DbLogger.error(String.format(
                        "Invalid configuration value: %s, for value type %s. Config key: %s",
                        newValue,
                        valueType.name(),
                        key
                ));
                throw new SpecificationBrokenException(String.format(
                        "Invalid configuration value: %s, for value type %s",
                        newValue,
                        valueType.name()
                ));
            }
        }
    }

    private void updateAuthorities(List<Integer> authoritiesToAdd, List<Integer> authoritiesToRemove, AppConfigEntity appConfig) {
        if(!CollectionUtils.isEmpty(authoritiesToRemove)){
            List<AuthorityEntity> authorities = getAuthorities(authoritiesToRemove);
            appConfig.getEditableBy().removeAll(authorities);
        }
        if(!CollectionUtils.isEmpty(authoritiesToAdd)) {
            List<AuthorityEntity> authorities = getAuthorities(authoritiesToAdd);
            appConfig.getEditableBy().addAll(authorities);
        }
    }

    private List<AuthorityEntity> getAuthorities(List<Integer> authorityIds) {
        List<AuthorityEntity> authorities = new ArrayList<>();
        if(CollectionUtils.isEmpty(authorityIds))
            return authorities;
        authorities = authorityRepository.findAllById(authorityIds);
        if(authorities.size() != authorityIds.size()) {
            Set<Integer> fetchedIds = authorities.stream()
                    .map(AuthorityEntity::getId)
                    .collect(Collectors.toSet());
            List<Integer> notFoundIds = authorityIds.stream()
                    .filter(id -> !fetchedIds.contains(id))
                    .toList();
            throw new SpecificationBrokenException("Cannot find Authorities with given IDs: " + notFoundIds);
        }
        return authorities;
    }

    private boolean isProperValueType(String value, AppConfigValueType actualValueType) {
        if(StringUtils.isEmpty(value) || actualValueType == null)
            return false;
        return switch (actualValueType) {
            case BOOLEAN -> isBooleanValue(value);
            case INTEGER -> isIntegerValue(value);
            case TEXT -> true;
        };
    }

    private boolean isIntegerValue(String value) {
        if(StringUtils.isNotEmpty(value)) {
            try {
                Integer.parseInt(value);
                return true;
            } catch (NumberFormatException ex) {
                DbLogger.info(String.format("Given value '%s' is not an Integer", value));
                return false;
            }
        }
        return false;
    }

    private boolean isBooleanValue(String value) {
        if(StringUtils.isNotEmpty(value))
            return Boolean.parseBoolean(value);
        return false;
    }

}
