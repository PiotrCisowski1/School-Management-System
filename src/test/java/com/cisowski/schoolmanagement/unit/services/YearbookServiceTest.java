package com.cisowski.schoolmanagement.unit.services;

import com.cisowski.schoolmanagement.common.exception.type.EntityAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.subject.service.SubjectService;
import com.cisowski.schoolmanagement.users.student.mapper.StudentMapper;
import com.cisowski.schoolmanagement.users.student.repository.StudentRepository;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherService;
import com.cisowski.schoolmanagement.yearbook.mapper.YearbookMapper;
import com.cisowski.schoolmanagement.yearbook.mapper.YearbookMapperImpl;
import com.cisowski.schoolmanagement.yearbook.model.*;
import com.cisowski.schoolmanagement.yearbook.repository.YearbookRepository;
import com.cisowski.schoolmanagement.yearbook.service.YearbookServiceImpl;
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
public class YearbookServiceTest {

    @Mock
    private YearbookRepository yearbookRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TeacherService teacherService;
    @Mock
    private YearbookMapperImpl mockedMapper;
    @Mock
    private SubjectService subjectService;
    @InjectMocks
    private YearbookServiceImpl yearbookService;

    private YearbookMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(YearbookMapper.class);
        TeacherMapper teacherMapper = Mappers.getMapper(TeacherMapper.class);
        StudentMapper studentMapper = Mappers.getMapper(StudentMapper.class);
        ReflectionTestUtils.setField(mapper, "teacherMapper", teacherMapper);
        ReflectionTestUtils.setField(mapper, "studentMapper", studentMapper);
    }

    @Test
    @DisplayName("addYearbook successful - returns YearbookDetailedResponse")
    public void addYearbook_successful() {
        long instancioSeed = 123;
        AddYearbookRequest request = Instancio.create(AddYearbookRequest.class);
        YearbookEntity requestEntity = Instancio.of(YearbookEntity.class).withSeed(instancioSeed).create();
        TeacherEntity fetchedTeacher = Instancio.create(TeacherEntity.class);
        YearbookEntity savedEntity = Instancio.of(YearbookEntity.class).withSeed(instancioSeed).create();
        savedEntity.setHeadTeacher(fetchedTeacher);
        YearbookDetailedResponse response = mapper.toDetailedResponse(savedEntity);

        when(yearbookRepository.findYearbookBySymbolOrHeadTeacher(any(), any())).thenReturn(Optional.empty());
        when(mockedMapper.toYearbookEntity(request)).thenReturn(requestEntity);
        when(yearbookRepository.save(any())).thenReturn(savedEntity);
        when(mockedMapper.toDetailedResponse(any())).thenReturn(response);
        when(teacherService.fetchTeacher(any())).thenReturn(fetchedTeacher);
        when(subjectService.fetchSubjects(any())).thenReturn(Collections.emptyList());

        YearbookDetailedResponse actual = yearbookService.addYearbook(request);

        assertNotNull(actual);
        assertEquals(requestEntity.getId(), actual.getId());
        assertEquals(requestEntity.getSymbol(), actual.getSymbol());
        assertEquals(requestEntity.getStartingYear(), actual.getStartingYear());
        assertEquals(requestEntity.getHeadTeacher().getId(), actual.getHeadTeacher().getId());
        assertEquals(requestEntity.getStudentsInYearbook().size(), actual.getStudentsInYearbook().size());
        verify(yearbookRepository, times(1)).findYearbookBySymbolOrHeadTeacher(any(), any());
        verify(mockedMapper, times(1)).toYearbookEntity(request);
        verify(yearbookRepository, times(1)).save(any());
        verify(mockedMapper, times(1)).toDetailedResponse(any());
    }

    @Test
    @DisplayName("addYearbook for existing entity - should throw EntityAlreadyExistsException")
    public void addYearbook_existingYearbook() {
        YearbookEntity entity = new YearbookEntity();
        entity.setSymbol("test");
        AddYearbookRequest request = new AddYearbookRequest();
        request.setSymbol(entity.getSymbol());

        when(yearbookRepository.findYearbookBySymbolOrHeadTeacher(any(), any())).thenReturn(Optional.of(entity));

        assertThrows(
                EntityAlreadyExistsException.class,
                () -> yearbookService.addYearbook(request));
    }

    @Test
    @DisplayName("deleteYearbook successful - no exception thrown")
    public void deleteYearbook_successful() {
        YearbookEntity existingEntity = Instancio.create(YearbookEntity.class);

        when(yearbookRepository.findById(any())).thenReturn(Optional.of(existingEntity));
        when(studentRepository.existsByYearbook(any())).thenReturn(false);

        yearbookService.deleteYearbook(1);

        verify(yearbookRepository, times(1)).findById(any());
        verify(yearbookRepository, times(1)).deleteById(any());
    }

    @Test
    @DisplayName("delete Yearboook no Entity found - should throw EntityNotFoundException")
    public void deleteYearbook_noEntityFound() {
        when(yearbookRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> yearbookService.deleteYearbook(1));
        verify(yearbookRepository,times(1)).findById(any());
    }

    @Test
    @DisplayName("getYearbook successful - should return YearbookDetailedResponse")
    public void getYearbook_successful() {
        YearbookEntity entity = Instancio.create(YearbookEntity.class);
        YearbookDetailedResponse response = mapper.toDetailedResponse(entity);

        when(yearbookRepository.findById(any())).thenReturn(Optional.of(entity));
        when(mockedMapper.toDetailedResponse(any())).thenReturn(response);

        YearbookDetailedResponse actual = yearbookService.getYearbook(1);

        verify(yearbookRepository, times(1)).findById(any());
        verify(mockedMapper, times(1)).toDetailedResponse(any());
        assertNotNull(actual);
        assertEquals(entity.getId(), actual.getId());
        assertEquals(entity.getHeadTeacher().getId(), actual.getHeadTeacher().getId());
        assertEquals(entity.getSymbol(), actual.getSymbol());
        assertEquals(entity.getStartingYear(), actual.getStartingYear());
        assertEquals(entity.getGraduationYear(), actual.getGraduationYear());
        assertEquals(entity.getStudentsInYearbook().size(), actual.getStudentsInYearbook().size());
    }

    @Test
    @DisplayName("getYearbook no entity found - should throw EntityNotFoundException")
    public void getYearbook_notFound() {
        when(yearbookRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> yearbookService.getYearbook(1));
        verify(yearbookRepository,times(1)).findById(any());
    }

    @Test
    @DisplayName("getYearbooks successful - should return Collection<YearbookSummaryResponse>")
    public void getYearbooks(){
        List<YearbookEntity> entities = Instancio.createList(YearbookEntity.class);
        List<YearbookSummaryResponse> response = mapper.toYearbookList(entities);
        when(yearbookRepository.findAll()).thenReturn(entities);
        when(mockedMapper.toYearbookList(any())).thenReturn(response);

        Collection<YearbookSummaryResponse> actual = yearbookService.getYearbooks();

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        verify(yearbookRepository, times(1)).findAll();
        verify(mockedMapper, times(1)).toYearbookList(any());
    }

    @Test
    @DisplayName("patchYearbook successful - should return YearbookDetailedResponse")
    public void patchYearbook_successful(){
        int yearbookId = 1;
        TeacherEntity headTeacher = Instancio.create(TeacherEntity.class);
        PatchYearbookRequest request = Instancio.create(PatchYearbookRequest.class);
        request.setHeadTeacherId(headTeacher.getId());
        YearbookEntity existingYearbook = Instancio.create(YearbookEntity.class);
        existingYearbook.setId(yearbookId);
        YearbookEntity requestEntity = mapper.toYearbookEntity(request);
        YearbookDetailedResponse response = mapper.toDetailedResponse(existingYearbook);

        when(yearbookRepository.findById(yearbookId)).thenReturn(Optional.of(existingYearbook));
        when(mockedMapper.toYearbookEntity(request)).thenReturn(requestEntity);
        doAnswer(invocation -> {
            mapper.patchYearbook(existingYearbook, requestEntity);
            return null;
        }).when(mockedMapper).patchYearbook(any(), any());
        when(yearbookRepository.save(any())).thenReturn(existingYearbook);
        when(mockedMapper.toDetailedResponse(any())).thenReturn(response);
        when(teacherService.fetchTeacher(any())).thenReturn(headTeacher);
        when(subjectService.fetchSubjects(any())).thenReturn(Collections.emptyList());

        YearbookDetailedResponse actual = yearbookService.updateYearbook(request, yearbookId);

        assertNotNull(actual);
        verify(yearbookRepository, times(1)).findById(yearbookId);
        verify(mockedMapper, times(1)).toYearbookEntity(request);
        verify(mockedMapper, times(1)).patchYearbook(any(),any());
        verify(yearbookRepository, times(1)).save(any());
        verify(mockedMapper, times(1)).toDetailedResponse(any());
    }

}
