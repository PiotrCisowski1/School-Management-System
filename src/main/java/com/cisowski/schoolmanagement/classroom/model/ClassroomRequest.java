package com.cisowski.schoolmanagement.classroom.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Collection;

@Data
public class ClassroomRequest {
    @Size(max = 50, min = 1, message = "Name must be between 1 and 50 characters")
    @NotBlank(message = "Name must have a value")
    private String name;
    @NotNull(message = "Capacity must have a value")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
    @Size(max = 250, message = "Notes cannot exceed 250 characters")
    private String notes;
    private Collection<EquipmentQuantity> equipments;
}
