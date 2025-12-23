package com.cisowski.schoolmanagement.grade.model.grade;

import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeValueResponse;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeResponse;
import com.cisowski.schoolmanagement.subject.model.SubjectSummaryResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentSummaryResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GradeSummaryResponse {
    private Long id;
    private StudentSummaryResponse student;
    private TeacherSummaryResponse teacher;
    private SubjectSummaryResponse subject;
    private GradeTypeResponse gradeType;
    private GradeValueResponse gradeValue;
    private LocalDate createdAt;
    private String comments;
}
