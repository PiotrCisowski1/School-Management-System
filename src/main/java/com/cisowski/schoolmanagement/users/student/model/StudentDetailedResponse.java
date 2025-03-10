package com.cisowski.schoolmanagement.users.student.model;

import com.cisowski.schoolmanagement.yearbook.model.YearbookSummaryResponse;
import com.cisowski.schoolmanagement.users.common.model.BaseUserDetailedResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentSummaryResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;
@EqualsAndHashCode(callSuper = true)
@Data
public class StudentDetailedResponse extends BaseUserDetailedResponse {

    private YearbookSummaryResponse yearbook;
    private Collection<ParentSummaryResponse> parents;

}
