package com.cisowski.schoolmanagement.service;

import com.cisowski.schoolmanagement.model.request.StudentCreateRequest;
import com.cisowski.schoolmanagement.model.request.StudentPatchRequest;
import com.cisowski.schoolmanagement.model.response.AddStudentResponse;
import com.cisowski.schoolmanagement.model.response.StudentDetailedResponse;
import com.cisowski.schoolmanagement.model.response.StudentSummaryResponse;

import java.util.List;

public interface StudentService extends BaseUserService {
    AddStudentResponse addStudent(StudentCreateRequest student);
    StudentDetailedResponse updateStudent(StudentPatchRequest student, Integer studentId);
    List<StudentSummaryResponse> findAll();
    StudentDetailedResponse findById(Integer studentId);
}
