package com.cisowski.schoolmanagement.service;


import com.cisowski.schoolmanagement.model.request.ParentCreateRequest;
import com.cisowski.schoolmanagement.model.request.ParentPatchRequest;
import com.cisowski.schoolmanagement.model.response.AddParentResponse;
import com.cisowski.schoolmanagement.model.response.ParentDetailedResponse;
import com.cisowski.schoolmanagement.model.response.ParentSummaryResponse;

import java.util.List;

public interface ParentService extends BaseUserService {
    AddParentResponse addParent(ParentCreateRequest parentDto);
    ParentDetailedResponse updateParent(ParentPatchRequest parentDto, Integer parentId);
    List<ParentSummaryResponse> findAll();
    ParentDetailedResponse findById(Integer parentId);
}
