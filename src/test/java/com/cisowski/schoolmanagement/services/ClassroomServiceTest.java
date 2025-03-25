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

import java.util.List;
import java.util.Optional;

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
}
