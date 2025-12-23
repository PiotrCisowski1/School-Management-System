package com.cisowski.schoolmanagement.grade.mapper;

import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.grade.model.gradeScale.*;
import jakarta.annotation.PostConstruct;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(config = BaseMapperConfig.class)
public abstract class GradeScaleMapper {

    private GradeValueMapper valueMapper;

    @PostConstruct
    void init(){
        valueMapper = Mappers.getMapper(GradeValueMapper.class);
    }

    @Mapping(target = "gradeValues", ignore = true)
    public abstract GradeScaleEntity toEntity(GradeScaleRequest request);

    @Mapping(target = "gradeValues", ignore = true)
    public abstract GradeScaleResponse toResponse(GradeScaleEntity entity);

    public abstract GradeScaleSummaryResponse toSummaryResponse(GradeScaleEntity entity);

    public abstract List<GradeScaleSummaryResponse> toResponseList(List<GradeScaleEntity> entities);

    @AfterMapping
    GradeScaleResponse mapGradeValueResponses(@MappingTarget GradeScaleResponse response, GradeScaleEntity entity){
        if(response == null || entity == null)
            return response;
        response.setGradeValues(valueMapper.toResponseList(entity.getGradeValues()));
        return response;
    }

    public abstract GradeScaleEntity toEntity(PatchGradeScaleRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "gradeValues", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract void patchEntity(@MappingTarget GradeScaleEntity existing, GradeScaleEntity request);
}
