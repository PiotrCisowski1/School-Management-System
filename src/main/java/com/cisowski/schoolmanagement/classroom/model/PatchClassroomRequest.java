package com.cisowski.schoolmanagement.classroom.model;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Collection;

@Data
public class PatchClassroomRequest {
    @Size(max = 50, message = "Name can be up to 50 characters")
    private String name;
    private Integer capacity;
    @Size(max = 250, message = "Notes cannot exceed 250 characters")
    private String notes;
    private Collection<EquipmentQuantity> equipmentIdsToAdd;
    private Collection<EquipmentQuantity> equipmentIdsToRemove;
}
