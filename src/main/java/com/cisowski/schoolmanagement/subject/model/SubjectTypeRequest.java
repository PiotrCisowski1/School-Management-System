package com.cisowski.schoolmanagement.subject.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SubjectTypeRequest {
    @NotNull
    @Size(max = 20)
    private String name;
}
