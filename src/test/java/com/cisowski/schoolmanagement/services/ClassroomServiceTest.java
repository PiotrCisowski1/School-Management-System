package com.cisowski.schoolmanagement.services;

import com.cisowski.schoolmanagement.classroom.mapper.ClassroomMapper;
import com.cisowski.schoolmanagement.classroom.model.*;
import com.cisowski.schoolmanagement.classroom.repository.ClassroomRepository;
import com.cisowski.schoolmanagement.classroom.service.EquipmentService;
import com.cisowski.schoolmanagement.classroom.service.impl.ClassroomServiceImpl;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClassroomServiceTest {

    @Mock
    private ClassroomRepository classroomRepository;
    @Mock
    private ClassroomMapper classroomMapper;
    @Mock
    private EquipmentService equipmentService;
    @InjectMocks
    private ClassroomServiceImpl classroomService;
    private ClassroomRequest validRequest;
    private ClassroomEntity savedEntity;
    private ClassroomDetailedResponse expectedResponse;
    private ClassroomSummaryResponse summaryResponse;

    @BeforeEach
    void setUp() {
        validRequest = new ClassroomRequest();
        validRequest.setName("Classroom 101");
        validRequest.setCapacity(30);
        validRequest.setNotes("IT classroom");

        savedEntity = new ClassroomEntity();
        savedEntity.setId(1);
        savedEntity.setName(validRequest.getName());
        savedEntity.setCapacity(validRequest.getCapacity());
        savedEntity.setNotes(validRequest.getNotes());

        expectedResponse = new ClassroomDetailedResponse();
        expectedResponse.setId(savedEntity.getId());
        expectedResponse.setName(savedEntity.getName());
        expectedResponse.setCapacity(savedEntity.getCapacity());
        expectedResponse.setNotes(savedEntity.getNotes());

        summaryResponse = new ClassroomSummaryResponse();
        summaryResponse.setId(1);
        summaryResponse.setName("Klasa 1A");
    }

    @Test
    void testAddClassroom_withEquipments() {
        EquipmentQuantity eqQuantity = new EquipmentQuantity();
        eqQuantity.setEquipmentId(100);
        eqQuantity.setQuantity(2);
        validRequest.setEquipments(List.of(eqQuantity));
        Equipment equipment = new Equipment();
        equipment.setId(100);

        when(classroomMapper.toClassroomEntity(validRequest)).thenReturn(savedEntity);
        when(equipmentService.fetchEquipment(eqQuantity.getEquipmentId())).thenReturn(equipment);
        when(classroomRepository.save(savedEntity)).thenReturn(savedEntity);
        when(classroomMapper.toClassroomDetailedResponse(savedEntity)).thenReturn(expectedResponse);

        ClassroomDetailedResponse response = classroomService.addClassroom(validRequest);

        verify(classroomMapper).toClassroomEntity(validRequest);
        verify(equipmentService).fetchEquipment(eqQuantity.getEquipmentId());
        verify(classroomRepository).save(savedEntity);
        verify(classroomMapper).toClassroomDetailedResponse(savedEntity);

        assertNotNull(response);
        assertEquals(expectedResponse.getId(), response.getId());
        assertEquals(expectedResponse.getName(), response.getName());
        assertTrue(savedEntity.getClassroomEquipments().stream().anyMatch(ce -> ce.getEquipment().equals(equipment) && ce.getQuantity() == 2));
    }

    @Test
    void testDeleteClassroom_whenClassroomExists() {
        Integer classroomId = 1;
        when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(savedEntity));

        classroomService.deleteClassroom(classroomId);

        verify(classroomRepository).findById(classroomId);
        verify(classroomRepository).delete(savedEntity);
    }

    @Test
    void testDeleteClassroom_whenClassroomDoesNotExist() {
        Integer classroomId = 999;
        when(classroomRepository.findById(classroomId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            classroomService.deleteClassroom(classroomId);
        });

        verify(classroomRepository).findById(classroomId);
        verify(classroomRepository, never()).delete(any());
    }

    @Test
    void testGetClassroomById_whenClassroomExists() {
        Integer classroomId = 1;
        when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(savedEntity));
        when(classroomMapper.toClassroomDetailedResponse(savedEntity)).thenReturn(expectedResponse);

        ClassroomDetailedResponse response = classroomService.getClassroomById(classroomId);

        verify(classroomRepository).findById(classroomId);
        verify(classroomMapper).toClassroomDetailedResponse(savedEntity);
        assertNotNull(response);
        assertEquals(expectedResponse.getId(), response.getId());
        assertEquals(expectedResponse.getName(), response.getName());
        assertEquals(expectedResponse.getCapacity(), response.getCapacity());
        assertEquals(expectedResponse.getNotes(), response.getNotes());
        assertIterableEquals(expectedResponse.getEquipments(), response.getEquipments());
    }

    @Test
    void testGetClassroomById_whenClassroomDoesNotExist() {
        Integer classroomId = 999;

        when(classroomRepository.findById(classroomId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                classroomService.getClassroomById(classroomId)
        );

        assertTrue(exception.getMessage().contains("ClassroomEntity"));
        assertTrue(exception.getMessage().contains("ID"));
        assertTrue(exception.getMessage().contains(classroomId.toString()));
        verify(classroomRepository).findById(classroomId);
        verify(classroomMapper, never()).toClassroomDetailedResponse(any());
    }

    @Test
    void testGetAllClassrooms() {
        List<ClassroomEntity> entities = Collections.singletonList(savedEntity);
        List<ClassroomSummaryResponse> summaryList = Collections.singletonList(summaryResponse);

        when(classroomRepository.findAll()).thenReturn(entities);
        when(classroomMapper.toSummaryResponseList(entities)).thenReturn(summaryList);

        Collection<ClassroomSummaryResponse> responses = classroomService.getAllClassrooms();

        verify(classroomRepository).findAll();
        verify(classroomMapper).toSummaryResponseList(entities);
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(summaryResponse.getId(), responses.iterator().next().getId());
    }
}
