package com.cisowski.schoolmanagement.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
public class StudentCreateRequest extends BaseCreateUserRequest {

    @NotNull
    private Integer yearbookId;
    private Collection<Integer> parentsIds;

}
