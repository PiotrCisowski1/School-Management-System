package com.cisowski.schoolmanagement.unit.services;

import com.cisowski.schoolmanagement.classroom.mapper.ClassroomMapper;
import com.cisowski.schoolmanagement.classroom.model.*;
import com.cisowski.schoolmanagement.classroom.repository.ClassroomRepository;
import com.cisowski.schoolmanagement.classroom.service.EquipmentService;
import com.cisowski.schoolmanagement.classroom.service.impl.ClassroomServiceImpl;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.timetable.schedule.repository.ScheduleRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @Mock
    private ScheduleRepository scheduleRepository;
    @InjectMocks
    private ClassroomServiceImpl classroomService;
    private ClassroomRequest validRequest;
    private ClassroomEntity savedEntity;
    private ClassroomDetailedResponse expectedResponse;
    private ClassroomSummaryResponse summaryResponse;
    private ClassroomEntity existingEntity;
    private Integer classroomId;

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
        summaryResponse.setName("1A");

        classroomId = 1;
        existingEntity = new ClassroomEntity();
        existingEntity.setId(classroomId);
        existingEntity.setName("Original Name");
        existingEntity.setCapacity(30);
        existingEntity.setNotes("Original Notes");
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

    @Test
    void testUpdateClassroom_success_withEquipmentChanges() {
        PatchClassroomRequest patchRequest = new PatchClassroomRequest();
        patchRequest.setName("Updated Name");
        patchRequest.setCapacity(35);
        patchRequest.setNotes("Updated Notes");

        EquipmentQuantity eqAdd = new EquipmentQuantity();
        eqAdd.setEquipmentId(5);
        eqAdd.setQuantity(10);
        patchRequest.setEquipmentIdsToAdd(Arrays.asList(eqAdd));

        EquipmentQuantity eqRemove = new EquipmentQuantity();
        eqRemove.setEquipmentId(3);
        eqRemove.setQuantity(2);
        patchRequest.setEquipmentIdsToRemove(Arrays.asList(eqRemove));

        when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(existingEntity));

        Equipment equipmentForRemovalSetup = new Equipment();
        equipmentForRemovalSetup.setId(3);
        existingEntity.addEquipment(equipmentForRemovalSetup, 5);

        Equipment equipmentToRemove = new Equipment();
        equipmentToRemove.setId(3);
        when(equipmentService.fetchEquipments(patchRequest.getEquipmentIdsToRemove()))
                .thenReturn(Arrays.asList(equipmentToRemove));

        Equipment equipmentToAdd = new Equipment();
        equipmentToAdd.setId(5);
        when(equipmentService.fetchEquipments(patchRequest.getEquipmentIdsToAdd()))
                .thenReturn(Arrays.asList(equipmentToAdd));
        ClassroomEntity requestEntity = new ClassroomEntity();
        requestEntity.setName(patchRequest.getName());
        requestEntity.setCapacity(patchRequest.getCapacity());
        requestEntity.setNotes(patchRequest.getNotes());
        when(classroomMapper.toClassroomEntity(patchRequest)).thenReturn(requestEntity);
        when(classroomRepository.save(existingEntity)).thenReturn(existingEntity);
        ClassroomDetailedResponse detailedResponse = new ClassroomDetailedResponse();
        detailedResponse.setId(classroomId);
        detailedResponse.setName(patchRequest.getName());
        detailedResponse.setCapacity(patchRequest.getCapacity());
        detailedResponse.setNotes(patchRequest.getNotes());
        when(classroomMapper.toClassroomDetailedResponse(existingEntity)).thenReturn(detailedResponse);

        ClassroomDetailedResponse response = classroomService.updateClassroom(patchRequest, classroomId);

        verify(classroomRepository).findById(classroomId);
        verify(classroomRepository).flush();
        verify(classroomMapper).patchClassroom(requestEntity, existingEntity);
        verify(classroomRepository).save(existingEntity);
        verify(equipmentService).fetchEquipments(patchRequest.getEquipmentIdsToRemove());
        verify(equipmentService).fetchEquipments(patchRequest.getEquipmentIdsToAdd());
        assertNotNull(response);
        assertEquals(patchRequest.getName(), response.getName());
        assertEquals(patchRequest.getCapacity(), response.getCapacity());
        assertEquals(patchRequest.getNotes(), response.getNotes());
    }

    @Test
    void testUpdateClassroom_success_withoutEquipmentChanges() {
        PatchClassroomRequest patchRequest = new PatchClassroomRequest();
        patchRequest.setName("Updated Name Only");
        patchRequest.setCapacity(40);
        patchRequest.setNotes("Updated Notes Only");
        patchRequest.setEquipmentIdsToAdd(null);
        patchRequest.setEquipmentIdsToRemove(null);
        when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(existingEntity));
        ClassroomEntity requestEntity = new ClassroomEntity();
        requestEntity.setName(patchRequest.getName());
        requestEntity.setCapacity(patchRequest.getCapacity());
        requestEntity.setNotes(patchRequest.getNotes());
        when(classroomMapper.toClassroomEntity(patchRequest)).thenReturn(requestEntity);
        when(classroomRepository.save(existingEntity)).thenReturn(existingEntity);
        ClassroomDetailedResponse detailedResponse = new ClassroomDetailedResponse();
        detailedResponse.setId(classroomId);
        detailedResponse.setName(patchRequest.getName());
        detailedResponse.setCapacity(patchRequest.getCapacity());
        detailedResponse.setNotes(patchRequest.getNotes());
        when(classroomMapper.toClassroomDetailedResponse(existingEntity)).thenReturn(detailedResponse);

        ClassroomDetailedResponse response = classroomService.updateClassroom(patchRequest, classroomId);

        verify(equipmentService, times(2)).fetchEquipments(any());
        verify(classroomRepository).findById(classroomId);
        verify(classroomRepository).flush();
        verify(classroomRepository).save(existingEntity);
        verify(classroomMapper).patchClassroom(requestEntity, existingEntity);
        assertNotNull(response);
        assertEquals(patchRequest.getName(), response.getName());
        assertEquals(patchRequest.getCapacity(), response.getCapacity());
        assertEquals(patchRequest.getNotes(), response.getNotes());
    }

    @Test
    void testUpdateClassroom_classroomNotFound() {
        PatchClassroomRequest patchRequest = new PatchClassroomRequest();
        patchRequest.setName("Any Name");

        when(classroomRepository.findById(classroomId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                classroomService.updateClassroom(patchRequest, classroomId)
        );

        assertTrue(exception.getMessage().contains("ClassroomEntity"));
        assertTrue(exception.getMessage().contains("ID"));
        assertTrue(exception.getMessage().contains(classroomId.toString()));
        verify(classroomRepository).findById(classroomId);
        verifyNoMoreInteractions(classroomRepository);
    }

    @Test
    @DisplayName("fetchClassroom - should return ClassroomEntity")
    void fetchClassroom_successful(){
        ClassroomEntity classroom = Instancio.create(ClassroomEntity.class);

        when(classroomRepository.findById(classroom.getId())).thenReturn(Optional.of(classroom));

        ClassroomEntity result = classroomService.fetchClassroom(classroom.getId());

        assertEquals(classroom.getId(), result.getId());
        assertEquals(classroom.getName(), result.getName());
        assertEquals(classroom.getCapacity(), result.getCapacity());
        assertEquals(classroom.getNotes(), result.getNotes());
    }

    @Test
    @DisplayName("fetchClassroom - no entity found")
    void fetchClassroom_entityNotFound(){
        when(classroomRepository.findById(any())).thenReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(
                EntityNotFoundException.class,
                () -> classroomService.fetchClassroom(1)
        );
        assertTrue(result.getMessage().contains("ID"));
        assertTrue(result.getMessage().contains("1"));
        assertTrue(result.getMessage().contains("ClassroomEntity"));
    }
}
