package com.cisowski.schoolmanagement.classroom.service.impl;

import com.cisowski.schoolmanagement.classroom.mapper.ClassroomMapper;
import com.cisowski.schoolmanagement.classroom.model.*;
import com.cisowski.schoolmanagement.classroom.repository.ClassroomRepository;
import com.cisowski.schoolmanagement.classroom.service.ClassroomService;
import com.cisowski.schoolmanagement.classroom.service.EquipmentService;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.repository.ScheduleRepository;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import com.cisowski.schoolmanagement.yearbook.service.YearbookService;
import io.jsonwebtoken.lang.Collections;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl implements ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final ClassroomMapper classroomMapper;
    private final EquipmentService equipmentService;
    private final ScheduleRepository scheduleRepository;
    private final YearbookService yearbookService;

    @Override
    @Transactional
    public ClassroomDetailedResponse addClassroom(ClassroomRequest request) {
        DbLogger.info("Adding Classroom for request: " + request.toString());
        ClassroomEntity requestEntity = classroomMapper.toClassroomEntity(request);
        setClassroomEqs(requestEntity, request.getEquipments());
        ClassroomEntity saved = classroomRepository.save(requestEntity);
        DbLogger.info("Successfully saved Classroom: " + saved.toString());
        return classroomMapper.toClassroomDetailedResponse(saved);
    }

    private void setClassroomEqs(ClassroomEntity classroom, Collection<EquipmentQuantity> equipments){
        if(CollectionUtils.isEmpty(equipments) || classroom == null)
            return;
        equipments.forEach(eq -> {
            Equipment equipment = equipmentService.fetchEquipment(eq.getEquipmentId());
            classroom.addEquipment(equipment, eq.getQuantity());
        });
    }

    @Override
    @Transactional
    public void deleteClassroom(Integer classroomId) {
        DbLogger.info("Removing Classroom with ID: " + classroomId);
        ClassroomEntity existingClassroom = findClassroomById(classroomId);
        checkClassroomIsInUse(existingClassroom);
        classroomRepository.delete(existingClassroom);
        DbLogger.info(String.format("Classroom with ID: %s, was successfully removed", classroomId));
    }

    @Override
    public ClassroomDetailedResponse getClassroomById(Integer classroomId) {
        DbLogger.info("Searching for Classroom with ID: " + classroomId);
        ClassroomEntity existingClassroom = findClassroomById(classroomId);
        DbLogger.info(String.format("Found Classroom with ID %s: %s", classroomId, existingClassroom.toString()));
        return classroomMapper.toClassroomDetailedResponse(existingClassroom);
    }

    @Override
    public Collection<ClassroomSummaryResponse> getAllClassrooms() {
        DbLogger.info("Searching for all Classroom records");
        List<ClassroomEntity> entities = classroomRepository.findAll();
        DbLogger.info(String.format("Found %s Classrooms records", entities.size()));
        return classroomMapper.toSummaryResponseList(entities);
    }

    @Override
    @Transactional
    public ClassroomDetailedResponse updateClassroom(PatchClassroomRequest request, Integer classroomId) {
        DbLogger.info(String.format("Updating Classroom with ID: %s, for request: %s", classroomId, request.toString()));
        ClassroomEntity existingClassroom = findClassroomById(classroomId);
        ClassroomEntity requestEntity = classroomMapper.toClassroomEntity(request);
        removeEqFromClassroom(request.getEquipmentIdsToRemove(), existingClassroom);
        classroomRepository.flush();
        addNewEquipmentToClassroom(request.getEquipmentIdsToAdd(), existingClassroom);
        classroomMapper.patchClassroom(requestEntity, existingClassroom);
        ClassroomEntity saved = classroomRepository.save(existingClassroom);
        DbLogger.info(String.format("Classroom with ID: %s, was successfully updated: %s",classroomId, saved.toString()));
        return classroomMapper.toClassroomDetailedResponse(saved);
    }

    private ClassroomEntity findClassroomById(Integer classroomId){
        Optional<ClassroomEntity> existingClassroom = classroomRepository.findById(classroomId);
        if(existingClassroom.isEmpty())
            throw new EntityNotFoundException(ClassroomEntity.class, "ID", classroomId.toString());
        return existingClassroom.get();
    }

    private void addNewEquipmentToClassroom(Collection<EquipmentQuantity> eqIdsToAdd, ClassroomEntity entity){
        Collection<Equipment> equipment = equipmentService.fetchEquipments(eqIdsToAdd);
        equipment.forEach(eq -> {
            int quantity = eqIdsToAdd.stream()
                    .filter(equip -> equip.getEquipmentId().equals(eq.getId()))
                    .findFirst()
                    .get()
                    .getQuantity();
            entity.addEquipment(eq, quantity);
        });
    }

    private void removeEqFromClassroom(Collection<EquipmentQuantity> eqIdsToRemove, ClassroomEntity entity){
        Collection<Equipment> equipment = equipmentService.fetchEquipments(eqIdsToRemove);
        equipment.forEach(entity::removeEquipment);
    }

    public ClassroomEntity fetchClassroom(Integer classroomId){
        if(classroomId == null || classroomId <= 0)
            throw new SpecificationBrokenException("Given Classroom ID is not valid integer value");
        DbLogger.info("Fetching Classroom with ID: " + classroomId);
        Optional<ClassroomEntity> classroom = classroomRepository.findById(classroomId);
        if(classroom.isEmpty())
            throw new EntityNotFoundException(ClassroomEntity.class, "ID", classroomId.toString());
        return classroom.get();
    }

    private void checkClassroomIsInUse(ClassroomEntity classroom) {
        List<ScheduleEntity> schedulesByClassroom = scheduleRepository.findByClassroomId(classroom.getId());
        if(!Collections.isEmpty(schedulesByClassroom)) {
            List<Integer> scheduleIds = schedulesByClassroom.stream().map(ScheduleEntity::getId).toList();
            throw new SpecificationBrokenException(String.format(
                    "Cannot delete Classroom with ID: %s, because there are some Schedules using it. Schedule IDs: %s",
                    classroom.getId(),
                    scheduleIds));
        }
    }

    @Override
    public Collection<ClassroomSummaryResponse> getAvailableClassrooms(GetClassroomAtRequest request, Integer yearbookId) {
        DbLogger.info("Searching for available classrooms: " + request.toString());
        List<ClassroomEntity> availableClassrooms = classroomRepository.findAllClassroomsWithinTimePeriod(
                DayOfWeek.of(request.getTimeRange().getDayOfWeek()),
                request.getTimeRange().getStartTime(),
                request.getTimeRange().getEndTime(),
                request.getStartDate(),
                request.getEndDate());
        List<ClassroomEntity> filteredResults = filterClassroomsByCapacity(availableClassrooms, yearbookId);
        return classroomMapper.toSummaryResponseList(filteredResults);
    }

    public List<ClassroomEntity> filterClassroomsByCapacity(List<ClassroomEntity> classrooms, Integer yearbookId) {
        if(yearbookId == null || CollectionUtils.isEmpty(classrooms))
            return classrooms;
        YearbookEntity yearbook = yearbookService.fetchYearbookEntity(yearbookId);
        Integer studentsCount;
        if(!CollectionUtils.isEmpty(yearbook.getStudentsInYearbook()))
            studentsCount = yearbook.getStudentsInYearbook().size();
        else
            return classrooms;

        return classrooms.stream()
                .filter(Objects::nonNull)
                .filter(classroom -> classroom.getCapacity() >= studentsCount)
                .toList();
    }
}
