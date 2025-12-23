package com.cisowski.schoolmanagement.grade.mapper;

import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeScaleEntity;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeValueDto;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeValueEntity;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeValueResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public interface GradeValueMapper {

    @Mapping(target = "gradeScale", source = "gradeScale")
    @Mapping(target = "displayValue", source = "dto.displayValue")
    @Mapping(target = "numericValue", source = "dto.numericValue")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "isPassingGrade", source = "dto.isPassingGrade")
    @Mapping(target = "id", ignore = true)
    GradeValueEntity toEntity(GradeValueDto dto, GradeScaleEntity gradeScale);

    List<GradeValueEntity> toEntityList(List<GradeValueDto> dtos);

    List<GradeValueResponse> toResponseList(List<GradeValueEntity> entities);

    @Named("toGradeValueResponse")
    GradeValueResponse toResponse(GradeValueEntity entity);
}
