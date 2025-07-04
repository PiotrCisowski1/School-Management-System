package com.cisowski.schoolmanagement.yearbook.mapper;

import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeEntity;
import com.cisowski.schoolmanagement.users.student.mapper.StudentMapper;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import com.cisowski.schoolmanagement.yearbook.model.*;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public abstract class YearbookMapper {

    @Autowired
    @Lazy
    protected TeacherMapper teacherMapper;
    @Autowired
    @Lazy
    protected StudentMapper studentMapper;

    @Mapping(target = "headTeacher", ignore = true)
    @Mapping(target = "studentsInYearbook", ignore = true)
    @Mapping(target = "mainCourseSubjects", ignore = true)
    public abstract YearbookEntity toYearbookEntity(AddYearbookRequest request);

    @Mapping(target = "headTeacher", ignore = true)
    @Mapping(target = "studentsInYearbook", ignore = true)
    @Mapping(target = "mainCourseSubjects", ignore = true)
    @Mapping(target = "graduationYear", source = "targetGraduationYear")
    public abstract YearbookEntity toYearbookEntity(PatchYearbookRequest request);

    @Mapping(target = "headTeacher", ignore = true)
    @Mapping(target = "studentsInYearbook", ignore = true)
    @Named("toYearbookDetailedResponse")
    public abstract YearbookDetailedResponse toDetailedResponse(YearbookEntity yearbookEntity);

    @AfterMapping
    protected void mapHeadTeacher(YearbookEntity yearbookEntity, @MappingTarget YearbookDetailedResponse response) {
        if (yearbookEntity.getHeadTeacher() != null) {
            response.setHeadTeacher(teacherMapper.toSummaryResponse(yearbookEntity.getHeadTeacher()));
        }
    }

    @AfterMapping
    protected void mapStudentsInYearbook(YearbookEntity yearbookEntity, @MappingTarget YearbookDetailedResponse response) {
        if(!CollectionUtils.isEmpty(yearbookEntity.getStudentsInYearbook())){
            response.setStudentsInYearbook(studentMapper.toStudentsResponse(yearbookEntity.getStudentsInYearbook()));
        }
    }

    public abstract List<YearbookSummaryResponse> toYearbookList(Collection<YearbookEntity> yearbooks);

    @Named("toYearbookSummaryResponse")
    @Mapping(target = "headTeacher", ignore = true)
    public abstract YearbookSummaryResponse toYearbookSummaryResponse(YearbookEntity yearbookEntity);

    @AfterMapping
    protected void mapHeadTeacher(YearbookEntity yearbookEntity, @MappingTarget YearbookSummaryResponse response) {
        if (yearbookEntity.getHeadTeacher() != null) {
            response.setHeadTeacher(teacherMapper.toSummaryResponse(yearbookEntity.getHeadTeacher()));
        }
    }

    public abstract void patchYearbook(@MappingTarget YearbookEntity targetEntity, YearbookEntity requestEntity);

    String toStringSubjectType(SubjectTypeEntity subjectType){
        if(subjectType == null)
            return null;
        return subjectType.getName();
    }
}
