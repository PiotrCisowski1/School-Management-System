package com.cisowski.schoolmanagement.subject.model;

import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@Data
@Entity
@Table(name = "subjects")
public class SubjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String code;
    @ManyToMany(mappedBy = "teachingSubjects", fetch = FetchType.LAZY)
    private Collection<TeacherEntity> teachers = new ArrayList<>();
    @Column
    private String description;
    @ManyToMany(mappedBy = "mainCourseSubjects", fetch = FetchType.LAZY)
    private Collection<YearbookEntity> yearbooksTakingSubject = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private SubjectTypeEntity subjectType;

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '}' +
                ", type='" + subjectType + '}';
    }
}
