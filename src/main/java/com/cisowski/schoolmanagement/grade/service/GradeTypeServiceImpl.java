package com.cisowski.schoolmanagement.grade.service;

import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.grade.mapper.GradeTypeMapper;
import com.cisowski.schoolmanagement.grade.model.gradeType.AddGradeTypeRequest;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeResponse;
import com.cisowski.schoolmanagement.grade.model.gradeType.PatchGradeTypeRequest;
import com.cisowski.schoolmanagement.grade.repository.GradeRepository;
import com.cisowski.schoolmanagement.grade.repository.GradeTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GradeTypeServiceImpl implements GradeTypeService {

    private final GradeTypeRepository repository;
    private final GradeTypeMapper mapper;
    private final GradeRepository gradeRepository;

    @Override
    @Transactional
    public GradeTypeResponse addGradeType(AddGradeTypeRequest request) {
        DbLogger.info("Creating GradeType for request: " + request.toString());
        checkGradeScopeUnique(request.getGradeScope());
        GradeTypeEntity entity = mapper.toEntity(request);
        GradeTypeEntity saved = repository.save(entity);
        DbLogger.info("GradeType was successfuly saved: " + saved.toString());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public GradeTypeResponse patchGradeType(PatchGradeTypeRequest request, Long gradeTypeId) {
        DbLogger.info(String.format("Updating GradeType with ID %s for request: %s", gradeTypeId, request.toString()));
        checkGradeScopeUnique(request.getGradeScope());
        GradeTypeEntity existingEntity = fetchGradeType(gradeTypeId);
        GradeTypeEntity patchingEntity = mapper.toEntity(request);
        mapper.patchEntity(patchingEntity, existingEntity);
        GradeTypeEntity updatedEntity = repository.save(existingEntity);
        DbLogger.info(String.format("GradeType with ID %s, was successfully updated: %s", gradeTypeId, updatedEntity.toString()));
        return mapper.toResponse(updatedEntity);
    }

    private void checkGradeScopeUnique(String gradeScope) {
        if(StringUtils.isEmpty(gradeScope))
            return;
        Optional<GradeTypeEntity> gradeType = repository.findByGradeScope(gradeScope);
        if(gradeType.isPresent())
            throw new SpecificationBrokenException(String.format(
                    "GradeType with name %s, already exists with ID: %s",
                    gradeScope,
                    gradeType.get().getId()));
    }

    @Override
    @Transactional
    public void deleteGradeType(Long gradeTypeId) {
        DbLogger.info("Deleting GradeType with ID: " + gradeTypeId);
        GradeTypeEntity existing = fetchGradeType(gradeTypeId);
        checkGradeTypeIsInUse(existing);
        repository.delete(existing);
        DbLogger.info(String.format("GradeType with ID %s was successfully removed", gradeTypeId));
    }

    @Override
    public GradeTypeResponse getGradeType(Long gradeTypeId) {
        DbLogger.info("Searching for GradeType with ID: " + gradeTypeId);
        GradeTypeEntity entity = fetchGradeType(gradeTypeId);
        return mapper.toResponse(entity);
    }

    @Override
    public Collection<GradeTypeResponse> getGradeTypes() {
        DbLogger.info("Searching for all GradeTypes");
        List<GradeTypeEntity> gradeTypes = repository.findAll();
        DbLogger.info(String.format("Found %s GradeTypes", gradeTypes.size()));
        return mapper.toResponseList(gradeTypes);
    }

    @Override
    public GradeTypeEntity fetchGradeType(Long gradeTypeId) {
        DbLogger.info("Fetching GradeType with ID: " + gradeTypeId);
        Optional<GradeTypeEntity> existingEntity = repository.findById(gradeTypeId);
        if (existingEntity.isEmpty())
            throw new EntityNotFoundException(GradeTypeEntity.class, "ID", gradeTypeId.toString());
        DbLogger.info(String.format("Found GradeType with ID %s: %s", gradeTypeId, existingEntity.get().toString()));
        return existingEntity.get();
    }

    private void checkGradeTypeIsInUse(GradeTypeEntity entity){
        if(entity != null){
            boolean isUsed = gradeRepository.existsByGradeType(entity);
            if(isUsed)
                throw new SpecificationBrokenException("Cannot make operation on this entity because it is still in use: " + entity.toString());
        }
    }
}
