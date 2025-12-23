package com.cisowski.schoolmanagement.unit.services;

import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.grade.mapper.GradeScaleMapper;
import com.cisowski.schoolmanagement.grade.mapper.GradeValueMapper;
import com.cisowski.schoolmanagement.grade.model.gradeScale.*;
import com.cisowski.schoolmanagement.grade.repository.GradeRepository;
import com.cisowski.schoolmanagement.grade.repository.GradeScaleRepository;
import com.cisowski.schoolmanagement.grade.repository.GradeValueRepository;
import com.cisowski.schoolmanagement.grade.service.GradeScaleServiceImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GradeScaleServiceImplTest {

    @Mock private GradeScaleRepository gradeScaleRepository;
    @Mock private GradeValueRepository gradeValueRepository;
    @Mock private GradeRepository gradeRepository;
    @Mock private GradeScaleMapper gradeScaleMapper;
    @Mock private GradeValueMapper gradeValueMapper;

    @InjectMocks
    private GradeScaleServiceImpl gradeScaleService;

    private GradeScaleRequest gradeScaleRequestInstancio;
    private GradeScaleEntity gradeScaleEntityInstancio;

    @BeforeEach
    void setUp() {
        gradeScaleRequestInstancio = Instancio.create(GradeScaleRequest.class);
        gradeScaleEntityInstancio = Instancio.create(GradeScaleEntity.class);
    }

    @Test
    void addGradeScale_shouldCreateAndReturnResponse() {
        GradeScaleRequest request = gradeScaleRequestInstancio;
        GradeScaleEntity entity = gradeScaleEntityInstancio;
        GradeScaleResponse response = Instancio.create(GradeScaleResponse.class);
        List<GradeValueEntity> gradeValues = Instancio.ofList(GradeValueEntity.class).size(2).create();

        when(gradeScaleMapper.toEntity(request)).thenReturn(entity);
        when(gradeValueMapper.toEntityList(request.getGradeValues())).thenReturn(gradeValues);
        when(gradeScaleRepository.save(any())).thenReturn(entity);
        when(gradeValueRepository.saveAll(anyList())).thenReturn(gradeValues);
        when(gradeScaleMapper.toResponse(entity)).thenReturn(response);
        when(gradeScaleRepository.findByIsActive(true)).thenReturn(Optional.of(mock(GradeScaleEntity.class)));

        GradeScaleResponse result = gradeScaleService.addGradeScale(request);

        assertEquals(response, result);
        verify(gradeScaleRepository).save(entity);
        verify(gradeValueRepository).saveAll(gradeValues);
    }

    @Test
    void addGradeScale_shouldThrow_whenNoActiveScaleAndNewIsNotActive() {
        GradeScaleRequest request = Instancio.of(GradeScaleRequest.class)
                .set(field("isActive"), false)
                .create();

        when(gradeScaleRepository.findByIsActive(true)).thenReturn(Optional.empty());

        assertThrows(SpecificationBrokenException.class, () -> gradeScaleService.addGradeScale(request));
    }

    @Test
    void patchGradeScale_shouldUpdateScaleAndReturnResponse() {
        Long id = 1L;
        PatchGradeScaleRequest request = Instancio.create(PatchGradeScaleRequest.class);
        GradeScaleEntity original = gradeScaleEntityInstancio;
        GradeScaleEntity mapped = gradeScaleEntityInstancio;
        GradeScaleResponse expectedResponse = Instancio.create(GradeScaleResponse.class);

        when(gradeScaleRepository.findById(id)).thenReturn(Optional.of(original));
        when(gradeScaleMapper.toEntity(request)).thenReturn(mapped);
        when(gradeScaleRepository.save(any())).thenReturn(original);
        when(gradeScaleMapper.toResponse(original)).thenReturn(expectedResponse);
        when(gradeScaleRepository.findByIsActive(true)).thenReturn(Optional.of(mock(GradeScaleEntity.class)));

        GradeScaleResponse response = gradeScaleService.patchGradeScale(request, id);

        assertEquals(expectedResponse, response);
        verify(gradeScaleMapper).patchEntity(original, mapped);
    }

    @Test
    void deleteGradeValue_shouldDelete_whenValueNotUsed() {
        Long scaleId = 1L;
        Long valueId = 2L;
        GradeScaleEntity scale = gradeScaleEntityInstancio;
        GradeValueEntity value = Instancio.create(GradeValueEntity.class);

        when(gradeScaleRepository.findById(scaleId)).thenReturn(Optional.of(scale));
        when(gradeValueRepository.findByGradeScaleAndId(scale, valueId)).thenReturn(Optional.of(value));
        when(gradeRepository.existsByGradeValue(value)).thenReturn(false);

        gradeScaleService.deleteGradeValue(scaleId, valueId);

        verify(gradeValueRepository).delete(value);
    }

    @Test
    void deleteGradeValue_shouldHide_whenValueUsed() {
        Long scaleId = 1L;
        Long valueId = 2L;
        GradeScaleEntity scale = gradeScaleEntityInstancio;
        GradeValueEntity value = Instancio.create(GradeValueEntity.class);

        when(gradeScaleRepository.findById(scaleId)).thenReturn(Optional.of(scale));
        when(gradeValueRepository.findByGradeScaleAndId(scale, valueId)).thenReturn(Optional.of(value));
        when(gradeRepository.existsByGradeValue(value)).thenReturn(true);

        gradeScaleService.deleteGradeValue(scaleId, valueId);

        assertTrue(value.getIsHide());
        verify(gradeValueRepository).save(value);
    }

    @Test
    void addGradeValue_shouldAddValueAndReturnResponse() {
        Long scaleId = 1L;
        GradeValueDto dto = Instancio.create(GradeValueDto.class);
        GradeScaleEntity scale = gradeScaleEntityInstancio;
        GradeValueEntity entity = Instancio.create(GradeValueEntity.class);
        GradeValueResponse response = Instancio.create(GradeValueResponse.class);

        when(gradeScaleRepository.findById(scaleId)).thenReturn(Optional.of(scale));
        when(gradeValueMapper.toEntity(dto, scale)).thenReturn(entity);
        when(gradeValueRepository.save(entity)).thenReturn(entity);
        when(gradeValueMapper.toResponse(entity)).thenReturn(response);

        GradeValueResponse result = gradeScaleService.addGradeValue(dto, scaleId);

        assertEquals(response, result);
    }

    @Test
    void deleteGradeScale_shouldThrow_whenOnlyActiveScale() {
        GradeScaleEntity active = gradeScaleEntityInstancio;
        active.setIsActive(true);

        when(gradeScaleRepository.findById(active.getId())).thenReturn(Optional.of(active));
        when(gradeScaleRepository.findByIsActive(true)).thenReturn(Optional.of(active));
        when(gradeScaleRepository.findFirstByIdNotOrderByCreatedAtDesc(active.getId())).thenReturn(Optional.empty());

        assertThrows(SpecificationBrokenException.class, () -> gradeScaleService.deleteGradeScale(active.getId()));
    }

    @Test
    void getGradeScaleById_shouldReturnMappedResponse() {
        Long id = 1L;
        GradeScaleEntity entity = gradeScaleEntityInstancio;
        GradeScaleResponse response = Instancio.create(GradeScaleResponse.class);

        when(gradeScaleRepository.findById(id)).thenReturn(Optional.of(entity));
        when(gradeScaleMapper.toResponse(entity)).thenReturn(response);

        GradeScaleResponse result = gradeScaleService.getGradeScaleById(id);
        assertEquals(response, result);
    }

    @Test
    void getAllGradeScales_shouldReturnMappedList() {
        List<GradeScaleEntity> list = Instancio.ofList(GradeScaleEntity.class).size(3).create();
        List<GradeScaleSummaryResponse> responseList = Instancio.ofList(GradeScaleSummaryResponse.class).size(3).create();

        when(gradeScaleRepository.findAll()).thenReturn(list);
        when(gradeScaleMapper.toResponseList(list)).thenReturn(responseList);

        List<GradeScaleSummaryResponse> result = gradeScaleService.getAllGradeScales();
        assertEquals(responseList, result);
    }

    @Test
    void getActiveGradeScale_shouldThrow_whenNoActive() {
        when(gradeScaleRepository.findByIsActive(true)).thenReturn(Optional.empty());
        assertThrows(SpecificationBrokenException.class, () -> gradeScaleService.getActiveGradeScale());
    }

}
