package com.cisowski.schoolmanagement.grade.service;

import com.cisowski.schoolmanagement.grade.model.grade.*;

import java.util.List;

public interface GradeService {
    GradeDetailedResponse addGrade(AddGradeRequest request);
    GradeDetailedResponse patchGrade(PatchGradeRequest request, Long gradeId);
    void deleteGrade(Long gradeId);
    GradeDetailedResponse getGradeById(Long gradeId);
    List<GradeSummaryResponse> getGradesByStudentId(Integer studentId);
    List<GradeSummaryResponse> getGradesByStudentIdAndSubject(Integer studentId, Integer subjectId);
    List<GradeSummaryResponse> getGradesBySubjectId(Integer subjectId);
    List<GradeSummaryResponse> getGradesByGradeType(Long gradeTypeId);
    GradeEntity fetchGrade(Long gradeId);
}
