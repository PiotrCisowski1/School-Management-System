package com.cisowski.schoolmanagement.model.entity;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "teachers")
public class Teacher extends User{
    @Column(nullable = false)
    private Date employmentStartDate;
    @Column(nullable = false)
    private Date employmentEndDate;
    @Column(nullable = false)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "teachers_specializations",
            joinColumns = @JoinColumn(
                    name = "teacher_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "specialization_id", referencedColumnName = "id"))
    private List<Specialization> specializations;

    public Teacher(Integer id, boolean isEnabled, Collection<Authority> authority, Date employmentStartDate, Date employmentEndDate, List<Specialization> specializations) {
        super(id, isEnabled, authority);
        this.employmentStartDate = employmentStartDate;
        this.employmentEndDate = employmentEndDate;
        this.specializations = specializations;
    }

    public Date getEmploymentStartDate() {
        return employmentStartDate;
    }

    public void setEmploymentStartDate(Date employmentStartDate) {
        this.employmentStartDate = employmentStartDate;
    }

    public Date getEmploymentEndDate() {
        return employmentEndDate;
    }

    public void setEmploymentEndDate(Date employmentEndDate) {
        this.employmentEndDate = employmentEndDate;
    }

    public List<Specialization> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<Specialization> specializations) {
        this.specializations = specializations;
    }
}
