package com.cisowski.schoolmanagement.classroom.model;

import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

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

    public void removeEquipment(Equipment equipment) {
        Optional<ClassroomEquipment> toRemove = classroomEquipments.stream()
                .filter(classroomEq -> classroomEq.getId().getEquipmentId().equals(equipment.getId()))
                .findFirst();
        if(toRemove.isEmpty())
            throw new SpecificationBrokenException(
                    String.format(
                            "Equipment with ID: %s, is not associated with Classroom with ID: %s",
                            equipment.getId(),
                            this.getId()));
        classroomEquipments.remove(toRemove.get());
    }

    @Override
    public String toString() {
        return  "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity;
    }
}
