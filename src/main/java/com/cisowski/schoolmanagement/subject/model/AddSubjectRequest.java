package com.cisowski.schoolmanagement.subject.model;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class AddSubjectRequest {
    @NotNull
    private String name;
    @NotNull
    @Length(max = 6, message = "Code's length cannot be more than 6 characters")
    private String code;
    private String description;
    @NotNull
    private Integer subjectTypeId;
}
