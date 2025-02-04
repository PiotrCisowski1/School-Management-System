package com.cisowski.schoolmanagement.mapper;

import com.cisowski.schoolmanagement.model.request.StudentCreateRequest;
import com.cisowski.schoolmanagement.model.request.StudentPatchRequest;
import com.cisowski.schoolmanagement.model.response.AddStudentResponse;
import com.cisowski.schoolmanagement.model.response.StudentSummaryResponse;
import com.cisowski.schoolmanagement.model.entity.Parent;
import com.cisowski.schoolmanagement.model.entity.Student;
import com.cisowski.schoolmanagement.model.response.StudentDetailedResponse;
import com.cisowski.schoolmanagement.model.entity.Yearbook;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentMapper {

    @Mapping(target = "parents", ignore = true)
    @Mapping(target = "password", ignore = true)
    Student toStudentEntity(StudentCreateRequest studentDTO);
    @Mapping(target = "parents", ignore = true)
    @Mapping(target = "password", ignore = true)
    Student toStudentEntity(StudentPatchRequest request);
    StudentDetailedResponse toStudentResponse(Student student);
    @Mapping(target = "authority", source = "authority")
    AddStudentResponse toAddStudentResponse(Student student);
    List<StudentSummaryResponse> toStudentsResponse(Collection<Student> students);
    StudentSummaryResponse toSummaryResponse(Student student);
    @Mapping(target = "parents", ignore = true)
    @Mapping(target = "password", ignore = true)
    void patchStudent(Student request, @MappingTarget Student existingEntity);
}
