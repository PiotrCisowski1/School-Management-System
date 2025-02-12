package com.cisowski.schoolmanagement.users.student.model;

import com.cisowski.schoolmanagement.users.common.model.BaseCreateUserRequest;
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
