package com.cisowski.schoolmanagement.model.entity;

import com.cisowski.schoolmanagement.model.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "parents_details")
@PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
@DiscriminatorValue("PARENT")
public class Parent extends User {
    @ManyToMany(mappedBy = "parents", fetch = FetchType.LAZY)
    Collection<Student> children = new ArrayList<>();

    @Override
    public String toString() {
        return super.toString();
    }
}
