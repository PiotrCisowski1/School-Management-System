package com.cisowski.schoolmanagement.grade.model.gradeValue;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;

@Data
@Entity
@Table(name = "grade_values")
public class GradeValueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;
    @Column(nullable = false)
    private Double value;
}
