package com.cisowski.schoolmanagement.grade.model.gradeType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddGradeTypeRequest {
    @NotNull
    @Size(min = 1, max = 50)
    private String name;
    @NotNull
    private Double weight;

    @Override
    public String toString() {
        return "AddGradeTypeRequest{" +
                "name='" + name + '\'' +
                ", weight=" + weight +
                '}';
    }
}
