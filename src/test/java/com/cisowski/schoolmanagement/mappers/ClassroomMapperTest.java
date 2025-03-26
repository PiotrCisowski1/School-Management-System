package com.cisowski.schoolmanagement.mappers;

import com.cisowski.schoolmanagement.classroom.mapper.ClassroomMapper;
import com.cisowski.schoolmanagement.classroom.model.ClassroomDetailedResponse;
import com.cisowski.schoolmanagement.classroom.model.ClassroomEntity;
import com.cisowski.schoolmanagement.classroom.model.ClassroomRequest;
import com.cisowski.schoolmanagement.classroom.model.ClassroomSummaryResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ClassroomMapperTest {

    ClassroomMapper classroomMapper;

    @BeforeEach
    public void setUp(){
        classroomMapper = Mappers.getMapper(ClassroomMapper.class);
    }

    @Test
    public void toClassroomEntity(){
        ClassroomRequest request = Instancio.create(ClassroomRequest.class);
        ClassroomEntity result = classroomMapper.toClassroomEntity(request);
        assertNotNull(result);
        assertEquals(request.getName(), result.getName());
        assertEquals(request.getCapacity(), result.getCapacity());
        assertEquals(request.getNotes(), result.getNotes());
    }

    @Test
    public void toClassroomDetailedResponse(){
        ClassroomEntity entity = Instancio.create(ClassroomEntity.class);
        ClassroomDetailedResponse result = classroomMapper.toClassroomDetailedResponse(entity);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getName(), result.getName());
        assertEquals(entity.getCapacity(), result.getCapacity());
        assertEquals(entity.getNotes(), result.getNotes());
    }

    @Test
    public void toClassroomSummaryResponse(){
        ClassroomEntity entity = Instancio.create(ClassroomEntity.class);
        ClassroomSummaryResponse result = classroomMapper.toSummaryResponse(entity);
        assertNotNull(result);
        assertEquals(entity.getCapacity(), result.getCapacity());
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getName(), result.getName());
        assertEquals(entity.getClassroomEquipments().size(), result.getEquipmentCount());
    }
}
