package com.cisowski.schoolmanagement.users.student.mapper;

import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.users.parent.mapper.ParentMapper;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentCreateRequest;
import com.cisowski.schoolmanagement.users.student.model.StudentPatchRequest;
import com.cisowski.schoolmanagement.users.student.model.AddStudentResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentSummaryResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentDetailedResponse;
import com.cisowski.schoolmanagement.yearbook.mapper.YearbookMapper;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;

@Mapper(config = BaseMapperConfig.class,
        uses = {YearbookMapper.class, ParentMapper.class})
public interface StudentMapper {

    @Mapping(target = "parents", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "yearbook", ignore = true)
    StudentEntity toStudentEntity(StudentCreateRequest studentDTO);
    @Mapping(target = "parents", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "yearbook", ignore = true)
    StudentEntity toStudentEntity(StudentPatchRequest request);
    @Mapping(target = "yearbook", source = "yearbook", qualifiedByName = "toYearbookSummaryResponse")
    @Mapping(target = "parents", source = "parents", qualifiedByName = "toParentSummaryResponseList")
    StudentDetailedResponse toStudentResponse(StudentEntity student);
    @Mapping(target = "authority", source = "authority")
    AddStudentResponse toAddStudentResponse(StudentEntity student);
    @Named("toStudentSummaryResponseList")
    List<StudentSummaryResponse> toStudentsResponse(Collection<StudentEntity> students);
    @Named("toStudentSummaryResponse")
    StudentSummaryResponse toSummaryResponse(StudentEntity student);
    @Mapping(target = "parents", ignore = true)
    @Mapping(target = "password", ignore = true)
    void patchStudent(StudentEntity request, @MappingTarget StudentEntity existingEntity);
}
