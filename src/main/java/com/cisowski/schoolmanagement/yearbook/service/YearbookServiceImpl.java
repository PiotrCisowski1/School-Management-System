package com.cisowski.schoolmanagement.yearbook.service;

import com.cisowski.schoolmanagement.common.exception.type.EntityAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.model.entity.SubjectEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.repository.TeacherRepository;
import com.cisowski.schoolmanagement.yearbook.mapper.YearbookMapper;
import com.cisowski.schoolmanagement.yearbook.model.*;
import com.cisowski.schoolmanagement.yearbook.repository.YearbookRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class YearbookServiceImpl implements YearbookService {

    private final YearbookRepository yearbookRepository;
    private final TeacherRepository teacherRepository;
    private final YearbookMapper mapper;

    @Override
    @Transactional
    public YearbookDetailedResponse addYearbook(AddYearbookRequest request) {
        DbLogger.info(String.format("Add Yearbook for request: %s", request.toString()));

        Optional<YearbookEntity> existingYearbook = yearbookRepository.findYearbookBySymbolOrHeadTeacher(request.getSymbol(), request.getHeadTeacherId());
        if(existingYearbook.isPresent())
            throw new EntityAlreadyExistsException(YearbookEntity.class, existingYearbook.get().getSymbol());

        YearbookEntity requestEntity = mapper.toYearbookEntity(request);
        requestEntity.setHeadTeacher(fetchTeacher(request.getHeadTeacherId()));
        requestEntity.setMainCourseSubjects(fetchSubjects(request.getMainCourseSubjectsIds()));

        YearbookEntity savedYearbook = yearbookRepository.save(requestEntity);
        DbLogger.info("Yearbook saved successfully: " + savedYearbook.toString());

        return mapper.toDetailedResponse(savedYearbook);
    }

    private TeacherEntity fetchTeacher(Integer teacherId){
        Optional<TeacherEntity> teacher = teacherRepository.findById(teacherId);
        if(teacher.isEmpty())
            throw new EntityNotFoundException(TeacherEntity.class, "ID", teacherId.toString());
        return teacher.get();
    }

    private List<SubjectEntity> fetchSubjects(Collection<Integer> subjectIds){
        //TODO: subject not yet implemented
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public YearbookDetailedResponse updateYearbook(PatchYearbookRequest request, Integer yearbookId) {
        DbLogger.info(String.format("Update Yearbook with ID %s, with request: %s", yearbookId.toString(), request.toString()));

        Optional<YearbookEntity> existingYearbook = yearbookRepository.findById(yearbookId);
        if(existingYearbook.isEmpty())
            throw new EntityNotFoundException(YearbookEntity.class, "ID", yearbookId.toString());
        YearbookEntity existingYearbookEntity = existingYearbook.get();
        YearbookEntity requestYearbook = mapper.toYearbookEntity(request);
        requestYearbook.setHeadTeacher(fetchTeacher(request.getHeadTeacherId()));
        //TODO: fetch subjects to request
        mapper.patchYearbook(existingYearbookEntity, requestYearbook);
        YearbookEntity updatedYearbook = yearbookRepository.save(existingYearbookEntity);

        DbLogger.info(String.format("Yearbook updated successfully: %s", updatedYearbook.toString()));

        YearbookDetailedResponse response = mapper.toDetailedResponse(updatedYearbook);
        return response;
    }

    @Override
    @Transactional
    public void deleteYearbook(Integer yearbookId) {
        DbLogger.info(String.format("Deleting Yearbook with ID %s", yearbookId.toString()));

        Optional<YearbookEntity> existingYearbook = yearbookRepository.findById(yearbookId);
        if(existingYearbook.isEmpty())
            throw new EntityNotFoundException(YearbookEntity.class, "ID", yearbookId.toString());
        yearbookRepository.deleteById(yearbookId);

        DbLogger.info(String.format("Yearbook with ID %s removed successfully",yearbookId));
    }

    @Override
    public YearbookDetailedResponse getYearbook(Integer yearbookId) {
        DbLogger.info(String.format("Searching for Yearbook with ID %s", yearbookId));

        Optional<YearbookEntity> existingYearbook = yearbookRepository.findById(yearbookId);
        if(existingYearbook.isEmpty())
            throw new EntityNotFoundException(YearbookEntity.class, "ID", yearbookId.toString());

        DbLogger.info(String.format("Found Yearbook with ID %s: %s", yearbookId, existingYearbook.get().toString()));

        return mapper.toDetailedResponse(existingYearbook.get());
    }

    @Override
    public Collection<YearbookSummaryResponse> getYearbooks() {
        DbLogger.info("Searching for all Yearbook entities");
        Collection<YearbookEntity> yearbooks = yearbookRepository.findAll();
        DbLogger.info(String.format("Found %d Yearbook entities", yearbooks.size()));
        return mapper.toYearbookList(yearbooks);
    }
}
