package com.cisowski.schoolmanagement.unit.services;

import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.schedule.model.ScheduleStatus;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.schedule.repository.ScheduleVersionRepository;
import com.cisowski.schoolmanagement.schedule.service.ScheduleVersionServiceImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleVersionServiceTest {

    @Mock
    private ScheduleVersionRepository scheduleVersionRepository;
    @InjectMocks
    private ScheduleVersionServiceImpl scheduleVersionService;


    @Test
    public void fetchScheduleVersion_success(){
        Integer scheduleVersionId = 1;
        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);
        scheduleVersion.setStatus(ScheduleStatus.SCHEDULED);
        scheduleVersion.setId(scheduleVersionId);

        when(scheduleVersionRepository.findById(scheduleVersionId)).thenReturn(Optional.of(scheduleVersion));

        ScheduleVersionEntity result = scheduleVersionService.fetchScheduleVersion(scheduleVersionId);

        assertNotNull(result);
        assertEquals(scheduleVersion.getId(), result.getId());
        assertEquals(scheduleVersion.getName(), result.getName());
        assertEquals(scheduleVersion.getCreateDate(), result.getCreateDate());
        assertEquals(scheduleVersion.isActive(), result.isActive());
        verify(scheduleVersionRepository).findById(scheduleVersionId);
    }

    @Test
    public void fetchScheduleVersion_notFound(){
        when(scheduleVersionRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException result = assertThrows(
                EntityNotFoundException.class,
                () -> scheduleVersionService.fetchScheduleVersion(1)
        );
    }

}
