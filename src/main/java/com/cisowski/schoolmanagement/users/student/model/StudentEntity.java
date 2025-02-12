package com.cisowski.schoolmanagement.users.student.model;

import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.model.entity.YearbookEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "students_details")
@PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
@DiscriminatorValue("STUDENT")
public class StudentEntity extends UserEntity {
    @ManyToOne()
    @JoinColumn(name = "yearbook_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    YearbookEntity yearbook;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "students_parents",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id", referencedColumnName = "user_id"))
    Collection<ParentEntity> parents = new ArrayList<>();
    ZonedDateTime dateOfGraduation;

    @Override
    public String toString() {
        return super.toString();
    }
}
