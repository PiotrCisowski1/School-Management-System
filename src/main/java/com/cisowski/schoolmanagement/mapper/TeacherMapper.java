package com.cisowski.schoolmanagement.mapper;

import com.cisowski.schoolmanagement.model.request.TeacherCreateRequest;
import com.cisowski.schoolmanagement.model.request.TeacherPatchRequest;
import com.cisowski.schoolmanagement.model.response.AddTeacherResponse;
import com.cisowski.schoolmanagement.model.response.TeacherDetailedResponse;
import com.cisowski.schoolmanagement.model.response.TeacherSummaryResponse;
import com.cisowski.schoolmanagement.model.entity.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TeacherMapper {
    @Mapping(target = "password", ignore = true)
    Teacher toTeacherEntity(TeacherCreateRequest teacherDto);
    Teacher toTeacherEntity(TeacherPatchRequest teacherDto);
    TeacherDetailedResponse toTeacherResponse(Teacher teacher);
    @Mapping(target = "authority", source = "authority")
    AddTeacherResponse toAddTeacherResponse(Teacher teacher);
    List<TeacherSummaryResponse>toTeachersResponse(Collection<Teacher> teachers);
    TeacherSummaryResponse toSummaryResponse(Teacher teacher);

}
