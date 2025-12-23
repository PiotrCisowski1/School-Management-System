package com.cisowski.schoolmanagement.grade.model.gradeScale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class GradeScaleRequest {

    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @Size(max = 250)
    private String description;

    @NotNull
    private Boolean isActive;

    @NotNull
    @NotEmpty
    @Valid
    private List<GradeValueDto> gradeValues;

    @Override
    public String toString() {
        return "GradeScaleRequest{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", isActive=" + isActive +
                ", gradeValues=" + gradeValues.toString() +
                '}';
    }
}
