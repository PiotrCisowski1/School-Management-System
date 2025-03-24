package com.cisowski.schoolmanagement.classroom.model;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Collection;

@Data
public class PatchClassroomRequest {
    @Size(max = 50)
    private String name;
    @Size(min = 1)
    private Integer capacity;
    @Size(max = 250)
    private String notes;
    private Collection<Integer> equipmentIdsToAdd;
    private Collection<Integer> equipmentIdsToRemove;
}
