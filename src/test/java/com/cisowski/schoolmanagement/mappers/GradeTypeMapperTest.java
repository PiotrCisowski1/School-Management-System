package com.cisowski.schoolmanagement.mappers;

import com.cisowski.schoolmanagement.grade.mapper.GradeTypeMapper;
import com.cisowski.schoolmanagement.grade.model.gradeType.AddGradeTypeRequest;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeResponse;
import com.cisowski.schoolmanagement.grade.model.gradeType.PatchGradeTypeRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@ExtendWith(MockitoExtension.class)
public class GradeTypeMapperTest {

    private final GradeTypeMapper mapper = Mappers.getMapper(GradeTypeMapper.class);

    @Test
    void toEntity_fromAddRequest() {
        AddGradeTypeRequest request = Instancio.create(AddGradeTypeRequest.class);

        GradeTypeEntity entity = mapper.toEntity(request);

        assertThat(entity)
                .isNotNull()
                .satisfies(e -> {
                    assertThat(e.getName()).isEqualTo(request.getName());
                    assertThat(e.getWeight()).isEqualTo(request.getWeight());
                    assertThat(e.getId()).isNull();
                });
    }

    @Test
    void toResponse_fromEntity() {
        GradeTypeEntity entity = Instancio.of(GradeTypeEntity.class)
                .set(field(GradeTypeEntity::getId), BigInteger.valueOf(123))
                .create();

        GradeTypeResponse response = mapper.toResponse(entity);

        assertThat(response)
                .isNotNull()
                .satisfies(r -> {
                    assertThat(r.getId()).isEqualTo(entity.getId());
                    assertThat(r.getName()).isEqualTo(entity.getName());
                    assertThat(r.getWeight()).isEqualTo(entity.getWeight());
                });
    }

    @Test
    void toEntity_fromPatchRequest() {
        PatchGradeTypeRequest request = Instancio.of(PatchGradeTypeRequest.class)
                .generate(field(PatchGradeTypeRequest::getWeight), gen -> gen.doubles().range(0.1, 1.0))
                .create();

        GradeTypeEntity entity = mapper.toEntity(request);

        assertThat(entity)
                .isNotNull()
                .satisfies(e -> {
                    assertThat(e.getName()).isEqualTo(request.getName());
                    assertThat(e.getWeight()).isEqualTo(request.getWeight());
                    assertThat(e.getId()).isNull();
                });
    }

    @Test
    void patchEntity_partialUpdate() {
        GradeTypeEntity existing = Instancio.of(GradeTypeEntity.class)
                .set(field(GradeTypeEntity::getId), BigInteger.valueOf(100))
                .set(field(GradeTypeEntity::getName), "Original Name")
                .set(field(GradeTypeEntity::getWeight), 0.5)
                .create();

        GradeTypeEntity patchData = Instancio.of(GradeTypeEntity.class)
                .set(field(GradeTypeEntity::getName), "Updated Name")
                .ignore(field(GradeTypeEntity::getId))
                .ignore(field(GradeTypeEntity::getWeight))
                .create();

        mapper.patchEntity(patchData, existing);

        assertThat(existing)
                .satisfies(e -> {
                    assertThat(e.getId()).isEqualTo(BigInteger.valueOf(100));
                    assertThat(e.getName()).isEqualTo("Updated Name");
                    assertThat(e.getWeight()).isEqualTo(0.5);
                });
    }

    @Test
    void toResponseList_multipleEntities() {
        List<GradeTypeEntity> entities = Instancio.ofList(GradeTypeEntity.class)
                .size(5)
                .create();

        List<GradeTypeResponse> responses = mapper.toResponseList(entities);

        assertThat(responses)
                .hasSameSizeAs(entities)
                .zipSatisfy(entities, (response, entity) -> {
                    assertThat(response.getId()).isEqualTo(entity.getId());
                    assertThat(response.getName()).isEqualTo(entity.getName());
                    assertThat(response.getWeight()).isEqualTo(entity.getWeight());
                });
    }

}
