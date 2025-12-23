package com.cisowski.schoolmanagement.users.teacher.mapper;

import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherCreateRequest;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherPatchRequest;
import com.cisowski.schoolmanagement.users.teacher.model.AddTeacherResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherDetailedResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import com.cisowski.schoolmanagement.yearbook.mapper.YearbookMapper;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;

@Mapper(config = BaseMapperConfig.class,
    uses = {YearbookMapper.class})
public interface TeacherMapper {
    @Mapping(target = "password", ignore = true)
    TeacherEntity toTeacherEntity(TeacherCreateRequest teacherDto);
    TeacherEntity toTeacherEntity(TeacherPatchRequest teacherDto);
    @Mapping(target = "leadingYearbook", source = "leadingYearbook", qualifiedByName = "toYearbookSummaryResponse")
    TeacherDetailedResponse toTeacherResponse(TeacherEntity teacher);
    @Mapping(target = "authority", source = "authority")
    AddTeacherResponse toAddTeacherResponse(TeacherEntity teacher);
    List<TeacherSummaryResponse>toTeachersResponse(Collection<TeacherEntity> teachers);
    @Named("toTeacherSummaryResponse")
    TeacherSummaryResponse toSummaryResponse(TeacherEntity teacher);
    void patchTeacher(@MappingTarget TeacherEntity targetEntity, TeacherEntity sourceEntity);

    default String toStringSubjectType(SubjectTypeEntity subjectType){
        if(subjectType == null)
            return null;
        return subjectType.getName();
    }

}
