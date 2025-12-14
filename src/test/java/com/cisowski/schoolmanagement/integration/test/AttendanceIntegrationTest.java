package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.OccurrenceStatus;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


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


}
