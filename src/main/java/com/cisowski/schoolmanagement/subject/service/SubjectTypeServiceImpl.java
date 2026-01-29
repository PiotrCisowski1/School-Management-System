package com.cisowski.schoolmanagement.subject.service;

import com.cisowski.schoolmanagement.common.exception.type.EntityAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.subject.mapper.SubjectTypeMapper;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeRequest;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeResponse;
import com.cisowski.schoolmanagement.subject.repository.SubjectRepository;
import com.cisowski.schoolmanagement.subject.repository.SubjectTypeRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SubjectTypeServiceImpl implements SubjectTypeService {

    private SubjectTypeRepository subjectTypeRepository;
    private SubjectTypeMapper subjectTypeMapper;
    private SubjectRepository subjectRepository;
    @Override
    @Transactional
    public SubjectTypeResponse addSubjectType(SubjectTypeRequest request) {
        DbLogger.info("Add SubjectType for request: " + request.toString());
        checkNameUnique(request.getName());
        SubjectTypeEntity requestEntity = subjectTypeMapper.toSubjectTypeEntity(request);
        SubjectTypeEntity savedSubject = subjectTypeRepository.save(requestEntity);
        DbLogger.info("SubjectType saved successfully: " + savedSubject.toString());
        return subjectTypeMapper.toSubjectTypeResponse(savedSubject);
    }

    @Override
    @Transactional
    public SubjectTypeResponse patchSubjectType(SubjectTypeRequest request, Integer subjectTypeId) {
        DbLogger.info(String.format("Patch SubjectType with ID %s for request: %s",subjectTypeId, request.toString()));
        Optional<SubjectTypeEntity> existingEntity = subjectTypeRepository.findById(subjectTypeId);
        if(existingEntity.isEmpty())
            throw new EntityNotFoundException(SubjectTypeEntity.class, "ID", subjectTypeId.toString());
        checkNameUnique(request.getName());
        SubjectTypeEntity requestEntity = subjectTypeMapper.toSubjectTypeEntity(request);
        subjectTypeMapper.patchSubjectType(existingEntity.get(), requestEntity);
        SubjectTypeEntity savedEntity = subjectTypeRepository.save(existingEntity.get());
        DbLogger.info(String.format("SubjectType with ID %s has been patched successfully: %s",subjectTypeId, savedEntity.toString()));
        return subjectTypeMapper.toSubjectTypeResponse(savedEntity);
    }

    private void checkNameUnique(String name) {
        if(StringUtils.isEmpty(name))
            return;
        Optional<SubjectTypeEntity> existingSubjectType = subjectTypeRepository.findByName(name);
        if(existingSubjectType.isPresent())
            throw new EntityAlreadyExistsException(SubjectTypeEntity.class, existingSubjectType.get().getId().toString());
    }

    @Override
    @Transactional
    public void deleteSubjectType(Integer subjectTypeId) {
        DbLogger.info(String.format("Delete SubjectType with ID %s",subjectTypeId));
        Optional<SubjectTypeEntity> existingEntity = subjectTypeRepository.findById(subjectTypeId);
        if(existingEntity.isEmpty())
            throw new EntityNotFoundException(SubjectTypeEntity.class, "ID", subjectTypeId.toString());
        checkIfSubjectTypeIsStillInUse(existingEntity.get());
        subjectTypeRepository.delete(existingEntity.get());
    }

    private void checkIfSubjectTypeIsStillInUse(SubjectTypeEntity subjectType){
        if(subjectRepository.existsBySubjectType(subjectType))
            throw new SpecificationBrokenException(String.format("Cannot remove SubjectType with ID %s, because it is still associated with Subjects", subjectType.getId()));
    }

    @Override
    public Collection<SubjectTypeResponse> getSubjectTypes() {
        DbLogger.info("Searching for all SubjectType entities");
        List<SubjectTypeEntity> entities = subjectTypeRepository.findAll();
        DbLogger.info(String.format("Found %s SubjectType entities", entities.size()));
        return subjectTypeMapper.toSubjectTypeResponses(entities);
    }

    @Override
    public SubjectTypeResponse getSubjectType(Integer subjectTypeId) {
        DbLogger.info(String.format("Searching for SubjectType with ID %s",subjectTypeId));
        Optional<SubjectTypeEntity> existingEntity = subjectTypeRepository.findById(subjectTypeId);
        if(existingEntity.isEmpty())
            throw new EntityNotFoundException(SubjectTypeEntity.class, "ID", subjectTypeId.toString());
        DbLogger.info(String.format("Found SubjectType entity: %s", existingEntity.get().toString()));
        return subjectTypeMapper.toSubjectTypeResponse(existingEntity.get());
    }
    @Override
    public SubjectTypeEntity fetchSubjectType(Integer subjectTypeId){
        if(subjectTypeId == null)
            return null;
        Optional<SubjectTypeEntity> subjectType = subjectTypeRepository.findById(subjectTypeId);
        if(subjectType.isEmpty())
            throw new EntityNotFoundException(SubjectEntity.class, "SubjectTypeEntity ID", subjectTypeId.toString());
        return subjectType.get();
    }
}
