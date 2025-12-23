package com.cisowski.schoolmanagement.grade.model.gradeScale;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "grade_values")
@Data
public class GradeValueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_scale_id", nullable = false)
    private GradeScaleEntity gradeScale;

    @Column(nullable = false)
    private String displayValue;

    @Column(nullable = false)
    private Integer numericValue;

    @Column
    @Length(max = 200)
    private String description;

    @Column(nullable = false)
    private Boolean isPassingGrade = false;

    private Boolean isHide = false;

    @Override
    public String toString() {
        Long gradeScaleId = -1L;
        if(gradeScale != null)
            gradeScaleId = gradeScale.getId();

        return "GradeValueEntity{" +
                "description='" + description + '\'' +
                ", id=" + id +
                ", gradeScaleId=" + gradeScaleId +
                ", displayValue='" + displayValue + '\'' +
                ", numericValue=" + numericValue +
                ", isPassingGrade=" + isPassingGrade +
                ", isHide=" + isHide +
                '}';
    }

}
