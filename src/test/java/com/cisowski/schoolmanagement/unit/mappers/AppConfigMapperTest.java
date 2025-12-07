package com.cisowski.schoolmanagement.unit.mappers;

import com.cisowski.schoolmanagement.appConfig.mapper.AppConfigMapper;
import com.cisowski.schoolmanagement.appConfig.model.*;
import com.cisowski.schoolmanagement.users.common.model.AuthorityEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AppConfigMapperTest {

    private final AppConfigMapper mapper = Mappers.getMapper(AppConfigMapper.class);


    @Test
    void toDetailedResponse_shouldMapEntityToDetailedResponse() {
        AppConfigEntity entity = Instancio.create(AppConfigEntity.class);
        entity.setModifiedBy(Instancio.create(UserEntity.class));

        AppConfigDetailedResponse response = mapper.toDetailedResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getKey()).isEqualTo(entity.getKey());
        assertThat(response.getValue()).isEqualTo(entity.getValue());
        assertThat(response.getValueType()).isEqualTo(entity.getValueType());
        assertThat(response.isEditable()).isEqualTo(entity.isEditable());
        assertThat(response.getEditableBy()).isEqualTo(entity.getEditableBy());
        assertThat(response.getMinValue()).isEqualTo(entity.getMinValue());
        assertThat(response.getMaxValue()).isEqualTo(entity.getMaxValue());
        assertThat(response.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(response.getModifiedAt()).isEqualTo(entity.getModifiedAt());
    }

    @Test
    void toDetailedResponse_whenModifiedByIsNull_shouldMapUserIdAsNull() {
        AppConfigEntity entity = Instancio.create(AppConfigEntity.class);
        entity.setModifiedBy(null);

        AppConfigDetailedResponse response = mapper.toDetailedResponse(entity);

        assertThat(response.getModifiedByUserId()).isNull();
    }

    @Test
    void toSummaryResponseList_shouldMapEntityListToResponseList() {
        List<AppConfigEntity> entities = Instancio.ofList(AppConfigEntity.class).size(3).create();

        List<AppConfigSummaryResponse> responses = mapper.toSummaryResponseList(entities);

        assertThat(responses).hasSameSizeAs(entities);
    }

    @Test
    void toSummaryResponse_shouldMapEntityToSummaryResponse() {
        AppConfigEntity entity = Instancio.create(AppConfigEntity.class);

        AppConfigSummaryResponse response = mapper.toSummaryResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(entity.getId());
        assertThat(response.getKey()).isEqualTo(entity.getKey());
        assertThat(response.getValue()).isEqualTo(entity.getValue());
        assertThat(response.isEditable()).isEqualTo(entity.isEditable());
        assertThat(response.getEditableBy()).isEqualTo(entity.getEditableBy());
        assertThat(response.getDescription()).isEqualTo(entity.getDescription());
    }

    @Test
    void patchConfig_shouldUpdateOnlyAllowedFields() {
        AppConfigEntity target = Instancio.create(AppConfigEntity.class);
        AppConfigEntity source = Instancio.create(AppConfigEntity.class);

        Long originalId = target.getId();
        String originalKey = target.getKey();
        AppConfigValueType originalValueType = target.getValueType();
        boolean originalEditable = target.isEditable();
        List<AuthorityEntity> originalEditableBy = target.getEditableBy() != null ?
                new ArrayList<>(target.getEditableBy()) : null;
        LocalDateTime originalCreatedAt = target.getCreatedAt();
        UserEntity originalModifiedBy = target.getModifiedBy();
        LocalDateTime originalModifiedAt = target.getModifiedAt();

        mapper.patchConfig(target, source);

        assertThat(target.getId()).isEqualTo(originalId);
        assertThat(target.getKey()).isEqualTo(originalKey);
        assertThat(target.getValueType()).isEqualTo(originalValueType);
        assertThat(target.isEditable()).isEqualTo(originalEditable);
        assertThat(target.getEditableBy()).isEqualTo(originalEditableBy);
        assertThat(target.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(target.getModifiedBy()).isEqualTo(originalModifiedBy);
        assertThat(target.getModifiedAt()).isEqualTo(originalModifiedAt);

        if (source.getValue() != null) {
            assertThat(target.getValue()).isEqualTo(source.getValue());
        }
        if (source.getDescription() != null) {
            assertThat(target.getDescription()).isEqualTo(source.getDescription());
        }
        if (source.getMinValue() != null) {
            assertThat(target.getMinValue()).isEqualTo(source.getMinValue());
        }
        if (source.getMaxValue() != null) {
            assertThat(target.getMaxValue()).isEqualTo(source.getMaxValue());
        }
    }
}
