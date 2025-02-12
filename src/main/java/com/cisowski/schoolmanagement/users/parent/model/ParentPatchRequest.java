package com.cisowski.schoolmanagement.users.parent.model;

import com.cisowski.schoolmanagement.users.common.model.BasePatchUserRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
public class ParentPatchRequest extends BasePatchUserRequest {
    Collection<Integer> childrenIdsToAdd;
    Collection<Integer> childrenIdsToRemove;
}
