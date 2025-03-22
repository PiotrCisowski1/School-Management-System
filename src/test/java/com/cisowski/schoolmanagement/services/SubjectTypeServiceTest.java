package com.cisowski.schoolmanagement.services;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.cisowski.schoolmanagement.common.exception.type.EntityAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.subject.mapper.SubjectTypeMapper;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeRequest;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeResponse;
import com.cisowski.schoolmanagement.subject.repository.SubjectRepository;
import com.cisowski.schoolmanagement.subject.repository.SubjectTypeRepository;
import com.cisowski.schoolmanagement.subject.service.SubjectTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;


@ExtendWith(MockitoExtension.class)
public class SubjectTypeServiceTest {

    @Mock
    private SubjectTypeRepository subjectTypeRepository;

    @Mock
    private SubjectTypeMapper subjectTypeMapper;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectTypeServiceImpl subjectTypeService;

    private SubjectTypeRequest subjectTypeRequest;
    private SubjectTypeEntity subjectTypeEntity;
    private SubjectTypeResponse subjectTypeResponse;

    @BeforeEach
    public void setUp() {
        subjectTypeRequest = new SubjectTypeRequest();
        subjectTypeRequest.setName("MATH");

        subjectTypeEntity = new SubjectTypeEntity();
        subjectTypeEntity.setId(1);
        subjectTypeEntity.setName("ENGLISH");

        subjectTypeResponse = new SubjectTypeResponse();
        subjectTypeResponse.setId(1);
        subjectTypeResponse.setName("POLISH");
    }

    @Test
    @Transactional
    public void testAddSubjectType_Success() {
        when(subjectTypeRepository.findByName(subjectTypeRequest.getName()))
                .thenReturn(Optional.empty());
        when(subjectTypeMapper.toSubjectTypeEntity(subjectTypeRequest))
                .thenReturn(subjectTypeEntity);
        when(subjectTypeRepository.save(subjectTypeEntity))
                .thenReturn(subjectTypeEntity);
        when(subjectTypeMapper.toSubjectTypeResponse(subjectTypeEntity))
                .thenReturn(subjectTypeResponse);

        SubjectTypeResponse response = subjectTypeService.addSubjectType(subjectTypeRequest);

        assertNotNull(response);
        assertEquals(subjectTypeResponse.getId(), response.getId());
        verify(subjectTypeRepository).findByName(subjectTypeRequest.getName());
        verify(subjectTypeRepository).save(subjectTypeEntity);
    }

    @Test
    public void testAddSubjectType_AlreadyExists() {
        when(subjectTypeRepository.findByName(anyString()))
                .thenReturn(Optional.of(subjectTypeEntity));

        EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class,
                () -> subjectTypeService.addSubjectType(subjectTypeRequest));
        assertTrue(exception.getMessage().contains("SubjectTypeEntity"));
        verify(subjectTypeRepository).findByName(subjectTypeRequest.getName());
        verify(subjectTypeRepository, never()).save(any());
    }

    @Test
    @Transactional
    public void testPatchSubjectType_Success() {
        when(subjectTypeRepository.findById(subjectTypeEntity.getId()))
                .thenReturn(Optional.of(subjectTypeEntity));
        when(subjectTypeMapper.toSubjectTypeEntity(subjectTypeRequest))
                .thenReturn(subjectTypeEntity);
        doAnswer(invocation -> {
            SubjectTypeEntity target = invocation.getArgument(0);
            SubjectTypeEntity source = invocation.getArgument(1);
            target.setName(source.getName());
            return null;
        }).when(subjectTypeMapper).patchSubjectType(any(SubjectTypeEntity.class), any(SubjectTypeEntity.class));

        when(subjectTypeRepository.save(subjectTypeEntity))
                .thenReturn(subjectTypeEntity);
        when(subjectTypeMapper.toSubjectTypeResponse(subjectTypeEntity))
                .thenReturn(subjectTypeResponse);

        SubjectTypeResponse response = subjectTypeService.patchSubjectType(subjectTypeRequest, subjectTypeEntity.getId());
        assertNotNull(response);
        verify(subjectTypeRepository).findById(subjectTypeEntity.getId());
        verify(subjectTypeRepository).save(subjectTypeEntity);
    }

    @Test
    public void testPatchSubjectType_NotFound() {
        when(subjectTypeRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> subjectTypeService.patchSubjectType(subjectTypeRequest, 999));
        assertTrue(exception.getMessage().contains("SubjectTypeEntity"));
        verify(subjectTypeRepository).findById(999);
        verify(subjectTypeRepository, never()).save(any());
    }

    @Test
    @Transactional
    public void testDeleteSubjectType_Success() {
        when(subjectTypeRepository.findById(subjectTypeEntity.getId()))
                .thenReturn(Optional.of(subjectTypeEntity));
        when(subjectRepository.existsBySubjectType(subjectTypeEntity)).thenReturn(false);

        subjectTypeService.deleteSubjectType(subjectTypeEntity.getId());

        verify(subjectTypeRepository).findById(subjectTypeEntity.getId());
        verify(subjectRepository).existsBySubjectType(subjectTypeEntity);
        verify(subjectTypeRepository).delete(subjectTypeEntity);
    }

    @Test
    @Transactional
    public void testDeleteSubjectType_InUse() {
        when(subjectTypeRepository.findById(subjectTypeEntity.getId()))
                .thenReturn(Optional.of(subjectTypeEntity));
        when(subjectRepository.existsBySubjectType(subjectTypeEntity)).thenReturn(true);

        SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                () -> subjectTypeService.deleteSubjectType(subjectTypeEntity.getId()));
        assertTrue(exception.getMessage().contains("Cannot remove SubjectType"));
        verify(subjectTypeRepository).findById(subjectTypeEntity.getId());
        verify(subjectRepository).existsBySubjectType(subjectTypeEntity);
        verify(subjectTypeRepository, never()).delete(any());
    }

    @Test
    public void testDeleteSubjectType_NotFound() {
        when(subjectTypeRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> subjectTypeService.deleteSubjectType(999));
        assertTrue(exception.getMessage().contains("SubjectTypeEntity"));
        verify(subjectTypeRepository).findById(999);
        verify(subjectTypeRepository, never()).delete(any());
    }

    @Test
    public void testGetSubjectTypes_Success() {
        List<SubjectTypeEntity> entities = Collections.singletonList(subjectTypeEntity);
        List<SubjectTypeResponse> responses = Collections.singletonList(subjectTypeResponse);
        when(subjectTypeRepository.findAll()).thenReturn(entities);
        when(subjectTypeMapper.toSubjectTypeResponses(entities)).thenReturn(responses);

        Collection<SubjectTypeResponse> result = subjectTypeService.getSubjectTypes();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(subjectTypeRepository).findAll();
    }

    @Test
    public void testGetSubjectType_Success() {
        when(subjectTypeRepository.findById(subjectTypeEntity.getId()))
                .thenReturn(Optional.of(subjectTypeEntity));
        when(subjectTypeMapper.toSubjectTypeResponse(subjectTypeEntity))
                .thenReturn(subjectTypeResponse);

        SubjectTypeResponse response = subjectTypeService.getSubjectType(subjectTypeEntity.getId());
        assertNotNull(response);
        verify(subjectTypeRepository).findById(subjectTypeEntity.getId());
    }

    @Test
    public void testGetSubjectType_NotFound() {
        when(subjectTypeRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> subjectTypeService.getSubjectType(999));
        assertTrue(exception.getMessage().contains("SubjectTypeEntity"));
        verify(subjectTypeRepository).findById(999);
    }

    @Test
    public void testFetchSubjectType_Success() {
        when(subjectTypeRepository.findById(subjectTypeEntity.getId()))
                .thenReturn(Optional.of(subjectTypeEntity));

        SubjectTypeEntity fetched = subjectTypeService.fetchSubjectType(subjectTypeEntity.getId());
        assertNotNull(fetched);
        assertEquals(subjectTypeEntity.getId(), fetched.getId());
        verify(subjectTypeRepository).findById(subjectTypeEntity.getId());
    }

    @Test
    public void testFetchSubjectType_NullId() {
        SubjectTypeEntity fetched = subjectTypeService.fetchSubjectType(null);
        assertNull(fetched);
    }

    @Test
    public void testFetchSubjectType_NotFound() {
        when(subjectTypeRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> subjectTypeService.fetchSubjectType(999));
        assertTrue(exception.getMessage().contains("SubjectTypeEntity"));
        verify(subjectTypeRepository).findById(999);
    }
}
