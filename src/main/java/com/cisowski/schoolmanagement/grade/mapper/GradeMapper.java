package com.cisowski.schoolmanagement.grade.mapper;

import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.grade.model.grade.*;
import com.cisowski.schoolmanagement.subject.mapper.SubjectMapper;
import com.cisowski.schoolmanagement.users.student.mapper.StudentMapper;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = BaseMapperConfig.class, uses = {TeacherMapper.class, StudentMapper.class, SubjectMapper.class,
        GradeTypeMapper.class, GradeValueMapper.class})
public interface GradeMapper {

    @Mapping(target = "comments", source = "comments")
    GradeEntity toEntity(AddGradeRequest request);

    @Mapping(target = "comments", source = "comments")
    GradeEntity toEntity(PatchGradeRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "student", source = "student", qualifiedByName = "toStudentSummaryResponse")
    @Mapping(target = "subject", source = "subject", qualifiedByName = "toSubjectSummaryResponse")
    @Mapping(target = "teacher", source = "teacher", qualifiedByName = "toTeacherSummaryResponse")
    @Mapping(target = "gradeType", source = "gradeType", qualifiedByName = "toGradeTypeResponse")
    @Mapping(target = "gradeValue", source = "gradeValue", qualifiedByName = "toGradeValueResponse")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "createdAt", source = "createdAt")
    GradeDetailedResponse toDetailedResponse(GradeEntity entity);

    @Mapping(target = "student", source = "student", qualifiedByName = "toStudentSummaryResponse")
    @Mapping(target = "subject", source = "subject", qualifiedByName = "toSubjectSummaryResponse")
    @Mapping(target = "teacher", source = "teacher", qualifiedByName = "toTeacherSummaryResponse")
    @Mapping(target = "gradeType", source = "gradeType", qualifiedByName = "toGradeTypeResponse")
    @Mapping(target = "gradeValue", source = "gradeValue", qualifiedByName = "toGradeValueResponse")
    GradeSummaryResponse toGradeSummaryResponse(GradeEntity grade);

    List<GradeSummaryResponse> toSummaryResponseList(List<GradeEntity> grades);
}
