package com.cisowski.schoolmanagement.model.response;

import lombok.Data;


@Data
public class SubjectSummaryResponse {
    private Integer id;
    private String name;
    private String code;
    private String description;

}
