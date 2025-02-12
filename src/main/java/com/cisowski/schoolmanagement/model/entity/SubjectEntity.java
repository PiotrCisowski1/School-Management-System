package com.cisowski.schoolmanagement.model.entity;

import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
    @ManyToMany(mappedBy = "teachingSubjects")
    private Collection<TeacherEntity> teachers;
    @Column
    private String description;
    @ManyToMany(mappedBy = "mainCourseSubjects")
    private Set<YearbookEntity> yearbooksTakingSubject = new HashSet<>();


    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '}';
    }
}
