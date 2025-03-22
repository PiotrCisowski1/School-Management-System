package com.cisowski.schoolmanagement.yearbook.service;

import com.cisowski.schoolmanagement.common.exception.type.EntityAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.subject.service.SubjectService;
import com.cisowski.schoolmanagement.users.student.repository.StudentRepository;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherService;
import com.cisowski.schoolmanagement.yearbook.mapper.YearbookMapper;
import com.cisowski.schoolmanagement.yearbook.model.*;
import com.cisowski.schoolmanagement.yearbook.repository.YearbookRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class YearbookServiceImpl implements YearbookService {

    private final YearbookRepository yearbookRepository;
    private final SubjectService subjectService;
    private final YearbookMapper mapper;
    private final StudentRepository studentRepository;
    private final TeacherService teacherService;

    @Override
    @Transactional
    public YearbookDetailedResponse addYearbook(AddYearbookRequest request) {
        DbLogger.info(String.format("Add Yearbook for request: %s", request.toString()));

        Optional<YearbookEntity> existingYearbook = yearbookRepository.findYearbookBySymbolOrHeadTeacher(request.getSymbol(), request.getHeadTeacherId());
        if(existingYearbook.isPresent())
            throw new EntityAlreadyExistsException(YearbookEntity.class, existingYearbook.get().getSymbol());

        YearbookEntity requestEntity = mapper.toYearbookEntity(request);
        requestEntity.setHeadTeacher(teacherService.fetchTeacher(request.getHeadTeacherId()));
        requestEntity.setMainCourseSubjects(subjectService.fetchSubjects(request.getMainCourseSubjectsIds()));

        YearbookEntity savedYearbook = yearbookRepository.save(requestEntity);
        DbLogger.info("Yearbook saved successfully: " + savedYearbook.toString());

        return mapper.toDetailedResponse(savedYearbook);
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
        existingYearbookEntity.setHeadTeacher(teacherService.fetchTeacher(request.getHeadTeacherId()));
        updateSubjects(request.getMainCourseSubjectsIdsToAdd(), request.getMainCourseSubjectsIdsToRemove(), existingYearbookEntity);
        mapper.patchYearbook(existingYearbookEntity, requestYearbook);
        YearbookEntity updatedYearbook = yearbookRepository.save(existingYearbookEntity);

        DbLogger.info(String.format("Yearbook updated successfully: %s", updatedYearbook.toString()));

        return mapper.toDetailedResponse(updatedYearbook);
    }

    private void updateSubjects(Collection<Integer> subjectsToAdd, Collection<Integer> subjectsToRemove, YearbookEntity yearbookEntity){
        if(!CollectionUtils.isEmpty(subjectsToRemove))
            removeSubjectsFromYearbook(yearbookEntity, subjectsToRemove);
        if(!CollectionUtils.isEmpty(subjectsToAdd))
            addNewSubjectsToYearbook(yearbookEntity, subjectsToAdd);

    }

    private void addNewSubjectsToYearbook(YearbookEntity yearbookEntity, Collection<Integer> subjectsIds){
        Collection<SubjectEntity> subjects = subjectService.fetchSubjects(subjectsIds);
        yearbookEntity.setMainCourseSubjects(subjects);
    }

    private void removeSubjectsFromYearbook(YearbookEntity yearbookEntity, Collection<Integer> subjectsIds){
        Collection<SubjectEntity> subjects = subjectService.fetchSubjects(subjectsIds);
        subjects.forEach(subject -> {
            if(!yearbookEntity.getMainCourseSubjects().contains(subject))
                throw new SpecificationBrokenException(String.format("Subject with ID %s is not associated with Yearbook with ID %s", subject.getId(), yearbookEntity.getId()));
        });
    }

    @Override
    @Transactional
    public void deleteYearbook(Integer yearbookId) {
        DbLogger.info(String.format("Deleting Yearbook with ID %s", yearbookId.toString()));

        Optional<YearbookEntity> existingYearbook = yearbookRepository.findById(yearbookId);
        if(existingYearbook.isEmpty())
            throw new EntityNotFoundException(YearbookEntity.class, "ID", yearbookId.toString());
        checkIfYearbookIsAssociatedWithStudents(existingYearbook.get());
        yearbookRepository.deleteById(yearbookId);

        DbLogger.info(String.format("Yearbook with ID %s removed successfully",yearbookId));
    }

    private void checkIfYearbookIsAssociatedWithStudents(YearbookEntity yearbookEntity){
        if(studentRepository.existsByYearbook(yearbookEntity))
            throw new SpecificationBrokenException(String.format("Cannot remove Yearbook with ID %s, because there are Students associated with it", yearbookEntity.getId()));
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

    @Override
    public YearbookEntity fetchYearbookEntity(Integer yearbookId){
        if(yearbookId == null)
            return null;
        Optional<YearbookEntity> yearbook = yearbookRepository.findById(yearbookId);
        if(yearbook.isEmpty())
            throw new SpecificationBrokenException(String.format("Given Yearbook ID does not exist: (%s)", yearbookId));
        return yearbook.get();
    }
}
