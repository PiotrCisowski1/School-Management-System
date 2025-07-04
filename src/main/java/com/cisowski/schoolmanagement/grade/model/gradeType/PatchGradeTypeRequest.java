package com.cisowski.schoolmanagement.grade.model.gradeType;

import lombok.Data;

@Data
public class PatchGradeTypeRequest {
    private String name;
    private Double weight;

    @Override
    public String toString() {
        return "PatchGradeTypeRequest{" +
                "name='" + name + '\'' +
                ", weight=" + weight +
                '}';
    }
}
