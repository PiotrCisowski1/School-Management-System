package com.cisowski.schoolmanagement.subject.model;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PatchSubjectRequest {
    private String name;
    @Length(max = 6, message = "Code's length cannot be more than 6 characters")
    private String code;
    private String description;
    private Integer subjectTypeId;
}
