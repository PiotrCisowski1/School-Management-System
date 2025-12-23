package com.cisowski.schoolmanagement.unit.services;

import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.grade.mapper.GradeMapper;
import com.cisowski.schoolmanagement.grade.model.grade.*;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import com.cisowski.schoolmanagement.grade.repository.GradeRepository;
import com.cisowski.schoolmanagement.grade.service.GradeScaleService;
import com.cisowski.schoolmanagement.grade.service.GradeServiceImpl;
import com.cisowski.schoolmanagement.grade.service.GradeTypeService;
import com.cisowski.schoolmanagement.grade.validation.GradeValidator;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.subject.service.SubjectService;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.student.service.StudentService;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherService;
import org.apache.commons.lang3.StringUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GradeServiceImplTest {

    @Mock
    private GradeRepository gradeRepository;
    @Mock
    private TeacherService teacherService;
    @Mock
    private StudentService studentService;
    @Mock
    private SubjectService subjectService;
    @Mock
    private GradeTypeService gradeTypeService;
    @Mock
    private GradeScaleService gradeScaleService;
    @Mock
    private GradeMapper gradeMapper;
    @Mock
    private GradeValidator gradeValidator;

    @InjectMocks
    private GradeServiceImpl gradeService;


    private AddGradeRequest createAddRequest() {
        return Instancio.create(AddGradeRequest.class);
    }

    private PatchGradeRequest createPatchRequest() {
        return Instancio.create(PatchGradeRequest.class);
    }

    private GradeEntity createGradeEntity() {
        return Instancio.create(GradeEntity.class);
    }

    private GradeDetailedResponse createDetailedResponse() {
        return Instancio.create(GradeDetailedResponse.class);
    }

    private GradeSummaryResponse createSummaryResponse() {
        return Instancio.create(GradeSummaryResponse.class);
    }

    @Test
    void addGrade_ValidRequest_ReturnsResponse() {
        AddGradeRequest request = createAddRequest();
        GradeEntity entity = createGradeEntity();
        GradeDetailedResponse expected = createDetailedResponse();

        when(gradeMapper.toEntity(request)).thenReturn(entity);
        when(studentService.fetchStudent(request.getStudentId())).thenReturn(entity.getStudent());
        when(teacherService.fetchTeacher(request.getTeacherId())).thenReturn(entity.getTeacher());
        when(subjectService.fetchSubject(request.getSubjectId())).thenReturn(entity.getSubject());
        when(gradeTypeService.fetchGradeType(request.getGradeTypeId())).thenReturn(entity.getGradeType());
        when(gradeScaleService.fetchGradeValue(request.getGradeValueId())).thenReturn(entity.getGradeValue());
        when(gradeRepository.save(entity)).thenReturn(entity);
        when(gradeMapper.toDetailedResponse(entity)).thenReturn(expected);

        GradeDetailedResponse result = gradeService.addGrade(request);

        assertThat(result).isEqualTo(expected);
        verify(gradeRepository).save(entity);
    }

    @Test
    void patchGrade_ValidRequest_UpdatesAndReturnsResponse() {
        Long gradeId = 1L;
        PatchGradeRequest request = createPatchRequest();
        GradeEntity existing = createGradeEntity();
        GradeEntity patched = createGradeEntity();
        GradeDetailedResponse expected = createDetailedResponse();

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(existing));
        when(gradeRepository.save(existing)).thenReturn(patched);
        when(gradeMapper.toDetailedResponse(patched)).thenReturn(expected);

        if (request.getStudentId() != null) {
            when(studentService.fetchStudent(request.getStudentId())).thenReturn(patched.getStudent());
        }
        if (request.getSubjectId() != null) {
            when(subjectService.fetchSubject(request.getSubjectId())).thenReturn(patched.getSubject());
        }
        if (request.getGradeTypeId() != null) {
            when(gradeTypeService.fetchGradeType(request.getGradeTypeId())).thenReturn(patched.getGradeType());
        }
        if (request.getGradeValueId() != null) {
            when(gradeScaleService.fetchGradeValue(request.getGradeValueId())).thenReturn(patched.getGradeValue());
        }

        GradeDetailedResponse result = gradeService.patchGrade(request, gradeId);

        assertThat(result).isEqualTo(expected);
        verify(gradeRepository).save(existing);
        verifyPatchOperations(existing, request);
    }

    private void verifyPatchOperations(GradeEntity entity, PatchGradeRequest request) {
        if (request.getStudentId() != null) {
            verify(studentService).fetchStudent(request.getStudentId());
            assertThat(entity.getStudent()).isNotNull();
        }
        if (request.getSubjectId() != null) {
            verify(subjectService).fetchSubject(request.getSubjectId());
            assertThat(entity.getSubject()).isNotNull();
        }
        if (request.getGradeTypeId() != null) {
            verify(gradeTypeService).fetchGradeType(request.getGradeTypeId());
            assertThat(entity.getGradeType()).isNotNull();
        }
        if (request.getGradeValueId() != null) {
            verify(gradeScaleService).fetchGradeValue(request.getGradeValueId());
            assertThat(entity.getGradeValue()).isNotNull();
        }
        if (StringUtils.isNotEmpty(request.getComments())) {
            assertThat(entity.getComments()).isEqualTo(request.getComments());
        }
    }

    @Test
    void patchGrade_NonExistingId_ThrowsException() {
        Long gradeId = 999L;
        PatchGradeRequest request = createPatchRequest();

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.patchGrade(request, gradeId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("GradeEntity")
                .hasMessageContaining(gradeId.toString());

        verify(gradeRepository, never()).save(any());
    }

    @Test
    void deleteGrade_ExistingId_DeletesEntity() {
        Long gradeId = 1L;
        GradeEntity entity = createGradeEntity();

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(entity));
        doNothing().when(gradeRepository).delete(entity);

        gradeService.deleteGrade(gradeId);

        verify(gradeRepository).delete(entity);
    }

    @Test
    void deleteGrade_NonExistingId_ThrowsException() {
        Long gradeId = 999L;

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.deleteGrade(gradeId))
                .isInstanceOf(EntityNotFoundException.class);

        verify(gradeRepository, never()).delete(any());
    }

    @Test
    void getGradeById_ExistingId_ReturnsResponse() {
        Long gradeId = 1L;
        GradeEntity entity = createGradeEntity();
        GradeDetailedResponse expected = createDetailedResponse();

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(entity));
        when(gradeMapper.toDetailedResponse(entity)).thenReturn(expected);

        GradeDetailedResponse result = gradeService.getGradeById(gradeId);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getGradeById_NonExistingId_ThrowsException() {
        Long gradeId = 999L;

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.getGradeById(gradeId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getGradesByStudentId_ExistingStudent_ReturnsResponses() {
        Integer studentId = 1;
        StudentEntity student = Instancio.create(StudentEntity.class);
        List<GradeEntity> grades = Instancio.ofList(GradeEntity.class).size(3).create();
        List<GradeSummaryResponse> expected = Instancio.ofList(GradeSummaryResponse.class).size(3).create();

        when(studentService.fetchStudent(studentId)).thenReturn(student);
        when(gradeRepository.findByStudent(student)).thenReturn(grades);
        when(gradeMapper.toSummaryResponseList(grades)).thenReturn(expected);

        List<GradeSummaryResponse> result = gradeService.getGradesByStudentId(studentId);

        assertThat(result).hasSize(3).isEqualTo(expected);
    }

    @Test
    void getGradesByStudentId_NoGrades_ReturnsEmptyList() {
        Integer studentId = 1;
        StudentEntity student = Instancio.create(StudentEntity.class);

        when(studentService.fetchStudent(studentId)).thenReturn(student);
        when(gradeRepository.findByStudent(student)).thenReturn(Collections.emptyList());
        when(gradeMapper.toSummaryResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<GradeSummaryResponse> result = gradeService.getGradesByStudentId(studentId);

        assertThat(result).isEmpty();
    }

    @Test
    void getGradesByStudentIdAndSubject_ExistingData_ReturnsResponses() {
        Integer studentId = 1;
        Integer subjectId = 2;
        StudentEntity student = Instancio.create(StudentEntity.class);
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        List<GradeEntity> grades = Instancio.ofList(GradeEntity.class).size(2).create();
        List<GradeSummaryResponse> expected = Instancio.ofList(GradeSummaryResponse.class).size(2).create();

        when(studentService.fetchStudent(studentId)).thenReturn(student);
        when(subjectService.fetchSubject(subjectId)).thenReturn(subject);
        when(gradeRepository.findByStudentAndSubject(student, subject)).thenReturn(grades);
        when(gradeMapper.toSummaryResponseList(grades)).thenReturn(expected);

        List<GradeSummaryResponse> result = gradeService.getGradesByStudentIdAndSubject(studentId, subjectId);

        assertThat(result).hasSize(2).isEqualTo(expected);
    }

    @Test
    void getGradesByStudentIdAndSubject_NoGrades_ReturnsEmptyList() {
        Integer studentId = 1;
        Integer subjectId = 2;
        StudentEntity student = Instancio.create(StudentEntity.class);
        SubjectEntity subject = Instancio.create(SubjectEntity.class);

        when(studentService.fetchStudent(studentId)).thenReturn(student);
        when(subjectService.fetchSubject(subjectId)).thenReturn(subject);
        when(gradeRepository.findByStudentAndSubject(student, subject)).thenReturn(Collections.emptyList());
        when(gradeMapper.toSummaryResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<GradeSummaryResponse> result = gradeService.getGradesByStudentIdAndSubject(studentId, subjectId);

        assertThat(result).isEmpty();
    }

    @Test
    void getGradesBySubjectId_ExistingSubject_ReturnsResponses() {
        Integer subjectId = 1;
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        List<GradeEntity> grades = Instancio.ofList(GradeEntity.class).size(4).create();
        List<GradeSummaryResponse> expected = Instancio.ofList(GradeSummaryResponse.class).size(4).create();

        when(subjectService.fetchSubject(subjectId)).thenReturn(subject);
        when(gradeRepository.findBySubject(subject)).thenReturn(grades);
        when(gradeMapper.toSummaryResponseList(grades)).thenReturn(expected);

        List<GradeSummaryResponse> result = gradeService.getGradesBySubjectId(subjectId);

        assertThat(result).hasSize(4).isEqualTo(expected);
    }

    @Test
    void getGradesByGradeType_ExistingType_ReturnsResponses() {
        Long gradeTypeId = 1L;
        GradeTypeEntity gradeType = Instancio.create(GradeTypeEntity.class);
        List<GradeEntity> grades = Instancio.ofList(GradeEntity.class).size(3).create();
        List<GradeSummaryResponse> expected = Instancio.ofList(GradeSummaryResponse.class).size(3).create();

        when(gradeTypeService.fetchGradeType(gradeTypeId)).thenReturn(gradeType);
        when(gradeRepository.findByGradeType(gradeType)).thenReturn(grades);
        when(gradeMapper.toSummaryResponseList(grades)).thenReturn(expected);

        List<GradeSummaryResponse> result = gradeService.getGradesByGradeType(gradeTypeId);

        assertThat(result).hasSize(3).isEqualTo(expected);
    }

    @Test
    void fetchGrade_ExistingId_ReturnsEntity() {
        Long gradeId = 1L;
        GradeEntity expected = createGradeEntity();

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(expected));

        GradeEntity result = gradeService.fetchGrade(gradeId);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void fetchGrade_NonExistingId_ThrowsException() {
        Long gradeId = 999L;

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeService.fetchGrade(gradeId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("GradeEntity")
                .hasMessageContaining(gradeId.toString());
    }
}
