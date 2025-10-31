package com.cisowski.schoolmanagement.unit.services;

import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.subject.service.SubjectServiceImpl;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapperImpl;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.common.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherCreateRequest;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherPatchRequest;
import com.cisowski.schoolmanagement.users.teacher.model.AddTeacherResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherDetailedResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import com.cisowski.schoolmanagement.users.teacher.repository.TeacherRepository;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherServiceImpl;
import com.cisowski.schoolmanagement.common.utility.PasswordGenerator;
import com.cisowski.schoolmanagement.yearbook.mapper.YearbookMapper;
import com.cisowski.schoolmanagement.yearbook.repository.YearbookRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTests {

    @Mock
    private TeacherRepository repository;
    @Mock
    private TeacherMapperImpl mockedTeacherMapper;
    @Mock
    private SubjectServiceImpl subjectService;
    @Mock
    private YearbookRepository yearbookRepository;
    private final TeacherMapper teacherMapper = Mappers.getMapper(TeacherMapper.class);
    @InjectMocks
    private TeacherServiceImpl service;
    private final String generatedPassword = PasswordGenerator.generatePassword();

    @BeforeEach
    public void setUp() {
        YearbookMapper yearbookMapper = Mappers.getMapper(YearbookMapper.class);
        ReflectionTestUtils.setField(teacherMapper, "yearbookMapper", yearbookMapper);
    }

    @Test
    @DisplayName("add Teacher successful - returns full TeacherResponse")
    public void addTeacher_successful() {
        TeacherCreateRequest dto = Instancio.create(TeacherCreateRequest.class);
        TeacherEntity teacher = teacherMapper.toTeacherEntity(dto);
        teacher.setPassword(generatedPassword);
        teacher.setId(1);
        AddTeacherResponse response = teacherMapper.toAddTeacherResponse(teacher);
        List<SubjectEntity> subjects = Instancio.ofList(SubjectEntity.class).size(3).create();

        when(repository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(mockedTeacherMapper.toTeacherEntity(dto)).thenReturn(teacher);
        when(repository.save(any())).thenReturn(teacher);
        when(mockedTeacherMapper.toAddTeacherResponse(any())).thenReturn(response);
        when(subjectService.fetchSubjects(any())).thenReturn(subjects);

        AddTeacherResponse result = service.addTeacher(dto);

        assertNotNull(result);
        assertNotNull(result.getPassword());
        assertEquals(response, result);
        assertEquals(response.getId(), result.getId());
        assertEquals(response.getEmail(), result.getEmail());
        assertEquals(response.getFirstName(), result.getFirstName());
        assertEquals(response.getLastName(), result.getLastName());
        assertEquals(response.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(response.getBirthDate(), result.getBirthDate());
        assertEquals(response.getGender(), result.getGender());
        assertEquals(response.getLeadingYearbook(), result.getLeadingYearbook());
        assertIterableEquals(response.getAuthority(), result.getAuthority());
        assertIterableEquals(response.getTeachingSubjects(), result.getTeachingSubjects());
    }

    @Test
    @DisplayName("addTeacher should throw EmailAlreadyExistsException")
    public void addTeacher_throwsEmailAlreadyExistsEx() {
        TeacherCreateRequest dto = Instancio.create(TeacherCreateRequest.class);
        TeacherEntity existingUser = new TeacherEntity();

        when(repository.findByEmail(any())).thenReturn(Optional.of(existingUser));

        assertThrows(EmailAlreadyExistsException.class, () ->
                service.addTeacher(dto));
    }

    @Test
    @DisplayName("updateTeacher successful - returns TeacherResponse")
    public void updateTeacher_successful() {
        TeacherPatchRequest dto = Instancio.create(TeacherPatchRequest.class);
        TeacherEntity teacher = teacherMapper.toTeacherEntity(dto);
        TeacherDetailedResponse response = teacherMapper.toTeacherResponse(teacher);

        when(repository.findById(any())).thenReturn(Optional.of(teacher));
        when(mockedTeacherMapper.toTeacherEntity(dto)).thenReturn(teacher);
        when(repository.save(any())).thenReturn(teacher);
        when(mockedTeacherMapper.toTeacherResponse(any())).thenReturn(response);

        TeacherDetailedResponse result = service.updateTeacher(dto, 1);

        assertNotNull(result);
        assertEquals(response, result);
        assertEquals(response.getId(), result.getId());
        assertEquals(response.getEmail(), result.getEmail());
        assertEquals(response.getFirstName(), result.getFirstName());
        assertEquals(response.getLastName(), result.getLastName());
        assertEquals(response.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(response.getBirthDate(), result.getBirthDate());
        assertEquals(response.getGender(), result.getGender());
        assertEquals(response.getLeadingYearbook(), result.getLeadingYearbook());
        assertIterableEquals(response.getAuthority(), result.getAuthority());
        assertIterableEquals(response.getTeachingSubjects(), result.getTeachingSubjects());
    }

    @Test
    @DisplayName("updateTeacher should throw EntityNotFoundException")
    public void updateTeacher_throwsEntityNotFoundEx() {
        TeacherPatchRequest dto = Instancio.create(TeacherPatchRequest.class);

        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                service.updateTeacher(dto, 1));
    }

    @Test
    @DisplayName("deleteTeacher should invoke delete method in repo")
    public void deleteTeacher_successful() {
        Integer userId = 1;
        TeacherEntity teacher = new TeacherEntity();
        teacher.setId(userId);

        when(repository.findById(userId)).thenReturn(Optional.of(teacher));
        when(yearbookRepository.existsByHeadTeacher(any())).thenReturn(false);

        service.deleteUser(userId);

        verify(repository, times(1)).findById(userId);
        verify(repository).delete(teacher);
    }

    @Test
    @DisplayName("deleteTeacher should throw EntityNotFoundException")
    public void deleteTeacher_throwsEntityNotFoundEx() {
        Integer userId = 1;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                service.deleteUser(userId));
    }

    @Test
    @DisplayName("findAll successful - should return Collection<TeacherResponse>")
    public void findAll_successful() {
        List<TeacherEntity> teachers = Instancio.createList(TeacherEntity.class);
        List<TeacherSummaryResponse> responses = teacherMapper.toTeachersResponse(teachers);

        when(repository.findAll()).thenReturn(teachers);
        when(mockedTeacherMapper.toTeachersResponse(any())).thenReturn(responses);

        List<TeacherSummaryResponse> result = service.findAll();

        verify(repository, times(1)).findAll();
        verify(mockedTeacherMapper, times(1)).toTeachersResponse(teachers);
        assertNotNull(result);
        assertIterableEquals(responses, result);
    }

    @Test
    @DisplayName("findById successful - should return TeacherResponse")
    public void findById_successful() {
        Integer userId = 1;
        TeacherEntity existingTeacher = Instancio.create(TeacherEntity.class);
        TeacherDetailedResponse response = teacherMapper.toTeacherResponse(existingTeacher);

        when(repository.findById(userId)).thenReturn(Optional.of(existingTeacher));
        when(mockedTeacherMapper.toTeacherResponse(existingTeacher)).thenReturn(response);

        TeacherDetailedResponse result = service.findById(userId);

        verify(repository, times(1)).findById(userId);
        verify(mockedTeacherMapper, times(1)).toTeacherResponse(existingTeacher);
        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    @DisplayName("findById - should throw EntityNotFoundEx")
    public void findById_throwsEntityNotFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                service.findById(1));
    }

    @Test
    @DisplayName("fetchTeacher successful")
    public void fetchTeacher_successful() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        when(repository.findById(1)).thenReturn(Optional.of(teacher));
        TeacherEntity response = service.fetchTeacher(1);
        assertEquals(teacher, response);
    }
}

