package com.cisowski.schoolmanagement.grade.model.gradeScale;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GradeValueDto {

    @NotNull
    @Size(max = 50)
    private String displayValue;

    @NotNull
    private Integer numericValue;

    @Size(max = 200)
    private String description;

    @NotNull
    private Boolean isPassingGrade;

    @Override
    public String toString() {
        return "GradeValueDto{" +
                "description='" + description + '\'' +
                ", displayValue='" + displayValue + '\'' +
                ", numericValue=" + numericValue +
                ", isPassingGrade=" + isPassingGrade +
                '}';
    }
}
