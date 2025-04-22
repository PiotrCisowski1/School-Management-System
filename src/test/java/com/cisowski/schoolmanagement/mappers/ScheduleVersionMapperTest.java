package com.cisowski.schoolmanagement.mappers;

import com.cisowski.schoolmanagement.schedule.mapper.ScheduleVersionMapper;
import com.cisowski.schoolmanagement.schedule.model.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.schedule.model.ScheduleVersionSummaryResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ScheduleVersionMapperTest {

    ScheduleVersionMapper scheduleVersionMapper;

    @BeforeEach
    void setUp(){
        scheduleVersionMapper = Mappers.getMapper(ScheduleVersionMapper.class);
    }

    @Test
    public void toSummaryResponse(){
        ScheduleVersionEntity entity = Instancio.create(ScheduleVersionEntity.class);

        ScheduleVersionSummaryResponse result = scheduleVersionMapper.toSummaryResponse(entity);

        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getName(), result.getName());
        assertEquals(entity.isActive(), result.isActive());
    }
}
