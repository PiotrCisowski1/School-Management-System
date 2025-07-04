package com.cisowski.schoolmanagement.grade.model.grade;

import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import com.cisowski.schoolmanagement.grade.model.gradeValue.GradeValueEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigInteger;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "grades")
public class GradeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

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
    @JoinColumn(name = "value_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private GradeValueEntity value;

    @CreationTimestamp
    private LocalDate createdAt;

    private String comments;
}
