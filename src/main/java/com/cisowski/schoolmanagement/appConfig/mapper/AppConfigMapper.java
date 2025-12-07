package com.cisowski.schoolmanagement.appConfig.mapper;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigDetailedResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigEntity;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigSummaryResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigUpdateRequest;
import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public interface AppConfigMapper {

    AppConfigEntity toEntity(AppConfigUpdateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "key", ignore = true)
    @Mapping(target = "valueType", ignore = true)
    @Mapping(target = "editable", ignore = true)
    @Mapping(target = "editableBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    void patchConfig(@MappingTarget AppConfigEntity targetEntity, AppConfigEntity sourceEntity);

    @Mapping(target = "modifiedByUserId", source = "modifiedBy", qualifiedByName = "mapUserId")
    AppConfigDetailedResponse toDetailedResponse(AppConfigEntity entity);

    List<AppConfigSummaryResponse> toSummaryResponseList(List<AppConfigEntity> configs);

    AppConfigSummaryResponse toSummaryResponse(AppConfigEntity entity);

    @Named("mapUserId")
    default Integer mapUserId(UserEntity user) {
        if(user == null)
            return null;
        return user.getId();
    }
}
