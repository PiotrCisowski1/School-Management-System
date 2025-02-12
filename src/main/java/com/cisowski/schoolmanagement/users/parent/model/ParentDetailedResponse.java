package com.cisowski.schoolmanagement.users.parent.model;

import com.cisowski.schoolmanagement.users.student.model.StudentSummaryResponse;
import com.cisowski.schoolmanagement.users.common.model.BaseUserDetailedResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;
@EqualsAndHashCode(callSuper = true)
@Data
public class ParentDetailedResponse extends BaseUserDetailedResponse {
    private Collection<StudentSummaryResponse> children;

}
