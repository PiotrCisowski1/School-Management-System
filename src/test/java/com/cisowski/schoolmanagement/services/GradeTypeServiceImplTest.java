package com.cisowski.schoolmanagement.services;

import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.grade.mapper.GradeTypeMapper;
import com.cisowski.schoolmanagement.grade.model.gradeType.AddGradeTypeRequest;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeResponse;
import com.cisowski.schoolmanagement.grade.model.gradeType.PatchGradeTypeRequest;
import com.cisowski.schoolmanagement.grade.repository.GradeRepository;
import com.cisowski.schoolmanagement.grade.repository.GradeTypeRepository;
import com.cisowski.schoolmanagement.grade.service.GradeTypeServiceImpl;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GradeTypeServiceImplTest {

    @Mock
    private GradeTypeRepository gradeTypeRepository;

    @Mock
    private GradeTypeMapper gradeTypeMapper;

    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private GradeTypeServiceImpl gradeTypeService;

    private AddGradeTypeRequest createAddRequest() {
        return Instancio.of(AddGradeTypeRequest.class)
                .set(Select.field(AddGradeTypeRequest::getName), "Homework")
                .set(Select.field(AddGradeTypeRequest::getWeight), 0.3)
                .create();
    }

    private GradeTypeEntity createEntity() {
        return Instancio.of(GradeTypeEntity.class)
                .set(Select.field(GradeTypeEntity::getId), BigInteger.valueOf(1))
                .set(Select.field(GradeTypeEntity::getName), "Homework")
                .set(Select.field(GradeTypeEntity::getWeight), 0.3)
                .create();
    }

    @Test
    void addGradeType_Success() {
        AddGradeTypeRequest request = createAddRequest();
        GradeTypeEntity entity = createEntity();
        GradeTypeResponse response = new GradeTypeResponse();

        when(gradeTypeRepository.findByName(request.getName())).thenReturn(Optional.empty());
        when(gradeTypeMapper.toEntity(request)).thenReturn(entity);
        when(gradeTypeRepository.save(entity)).thenReturn(entity);
        when(gradeTypeMapper.toResponse(entity)).thenReturn(response);

        GradeTypeResponse result = gradeTypeService.addGradeType(request);

        assertThat(result).isSameAs(response);
        verify(gradeTypeRepository).findByName(request.getName());
        verify(gradeTypeMapper).toEntity(request);
        verify(gradeTypeRepository).save(entity);
        verify(gradeTypeMapper).toResponse(entity);
    }

    @Test
    void addGradeType_DuplicateName_ThrowsException() {
        AddGradeTypeRequest request = createAddRequest();
        GradeTypeEntity existing = createEntity();

        when(gradeTypeRepository.findByName(request.getName())).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> gradeTypeService.addGradeType(request))
                .isInstanceOf(SpecificationBrokenException.class);

        verify(gradeTypeRepository, never()).save(any());
    }

    @Test
    void patchGradeType_Success() {
        BigInteger id = BigInteger.ONE;
        PatchGradeTypeRequest request = new PatchGradeTypeRequest();
        request.setName("Updated Homework");

        GradeTypeEntity existing = createEntity();
        GradeTypeEntity patchingEntity = new GradeTypeEntity();
        patchingEntity.setName("Updated Homework");
        GradeTypeEntity updatedEntity = createEntity();
        updatedEntity.setName("Updated Homework");
        GradeTypeResponse response = new GradeTypeResponse();

        when(gradeTypeRepository.findById(id)).thenReturn(Optional.of(existing));
        when(gradeTypeMapper.toEntity(request)).thenReturn(patchingEntity);
        when(gradeTypeRepository.save(existing)).thenReturn(updatedEntity);
        when(gradeTypeMapper.toResponse(updatedEntity)).thenReturn(response);

        GradeTypeResponse result = gradeTypeService.patchGradeType(request, id);

        assertThat(result).isSameAs(response);
        verify(gradeTypeMapper).patchEntity(patchingEntity, existing);
        verify(gradeTypeRepository).save(existing);
    }

    @Test
    void patchGradeType_NotFound_ThrowsException() {
        BigInteger id = BigInteger.ONE;
        PatchGradeTypeRequest request = new PatchGradeTypeRequest();

        when(gradeTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeTypeService.patchGradeType(request, id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteGradeType_Success() {
        BigInteger id = BigInteger.ONE;
        GradeTypeEntity entity = createEntity();

        when(gradeTypeRepository.findById(id)).thenReturn(Optional.of(entity));
        when(gradeRepository.findByGradeType(entity)).thenReturn(false);

        gradeTypeService.deleteGradeType(id);

        verify(gradeRepository).findByGradeType(entity);
        verify(gradeTypeRepository).delete(entity);
    }

    @Test
    void deleteGradeType_InUse_ThrowsException() {
        BigInteger id = BigInteger.ONE;
        GradeTypeEntity entity = createEntity();

        when(gradeTypeRepository.findById(id)).thenReturn(Optional.of(entity));
        when(gradeRepository.findByGradeType(entity)).thenReturn(true);

        assertThatThrownBy(() -> gradeTypeService.deleteGradeType(id))
                .isInstanceOf(SpecificationBrokenException.class)
                .hasMessageContaining("Cannot make operation on this entity because it is still in use");

        verify(gradeTypeRepository, never()).delete(any());
    }

    @Test
    void getGradeType_Success() {
        BigInteger id = BigInteger.ONE;
        GradeTypeEntity entity = createEntity();
        GradeTypeResponse response = new GradeTypeResponse();

        when(gradeTypeRepository.findById(id)).thenReturn(Optional.of(entity));
        when(gradeTypeMapper.toResponse(entity)).thenReturn(response);

        GradeTypeResponse result = gradeTypeService.getGradeType(id);

        assertThat(result).isSameAs(response);
    }

    @Test
    void getGradeType_NotFound_ThrowsException() {
        BigInteger id = BigInteger.ONE;
        when(gradeTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeTypeService.getGradeType(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getGradeTypes_Success() {
        GradeTypeEntity entity1 = createEntity();
        GradeTypeEntity entity2 = createEntity();
        entity2.setId(BigInteger.valueOf(2));

        GradeTypeResponse response1 = new GradeTypeResponse();
        GradeTypeResponse response2 = new GradeTypeResponse();

        when(gradeTypeRepository.findAll()).thenReturn(List.of(entity1, entity2));
        when(gradeTypeMapper.toResponseList(List.of(entity1, entity2)))
                .thenReturn(List.of(response1, response2));

        Collection<GradeTypeResponse> result = gradeTypeService.getGradeTypes();

        assertThat(result)
                .hasSize(2)
                .containsExactly(response1, response2);
    }

    @Test
    void getGradeTypes_Empty() {
        when(gradeTypeRepository.findAll()).thenReturn(List.of());

        Collection<GradeTypeResponse> result = gradeTypeService.getGradeTypes();

        assertThat(result).isEmpty();
    }

    @Test
    void fetchGradeType_Success() {
        BigInteger id = BigInteger.ONE;
        GradeTypeEntity entity = createEntity();
        when(gradeTypeRepository.findById(id)).thenReturn(Optional.of(entity));

        GradeTypeEntity result = gradeTypeService.fetchGradeType(id);

        assertThat(result).isSameAs(entity);
    }

    @Test
    void fetchGradeType_NotFound_ThrowsException() {
        BigInteger id = BigInteger.ONE;
        when(gradeTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gradeTypeService.fetchGradeType(id))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
