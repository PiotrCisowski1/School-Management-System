package com.cisowski.schoolmanagement.users.student.service;

import com.cisowski.schoolmanagement.users.common.service.BaseUserService;
import com.cisowski.schoolmanagement.users.student.model.StudentCreateRequest;
import com.cisowski.schoolmanagement.users.student.model.StudentPatchRequest;
import com.cisowski.schoolmanagement.users.student.model.AddStudentResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentDetailedResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentSummaryResponse;

import java.util.List;

public interface StudentService extends BaseUserService {
    AddStudentResponse addStudent(StudentCreateRequest student);
    StudentDetailedResponse updateStudent(StudentPatchRequest student, Integer studentId);
    List<StudentSummaryResponse> findAll();
    StudentDetailedResponse findById(Integer studentId);
}
