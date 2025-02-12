package com.cisowski.schoolmanagement.users.teacher.mapper;

import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherCreateRequest;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherPatchRequest;
import com.cisowski.schoolmanagement.users.teacher.model.AddTeacherResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherDetailedResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TeacherMapper {
    @Mapping(target = "password", ignore = true)
    TeacherEntity toTeacherEntity(TeacherCreateRequest teacherDto);
    TeacherEntity toTeacherEntity(TeacherPatchRequest teacherDto);
    TeacherDetailedResponse toTeacherResponse(TeacherEntity teacher);
    @Mapping(target = "authority", source = "authority")
    AddTeacherResponse toAddTeacherResponse(TeacherEntity teacher);
    List<TeacherSummaryResponse>toTeachersResponse(Collection<TeacherEntity> teachers);
    TeacherSummaryResponse toSummaryResponse(TeacherEntity teacher);

}
