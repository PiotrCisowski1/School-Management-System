package com.cisowski.schoolmanagement.classroom.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;

@Entity
@Table(name = "classrooms")
@Data
public class ClassroomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    private String notes;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "classroom_equipment",
            joinColumns = {@JoinColumn(name = "classroom_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "subject_id", referencedColumnName = "id")}
    )
    private Collection<Equipment> equipments;

    @Override
    public String toString() {
        return  "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity;
    }
}
