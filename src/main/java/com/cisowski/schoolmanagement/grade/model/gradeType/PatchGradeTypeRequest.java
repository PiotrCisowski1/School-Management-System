package com.cisowski.schoolmanagement.grade.model.gradeType;

import lombok.Data;

@Data
public class PatchGradeTypeRequest {
    private String gradeScope;
    private Double weight;

    @Override
    public String toString() {
        return "PatchGradeTypeRequest{" +
                "gradeScope='" + gradeScope + '\'' +
                ", weight=" + weight +
                '}';
    }
}
