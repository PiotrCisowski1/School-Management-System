package com.cisowski.schoolmanagement.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.cisowski.schoolmanagement.common.exception.type.EntityAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.subject.mapper.SubjectMapper;
import com.cisowski.schoolmanagement.subject.model.*;
import com.cisowski.schoolmanagement.subject.repository.SubjectRepository;
import com.cisowski.schoolmanagement.subject.service.SubjectServiceImpl;
import com.cisowski.schoolmanagement.subject.service.SubjectTypeService;
import com.cisowski.schoolmanagement.users.teacher.repository.TeacherRepository;
import com.cisowski.schoolmanagement.yearbook.repository.YearbookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;


@ExtendWith(MockitoExtension.class)
public class SubjectServiceTest {

    @Mock
    private SubjectMapper subjectMapper;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private YearbookRepository yearbookRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private SubjectTypeService subjectTypeService;

    @InjectMocks
    private SubjectServiceImpl subjectService;

    private AddSubjectRequest addSubjectRequest;
    private SubjectEntity subjectEntity;
    private SubjectDetailedResponse detailedResponse;
    private PatchSubjectRequest patchSubjectRequest;
    private SubjectEntity updatedSubjectEntity;

    @BeforeEach
    public void setUp() {
        addSubjectRequest = new AddSubjectRequest();
        addSubjectRequest.setName("Math 101");
        addSubjectRequest.setCode("MAT01");
        addSubjectRequest.setDescription("Mathematics");
        addSubjectRequest.setSubjectTypeId(1);

        subjectEntity = new SubjectEntity();
        subjectEntity.setId(100);
        subjectEntity.setName("Math 101");
        subjectEntity.setCode("MAT01");
        subjectEntity.setDescription("Mathematics");

        detailedResponse = new SubjectDetailedResponse();
        detailedResponse.setId(100);
        detailedResponse.setName("Math 101");
        detailedResponse.setCode("MAT01");
        detailedResponse.setDescription("Mathematics");
        detailedResponse.setSubjectType("MATH");

        patchSubjectRequest = new PatchSubjectRequest();
        patchSubjectRequest.setName("Math extended");
        patchSubjectRequest.setCode("MAT02");
        patchSubjectRequest.setDescription("Extended Mathematics");
        patchSubjectRequest.setSubjectTypeId(2);

        updatedSubjectEntity = new SubjectEntity();
        updatedSubjectEntity.setId(100);
        updatedSubjectEntity.setName("Math extended");
        updatedSubjectEntity.setCode("MAT02");
        updatedSubjectEntity.setDescription("Extended Mathematics");
    }

    @Test
    @Transactional
    public void testAddSubject_Success() {
        when(subjectRepository.findSubjectByCode(anyString())).thenReturn(Optional.empty());
        when(subjectMapper.toSubjectEntity(addSubjectRequest)).thenReturn(subjectEntity);
        SubjectTypeEntity subjectType = new SubjectTypeEntity();
        subjectType.setId(1);
        subjectType.setName("Podstawowy");
        when(subjectTypeService.fetchSubjectType(addSubjectRequest.getSubjectTypeId())).thenReturn(subjectType);
        subjectEntity.setSubjectType(subjectType);
        when(subjectRepository.save(subjectEntity)).thenReturn(subjectEntity);
        when(subjectMapper.toDetailedResponse(subjectEntity)).thenReturn(detailedResponse);

        SubjectDetailedResponse response = subjectService.addSubject(addSubjectRequest);

        assertNotNull(response);
        assertEquals("MAT01", response.getCode());
        verify(subjectRepository).findSubjectByCode(addSubjectRequest.getCode());
        verify(subjectRepository).save(subjectEntity);
    }

    @Test
    public void testAddSubject_AlreadyExists() {
        when(subjectRepository.findSubjectByCode(anyString())).thenReturn(Optional.of(subjectEntity));

        EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class,
                () -> subjectService.addSubject(addSubjectRequest));
        assertTrue(exception.getMessage().contains("SubjectEntity"));
        verify(subjectRepository).findSubjectByCode(addSubjectRequest.getCode());
        verify(subjectRepository, never()).save(any());
    }

    @Test
    @Transactional
    public void testPatchSubject_Success() {
        when(subjectRepository.findById(anyInt())).thenReturn(Optional.of(subjectEntity));
        when(subjectMapper.toSubjectEntity(patchSubjectRequest)).thenReturn(updatedSubjectEntity);
        SubjectTypeEntity subjectType = new SubjectTypeEntity();
        subjectType.setId(2);
        subjectType.setName("Zaawansowany");
        when(subjectTypeService.fetchSubjectType(patchSubjectRequest.getSubjectTypeId())).thenReturn(subjectType);
        updatedSubjectEntity.setSubjectType(subjectType);
        doAnswer(invocation -> {
            SubjectEntity target = invocation.getArgument(0);
            SubjectEntity source = invocation.getArgument(1);
            target.setName(source.getName());
            target.setCode(source.getCode());
            target.setDescription(source.getDescription());
            target.setSubjectType(source.getSubjectType());
            return null;
        }).when(subjectMapper).patchSubject(any(SubjectEntity.class), any(SubjectEntity.class));
        when(subjectRepository.save(subjectEntity)).thenReturn(subjectEntity);
        when(subjectMapper.toDetailedResponse(subjectEntity)).thenReturn(detailedResponse);

        SubjectDetailedResponse response = subjectService.patchSubject(patchSubjectRequest, subjectEntity.getId());

        assertNotNull(response);
        verify(subjectRepository).findById(subjectEntity.getId());
        verify(subjectRepository).save(subjectEntity);
    }

    @Test
    public void testPatchSubject_NotFound() {
        when(subjectRepository.findById(anyInt())).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> subjectService.patchSubject(patchSubjectRequest, 999));
        assertTrue(exception.getMessage().contains("SubjectEntity"));
        verify(subjectRepository).findById(999);
        verify(subjectRepository, never()).save(any());
    }

    @Test
    public void testGetAllSubjects_Success() {
        List<SubjectEntity> subjectList = Arrays.asList(subjectEntity);
        when(subjectRepository.findAll()).thenReturn(subjectList);
        List<SubjectSummaryResponse> summaries = new ArrayList<>();
        summaries.add(new SubjectSummaryResponse());
        when(subjectMapper.toSubjectSummaryResponseList(subjectList)).thenReturn(summaries);

        Collection<SubjectSummaryResponse> result = subjectService.getAllSubjects();
        assertEquals(1, result.size());
        verify(subjectRepository).findAll();
    }

    @Test
    public void testGetSubjectById_Success() {
        when(subjectRepository.findById(anyInt())).thenReturn(Optional.of(subjectEntity));
        when(subjectMapper.toDetailedResponse(subjectEntity)).thenReturn(detailedResponse);

        SubjectDetailedResponse result = subjectService.getSubjectById(subjectEntity.getId());
        assertNotNull(result);
        verify(subjectRepository).findById(subjectEntity.getId());
    }

    @Test
    public void testGetSubjectById_NotFound() {
        when(subjectRepository.findById(anyInt())).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> subjectService.getSubjectById(123));
        assertTrue(exception.getMessage().contains("SubjectEntity"));
        verify(subjectRepository).findById(123);
    }

    @Test
    public void testGetSubjectByCode_Success() {
        when(subjectRepository.findSubjectByCode(anyString())).thenReturn(Optional.of(subjectEntity));
        when(subjectMapper.toDetailedResponse(subjectEntity)).thenReturn(detailedResponse);

        SubjectDetailedResponse result = subjectService.getSubjectByCode("MAT01");
        assertNotNull(result);
        verify(subjectRepository).findSubjectByCode("MAT01");
    }

    @Test
    public void testGetSubjectByCode_NotFound() {
        when(subjectRepository.findSubjectByCode(anyString())).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> subjectService.getSubjectByCode("NONEXISTENT"));
        assertTrue(exception.getMessage().contains("SubjectEntity"));
        verify(subjectRepository).findSubjectByCode("NONEXISTENT");
    }

    @Test
    public void testGetSubjectsByType_Success() {
        String subjectType = "Podstawowy";
        List<SubjectEntity> subjectList = Collections.singletonList(subjectEntity);
        when(subjectRepository.findSubjectsBySubjectType_Name(subjectType)).thenReturn(subjectList);
        List<SubjectSummaryResponse> summaries = Collections.singletonList(new SubjectSummaryResponse());
        when(subjectMapper.toSubjectSummaryResponseList(subjectList)).thenReturn(summaries);

        Collection<SubjectSummaryResponse> result = subjectService.getSubjectsByType(subjectType);
        assertEquals(1, result.size());
        verify(subjectRepository).findSubjectsBySubjectType_Name(subjectType);
    }

    @Test
    @Transactional
    public void testDeleteSubject_Success() {
        when(subjectRepository.findById(anyInt())).thenReturn(Optional.of(subjectEntity));
        when(yearbookRepository.existsByMainCourseSubjects(subjectEntity)).thenReturn(false);
        when(teacherRepository.existsByTeachingSubjects(subjectEntity)).thenReturn(false);

        subjectService.deleteSubject(subjectEntity.getId());

        verify(subjectRepository).findById(subjectEntity.getId());
        verify(yearbookRepository).existsByMainCourseSubjects(subjectEntity);
        verify(teacherRepository).existsByTeachingSubjects(subjectEntity);
        verify(subjectRepository).delete(subjectEntity);
    }

    @Test
    @Transactional
    public void testDeleteSubject_RelationsExist() {
        when(subjectRepository.findById(anyInt())).thenReturn(Optional.of(subjectEntity));
        when(yearbookRepository.existsByMainCourseSubjects(subjectEntity)).thenReturn(true);

        SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                () -> subjectService.deleteSubject(subjectEntity.getId()));
        assertTrue(exception.getMessage().contains("Cannot remove Subject"));
        verify(subjectRepository).findById(subjectEntity.getId());
        verify(yearbookRepository).existsByMainCourseSubjects(subjectEntity);
        verify(subjectRepository, never()).delete(any());
    }

    @Test
    public void testFetchSubjects_EmptyInput() {
        Collection<SubjectEntity> result = subjectService.fetchSubjects(Collections.emptySet());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFetchSubjects_InvalidIds() {
        Set<Integer> subjectIds = new HashSet<>(Arrays.asList(1, 2));
        when(subjectRepository.findAllById(subjectIds)).thenReturn(Collections.singletonList(subjectEntity));

        SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                () -> subjectService.fetchSubjects(subjectIds));
        assertTrue(exception.getMessage().contains("Some of given Subject IDs"));
    }

    @Test
    public void testFetchSubjects_Success() {
        Set<Integer> subjectIds = new HashSet<>(Arrays.asList(100));
        when(subjectRepository.findAllById(subjectIds)).thenReturn(Collections.singletonList(subjectEntity));

        Collection<SubjectEntity> result = subjectService.fetchSubjects(subjectIds);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("fetchSubject - should return SubjectEntity")
    public void fetchSubject_successful(){
        when(subjectRepository.findById(updatedSubjectEntity.getId())).thenReturn(Optional.of(updatedSubjectEntity));

        SubjectEntity result = subjectService.fetchSubject(updatedSubjectEntity.getId());

        assertEquals(updatedSubjectEntity.getId(), result.getId());
        assertEquals(updatedSubjectEntity.getName(), result.getName());
        assertEquals(updatedSubjectEntity.getCode(), result.getCode());
        assertEquals(updatedSubjectEntity.getDescription(), result.getDescription());
        assertEquals(updatedSubjectEntity.getSubjectType(), result.getSubjectType());
        assertIterableEquals(updatedSubjectEntity.getTeachers(), result.getTeachers());
        assertIterableEquals(updatedSubjectEntity.getYearbooksTakingSubject(), result.getYearbooksTakingSubject());
    }

    @Test
    @DisplayName("fetchSubject entity not found - should throw EntityNotFoundEx")
    public void fetchSubject_entityNotFound(){
        when(subjectRepository.findById(any())).thenReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(
                EntityNotFoundException.class,
                () -> subjectService.fetchSubject(1)
        );

        assertTrue(result.getMessage().contains("ID"));
        assertTrue(result.getMessage().contains("1"));
        assertTrue(result.getMessage().contains("SubjectEntity"));
    }
}
