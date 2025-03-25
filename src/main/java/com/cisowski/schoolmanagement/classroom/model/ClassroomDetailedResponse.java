package com.cisowski.schoolmanagement.classroom.model;

import lombok.Data;

import java.util.Collection;

@Data
public class ClassroomDetailedResponse {
    private Integer id;
    private String name;
    private Integer capacity;
    private String notes;
    private Collection<ClassroomEquipment> equipments;
}
