package com.cisowski.schoolmanagement.grade.model.grade;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddGradeRequest {

    @NotNull
    private Integer studentId;
    @NotNull
    private Integer teacherId;
    @NotNull
    private Integer subjectId;
    @NotNull
    private Long gradeTypeId;
    @NotNull
    private Long gradeValueId;

    private String comments;

    @Override
    public String toString() {
        return "AddGradeRequest{" +
                "comments='" + comments + '\'' +
                ", studentId=" + studentId +
                ", teacherId=" + teacherId +
                ", subjectId=" + subjectId +
                ", gradeTypeId=" + gradeTypeId +
                ", gradeValueId=" + gradeValueId +
                '}';
    }
}
