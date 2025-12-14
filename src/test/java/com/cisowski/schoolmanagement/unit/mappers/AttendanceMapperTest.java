package com.cisowski.schoolmanagement.unit.mappers;

import com.cisowski.schoolmanagement.timetable.attendance.mapper.AttendanceMapperImpl;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceDetailedResponse;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceEntity;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceStatus;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceSummaryResponse;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.mapper.ScheduleOccurrenceMapper;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceSummaryResponse;
import com.cisowski.schoolmanagement.users.common.mapper.BaseUserMapper;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.common.model.UserSummaryResponse;
import com.cisowski.schoolmanagement.users.student.mapper.StudentMapper;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentSummaryResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttendanceMapperTest {

    @Mock
    private StudentMapper studentMapper;
    @Mock
    private ScheduleOccurrenceMapper scheduleOccurrenceMapper;
    @Mock
    private BaseUserMapper baseUserMapper;

    @InjectMocks
    private AttendanceMapperImpl attendanceMapper;

    private StudentEntity studentEntity;
    private StudentSummaryResponse studentSummaryResponse;
    private ScheduleOccurrenceEntity occurrenceEntity;
    private ScheduleOccurrenceSummaryResponse occurrenceSummaryResponse;
    private UserEntity lastModifiedByUser;
    private UserSummaryResponse lastModifiedByResponse;

    @BeforeEach
    void setup() {
        studentEntity = Instancio.create(StudentEntity.class);
        studentSummaryResponse = Instancio.create(StudentSummaryResponse.class);

        occurrenceEntity = Instancio.create(ScheduleOccurrenceEntity.class);
        occurrenceSummaryResponse = Instancio.create(ScheduleOccurrenceSummaryResponse.class);

        lastModifiedByUser = Instancio.of(UserEntity.class).create();
        lastModifiedByResponse = Instancio.of(UserSummaryResponse.class).create();
    }

    @Nested
    @DisplayName("Tests for toSummaryResponse")
    class ToSummaryResponseTests {

        private AttendanceEntity attendanceEntity;

        @BeforeEach
        void innerSetup() {
            attendanceEntity = Instancio.of(AttendanceEntity.class)
                    .set(field(AttendanceEntity::getId), 1L)
                    .set(field(AttendanceEntity::getAttendanceStatus), AttendanceStatus.PRESENT)
                    .set(field(AttendanceEntity::getStudent), studentEntity)
                    .set(field(AttendanceEntity::getOccurrence), occurrenceEntity)
                    .create();
        }

        @Test
        void toSummaryResponse_ValidEntity_ReturnsValidResponse() {
            when(studentMapper.toSummaryResponse(any(StudentEntity.class))).thenReturn(studentSummaryResponse);
            when(scheduleOccurrenceMapper.toSummaryResponse(any(ScheduleOccurrenceEntity.class))).thenReturn(occurrenceSummaryResponse);

            AttendanceSummaryResponse response = attendanceMapper.toSummaryResponse(attendanceEntity);

            assertNotNull(response);
            assertEquals(attendanceEntity.getId(), response.getId());
            assertEquals(attendanceEntity.getAttendanceStatus(), response.getStatus());

            assertEquals(studentSummaryResponse, response.getStudent());
            assertEquals(occurrenceSummaryResponse, response.getScheduleOccurrence());

            verify(studentMapper, times(1)).toSummaryResponse(studentEntity);
            verify(scheduleOccurrenceMapper, times(1)).toSummaryResponse(occurrenceEntity);
        }

        @Test
        void toSummaryResponse_NullEntity_ReturnsNull() {
            assertNull(attendanceMapper.toSummaryResponse(null));

            verify(studentMapper, never()).toSummaryResponse(any());
            verify(scheduleOccurrenceMapper, never()).toSummaryResponse(any());
        }
    }

    @Nested
    @DisplayName("Tests for toSummaryResponseList")
    class ToSummaryResponseListTests {

        @Test
        void toSummaryResponseList_ValidList_ReturnsValidResponseList() {
            List<AttendanceEntity> entities = Instancio.ofList(AttendanceEntity.class).size(3)
                    .set(field(AttendanceEntity::getStudent), studentEntity)
                    .set(field(AttendanceEntity::getOccurrence), occurrenceEntity)
                    .create();

            when(studentMapper.toSummaryResponse(any(StudentEntity.class))).thenReturn(studentSummaryResponse);
            when(scheduleOccurrenceMapper.toSummaryResponse(any(ScheduleOccurrenceEntity.class))).thenReturn(occurrenceSummaryResponse);

            List<AttendanceSummaryResponse> responses = attendanceMapper.toSummaryResponseList(entities);

            assertNotNull(responses);
            assertEquals(entities.size(), responses.size());

            for (AttendanceSummaryResponse response : responses) {
                assertEquals(studentSummaryResponse, response.getStudent());
                assertEquals(occurrenceSummaryResponse, response.getScheduleOccurrence());
            }

            verify(studentMapper, times(entities.size())).toSummaryResponse(studentEntity);
            verify(scheduleOccurrenceMapper, times(entities.size())).toSummaryResponse(occurrenceEntity);
        }

        @Test
        void toSummaryResponseList_EmptyList_ReturnsEmptyList() {
            List<AttendanceSummaryResponse> responses = attendanceMapper.toSummaryResponseList(Collections.emptyList());

            assertNotNull(responses);
            assertTrue(responses.isEmpty());

            verify(studentMapper, never()).toSummaryResponse(any());
            verify(scheduleOccurrenceMapper, never()).toSummaryResponse(any());
        }

        @Test
        void toSummaryResponseList_NullList_ReturnsNull() {
            assertNull(attendanceMapper.toSummaryResponseList(null));

            verify(studentMapper, never()).toSummaryResponse(any());
            verify(scheduleOccurrenceMapper, never()).toSummaryResponse(any());
        }
    }

    @Nested
    @DisplayName("Tests for toDetailedResponse")
    class ToDetailedResponseTests {

        private AttendanceEntity attendanceEntity;

        @BeforeEach
        void innerSetup() {
            attendanceEntity = Instancio.of(AttendanceEntity.class)
                    .set(field(AttendanceEntity::getId), 10L)
                    .set(field(AttendanceEntity::getAttendanceStatus), AttendanceStatus.ABSENT)
                    .set(field(AttendanceEntity::getStudent), studentEntity)
                    .set(field(AttendanceEntity::getOccurrence), occurrenceEntity)
                    .set(field(AttendanceEntity::getLastModifiedBy), lastModifiedByUser)
                    .set(field(AttendanceEntity::getCreatedAt), LocalDateTime.now().minusDays(1))
                    .set(field(AttendanceEntity::getLastModifiedAt), LocalDateTime.now())
                    .create();
        }

        @Test
        void toDetailedResponse_ValidEntity_ReturnsValidResponse() {
            when(studentMapper.toSummaryResponse(any(StudentEntity.class))).thenReturn(studentSummaryResponse);
            when(scheduleOccurrenceMapper.toSummaryResponse(any(ScheduleOccurrenceEntity.class))).thenReturn(occurrenceSummaryResponse);
            when(baseUserMapper.toSummaryResponse(any(UserEntity.class))).thenReturn(lastModifiedByResponse);

            AttendanceDetailedResponse response = attendanceMapper.toDetailedResponse(attendanceEntity);

            assertNotNull(response);
            assertEquals(attendanceEntity.getId(), response.getId());
            assertEquals(attendanceEntity.getAttendanceStatus(), response.getStatus());
            assertEquals(attendanceEntity.getCreatedAt(), response.getCreatedAt());
            assertEquals(attendanceEntity.getLastModifiedAt(), response.getLastModifiedAt());

            assertEquals(studentSummaryResponse, response.getStudent());
            assertEquals(occurrenceSummaryResponse, response.getOccurrence());
            assertEquals(lastModifiedByResponse, response.getLastModifiedBy());

            verify(studentMapper, times(1)).toSummaryResponse(studentEntity);
            verify(scheduleOccurrenceMapper, times(1)).toSummaryResponse(occurrenceEntity);
            verify(baseUserMapper, times(1)).toSummaryResponse(lastModifiedByUser);
        }

        @Test
        void toDetailedResponse_NullEntity_ReturnsNull() {
            assertNull(attendanceMapper.toDetailedResponse(null));

            verify(studentMapper, never()).toSummaryResponse(any());
            verify(scheduleOccurrenceMapper, never()).toSummaryResponse(any());
            verify(baseUserMapper, never()).toSummaryResponse(any());
        }
    }
}
