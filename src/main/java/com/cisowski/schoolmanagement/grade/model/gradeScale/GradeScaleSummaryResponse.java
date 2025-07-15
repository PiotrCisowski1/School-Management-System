package com.cisowski.schoolmanagement.grade.model.gradeScale;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GradeScaleSummaryResponse {
    private Long id;
    private String name;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Boolean isHide;

    @Override
    public String toString() {
        return "GradeScaleSummaryResponse{" +
                "createdAt=" + createdAt +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", isActive=" + isActive +
                ", isHide=" + isHide +
                '}';
    }
}
