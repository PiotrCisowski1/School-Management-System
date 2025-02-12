package com.cisowski.schoolmanagement.users.teacher.service;


import com.cisowski.schoolmanagement.users.common.service.BaseUserService;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherCreateRequest;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherPatchRequest;
import com.cisowski.schoolmanagement.users.teacher.model.AddTeacherResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherDetailedResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;

import java.util.List;

public interface TeacherService extends BaseUserService {
    AddTeacherResponse addTeacher(TeacherCreateRequest teacherDto);
    TeacherDetailedResponse updateTeacher(TeacherPatchRequest teacherDto);
    List<TeacherSummaryResponse> findAll();
    TeacherDetailedResponse findById(Integer teacherId);
}
