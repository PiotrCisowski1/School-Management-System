package com.cisowski.schoolmanagement.model.entity;

import com.cisowski.schoolmanagement.model.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "employees")
@PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
public abstract class Employee extends User {
    @Column(nullable = false)
    private Date employmentStartDate;
    private Date employmentEndDate;

    @Override
    public String toString() {
        return super.toString();
    }
}
