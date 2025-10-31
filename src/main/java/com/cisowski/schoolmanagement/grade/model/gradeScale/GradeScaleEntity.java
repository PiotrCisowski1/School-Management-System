package com.cisowski.schoolmanagement.grade.model.gradeScale;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grade_scales")
@Data
public class GradeScaleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 250)
    private String description;

    @Column(nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "gradeScale", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<GradeValueEntity> gradeValues = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Boolean isHide = false;

    @Override
    public String toString() {
        return "GradeScaleEntity{" +
                "name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", description='" + description + '\'' +
                ", gradeValuesCount=" + (gradeValues != null ? gradeValues.size() : 0) +
                ", id=" + id +
                ", isActive=" + isActive +
                ", updatedAt=" + updatedAt +
                ", isHide=" + isHide +
                '}';
    }
}
