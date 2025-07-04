package com.cisowski.schoolmanagement.grade.model.gradeType;

import lombok.Data;

import java.math.BigInteger;

@Data
public class GradeTypeResponse {
    private BigInteger id;
    private String name;
    private Double weight;

    @Override
    public String toString() {
        return "GradeTypeResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                '}';
    }
}
