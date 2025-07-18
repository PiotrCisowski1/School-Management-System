package com.cisowski.schoolmanagement.grade.mapper;

import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.grade.model.gradeType.AddGradeTypeRequest;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeResponse;
import com.cisowski.schoolmanagement.grade.model.gradeType.PatchGradeTypeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public interface GradeTypeMapper {

    GradeTypeEntity toEntity(AddGradeTypeRequest request);

    @Named("toGradeTypeResponse")
    GradeTypeResponse toResponse(GradeTypeEntity entity);

    GradeTypeEntity toEntity(PatchGradeTypeRequest request);

    void patchEntity(GradeTypeEntity patchingEntity, @MappingTarget GradeTypeEntity existingEntity);

    List<GradeTypeResponse> toResponseList(List<GradeTypeEntity> entities);
}
