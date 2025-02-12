package com.cisowski.schoolmanagement.users.parent.service;


import com.cisowski.schoolmanagement.users.common.service.BaseUserService;
import com.cisowski.schoolmanagement.users.parent.model.ParentCreateRequest;
import com.cisowski.schoolmanagement.users.parent.model.ParentPatchRequest;
import com.cisowski.schoolmanagement.users.parent.model.AddParentResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentDetailedResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentSummaryResponse;

import java.util.List;

public interface ParentService extends BaseUserService {
    AddParentResponse addParent(ParentCreateRequest parentDto);
    ParentDetailedResponse updateParent(ParentPatchRequest parentDto, Integer parentId);
    List<ParentSummaryResponse> findAll();
    ParentDetailedResponse findById(Integer parentId);
}
