package com.cisowski.schoolmanagement.classroom.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Collection<ClassroomEquipment> classroomEquipments = new ArrayList<>();

    public void addEquipment(Equipment equipment, int quantity){
        classroomEquipments.add(ClassroomEquipment.builder()
                .id(new ClassroomEquipmentId(this.id, equipment.getId()))
                .classroom(this)
                .equipment(equipment)
                .quantity(quantity)
                .build());
    }

    @Override
    public String toString() {
        return  "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity;
    }
}
