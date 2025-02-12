package com.cisowski.schoolmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Data
@Entity
@Table(name = "yearbooks")
public class Yearbook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne()
    @JoinColumn(name = "head_teacher_user_id", referencedColumnName = "user_id")
    private Teacher headTeacher;
    @Column(nullable = false)
    private String symbol;
    @Column(nullable = false)
    private ZonedDateTime startingYear;
    private ZonedDateTime graduationYear;
    @OneToMany(mappedBy = "yearbook")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonIgnore
    private Collection<Student> studentsInYearbook = new ArrayList<>();
    @ManyToMany
    @JoinTable(
            name = "yearbooks_subjects",
            joinColumns = {@JoinColumn(name = "yearbook_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "subject_id", referencedColumnName = "id")}
    )
    private Collection<Subject> mainCourseSubjects = new ArrayList<>();

    @Override
    public String toString() {
        return "Yearbook{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
