package com.cisowski.schoolmanagement.users.student.service;

import com.cisowski.schoolmanagement.users.common.service.BaseUserService;
import com.cisowski.schoolmanagement.users.student.model.*;

import java.util.List;

public interface StudentService extends BaseUserService {
    AddStudentResponse addStudent(StudentCreateRequest student);
    StudentDetailedResponse updateStudent(StudentPatchRequest student, Integer studentId);
    List<StudentSummaryResponse> findAll();
    StudentDetailedResponse findById(Integer studentId);
    StudentEntity fetchStudent(Integer studentId);
}
