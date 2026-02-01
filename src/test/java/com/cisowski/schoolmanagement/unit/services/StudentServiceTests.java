package com.cisowski.schoolmanagement.unit.services;

import com.cisowski.schoolmanagement.grade.repository.GradeRepository;
import com.cisowski.schoolmanagement.timetable.attendance.repository.AttendanceRepository;
import com.cisowski.schoolmanagement.users.common.service.AuthorityService;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.parent.service.ParentService;
import com.cisowski.schoolmanagement.users.student.mapper.StudentMapperImpl;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import com.cisowski.schoolmanagement.yearbook.mapper.YearbookMapper;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import com.cisowski.schoolmanagement.common.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.users.parent.mapper.ParentMapper;
import com.cisowski.schoolmanagement.users.student.mapper.StudentMapper;
import com.cisowski.schoolmanagement.users.student.model.StudentCreateRequest;
import com.cisowski.schoolmanagement.users.student.model.StudentPatchRequest;
import com.cisowski.schoolmanagement.users.student.model.AddStudentResponse;
import com.cisowski.schoolmanagement.users.common.model.BaseUserSummaryResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentDetailedResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentSummaryResponse;
import com.cisowski.schoolmanagement.users.student.repository.StudentRepository;
import com.cisowski.schoolmanagement.users.student.service.StudentServiceImpl;
import com.cisowski.schoolmanagement.common.utility.PasswordGenerator;
import com.cisowski.schoolmanagement.yearbook.model.YearbookSummaryResponse;
import com.cisowski.schoolmanagement.yearbook.service.YearbookService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTests {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StudentMapperImpl mockedStudentMapper;
    @Mock
    private YearbookService yearbookService;
    @Mock
    private ParentService parentService;
    private final StudentMapper studentMapper = Mappers.getMapper(StudentMapper.class);
    private final ParentMapper parentMapper = Mappers.getMapper(ParentMapper.class);
    @InjectMocks
    private StudentServiceImpl studentService;
    private final String generatedPassword = PasswordGenerator.generatePassword();
    @Mock
    private AuthorityService authorityService;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private GradeRepository gradeRepository;

    @BeforeEach
    public void setUp(){
        ReflectionTestUtils.setField(studentMapper, "parentMapper", parentMapper);
        YearbookMapper yearbookMapper = Mappers.getMapper(YearbookMapper.class);
        ReflectionTestUtils.setField(studentMapper, "yearbookMapper", yearbookMapper);
        TeacherMapper teacherMapper = Mappers.getMapper(TeacherMapper.class);
        ReflectionTestUtils.setField(yearbookMapper, "teacherMapper", teacherMapper);
    }

    @Test
    @DisplayName("add Student successful - returns full StudentResponse")
    public void addStudent_successful() {
        StudentCreateRequest studentDto = Instancio.create(StudentCreateRequest.class);
        studentDto.setParentsIds(Collections.singletonList(2));
        StudentEntity student = studentMapper.toStudentEntity(studentDto);
        YearbookEntity yearbook = new YearbookEntity();
        yearbook.setId(studentDto.getYearbookId());
        List<ParentEntity> parents = Collections.singletonList(Instancio.create(ParentEntity.class));
        parents.get(0).setId(2);
        student.setPassword(generatedPassword);
        student.setId(1);
        student.setParents(parents);
        AddStudentResponse response = studentMapper.toAddStudentResponse(student);
        YearbookSummaryResponse yearbookSummaryResponse = new YearbookSummaryResponse();
        yearbookSummaryResponse.setId(yearbook.getId());
        response.setYearbook(yearbookSummaryResponse);

        when(studentRepository.findByEmail(studentDto.getEmail())).thenReturn(Optional.empty());
        when(mockedStudentMapper.toStudentEntity(studentDto)).thenReturn(student);
        when(studentRepository.save(any())).thenReturn(student);
        when(mockedStudentMapper.toAddStudentResponse(any())).thenReturn(response);
        when(yearbookService.fetchYearbookEntity(any())).thenReturn(yearbook);
        when(parentService.fetchParentEntities(any())).thenReturn(parents);

        AddStudentResponse result = studentService.addStudent(studentDto);

        assertNotNull(result);
        assertNotNull(result.getPassword());
        assertEquals(response.getId(), result.getId());
        assertEquals(response.getEmail(), result.getEmail());
        assertEquals(response.getFirstName(), result.getFirstName());
        assertEquals(response.getLastName(), result.getLastName());
        assertEquals(response.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(response.getBirthDate(), result.getBirthDate());
        assertEquals(response.getGender(), result.getGender());
        assertIterableEquals(response.getAuthority(), result.getAuthority());
        assertIterableEquals(response.getParents(), result.getParents());
        assertEquals(response.getYearbook(), result.getYearbook());
        assertEquals(studentDto.getParentsIds().size(), result.getParents().size());
        assertEquals(response.getYearbook().getId(), result.getYearbook().getId());
        assertIterableEquals(studentDto.getParentsIds(), result.getParents().stream()
                .map(BaseUserSummaryResponse::getId)
                .collect(Collectors.toList()));
        verify(mockedStudentMapper, times(1)).toStudentEntity(studentDto);
        verify(mockedStudentMapper, times(1)).toAddStudentResponse(student);
    }

    @Test
    @DisplayName("addStudent should throw EmailAlreadyExistsException")
    public void addStudent_throwsEmailAlreadyExistsEx() {
        StudentCreateRequest studentDto = Instancio.create(StudentCreateRequest.class);
        StudentEntity existingUser = new StudentEntity();

        when(studentRepository.findByEmail(any())).thenReturn(Optional.of(existingUser));

        assertThrows(EmailAlreadyExistsException.class, () ->
                studentService.addStudent(studentDto));
    }

    @Test
    @DisplayName("updateStudent successful - returns StudentResponse")
    public void updateStudent_successful() {
        Integer studentId = 1;
        StudentPatchRequest studentDto = Instancio.create(StudentPatchRequest.class);
        List<Integer> parentIdsToAdd = Collections.singletonList(2);
        List<Integer> parentIdsToRemove = Collections.singletonList(3);
        StudentEntity student = studentMapper.toStudentEntity(studentDto);
        student.setId(studentId);
        YearbookEntity yearbook = new YearbookEntity();
        yearbook.setId(studentDto.getYearbookId());
        student.setYearbook(yearbook);
        ParentEntity parentToAdd = Instancio.create(ParentEntity.class);
        ParentEntity parentToRemove = Instancio.create(ParentEntity.class);
        student.setParents(Collections.singletonList(parentToRemove));
        StudentDetailedResponse studentResponse = studentMapper.toStudentResponse(student);
        studentResponse.setParents(Collections.singletonList(parentMapper.toSummaryResponse(parentToAdd)));

        when(studentRepository.findByIdAndIsHideFalse(studentId)).thenReturn(Optional.of(student));
        when(mockedStudentMapper.toStudentEntity(studentDto)).thenReturn(student);
        when(studentRepository.save(any())).thenReturn(student);
        when(mockedStudentMapper.toStudentResponse(any())).thenReturn(studentResponse);

        StudentDetailedResponse result = studentService.updateStudent(studentDto, studentId);

        assertNotNull(result);
        assertEquals(studentResponse, result);
        assertEquals(studentResponse.getId(), result.getId());
        assertEquals(studentResponse.getEmail(), result.getEmail());
        assertEquals(studentResponse.getFirstName(), result.getFirstName());
        assertEquals(studentResponse.getLastName(), result.getLastName());
        assertEquals(studentResponse.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(studentResponse.getBirthDate(), result.getBirthDate());
        assertEquals(studentResponse.getGender(), result.getGender());
        assertIterableEquals(studentResponse.getAuthority(), result.getAuthority());
        assertIterableEquals(studentResponse.getParents(), result.getParents());
        assertEquals(studentResponse.getYearbook().getId(), result.getYearbook().getId());
        verify(studentRepository, times(1)).findByIdAndIsHideFalse(studentId);
        verify(mockedStudentMapper, times(1)).toStudentEntity(studentDto);
        verify(mockedStudentMapper, times(1)).patchStudent(any(), any());
        verify(studentRepository, times(1)).save(any());
        verify(mockedStudentMapper, times(1)).toStudentResponse(any());
    }

    @Test
    @DisplayName("updateStudent should throw EntityNotFoundException")
    public void updateStudent_throwsEntityNotFoundEx() {
        StudentPatchRequest studentDto = Instancio.create(StudentPatchRequest.class);

        when(studentRepository.findByIdAndIsHideFalse(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                studentService.updateStudent(studentDto, 1));
    }

    @Test
    @DisplayName("deleteStudent should invoke delete method in repo")
    public void deleteStudent_successful() {
        Integer studentId = 1;
        StudentEntity student = new StudentEntity();
        student.setId(studentId);

        when(studentRepository.findByIdAndIsHideFalse(studentId)).thenReturn(Optional.of(student));
        when(attendanceRepository.existsByStudent(student)).thenReturn(false);
        when(gradeRepository.existsByStudent(student)).thenReturn(false);

        studentService.deleteUser(studentId);

        verify(studentRepository, times(1)).findByIdAndIsHideFalse(studentId);
        verify(studentRepository).delete(student);
    }

    @Test
    @DisplayName("deleteStudent should throw EntityNotFoundException")
    public void deleteStudent_throwsEntityNotFoundEx() {
        Integer studentId = 1;

        when(studentRepository.findByIdAndIsHideFalse(studentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                studentService.deleteUser(studentId));
    }

    @Test
    @DisplayName("findAll successful - should return Collection<StudentResponse>")
    public void findAll_successful() {
        List<StudentEntity> students = Instancio.createList(StudentEntity.class);
        List<StudentSummaryResponse> responses = studentMapper.toStudentsResponse(students);

        when(studentRepository.findAllByIsHideFalse()).thenReturn(students);
        when(mockedStudentMapper.toStudentsResponse(any())).thenReturn(responses);

        List<StudentSummaryResponse> result = studentService.findAll();

        verify(studentRepository, times(1)).findAllByIsHideFalse();
        verify(mockedStudentMapper, times(1)).toStudentsResponse(students);
        assertNotNull(result);
        assertIterableEquals(responses, result);
    }

    @Test
    @DisplayName("findById successful - should return StudentResponse")
    public void findById_successful() {
        Integer studentId = 1;
        StudentEntity existingStudent = Instancio.create(StudentEntity.class);
        StudentDetailedResponse studentResponse = studentMapper.toStudentResponse(existingStudent);

        when(studentRepository.findByIdAndIsHideFalse(studentId)).thenReturn(Optional.of(existingStudent));
        when(mockedStudentMapper.toStudentResponse(existingStudent)).thenReturn(studentResponse);

        StudentDetailedResponse result = studentService.findById(studentId);

        verify(studentRepository, times(1)).findByIdAndIsHideFalse(studentId);
        verify(mockedStudentMapper, times(1)).toStudentResponse(existingStudent);
        assertNotNull(result);
        assertEquals(studentResponse, result);
    }

    @Test
    @DisplayName("findById - should throw EntityNotFoundEx")
    public void findById_throwsEntityNotFound() {
        when(studentRepository.findByIdAndIsHideFalse(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                studentService.findById(1));
    }

    @Test
    void fetchStudents_ShouldReturnStudents_WhenAllIdsFound() {
        List<Integer> ids = List.of(1, 2, 3);
        List<StudentEntity> foundStudents = ids.stream()
                .map(id -> Instancio.of(StudentEntity.class)
                        .set(field(StudentEntity::getId), id)
                        .create())
                .toList();

        when(studentRepository.findAllByIdInAndIsHideFalse(ids)).thenReturn(foundStudents);

        List<StudentEntity> result = studentService.fetchStudents(ids);

        assertThat(result).hasSize(3).isEqualTo(foundStudents);
    }

    @Test
    void fetchStudents_ShouldThrowException_WhenSizeMismatch() {
        List<Integer> ids = List.of(1, 2, 3);
        List<StudentEntity> incompleteStudents = List.of(
                Instancio.of(StudentEntity.class)
                        .set(field(StudentEntity::getId), 1)
                        .set(field(StudentEntity::isHide), false)
                        .create()
        );

        when(studentRepository.findAllByIdInAndIsHideFalse(ids)).thenReturn(incompleteStudents);

        assertThatThrownBy(() -> studentService.fetchStudents(ids))
                .isInstanceOf(SpecificationBrokenException.class)
                .hasMessageContaining("Student IDs are invalid");
    }

    @Test
    void fetchStudents_ShouldHandleDuplicateIds_WhenEntitiesFound() {
        List<Integer> idsWithDuplicates = List.of(1, 1, 2);
        List<Integer> distinctIds = List.of(1, 2);

        List<StudentEntity> foundStudents = distinctIds.stream()
                .map(id -> Instancio.of(StudentEntity.class)
                        .set(field(StudentEntity::getId), id)
                        .set(field(StudentEntity::isHide), false)
                        .create())
                .toList();

        when(studentRepository.findAllByIdInAndIsHideFalse(idsWithDuplicates)).thenReturn(foundStudents);

        List<StudentEntity> result = studentService.fetchStudents(idsWithDuplicates);

        assertThat(result).hasSize(2);
    }

    @Test
    void fetchStudents_ShouldReturnEmptyList_WhenInputIsEmpty() {
        List<Integer> emptyIds = Collections.emptyList();
        when(studentRepository.findAllByIdInAndIsHideFalse(emptyIds)).thenReturn(Collections.emptyList());

        List<StudentEntity> result = studentService.fetchStudents(emptyIds);

        assertThat(result).isEmpty();
        verify(studentRepository).findAllByIdInAndIsHideFalse(emptyIds);
    }
}
