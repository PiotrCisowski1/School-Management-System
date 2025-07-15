package com.cisowski.schoolmanagement.grade.model.gradeType;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;

@Data
@Entity
@Table(name = "grade_type")
public class GradeTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;
    @Column(nullable = false)
    private String gradeScope;
    @Column(nullable = false)
    private Double weight;

    @Override
    public String toString() {
        return "GradeTypeEntity{" +
                "id=" + id +
                ", gradeScope='" + gradeScope + '\'' +
                ", weight=" + weight +
                '}';
    }
}
