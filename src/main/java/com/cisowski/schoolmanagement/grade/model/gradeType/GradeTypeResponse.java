package com.cisowski.schoolmanagement.grade.model.gradeType;

import lombok.Data;


@Data
public class GradeTypeResponse {
    private Long id;
    private String gradeScope;
    private Double weight;

    @Override
    public String toString() {
        return "GradeTypeResponse{" +
                "id=" + id +
                ", gradeScope='" + gradeScope + '\'' +
                ", weight=" + weight +
                '}';
    }
}
