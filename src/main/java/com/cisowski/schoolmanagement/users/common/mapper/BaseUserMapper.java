package com.cisowski.schoolmanagement.users.common.mapper;

import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.common.model.UserSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(config = BaseMapperConfig.class)
public interface BaseUserMapper {

    @Named("toUserSummaryResponse")
    UserSummaryResponse toSummaryResponse(UserEntity user);
}
