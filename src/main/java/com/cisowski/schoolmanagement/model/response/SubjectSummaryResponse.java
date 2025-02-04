package com.cisowski.schoolmanagement.model.response;

import lombok.Data;

import java.util.Collection;
import java.util.Set;

@Data
public class SubjectSummaryResponse {
    private Integer id;
    private String name;
    private String code;
    private String description;

}
