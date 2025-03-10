package com.cisowski.schoolmanagement.users.parent.mapper;

import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentPatchRequest;
import com.cisowski.schoolmanagement.users.parent.model.AddParentResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentCreateRequest;
import com.cisowski.schoolmanagement.users.parent.model.ParentDetailedResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentSummaryResponse;
import com.cisowski.schoolmanagement.users.student.mapper.StudentMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ParentMapper {

    @Autowired
    @Lazy
    protected StudentMapper studentMapper;

    @Mapping(target = "children", ignore = true)
    @Mapping(target = "password", ignore = true)
    public abstract ParentEntity toParentEntity(ParentCreateRequest parentDto);
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "password", ignore = true)
    public abstract ParentEntity toParentEntity(ParentPatchRequest parentDto);
    @Mapping(target = "children", ignore = true)
    public abstract ParentDetailedResponse toParentDetailedResponse(ParentEntity parent);

    @AfterMapping
    protected void mapStudentList(ParentEntity parentEntity, @MappingTarget ParentDetailedResponse response){
        if(!CollectionUtils.isEmpty(parentEntity.getChildren())){
            response.setChildren(studentMapper.toStudentsResponse(parentEntity.getChildren()));
        }
    }

    @Mapping(target = "authority", source = "authority")
    public abstract AddParentResponse toAddParentResponse(ParentEntity parent);
    @Named("toParentSummaryResponseList")
    public abstract List<ParentSummaryResponse> toParentsResponse(Collection<ParentEntity> parents);
    public abstract ParentSummaryResponse toSummaryResponse(ParentEntity parent);
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "password", ignore = true)
    public abstract void patchParentEntity(ParentEntity requestParent, @MappingTarget ParentEntity existingParentEntity);


}
