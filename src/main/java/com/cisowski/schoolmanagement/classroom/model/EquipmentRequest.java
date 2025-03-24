package com.cisowski.schoolmanagement.classroom.model;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EquipmentRequest {
    @Size(min = 1, max = 50)
    private String name;
}
