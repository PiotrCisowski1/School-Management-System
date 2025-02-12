package com.cisowski.schoolmanagement.users.parent.model;

import com.cisowski.schoolmanagement.users.common.model.BaseCreateUserRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
public class ParentCreateRequest extends BaseCreateUserRequest {
    @NotEmpty(message = "Cannot create Parent without children selected")
    @NotNull(message = "Selected children cannot be null")
    Collection<Integer> childrenIds;

}
