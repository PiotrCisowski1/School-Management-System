package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceAbsenceByScheduleResponse;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceEntity;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceOverallSummaryResponse;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceStatus;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.OccurrenceStatus;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class AttendanceIntegrationTest extends BaseIntegrationTest {

    @Nested
    class GetAttendanceWithStatusTests {

        ScheduleOccurrenceEntity activeOccurrence;
        ScheduleOccurrenceEntity completedOccurrence;
        int activeAttendanceSize;
        int completedAttendanceSize;

        @BeforeEach
        void setUp() {
            activeOccurrence = dataHelper.createScheduleOccurrence(null, OccurrenceStatus.ONGOING);
            completedOccurrence = dataHelper.createScheduleOccurrence(null, OccurrenceStatus.COMPLETED);

            activeAttendanceSize = 5;
            completedAttendanceSize = 3;
            for(int i = 0; i < activeAttendanceSize; i++) {
                dataHelper.createAttendance(null, null, activeOccurrence);
            }
            for(int i = 0; i < completedAttendanceSize; i++) {
                dataHelper.createAttendance(null, null, completedOccurrence);
            }
        }

        @Test
        void shouldGetOnlyActiveAttendance() {
            String path = String.format("/scheduleOccurrence/%s/active", activeOccurrence.getId());
            given()
                    .headers(fullAdminHeaders)
            .when()
                    .get(path)
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$.size()", equalTo(activeAttendanceSize));
        }

        @ParameterizedTest
        @ValueSource(strings = {"SCHEDULED", "COMPLETED", "CANCELLED"})
        void shouldThrowExWhenTryingToGetActiveAttendanceForUnactiveOccurrence(String stringStatus) {
            OccurrenceStatus status = OccurrenceStatus.valueOf(stringStatus);
            ScheduleOccurrenceEntity completedOccurrence = dataHelper.createScheduleOccurrence(null, status);

            String path = String.format("/scheduleOccurrence/%s/active", completedOccurrence.getId());
            given()
                    .headers(fullAdminHeaders)
            .when()
                    .get(path)
            .then()
                    .statusCode(HttpStatus.NOT_ACCEPTABLE.value());
        }

        @Test
        void shouldGetOnlyCompletedAttendance() {
            String path = String.format("/scheduleOccurrence/%s/complete", completedOccurrence.getId());
            given()
                    .headers(fullAdminHeaders)
            .when()
                    .get(path)
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$.size()", equalTo(completedAttendanceSize));
        }

        @ParameterizedTest
        @ValueSource(strings = {"SCHEDULED", "ONGOING", "CANCELLED"})
        void shouldThrowExWhenTryingToGetCompleteAttendance(String stringStatus) {
            OccurrenceStatus status = OccurrenceStatus.valueOf(stringStatus);
            ScheduleOccurrenceEntity occurrence = dataHelper.createScheduleOccurrence(null, status);

            String path = String.format("/scheduleOccurrence/%s/complete", occurrence.getId());
            given()
                    .headers(fullAdminHeaders)
            .when()
                    .get(path)
            .then()
                    .statusCode(HttpStatus.NOT_ACCEPTABLE.value());
        }
    }

    @Test
    void shouldReturnAttendanceAbsenceStatsForStudent() {
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        StudentEntity student = dataHelper.createStudent(yearbook, null);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion, null, null);
        ScheduleOccurrenceEntity occurrence = dataHelper.createScheduleOccurrence(schedule);
        ScheduleOccurrenceEntity anotherOccurrence = dataHelper.createScheduleOccurrence(schedule);
        int presentAttendance = 5;
        int absentAttendance = 6;
        int unmarkedAttendance = 7;
        for(int i = 0; i < presentAttendance; i++) {
            dataHelper.createAttendance(student, AttendanceStatus.PRESENT, occurrence);
        }
        for(int i = 0; i < absentAttendance; i++) {
            dataHelper.createAttendance(student, AttendanceStatus.ABSENT, anotherOccurrence);
        }
        for(int i = 0; i < unmarkedAttendance; i++) {
            dataHelper.createAttendance(student, AttendanceStatus.UNMARKED, occurrence);
        }
        StudentEntity anotherStudent = dataHelper.createStudent(null, null);
        AttendanceEntity attendance = dataHelper.createAttendance(anotherStudent, AttendanceStatus.ABSENT, anotherOccurrence);
        AttendanceEntity attendance2 = dataHelper.createAttendance(anotherStudent, AttendanceStatus.UNMARKED, occurrence);

        String path = String.format("/student/%s/absence-by-schedule", student.getId());
        List<AttendanceAbsenceByScheduleResponse> response = given()
                .headers(fullAdminHeaders)
        .when()
                .get(path)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList("", AttendanceAbsenceByScheduleResponse.class);

        assertEquals(absentAttendance, response.get(0).getTotalAbsenceCount());
        assertEquals(unmarkedAttendance, response.get(0).getUnmarkedAbsenceCount());
        assertEquals(schedule.getId(), response.get(0).getScheduleId());
        assertEquals(schedule.getSubject().getName(), response.get(0).getScheduleName());
    }

    @Test
    void shouldReturnSummaryOfAllAttendances() {
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        StudentEntity student = dataHelper.createStudent(yearbook, null);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion, null, null);
        ScheduleOccurrenceEntity occurrence = dataHelper.createScheduleOccurrence(schedule);
        int presentAttendance = 4;
        int absentAttendance = 4;
        int unmarkedAttendance = 4;
        for(int i = 0; i < presentAttendance; i++) {
            dataHelper.createAttendance(student, AttendanceStatus.PRESENT, occurrence);
        }
        for(int i = 0; i < absentAttendance; i++) {
            dataHelper.createAttendance(student, AttendanceStatus.ABSENT, occurrence);
        }
        for(int i = 0; i < unmarkedAttendance; i++) {
            dataHelper.createAttendance(student, AttendanceStatus.UNMARKED, occurrence);
        }

        int totalAttendance = presentAttendance + absentAttendance + unmarkedAttendance;
        double present = ((double) (totalAttendance - absentAttendance) / totalAttendance * 100);
        String path = String.format("/student/%s/summary", student.getId());
        Double expectedPresent = Math.round(present * 10.0) / 10.0;

        AttendanceOverallSummaryResponse response = given()
                .headers(fullAdminHeaders)
        .when()
                .get(path)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(AttendanceOverallSummaryResponse.class);

        assertEquals(totalAttendance, response.getTotalAttendanceCount());
        assertEquals(absentAttendance, response.getTotalAbsence());
        assertEquals(unmarkedAttendance, response.getTotalUnmarked());
        assertEquals(expectedPresent, response.getPresentPercentage());
    }
}