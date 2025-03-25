package com.cisowski.schoolmanagement.classroom.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
public class ClassroomEquipmentId implements Serializable {
    @Column(name = "classroom_id")
    private Integer classroomId;
    @Column(name = "equipment_id")
    private Integer equipmentId;
}
