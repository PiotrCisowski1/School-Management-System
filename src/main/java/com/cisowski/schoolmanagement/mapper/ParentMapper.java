package com.cisowski.schoolmanagement.mapper;

import com.cisowski.schoolmanagement.model.request.ParentPatchRequest;
import com.cisowski.schoolmanagement.model.response.AddParentResponse;
import com.cisowski.schoolmanagement.model.request.ParentCreateRequest;
import com.cisowski.schoolmanagement.model.response.ParentDetailedResponse;
import com.cisowski.schoolmanagement.model.response.ParentSummaryResponse;
import com.cisowski.schoolmanagement.model.entity.Parent;
import com.cisowski.schoolmanagement.model.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ParentMapper {
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "password", ignore = true)
    Parent toParentEntity(ParentCreateRequest parentDto);
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "password", ignore = true)
    Parent toParentEntity(ParentPatchRequest parentDto);
    ParentDetailedResponse toParentDetailedResponse(Parent parent);
    @Mapping(target = "authority", source = "authority")
    AddParentResponse toAddParentResponse(Parent parent);
    List<ParentSummaryResponse> toParentsResponse(Collection<Parent> parents);
    ParentSummaryResponse toSummaryResponse(Parent parent);
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "password", ignore = true)
    void patchParentEntity(Parent requestParent, @MappingTarget Parent existingParentEntity);


}
