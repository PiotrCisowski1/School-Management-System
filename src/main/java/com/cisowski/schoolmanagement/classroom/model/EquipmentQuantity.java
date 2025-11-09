package com.cisowski.schoolmanagement.classroom.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentQuantity {
    private Integer equipmentId;
    private Integer quantity;
}
