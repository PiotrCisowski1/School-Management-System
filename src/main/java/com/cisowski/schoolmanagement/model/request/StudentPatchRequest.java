package com.cisowski.schoolmanagement.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
public class StudentPatchRequest extends BasePatchUserRequest {
    private Integer yearbookId;
    private Collection<Integer> parentIdsToAdd;
    private Collection<Integer> parentIdsToRemove;
}
