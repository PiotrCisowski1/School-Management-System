package com.cisowski.schoolmanagement.users.teacher.model;

import com.cisowski.schoolmanagement.model.entity.SubjectEntity;
import com.cisowski.schoolmanagement.model.entity.YearbookEntity;
import com.cisowski.schoolmanagement.users.teacher.model.EmployeeEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "teachers_details")
@PrimaryKeyJoinColumn(name = "user_id")
@DiscriminatorValue("TEACHER")
public class TeacherEntity extends EmployeeEntity {
    @OneToOne(mappedBy = "headTeacher")
    private YearbookEntity leadingYearbook;
    @ManyToMany()
    @JoinTable(
            name = "teachers_subjects",
            joinColumns = {@JoinColumn(name = "teacher_details_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "subject_id", referencedColumnName = "id")}
    )
    private Set<SubjectEntity> teachingSubjects = new HashSet<>();

    @Override
    public String toString() {
        return super.toString();
    }
}
