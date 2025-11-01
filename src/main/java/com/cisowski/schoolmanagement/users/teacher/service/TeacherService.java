package com.cisowski.schoolmanagement.users.teacher.service;

import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.common.service.BaseUserService;
import com.cisowski.schoolmanagement.users.teacher.model.*;

import java.util.List;

public interface TeacherService extends BaseUserService {
    AddTeacherResponse addTeacher(TeacherCreateRequest teacherDto);
    TeacherDetailedResponse updateTeacher(TeacherPatchRequest teacherDto, Integer teacherId);
    List<TeacherSummaryResponse> findAll();
    TeacherDetailedResponse findById(Integer teacherId);
    TeacherEntity fetchTeacher(Integer teacherId);
    List<SubjectEntity> fetchTeacherSubjects(Integer teacherId);
}
