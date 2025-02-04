package com.cisowski.schoolmanagement.service;


import com.cisowski.schoolmanagement.model.request.TeacherCreateRequest;
import com.cisowski.schoolmanagement.model.request.TeacherPatchRequest;
import com.cisowski.schoolmanagement.model.response.AddTeacherResponse;
import com.cisowski.schoolmanagement.model.response.TeacherDetailedResponse;
import com.cisowski.schoolmanagement.model.response.TeacherSummaryResponse;

import java.util.List;

public interface TeacherService extends BaseUserService {
    AddTeacherResponse addTeacher(TeacherCreateRequest teacherDto);
    TeacherDetailedResponse updateTeacher(TeacherPatchRequest teacherDto);
    List<TeacherSummaryResponse> findAll();
    TeacherDetailedResponse findById(Integer teacherId);
}
