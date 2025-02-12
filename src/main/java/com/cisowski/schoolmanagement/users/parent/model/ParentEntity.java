package com.cisowski.schoolmanagement.users.parent.model;

import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "parents_details")
@PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
@DiscriminatorValue("PARENT")
public class ParentEntity extends UserEntity {
    @ManyToMany(mappedBy = "parents", fetch = FetchType.LAZY)
    Collection<StudentEntity> children = new ArrayList<>();

    @Override
    public String toString() {
        return super.toString();
    }
}
