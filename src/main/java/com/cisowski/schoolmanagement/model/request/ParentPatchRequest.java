package com.cisowski.schoolmanagement.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
public class ParentPatchRequest extends BasePatchUserRequest {
    Collection<Integer> childrenIdsToAdd;
    Collection<Integer> childrenIdsToRemove;
}
