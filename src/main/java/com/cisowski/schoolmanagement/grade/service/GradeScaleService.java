package com.cisowski.schoolmanagement.grade.service;

import com.cisowski.schoolmanagement.grade.model.gradeScale.*;

import java.util.List;

public interface GradeScaleService {
    GradeScaleResponse addGradeScale(GradeScaleRequest request);
    GradeScaleResponse patchGradeScale(PatchGradeScaleRequest request, Long gradeScaleId);
    void deleteGradeValue(Long gradeScaleId, Long gradeValueId);
    GradeValueResponse addGradeValue(GradeValueDto gradeValueDto, Long gradeScaleId);
    GradeScaleEntity fetchGradeScale(Long gradeScaleId);
    void deleteGradeScale(Long gradeScale);
    GradeScaleResponse getGradeScaleById(Long gradeScale);
    List<GradeScaleSummaryResponse> getAllGradeScales();
    GradeScaleResponse getActiveGradeScale();
}
