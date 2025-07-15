package com.cisowski.schoolmanagement.grade.model.gradeScale;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GradeScaleResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private List<GradeValueResponse> gradeValues;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isHide;

    @Override
    public String toString() {
        return "GradeScaleResponse{" +
                "createdAt=" + createdAt +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                ", gradeValues=" + gradeValues.toString() +
                ", updatedAt=" + updatedAt +
                ", isHide=" + isHide +
                '}';
    }
}
