package com.cisowski.schoolmanagement.subject.mapper;

import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeRequest;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.util.StringUtils;

import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public interface SubjectTypeMapper {

    SubjectTypeEntity toSubjectTypeEntity(SubjectTypeRequest request);
    SubjectTypeResponse toSubjectTypeResponse(SubjectTypeEntity entity);
    List<SubjectTypeResponse> toSubjectTypeResponses(List<SubjectTypeEntity> entities);

    default void patchSubjectType(SubjectTypeEntity existing, SubjectTypeEntity request){
        if(request != null && StringUtils.hasLength(request.getName()))
            existing.setName(request.getName());
    }
}
