package com.cisowski.schoolmanagement.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;
@EqualsAndHashCode(callSuper = true)
@Data
public class StudentDetailedResponse extends BaseUserDetailedResponse {

    private YearbookSummaryResponse yearbook;
    private Collection<ParentSummaryResponse> parents;

}
