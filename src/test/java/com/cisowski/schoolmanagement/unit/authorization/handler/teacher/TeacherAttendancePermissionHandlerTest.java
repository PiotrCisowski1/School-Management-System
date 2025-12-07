package com.cisowski.schoolmanagement.unit.authorization.handler.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher.TeacherAttendancePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.service.ScheduleService;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeacherAttendancePermissionHandlerTest {

    @Mock
    private ScheduleService scheduleService;
    @InjectMocks
    private TeacherAttendancePermissionHandler handler;

    private PermissionContext readContext;

    @BeforeEach
    void setUp() {
        readContext = Instancio.of(PermissionContext.class)
                .set(field(PermissionContext::getAction), ResourceActionType.READ)
                .set(field(PermissionContext::getResourceType), ResourceType.ATTENDANCE)
                .create();
    }

    @Test
    void getSupportedUserType_ShouldReturnTeacher() {
        assertEquals(UserType.TEACHER, handler.getSupportedUserType());
    }

    @Test
    void getSupportedResourceType_ShouldReturnAttendance() {
        assertEquals(ResourceType.ATTENDANCE, handler.getSupportedResourceType());
    }

    @Test
    void canAccess_Read_Granted() {
        Integer scheduleId = 100;
        Integer teacherId = 5;
        TeacherEntity accessingTeacher = Instancio.of(TeacherEntity.class)
                .set(field(TeacherEntity::getId), teacherId)
                .create();
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getTeacher), accessingTeacher)
                .create();

        readContext.putAttribute(PermissionContextAttributeKey.TEACHER_ENTITY, accessingTeacher);
        ResourceAccessContext accessContext = new ResourceAccessContext(ResourceType.ATTENDANCE, ResourceActionType.CREATE);
        accessContext.put("scheduleId", scheduleId);

        when(scheduleService.fetchSchedule(scheduleId)).thenReturn(schedule);

        assertTrue(handler.canAccess(readContext, accessContext));
    }

    @Test
    void canAccess_Read_Denied_TeacherMismatch() {
        Integer scheduleId = 101;
        Integer accessingTeacherId = 5;
        Integer otherTeacherId = 6;
        TeacherEntity accessingTeacher = Instancio.of(TeacherEntity.class)
                .set(field(TeacherEntity::getId), accessingTeacherId)
                .create();
        TeacherEntity otherTeacher = Instancio.of(TeacherEntity.class)
                .set(field(TeacherEntity::getId), otherTeacherId)
                .create();
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getTeacher), otherTeacher)
                .create();

        readContext.putAttribute(PermissionContextAttributeKey.TEACHER_ENTITY, accessingTeacher);
        ResourceAccessContext accessContext = new ResourceAccessContext(ResourceType.ATTENDANCE, ResourceActionType.READ);

        assertFalse(handler.canAccess(readContext, accessContext));
    }

    @Test
    void canAccess_Read_Denied_MissingScheduleId() {
        TeacherEntity accessingTeacher = Instancio.create(TeacherEntity.class);

        readContext.putAttribute(PermissionContextAttributeKey.TEACHER_ENTITY, accessingTeacher);
        ResourceAccessContext accessContext = new ResourceAccessContext(ResourceType.ATTENDANCE, ResourceActionType.READ);

        assertFalse(handler.canAccess(readContext, accessContext));
    }


    @Test
    void canAccess_Denied_UnsupportedAction() {
        ResourceAccessContext accessContext = new ResourceAccessContext(ResourceType.ATTENDANCE, ResourceActionType.DELETE);

        assertFalse(handler.canAccess(readContext, accessContext));
    }
}
