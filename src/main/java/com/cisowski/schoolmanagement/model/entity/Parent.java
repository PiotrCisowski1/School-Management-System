package com.cisowski.schoolmanagement.model.entity;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "parents")
public class Parent extends User{

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "students_parents", joinColumns = @JoinColumn(name = "parent_id",referencedColumnName = "id"),
                inverseJoinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"))
    private List<Student> children;

    public Parent(Integer id, boolean isEnabled, Collection<Authority> authority, List<Student> children) {
        super(id, isEnabled, authority);
        this.children = children;
    }

    public List<Student> getChildren() {
        return children;
    }

    public void setChildren(List<Student> children) {
        this.children = children;
    }
}
