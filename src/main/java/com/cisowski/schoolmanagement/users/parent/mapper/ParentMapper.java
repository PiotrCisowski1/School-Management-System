package com.cisowski.schoolmanagement.users.parent.mapper;

import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentPatchRequest;
import com.cisowski.schoolmanagement.users.parent.model.AddParentResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentCreateRequest;
import com.cisowski.schoolmanagement.users.parent.model.ParentDetailedResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ParentMapper {
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "password", ignore = true)
    ParentEntity toParentEntity(ParentCreateRequest parentDto);
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "password", ignore = true)
    ParentEntity toParentEntity(ParentPatchRequest parentDto);
    ParentDetailedResponse toParentDetailedResponse(ParentEntity parent);
    @Mapping(target = "authority", source = "authority")
    AddParentResponse toAddParentResponse(ParentEntity parent);
    List<ParentSummaryResponse> toParentsResponse(Collection<ParentEntity> parents);
    ParentSummaryResponse toSummaryResponse(ParentEntity parent);
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "password", ignore = true)
    void patchParentEntity(ParentEntity requestParent, @MappingTarget ParentEntity existingParentEntity);


}
