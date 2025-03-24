package com.cisowski.schoolmanagement.classroom.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Collection;

@Data
public class ClassroomRequest {
    @NotNull
    @Size(max = 50, min = 1)
    private String name;
    @NotNull
    @Size(min = 1)
    private Integer capacity;
    @Size(max = 250)
    private String notes;
    private Collection<Integer> equipmentIds;
}
