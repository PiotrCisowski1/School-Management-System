package com.cisowski.schoolmanagement.classroom.model;

import lombok.Data;

@Data
public class ClassroomSummaryResponse {
    private Integer id;
    private String name;
    private Integer capacity;
    private Integer equipmentCount;
}
