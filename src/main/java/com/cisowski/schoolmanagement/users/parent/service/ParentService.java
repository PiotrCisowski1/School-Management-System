package com.cisowski.schoolmanagement.users.parent.service;


import com.cisowski.schoolmanagement.users.common.service.BaseUserService;
import com.cisowski.schoolmanagement.users.parent.model.*;

import java.util.Collection;
import java.util.List;

public interface ParentService extends BaseUserService {
    AddParentResponse addParent(ParentCreateRequest parentDto);
    ParentDetailedResponse updateParent(ParentPatchRequest parentDto, Integer parentId);
    List<ParentSummaryResponse> findAll();
    ParentDetailedResponse findById(Integer parentId);
    List<ParentEntity> fetchParentEntities(Collection<Integer> parentIds);
    ParentEntity fetchParentEntity(Integer parentId);
}
