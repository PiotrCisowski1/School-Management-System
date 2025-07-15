package com.cisowski.schoolmanagement.grade.model.gradeScale;

import lombok.Data;

@Data
public class PatchGradeScaleRequest {

    private String name;
    private String description;
    private Boolean isActive;
}
