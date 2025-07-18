package com.cisowski.schoolmanagement.grade.service;

import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.grade.mapper.GradeScaleMapper;
import com.cisowski.schoolmanagement.grade.mapper.GradeValueMapper;
import com.cisowski.schoolmanagement.grade.model.gradeScale.*;
import com.cisowski.schoolmanagement.grade.repository.GradeRepository;
import com.cisowski.schoolmanagement.grade.repository.GradeScaleRepository;
import com.cisowski.schoolmanagement.grade.repository.GradeValueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GradeScaleServiceImpl implements GradeScaleService {

    private final GradeScaleRepository gradeScaleRepository;
    private final GradeValueRepository gradeValueRepository;
    private final GradeScaleMapper gradeScaleMapper;
    private final GradeValueMapper gradeValueMapper;
    private final GradeRepository gradeRepository;


    @Override
    @Transactional
    public GradeScaleResponse addGradeScale(GradeScaleRequest request) {
        DbLogger.info("Creating GradeScale for request: " + request.toString());
        manageScaleActivityChange(request.getIsActive(), null);

        GradeScaleEntity gradeScaleRequestEntity = gradeScaleMapper.toEntity(request);
        List<GradeValueEntity> gradeValueEntities = gradeValueMapper.toEntityList(request.getGradeValues());
        GradeScaleEntity saved = gradeScaleRepository.save(gradeScaleRequestEntity);

        gradeValueEntities.forEach(gv -> gv.setGradeScale(saved));
        List<GradeValueEntity> savedGradeValues = gradeValueRepository.saveAll(gradeValueEntities);
        saved.setGradeValues(savedGradeValues);

        DbLogger.info("GradeScale saved successfully: " + saved.toString());
        return gradeScaleMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public GradeScaleResponse patchGradeScale(PatchGradeScaleRequest request, Long gradeScaleId) {
        DbLogger.info(String.format("Updating GradeScale with ID %s for request: %s", gradeScaleId, request.toString()));
        GradeScaleEntity existingGradeScale = fetchGradeScale(gradeScaleId);
        manageScaleActivityChange(request.getIsActive(), gradeScaleId);

        GradeScaleEntity requestEntity = gradeScaleMapper.toEntity(request);
        gradeScaleMapper.patchEntity(existingGradeScale, requestEntity);

        GradeScaleEntity saved = gradeScaleRepository.save(existingGradeScale);
        DbLogger.info("GradeScale updated successfully: " + saved.toString());
        return gradeScaleMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteGradeValue(Long gradeScaleId, Long gradeValueId) {
        DbLogger.info("Deleting GradeValue with ID: " + gradeValueId);
        GradeScaleEntity gradeScale = fetchGradeScale(gradeScaleId);
        GradeValueEntity gradeValue = fetchGradeValueEntity(gradeValueId, gradeScale);

        if(isGradeValueUsed(gradeValue)) {
            DbLogger.info(String.format(
                    "Hiding GradeValue with ID: %s instead of deleting, because it is still used in historical records",
                    gradeValue.getId()));
            gradeValue.setIsHide(true);
            gradeValueRepository.save(gradeValue);
        }
        else {
            gradeValueRepository.delete(gradeValue);
            DbLogger.info(String.format("GradeValue with ID %s was removed successfully", gradeValue.getId()));
        }
    }

    @Override
    @Transactional
    public GradeValueResponse addGradeValue(GradeValueDto gradeValueDto, Long gradeScaleId) {
        DbLogger.info(String.format("For GradeScale with ID: %s adding GradeValue with: %s", gradeScaleId, gradeValueDto.toString()));
        GradeScaleEntity gradeScale = fetchGradeScale(gradeScaleId);
        GradeValueEntity requestEntity = gradeValueMapper.toEntity(gradeValueDto, gradeScale);
        GradeValueEntity saved = gradeValueRepository.save(requestEntity);
        DbLogger.info("GradeValue successfully saved: " + saved.toString());
        return gradeValueMapper.toResponse(saved);
    }

    @Override
    public GradeScaleEntity fetchGradeScale(Long gradeScaleId) {
        DbLogger.info("Searching for GradeScale with ID" + gradeScaleId);
        Optional<GradeScaleEntity> existingGradeScale = gradeScaleRepository.findById(gradeScaleId);
        if(existingGradeScale.isEmpty())
            throw new EntityNotFoundException(GradeScaleEntity.class, "ID", gradeScaleId.toString());
        DbLogger.info(String.format("Found GradeScale with ID: %s, entity: %s", gradeScaleId, existingGradeScale.get().toString()));
        return existingGradeScale.get();
    }

    @Override
    @Transactional
    public void deleteGradeScale(Long gradeScaleId) {
        DbLogger.info("Deleting GradeScale with ID: " + gradeScaleId);
        GradeScaleEntity gradeScale = fetchGradeScale(gradeScaleId);
        checkScaleActivityBeforeDelete(gradeScale);
        if(isGradeScaleUsed(gradeScale)) {
            DbLogger.info(String.format(
                    "Hiding GradeScale with ID: %s instead of deleting, because it is still used in historical records",
                    gradeScale.getId()));
            gradeScale.setIsHide(true);
            gradeScaleRepository.save(gradeScale);
        }
        else
            gradeScaleRepository.delete(gradeScale);
        DbLogger.info(String.format("GradeScale with ID: %s, was successfully removed", gradeScaleId));
    }

    @Override
    public GradeScaleResponse getGradeScaleById(Long gradeScaleId) {
        GradeScaleEntity entity = fetchGradeScale(gradeScaleId);
        DbLogger.info(String.format("Found GradeScale with ID %s: %s", gradeScaleId, entity.toString()));
        return gradeScaleMapper.toResponse(entity);
    }

    @Override
    public List<GradeScaleSummaryResponse> getAllGradeScales() {
        DbLogger.info("Searching for all GradeScale entities");
        List<GradeScaleEntity> gradeScales = gradeScaleRepository.findAll();
        DbLogger.info(String.format("Found %s GradeScales", gradeScales.size()));
        return gradeScaleMapper.toResponseList(gradeScales);
    }

    @Override
    public GradeScaleResponse getActiveGradeScale() {
        DbLogger.info("Searching for current active GradeScale");
        Optional<GradeScaleEntity> activeGradeScale = gradeScaleRepository.findByIsActive(true);
        if(activeGradeScale.isEmpty())
            throw new SpecificationBrokenException("No active GradeScale in the system");
        return gradeScaleMapper.toResponse(activeGradeScale.get());
    }

    @Override
    public GradeValueEntity fetchGradeValue(Long gradeValueId) {
        DbLogger.info("Searching for GradeValue with ID: " + gradeValueId);
        Optional<GradeValueEntity> gradeValue = gradeValueRepository.findById(gradeValueId);
        if(gradeValue.isEmpty())
            throw new EntityNotFoundException(GradeValueEntity.class, "ID", gradeValueId.toString());
        return gradeValue.get();
    }

    private void changeGradeScaleActivity(GradeScaleEntity gradeScale, boolean targetActivity){
        if(gradeScale == null)
            return;
        DbLogger.info(String.format("Changing grade scale (ID: %s) activity for: %s", gradeScale.getId(), targetActivity));
        gradeScale.setIsActive(targetActivity);
        gradeScaleRepository.save(gradeScale);
    }

    private void manageScaleActivityChange(boolean targetGradeScaleActivity, Long gradeScaleId) {
        Optional<GradeScaleEntity> activeGradeScale = gradeScaleRepository.findByIsActive(true);
        if (!targetGradeScaleActivity && activeGradeScale.isEmpty())
            throw new SpecificationBrokenException("Given grade scale must be set to active, because there is no active scale at the moment");
        else {
            if(activeGradeScale.isPresent() && !activeGradeScale.get().getId().equals(gradeScaleId))
                changeGradeScaleActivity(activeGradeScale.get(), !targetGradeScaleActivity);
            else
                throw new SpecificationBrokenException("Given grade scale must be set to active, because there is no active scale at the moment");
        }
    }

    private void checkScaleActivityBeforeDelete(GradeScaleEntity gradeScale){
        DbLogger.info(String.format("Checking if removal of GradeScale with ID %s is available", gradeScale.getId()));
        GradeScaleEntity activeGradeScale = gradeScaleRepository.findByIsActive(true).get();
        if(activeGradeScale.getId().equals(gradeScale.getId())){
            Optional<GradeScaleEntity> entityToActivate = gradeScaleRepository.findFirstByIdNotOrderByCreatedAtDesc(gradeScale.getId());
            if(entityToActivate.isEmpty())
                throw new SpecificationBrokenException(String.format(
                        "Grade scale with ID: %s cannot be removed, because it is the only grade scale in the system",
                        gradeScale.getId()
                ));
            changeGradeScaleActivity(entityToActivate.get(), true);
            changeGradeScaleActivity(gradeScale, false);
        }
    }

    private boolean isGradeValueUsed(GradeValueEntity gradeValue){
        DbLogger.info(String.format("Checking if GradeValue with ID %s is used in Grade records", gradeValue.getId()));
        return gradeRepository.existsByGradeValue(gradeValue);
    }

    private GradeValueEntity fetchGradeValueEntity(Long gradeValueId, GradeScaleEntity gradeScale){
        DbLogger.info(String.format("Searching for GradeValue with ID: %s and GradeScale with ID: %s", gradeValueId, gradeScale.getId()));
        Optional<GradeValueEntity> existing = gradeValueRepository.findByGradeScaleAndId(gradeScale, gradeValueId);
        if(existing.isEmpty())
            throw new EntityNotFoundException(GradeValueEntity.class, "ID", gradeValueId.toString());
        return existing.get();
    }

    private boolean isGradeScaleUsed(GradeScaleEntity gradeScale){
        DbLogger.info(String.format("Checking if GradeScale with ID: %s is used in Grade records", gradeScale.getId()));
        return gradeRepository.existsByGradeValueIn(gradeScale.getGradeValues());
    }
}
