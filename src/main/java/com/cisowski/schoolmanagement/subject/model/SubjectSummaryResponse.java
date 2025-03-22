package com.cisowski.schoolmanagement.subject.model;

import lombok.Data;


@Data
public class SubjectSummaryResponse {
    private Integer id;
    private String name;
    private String code;
    private String description;
    private String subjectType;
}
