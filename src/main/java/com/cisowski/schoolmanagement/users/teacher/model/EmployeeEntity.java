package com.cisowski.schoolmanagement.users.teacher.model;

import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "employees")
@PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
public abstract class EmployeeEntity extends UserEntity {
    @Column(nullable = false)
    private Date employmentStartDate;
    private Date employmentEndDate;

    @Override
    public String toString() {
        return super.toString();
    }
}
