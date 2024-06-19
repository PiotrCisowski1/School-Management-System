package com.cisowski.schoolmanagement.model.entity;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "students")
public class Student extends User {
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "students_parents", joinColumns = @JoinColumn(name = "student_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id", referencedColumnName = "id"))
    private List<Parent> parents;

    public Student(Integer id, boolean isEnabled, Collection<Authority> authority, List<Parent> parents) {
        super(id, isEnabled, authority);
        this.parents = parents;
    }

    public List<Parent> getParents() {
        return parents;
    }

    public void setParents(List<Parent> parents) {
        this.parents = parents;
    }
}
