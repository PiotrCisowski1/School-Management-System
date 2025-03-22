package com.cisowski.schoolmanagement.users.teacher.model;

import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "teachers_details")
@PrimaryKeyJoinColumn(name = "user_id")
@DiscriminatorValue("TEACHER")
public class TeacherEntity extends EmployeeEntity {
    @OneToOne(mappedBy = "headTeacher")
    private YearbookEntity leadingYearbook;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "teachers_subjects",
            joinColumns = {@JoinColumn(name = "teacher_details_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "subject_id", referencedColumnName = "id")}
    )
    private Collection<SubjectEntity> teachingSubjects;

    @Override
    public String toString() {
        return super.toString();
    }
}
