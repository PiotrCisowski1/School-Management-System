package com.cisowski.schoolmanagement.grade.model.grade;

import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeValueEntity;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.*;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "grades")
public class GradeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "teacher_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private TeacherEntity teacher;

    @ManyToOne
    @JoinColumn(name = "subject_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private SubjectEntity subject;

    @ManyToOne
    @JoinColumn(name = "grade_type_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private GradeTypeEntity gradeType;

    @ManyToOne
    @JoinColumn(name = "grade_value_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private GradeValueEntity gradeValue;

    @CreationTimestamp
    private LocalDate createdAt;

    private String comments;

    @Override
    public String toString() {
        return "GradeEntity{" +
                "id=" + id +
                ", student=" + student.getId() +
                ", teacher=" + teacher.getId() +
                ", subject=" + subject.getId() +
                ", gradeType=" + gradeType.getId() +
                ", gradeValue=" + gradeValue.getId() +
                ", createdAt=" + createdAt +
                ", comments='" + comments + '\'' +
                '}';
    }
}
