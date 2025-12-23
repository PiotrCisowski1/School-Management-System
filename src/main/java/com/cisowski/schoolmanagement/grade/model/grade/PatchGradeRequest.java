package com.cisowski.schoolmanagement.grade.model.grade;

import lombok.Data;

@Data
public class PatchGradeRequest {
    private Integer studentId;
    private Integer subjectId;
    private Long gradeTypeId;
    private Long gradeValueId;
    private String comments;

    @Override
    public String toString() {
        return "PatchGradeRequest{" +
                "comments='" + comments + '\'' +
                ", studentId=" + studentId +
                ", subjectId=" + subjectId +
                ", gradeTypeId=" + gradeTypeId +
                ", gradeValueId=" + gradeValueId +
                '}';
    }
}
