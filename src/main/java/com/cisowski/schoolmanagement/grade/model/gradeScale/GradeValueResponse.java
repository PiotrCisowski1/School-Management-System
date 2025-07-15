package com.cisowski.schoolmanagement.grade.model.gradeScale;

import lombok.Data;

@Data
public class GradeValueResponse {

    private Long id;
    private String displayValue;
    private Integer numericValue;
    private String description;
    private Boolean isPassingGrade;
    private Boolean isHide;

    @Override
    public String toString() {
        return "GradeValueResponse{" +
                "description='" + description + '\'' +
                ", id=" + id +
                ", displayValue='" + displayValue + '\'' +
                ", numericValue=" + numericValue +
                ", isPassingGrade=" + isPassingGrade +
                ", isHide=" + isHide +
                '}';
    }
}
