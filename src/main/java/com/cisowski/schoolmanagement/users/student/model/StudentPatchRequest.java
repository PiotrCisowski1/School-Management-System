package com.cisowski.schoolmanagement.users.student.model;

import com.cisowski.schoolmanagement.users.common.model.BasePatchUserRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
public class StudentPatchRequest extends BasePatchUserRequest {
    private Integer yearbookId;
}
