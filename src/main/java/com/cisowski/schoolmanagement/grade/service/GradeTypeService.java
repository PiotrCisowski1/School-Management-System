package com.cisowski.schoolmanagement.grade.service;

import com.cisowski.schoolmanagement.grade.model.gradeType.AddGradeTypeRequest;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeResponse;
import com.cisowski.schoolmanagement.grade.model.gradeType.PatchGradeTypeRequest;

import java.util.Collection;

public interface GradeTypeService {
    GradeTypeResponse addGradeType(AddGradeTypeRequest request);
    GradeTypeResponse patchGradeType(PatchGradeTypeRequest request, Long gradeTypeId);
    void deleteGradeType(Long gradeTypeId);
    GradeTypeResponse getGradeType(Long gradeTypeId);
    Collection<GradeTypeResponse> getGradeTypes();
    GradeTypeEntity fetchGradeType(Long gradeTypeId);
}
