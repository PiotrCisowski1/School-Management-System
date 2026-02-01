package com.cisowski.schoolmanagement.subject.service;

import com.cisowski.schoolmanagement.common.exception.type.EntityAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.subject.mapper.SubjectMapper;
import com.cisowski.schoolmanagement.subject.model.*;
import com.cisowski.schoolmanagement.subject.repository.SubjectRepository;
import com.cisowski.schoolmanagement.users.teacher.repository.TeacherRepository;
import com.cisowski.schoolmanagement.yearbook.repository.YearbookRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@AllArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectMapper subjectMapper;
    private final SubjectRepository subjectRepository;
    private final YearbookRepository yearbookRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectTypeService subjectTypeService;

    @Override
    @Transactional
    public SubjectDetailedResponse addSubject(AddSubjectRequest subjectRequest) {
        DbLogger.info(String.format("Add Subject for request: %s", subjectRequest.toString()));

        Optional<SubjectEntity> existingSubject = subjectRepository.findSubjectByCode(subjectRequest.getCode());
        if(existingSubject.isPresent())
            throw new EntityAlreadyExistsException(SubjectEntity.class, existingSubject.get().getCode());

        SubjectEntity requestEntity = subjectMapper.toSubjectEntity(subjectRequest);
        requestEntity.setSubjectType(subjectTypeService.fetchSubjectType(subjectRequest.getSubjectTypeId()));

        SubjectEntity savedEntity = subjectRepository.save(requestEntity);
        DbLogger.info(String.format("Subject saved successfuly: %s", savedEntity.toString()));

        return subjectMapper.toDetailedResponse(savedEntity);
    }

    @Override
    @Transactional
    public SubjectDetailedResponse patchSubject(PatchSubjectRequest subjectRequest, Integer subjectId) {
        DbLogger.info(String.format("Update Subject with ID: %s, with request: %s", subjectId.toString(), subjectRequest.toString()));
        Optional<SubjectEntity> existingSubject = subjectRepository.findById(subjectId);
        if(existingSubject.isEmpty())
            throw new EntityNotFoundException(SubjectEntity.class, "ID", subjectId.toString());
        SubjectEntity requestEntity = subjectMapper.toSubjectEntity(subjectRequest);
        requestEntity.setSubjectType(subjectTypeService.fetchSubjectType(subjectRequest.getSubjectTypeId()));
        subjectMapper.patchSubject(existingSubject.get(), requestEntity);
        SubjectEntity updatedSubject = subjectRepository.save(existingSubject.get());
        DbLogger.info("Subject updated successfully: " + updatedSubject.toString());
        return subjectMapper.toDetailedResponse(updatedSubject);
    }

    @Override
    public Collection<SubjectSummaryResponse> getAllSubjects() {
        DbLogger.info("Searching for all existing Subjects");
        List<SubjectEntity> subjects = subjectRepository.findAll();
        DbLogger.info(String.format("Found %s Subjects", subjects.size()));
        return subjectMapper.toSubjectSummaryResponseList(subjects);
    }

    @Override
    public SubjectDetailedResponse getSubjectById(Integer subjectId) {
        DbLogger.info(String.format("Searching for Subject with ID: %s", subjectId));
        Optional<SubjectEntity> subjectEntity = subjectRepository.findById(subjectId);
        if(subjectEntity.isEmpty())
            throw new EntityNotFoundException(SubjectEntity.class, "ID", subjectId.toString());
        DbLogger.info(String.format("Found Subject with ID: %s, object: %s", subjectId, subjectEntity.get().toString()));
        return subjectMapper.toDetailedResponse(subjectEntity.get());
    }

    @Override
    public SubjectDetailedResponse getSubjectByCode(String subjectCode) {
        DbLogger.info(String.format("Searching for Subject with code: %s", subjectCode));
        Optional<SubjectEntity> subjectEntity = subjectRepository.findSubjectByCode(subjectCode);
        if(subjectEntity.isEmpty())
            throw new EntityNotFoundException(SubjectEntity.class, "Code", subjectCode);
        DbLogger.info(String.format("Found Subject with code %s", subjectCode));
        return subjectMapper.toDetailedResponse(subjectEntity.get());
    }

    @Override
    public Collection<SubjectSummaryResponse> getSubjectsByType(String subjectType) {
        DbLogger.info(String.format("Searching for Subjects with type: %s", subjectType));
        List<SubjectEntity> subjectEntities = subjectRepository.findSubjectsBySubjectType_Name(subjectType);
        DbLogger.info(String.format("Found %s Subjects with type %s", subjectEntities.size(), subjectType));
        return subjectMapper.toSubjectSummaryResponseList(subjectEntities);
    }

    @Override
    @Transactional
    public void deleteSubject(Integer subjectId) {
        DbLogger.info(String.format("Deleting Subject with ID: %s", subjectId));
        Optional<SubjectEntity> existingSubject = subjectRepository.findById(subjectId);
        if(existingSubject.isEmpty())
            throw new EntityNotFoundException(SubjectEntity.class, "ID", subjectId.toString());
        checkRelations(existingSubject.get());
        subjectRepository.delete(existingSubject.get());
        DbLogger.info(String.format("Subject with ID %s was deleted successfully", subjectId));
    }

    private void checkRelations(SubjectEntity subject) {
        if(yearbookRepository.existsByMainCourseSubjects(subject))
            throw new SpecificationBrokenException(String.format("Cannot remove Subject with ID %s, because there is atleast one Yearbook associated", subject.getId()));
        if(teacherRepository.existsByTeachingSubjectsAndIsHideFalse(subject))
            throw new SpecificationBrokenException(String.format("Cannot remove Subject with ID %s, because there is atleast one Teacher associated", subject.getId()));
    }

    public Collection<SubjectEntity> fetchSubjects(Collection<Integer> subjectIds){
        if(CollectionUtils.isEmpty(subjectIds))
            return Collections.emptySet();
        List<SubjectEntity> subjectEntities = subjectRepository.findAllById(subjectIds);
        if(subjectEntities.size() != subjectIds.size())
            throw new SpecificationBrokenException("Some of given Subject IDs are invalid or non-existent");
        return new HashSet<>(subjectEntities);
    }

    @Override
    public SubjectEntity fetchSubject(Integer subjectId) {
        if(subjectId == null || subjectId <= 0)
            throw new SpecificationBrokenException("Given Subject ID is not valid integer value");
        Optional<SubjectEntity> subject = subjectRepository.findById(subjectId);
        if(subject.isEmpty())
            throw new EntityNotFoundException(SubjectEntity.class, "ID", subjectId.toString());
        return subject.get();
    }
}
