package com.cisowski.schoolmanagement.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String code;
    @ManyToMany(mappedBy = "teachingSubjects")
    private Collection<Teacher> teachers;
    @Column
    private String description;
    @ManyToMany(mappedBy = "mainCourseSubjects")
    private Set<Yearbook> yearbooksTakingSubject = new HashSet<>();


    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '}';
    }
}
