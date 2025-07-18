package com.cisowski.schoolmanagement.mappers;

import com.cisowski.schoolmanagement.grade.mapper.GradeMapper;
import com.cisowski.schoolmanagement.grade.mapper.GradeMapperImpl;
import com.cisowski.schoolmanagement.grade.mapper.GradeTypeMapper;
import com.cisowski.schoolmanagement.grade.mapper.GradeValueMapper;
import com.cisowski.schoolmanagement.grade.model.grade.*;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeValueResponse;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeResponse;
import com.cisowski.schoolmanagement.subject.mapper.SubjectMapper;
import com.cisowski.schoolmanagement.subject.model.SubjectSummaryResponse;
import com.cisowski.schoolmanagement.users.student.mapper.StudentMapper;
import com.cisowski.schoolmanagement.users.student.model.StudentSummaryResponse;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GradeMapperTest {

    @Mock
    private TeacherMapper teacherMapper;
    @Mock
    private StudentMapper studentMapper;
    @Mock
    private SubjectMapper subjectMapper;
    @Mock
    private GradeTypeMapper gradeTypeMapper;
    @Mock
    private GradeValueMapper gradeValueMapper;

    @InjectMocks
    private GradeMapperImpl gradeMapper;


    @Test
    void toEntity_WithAddGradeRequest_ReturnsEntityWithComments() {
        AddGradeRequest request = Instancio.create(AddGradeRequest.class);

        GradeEntity result = gradeMapper.toEntity(request);

        assertThat(result).isNotNull();
        assertThat(result.getComments()).isEqualTo(request.getComments());
    }

    @Test
    void toEntity_WithNullAddGradeRequest_ReturnsNull() {
        assertThat(gradeMapper.toEntity((AddGradeRequest) null)).isNull();
    }

    @Test
    void toEntity_WithPatchGradeRequest_ReturnsEntityWithComments() {
        PatchGradeRequest request = Instancio.create(PatchGradeRequest.class);

        GradeEntity result = gradeMapper.toEntity(request);

        assertThat(result).isNotNull();
        assertThat(result.getComments()).isEqualTo(request.getComments());
    }

    @Test
    void toEntity_WithNullPatchGradeRequest_ReturnsNull() {
        assertThat(gradeMapper.toEntity((PatchGradeRequest) null)).isNull();
    }

    @Test
    void toDetailedResponse_WithEntity_ReturnsMappedResponse() {
        GradeEntity entity = Instancio.create(GradeEntity.class);
        setupMapperMocks(entity);

        GradeDetailedResponse result = gradeMapper.toDetailedResponse(entity);

        assertThat(result).isNotNull();
        verifyMappings(entity, result);
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getComments()).isEqualTo(entity.getComments());
        assertThat(result.getCreatedAt()).isEqualTo(entity.getCreatedAt());
    }

    @Test
    void toDetailedResponse_WithNullEntity_ReturnsNull() {
        assertThat(gradeMapper.toDetailedResponse(null)).isNull();
    }

    @Test
    void toGradeSummaryResponse_WithEntity_ReturnsMappedResponse() {
        GradeEntity entity = Instancio.create(GradeEntity.class);
        setupMapperMocks(entity);

        GradeSummaryResponse result = gradeMapper.toGradeSummaryResponse(entity);

        assertThat(result).isNotNull();
        verifyMappings(entity, result);
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getComments()).isEqualTo(entity.getComments());
        assertThat(result.getCreatedAt()).isEqualTo(entity.getCreatedAt());
    }

    @Test
    void toGradeSummaryResponse_WithNullEntity_ReturnsNull() {
        assertThat(gradeMapper.toGradeSummaryResponse(null)).isNull();
    }

    @Test
    void toSummaryResponseList_WithEntityList_ReturnsMappedList() {
        List<GradeEntity> entities = Instancio.ofList(GradeEntity.class).size(3).create();
        entities.forEach(this::setupMapperMocks);

        List<GradeSummaryResponse> result = gradeMapper.toSummaryResponseList(entities);

        assertThat(result)
                .hasSameSizeAs(entities)
                .allSatisfy(item -> assertThat(item).isNotNull());
    }

    @Test
    void toSummaryResponseList_WithNullList_ReturnsNull() {
        assertThat(gradeMapper.toSummaryResponseList(null)).isNull();
    }

    @Test
    void toSummaryResponseList_WithEmptyList_ReturnsEmptyList() {
        List<GradeEntity> entities = List.of();
        List<GradeSummaryResponse> result = gradeMapper.toSummaryResponseList(entities);
        assertThat(result).isEmpty();
    }

    private void setupMapperMocks(GradeEntity entity) {
        when(studentMapper.toSummaryResponse(entity.getStudent()))
                .thenReturn(Instancio.create(StudentSummaryResponse.class));
        when(subjectMapper.toSubjectSummaryResponse(entity.getSubject()))
                .thenReturn(Instancio.create(SubjectSummaryResponse.class));
        when(teacherMapper.toSummaryResponse(entity.getTeacher()))
                .thenReturn(Instancio.create(TeacherSummaryResponse.class));
        when(gradeTypeMapper.toResponse(entity.getGradeType()))
                .thenReturn(Instancio.create(GradeTypeResponse.class));
        when(gradeValueMapper.toResponse(entity.getGradeValue()))
                .thenReturn(Instancio.create(GradeValueResponse.class));
    }

    private void verifyMappings(GradeEntity entity, GradeDetailedResponse response) {
        verify(studentMapper).toSummaryResponse(entity.getStudent());
        verify(subjectMapper).toSubjectSummaryResponse(entity.getSubject());
        verify(teacherMapper).toSummaryResponse(entity.getTeacher());
        verify(gradeTypeMapper).toResponse(entity.getGradeType());
        verify(gradeValueMapper).toResponse(entity.getGradeValue());
    }

    private void verifyMappings(GradeEntity entity, GradeSummaryResponse response) {
        verify(studentMapper).toSummaryResponse(entity.getStudent());
        verify(subjectMapper).toSubjectSummaryResponse(entity.getSubject());
        verify(teacherMapper).toSummaryResponse(entity.getTeacher());
        verify(gradeTypeMapper).toResponse(entity.getGradeType());
        verify(gradeValueMapper).toResponse(entity.getGradeValue());
    }
}
