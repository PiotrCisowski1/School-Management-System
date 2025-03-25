package com.cisowski.schoolmanagement.classroom.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classroom_equipment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomEquipment {
    @EmbeddedId
    private ClassroomEquipmentId id;

    @ManyToOne
    @MapsId("classroomId")
    @JoinColumn(name = "classroom_id")
    @JsonBackReference
    private ClassroomEntity classroom;

    @ManyToOne
    @MapsId("equipmentId")
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @Column(nullable = false)
    private Integer quantity;
}
