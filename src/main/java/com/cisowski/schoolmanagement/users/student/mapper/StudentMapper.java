package com.cisowski.schoolmanagement.users.student.mapper;

import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentCreateRequest;
import com.cisowski.schoolmanagement.users.student.model.StudentPatchRequest;
import com.cisowski.schoolmanagement.users.student.model.AddStudentResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentSummaryResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentDetailedResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentMapper {

    @Mapping(target = "parents", ignore = true)
    @Mapping(target = "password", ignore = true)
    StudentEntity toStudentEntity(StudentCreateRequest studentDTO);
    @Mapping(target = "parents", ignore = true)
    @Mapping(target = "password", ignore = true)
    StudentEntity toStudentEntity(StudentPatchRequest request);
    StudentDetailedResponse toStudentResponse(StudentEntity student);
    @Mapping(target = "authority", source = "authority")
    AddStudentResponse toAddStudentResponse(StudentEntity student);
    List<StudentSummaryResponse> toStudentsResponse(Collection<StudentEntity> students);
    StudentSummaryResponse toSummaryResponse(StudentEntity student);
    @Mapping(target = "parents", ignore = true)
    @Mapping(target = "password", ignore = true)
    void patchStudent(StudentEntity request, @MappingTarget StudentEntity existingEntity);
}
