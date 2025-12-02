package com.cisowski.schoolmanagement.unit.services;

import com.cisowski.schoolmanagement.appConfig.mapper.AppConfigMapper;
import com.cisowski.schoolmanagement.appConfig.model.*;
import com.cisowski.schoolmanagement.appConfig.repository.AppConfigRepository;
import com.cisowski.schoolmanagement.appConfig.service.AppConfigServiceImpl;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.users.common.model.AuthorityEntity;
import com.cisowski.schoolmanagement.users.common.model.UserDetailsEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.common.repository.AuthorityRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppConfigServiceImplTest {

    @Mock
    private AppConfigRepository configRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private AppConfigMapper configMapper;

    @InjectMocks
    private AppConfigServiceImpl appConfigService;

    private SecurityContext originalSecurityContext;

    @BeforeEach
    void setUp() {
        originalSecurityContext = SecurityContextHolder.getContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.setContext(originalSecurityContext);
    }

    @Test
    void getAllConfigValues_shouldReturnListOfEditableConfigs() {
        List<AppConfigEntity> editableConfigs = Instancio.ofList(AppConfigEntity.class)
                .size(3)
                .generate(field(AppConfigEntity::isEditable), gen -> gen.booleans().probability(1.0))
                .create();

        List<AppConfigSummaryResponse> expectedResponses = Instancio.ofList(AppConfigSummaryResponse.class)
                .size(3)
                .create();

        when(configRepository.findAllByIsEditable(true)).thenReturn(editableConfigs);
        when(configMapper.toSummaryResponseList(editableConfigs)).thenReturn(expectedResponses);

        List<AppConfigSummaryResponse> result = appConfigService.getAllConfigValues();

        assertThat(result).isEqualTo(expectedResponses);
        verify(configRepository).findAllByIsEditable(true);
        verify(configMapper).toSummaryResponseList(editableConfigs);
    }

    @Test
    void getAllConfigValues_whenNoEditableConfigsFound_shouldReturnEmptyList() {
        when(configRepository.findAllByIsEditable(true)).thenReturn(Collections.emptyList());
        when(configMapper.toSummaryResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<AppConfigSummaryResponse> result = appConfigService.getAllConfigValues();

        assertThat(result).isEmpty();
        verify(configRepository).findAllByIsEditable(true);
        verify(configMapper).toSummaryResponseList(Collections.emptyList());
    }

    @Test
    void getConfigByKey_whenConfigExists_shouldReturnDetailedResponse() {
        String configKey = "app.timeout";
        AppConfigEntity configEntity = Instancio.create(AppConfigEntity.class);
        AppConfigDetailedResponse expectedResponse = Instancio.create(AppConfigDetailedResponse.class);

        when(configRepository.findByKey(configKey)).thenReturn(Optional.of(configEntity));
        when(configMapper.toDetailedResponse(configEntity)).thenReturn(expectedResponse);

        AppConfigDetailedResponse result = appConfigService.getConfigByKey(configKey);

        assertThat(result).isEqualTo(expectedResponse);
        verify(configRepository).findByKey(configKey);
        verify(configMapper).toDetailedResponse(configEntity);
    }

    @Test
    void getConfigByKey_whenConfigDoesNotExist_shouldThrowEntityNotFoundException() {
        String nonExistentKey = "non.existent.key";
        when(configRepository.findByKey(nonExistentKey)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appConfigService.getConfigByKey(nonExistentKey))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("KEY")
                .hasMessageContaining(nonExistentKey);

        verify(configRepository).findByKey(nonExistentKey);
        verifyNoInteractions(configMapper);
    }

    @Test
    void updateConfigValue_whenConfigExistsAndIsEditable_shouldUpdateAndReturnResponse() {
        String configKey = "app.timeout";
        AppConfigUpdateRequest request = Instancio.of(AppConfigUpdateRequest.class)
                .set(field(AppConfigUpdateRequest::getValue), "30")
                .set(field(AppConfigUpdateRequest::getAuthoritiesToAddAsEditableBy), null)
                .set(field(AppConfigUpdateRequest::getAuthoritiesToRemoveAsEditableBy), null)
                .create();

        AppConfigEntity existingConfig = Instancio.of(AppConfigEntity.class)
                .set(field(AppConfigEntity::getKey), configKey)
                .set(field(AppConfigEntity::isEditable), true)
                .set(field(AppConfigEntity::getValueType), AppConfigValueType.INTEGER)
                .set(field(AppConfigEntity::getEditableBy), new ArrayList<>())
                .create();

        UserEntity mockUser = Instancio.create(UserEntity.class);
        UserDetailsEntity mockUserDetails = mock(UserDetailsEntity.class);

        AppConfigEntity savedConfig = Instancio.create(AppConfigEntity.class);
        AppConfigDetailedResponse expectedResponse = Instancio.create(AppConfigDetailedResponse.class);

        when(configRepository.findByKey(configKey)).thenReturn(Optional.of(existingConfig));
        when(configMapper.toEntity(request)).thenReturn(new AppConfigEntity());
        when(configRepository.save(any(AppConfigEntity.class))).thenReturn(savedConfig);
        when(configMapper.toDetailedResponse(savedConfig)).thenReturn(expectedResponse);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUserDetails);
        when(mockUserDetails.getUser()).thenReturn(mockUser);
        SecurityContextHolder.setContext(securityContext);

        AppConfigDetailedResponse result = appConfigService.updateConfigValue(request, configKey);

        assertThat(result).isEqualTo(expectedResponse);
        verify(configRepository).findByKey(configKey);
        verify(configMapper).patchConfig(eq(existingConfig), any(AppConfigEntity.class));
        verify(configRepository).save(existingConfig);
        verify(configMapper).toDetailedResponse(savedConfig);
        assertThat(existingConfig.getModifiedAt()).isNotNull();
        assertThat(existingConfig.getModifiedBy()).isEqualTo(mockUser);
    }

    @Test
    void updateConfigValue_whenConfigDoesNotExist_shouldThrowEntityNotFoundException() {
        String nonExistentKey = "non.existent.key";
        AppConfigUpdateRequest request = Instancio.create(AppConfigUpdateRequest.class);

        when(configRepository.findByKey(nonExistentKey)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appConfigService.updateConfigValue(request, nonExistentKey))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("KEY")
                .hasMessageContaining(nonExistentKey);

        verify(configRepository).findByKey(nonExistentKey);
        verifyNoInteractions(authorityRepository, configMapper);
    }

    @Test
    void updateConfigValue_whenConfigIsNotEditable_shouldThrowSpecificationBrokenException() {
        String configKey = "readonly.config";
        AppConfigUpdateRequest request = Instancio.create(AppConfigUpdateRequest.class);

        AppConfigEntity nonEditableConfig = Instancio.of(AppConfigEntity.class)
                .set(field(AppConfigEntity::getKey), configKey)
                .set(field(AppConfigEntity::isEditable), false)
                .create();

        when(configRepository.findByKey(configKey)).thenReturn(Optional.of(nonEditableConfig));

        assertThatThrownBy(() -> appConfigService.updateConfigValue(request, configKey))
                .isInstanceOf(SpecificationBrokenException.class)
                .hasMessageContaining("uneditable")
                .hasMessageContaining(configKey);

        verify(configRepository).findByKey(configKey);
        verifyNoInteractions(authorityRepository, configMapper);
    }

    @Test
    void updateConfigValue_withInvalidBooleanValue_shouldThrowSpecificationBrokenException() {
        String configKey = "app.enabled";
        AppConfigUpdateRequest request = Instancio.of(AppConfigUpdateRequest.class)
                .set(field(AppConfigUpdateRequest::getValue), "not-a-boolean")
                .create();

        AppConfigEntity config = Instancio.of(AppConfigEntity.class)
                .set(field(AppConfigEntity::getKey), configKey)
                .set(field(AppConfigEntity::isEditable), true)
                .set(field(AppConfigEntity::getValueType), AppConfigValueType.BOOLEAN)
                .create();

        when(configRepository.findByKey(configKey)).thenReturn(Optional.of(config));

        assertThatThrownBy(() -> appConfigService.updateConfigValue(request, configKey))
                .isInstanceOf(SpecificationBrokenException.class)
                .hasMessageContaining("Invalid configuration value");

        verify(configRepository).findByKey(configKey);
        verifyNoInteractions(authorityRepository, configMapper);
    }

    @Test
    void updateConfigValue_withInvalidIntegerValue_shouldThrowSpecificationBrokenException() {
        String configKey = "app.retry.count";
        AppConfigUpdateRequest request = Instancio.of(AppConfigUpdateRequest.class)
                .set(field(AppConfigUpdateRequest::getValue), "not-an-integer")
                .create();

        AppConfigEntity config = Instancio.of(AppConfigEntity.class)
                .set(field(AppConfigEntity::getKey), configKey)
                .set(field(AppConfigEntity::isEditable), true)
                .set(field(AppConfigEntity::getValueType), AppConfigValueType.INTEGER)
                .create();

        when(configRepository.findByKey(configKey)).thenReturn(Optional.of(config));

        assertThatThrownBy(() -> appConfigService.updateConfigValue(request, configKey))
                .isInstanceOf(SpecificationBrokenException.class)
                .hasMessageContaining("Invalid configuration value");

        verify(configRepository).findByKey(configKey);
        verifyNoInteractions(authorityRepository, configMapper);
    }

    @Test
    void updateConfigValue_withAuthoritiesToAdd_shouldAddAuthorities() {
        String configKey = "app.config";
        List<Integer> authorityIdsToAdd = Arrays.asList(1, 2, 3);

        AppConfigUpdateRequest request = Instancio.of(AppConfigUpdateRequest.class)
                .set(field(AppConfigUpdateRequest::getValue), "test")
                .set(field(AppConfigUpdateRequest::getAuthoritiesToAddAsEditableBy), authorityIdsToAdd)
                .set(field(AppConfigUpdateRequest::getAuthoritiesToRemoveAsEditableBy), null)
                .create();

        AppConfigEntity config = Instancio.of(AppConfigEntity.class)
                .set(field(AppConfigEntity::getKey), configKey)
                .set(field(AppConfigEntity::isEditable), true)
                .set(field(AppConfigEntity::getValueType), AppConfigValueType.TEXT)
                .set(field(AppConfigEntity::getEditableBy), new ArrayList<>())
                .create();

        List<AuthorityEntity> authoritiesToAdd = Instancio.ofList(AuthorityEntity.class)
                .size(3)
                .create();

        for (int i = 0; i < authorityIdsToAdd.size(); i++) {
            authoritiesToAdd.get(i).setId(authorityIdsToAdd.get(i));
        }

        when(configRepository.findByKey(configKey)).thenReturn(Optional.of(config));
        when(authorityRepository.findAllById(authorityIdsToAdd)).thenReturn(authoritiesToAdd);
        when(configMapper.toEntity(request)).thenReturn(new AppConfigEntity());
        when(configRepository.save(any(AppConfigEntity.class))).thenReturn(config);
        when(configMapper.toDetailedResponse(any())).thenReturn(Instancio.create(AppConfigDetailedResponse.class));

        setupMockSecurityContext();

        appConfigService.updateConfigValue(request, configKey);

        verify(authorityRepository).findAllById(authorityIdsToAdd);
        assertThat(config.getEditableBy()).containsExactlyElementsOf(authoritiesToAdd);
    }

    @Test
    void updateConfigValue_withAuthoritiesToRemove_shouldRemoveAuthorities() {
        String configKey = "app.config";

        List<AuthorityEntity> existingAuthorities = Instancio.ofList(AuthorityEntity.class)
                .size(3)
                .generate(field(AuthorityEntity::getId), gen -> gen.ints().range(1, 4))
                .create();
        List<Integer> authorityIdsToRemove = List.of(existingAuthorities.get(0).getId(), existingAuthorities.get(1).getId());

        AppConfigUpdateRequest request = Instancio.of(AppConfigUpdateRequest.class)
                .set(field(AppConfigUpdateRequest::getValue), "test")
                .set(field(AppConfigUpdateRequest::getAuthoritiesToAddAsEditableBy), null)
                .set(field(AppConfigUpdateRequest::getAuthoritiesToRemoveAsEditableBy), authorityIdsToRemove)
                .create();

        AppConfigEntity config = Instancio.of(AppConfigEntity.class)
                .set(field(AppConfigEntity::getKey), configKey)
                .set(field(AppConfigEntity::isEditable), true)
                .set(field(AppConfigEntity::getValueType), AppConfigValueType.TEXT)
                .set(field(AppConfigEntity::getEditableBy), new ArrayList<>(existingAuthorities))
                .create();

        List<AuthorityEntity> authoritiesToRemove = existingAuthorities.stream()
                .filter(a -> authorityIdsToRemove.contains(a.getId()))
                .toList();

        when(configRepository.findByKey(configKey)).thenReturn(Optional.of(config));
        when(authorityRepository.findAllById(authorityIdsToRemove)).thenReturn(authoritiesToRemove);
        when(configMapper.toEntity(request)).thenReturn(new AppConfigEntity());
        when(configRepository.save(any(AppConfigEntity.class))).thenReturn(config);
        when(configMapper.toDetailedResponse(any())).thenReturn(Instancio.create(AppConfigDetailedResponse.class));

        setupMockSecurityContext();

        appConfigService.updateConfigValue(request, configKey);

        assertThat(config.getEditableBy()).doesNotContainAnyElementsOf(authoritiesToRemove);
    }

    @Test
    void updateConfigValue_whenSomeAuthoritiesNotFound_shouldThrowSpecificationBrokenException() {
        String configKey = "app.config";
        List<Integer> authorityIds = Arrays.asList(1, 2, 3, 99);

        AppConfigUpdateRequest request = Instancio.of(AppConfigUpdateRequest.class)
                .set(field(AppConfigUpdateRequest::getValue), "test")
                .set(field(AppConfigUpdateRequest::getAuthoritiesToAddAsEditableBy), authorityIds)
                .create();

        AppConfigEntity config = Instancio.of(AppConfigEntity.class)
                .set(field(AppConfigEntity::getKey), configKey)
                .set(field(AppConfigEntity::isEditable), true)
                .set(field(AppConfigEntity::getValueType), AppConfigValueType.TEXT)
                .create();

        List<AuthorityEntity> foundAuthorities = Instancio.ofList(AuthorityEntity.class)
                .size(3)
                .generate(field(AuthorityEntity::getId), gen -> gen.ints().range(1, 4))
                .create();

        when(configRepository.findByKey(any())).thenReturn(Optional.of(config));
        when(authorityRepository.findAllById(any())).thenReturn(foundAuthorities);

        assertThatThrownBy(() -> appConfigService.updateConfigValue(request, configKey))
                .isInstanceOf(SpecificationBrokenException.class)
                .hasMessageContaining("Cannot find Authorities with given IDs");
    }

    private void setupMockSecurityContext() {
        UserEntity mockUser = Instancio.create(UserEntity.class);
        UserDetailsEntity mockUserDetails = mock(UserDetailsEntity.class);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUserDetails);
        when(mockUserDetails.getUser()).thenReturn(mockUser);

        SecurityContextHolder.setContext(securityContext);
    }
}
