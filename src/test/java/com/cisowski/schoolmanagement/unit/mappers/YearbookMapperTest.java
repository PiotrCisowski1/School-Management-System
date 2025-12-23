package com.cisowski.schoolmanagement.unit.mappers;

import com.cisowski.schoolmanagement.users.student.mapper.StudentMapper;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import com.cisowski.schoolmanagement.yearbook.mapper.YearbookMapper;
import com.cisowski.schoolmanagement.yearbook.model.*;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class YearbookMapperTest {
    private YearbookMapper yearbookMapper;


    @BeforeEach
    public void setUp(){
        yearbookMapper = Mappers.getMapper(YearbookMapper.class);
        TeacherMapper teacherMapper = Mappers.getMapper(TeacherMapper.class);
        StudentMapper studentMapper = Mappers.getMapper(StudentMapper.class);
        ReflectionTestUtils.setField(yearbookMapper, "teacherMapper", teacherMapper);
        ReflectionTestUtils.setField(yearbookMapper, "studentMapper", studentMapper);
    }

    @Test
    @DisplayName("AddYearbookRequest to YearbookEntity")
    public void addYearbookRequestToYearbookEntityTest(){
        AddYearbookRequest request = Instancio.create(AddYearbookRequest.class);
        YearbookEntity expected = yearbookMapper.toYearbookEntity(request);
        assertEquals(expected.getSymbol(), request.getSymbol());
        assertEquals(expected.getStartingYear(), request.getStartingYear());
        assertEquals(expected.getGraduationYear(), request.getGraduationYear());
    }

    @Test
    @DisplayName("PatchYearbookRequest to YearbookEntity")
    public void patchYearbookRequestToYearbookEntityTest(){
        PatchYearbookRequest request = Instancio.create(PatchYearbookRequest.class);
        YearbookEntity entity = yearbookMapper.toYearbookEntity(request);
        assertEquals(request.getSymbol(), entity.getSymbol());
        assertEquals(request.getStartingYear(), entity.getStartingYear());
        assertEquals(request.getTargetGraduationYear(), entity.getGraduationYear());
    }

    @Test
    @DisplayName("YearbookEntity to YearbookDetailedResponse")
    public void yearbookEntityToDetailedResponse(){
        YearbookEntity request = Instancio.create(YearbookEntity.class);
        YearbookDetailedResponse actual = yearbookMapper.toDetailedResponse(request);
        assertEquals(request.getId(), actual.getId());
        assertEquals(request.getHeadTeacher().getId(), actual.getHeadTeacher().getId());
        assertEquals(request.getStartingYear(), actual.getStartingYear());
        assertEquals(request.getGraduationYear(), actual.getGraduationYear());
        assertEquals(request.getSymbol(), actual.getSymbol());
        assertFalse(actual.getStudentsInYearbook().isEmpty());
        assertFalse(actual.getMainCourseSubjects().isEmpty());
    }

    @Test
    @DisplayName("YearbookEntity to YearbookSummaryResponse")
    public void yearbookEntityToYearbookSummary(){
        YearbookEntity entity = Instancio.create(YearbookEntity.class);
        YearbookSummaryResponse actual = yearbookMapper.toYearbookSummaryResponse(entity);
        assertNotNull(actual);
        assertEquals(entity.getId(), actual.getId());
        assertEquals(entity.getSymbol(), actual.getSymbol());
        assertEquals(entity.getStartingYear(), actual.getStartingYear());
        assertEquals(entity.getGraduationYear(), actual.getGraduationYear());
        assertEquals(entity.getHeadTeacher().getId(), actual.getHeadTeacher().getId());
    }

    @Test
    @DisplayName("Patch two YearbookEntities")
    public void patchEntities(){
        YearbookEntity requestEntity = Instancio.create(YearbookEntity.class);
        YearbookEntity targetEntity = Instancio.create(YearbookEntity.class);
        yearbookMapper.patchYearbook(targetEntity, requestEntity);
        assertEquals(requestEntity.getId(), targetEntity.getId());
        assertEquals(requestEntity.getHeadTeacher(), targetEntity.getHeadTeacher());
        assertEquals(requestEntity.getSymbol(), targetEntity.getSymbol());
        assertEquals(requestEntity.getStartingYear(), targetEntity.getStartingYear());
        assertEquals(requestEntity.getGraduationYear(), targetEntity.getGraduationYear());
        assertIterableEquals(requestEntity.getStudentsInYearbook(), targetEntity.getStudentsInYearbook());
    }
}
