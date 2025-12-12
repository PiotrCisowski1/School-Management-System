package com.cisowski.schoolmanagement.integration.test.auth;

import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceStatus;
import com.cisowski.schoolmanagement.timetable.attendance.model.MarkAttendanceRequest;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import io.restassured.http.Headers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;

public class AttendanceAuthTest extends BaseIntegrationTest {

    @Test
    void shouldAllowTeacherToSetAttendanceStatusForStudents() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.emptyList(), teacher);
        StudentEntity student = dataHelper.createStudent(yearbook, Collections.emptyList());
        yearbook.setStudentsInYearbook(Collections.singletonList(student));
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion, teacher, null);
        ScheduleOccurrenceEntity occurrence = dataHelper.createScheduleOccurrence(schedule);
        Integer studentId = schedule.getScheduleVersion().getYearbook().getStudentsInYearbook().iterator().next().getId();
        MarkAttendanceRequest request = new MarkAttendanceRequest(List.of(studentId), AttendanceStatus.PRESENT);
        Headers headers = buildBasicHeaders(teacher.getEmail());
        String url = String.format("/scheduleOccurrence/%s/mark", occurrence.getId());

        given()
                .headers(headers)
                .body(request)
        .when()
                .post(url)
        .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldNotAllowTeacherToSetAttendanceIfNotScheduleTeacher() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(null, null, null);
        ScheduleOccurrenceEntity occurrence = dataHelper.createScheduleOccurrence(schedule);
        MarkAttendanceRequest request = new MarkAttendanceRequest(List.of(1), AttendanceStatus.PRESENT);
        Headers headers = buildBasicHeaders(teacher.getEmail());
        String url = String.format("/scheduleOccurrence/%s/mark", occurrence.getId());

        given()
                .headers(headers)
                .body(request)
        .when()
                .post(url)
        .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldNotAllowOtherUsersToSetAttendanceIfNotScheduleTeacher() {
        Headers headers = createHeadersForRandomUserNotAdmin(List.of(UserType.PARENT.name(), UserType.STUDENT.name()));
        ScheduleEntity schedule = dataHelper.createScheduleEntity(null, null, null);
        ScheduleOccurrenceEntity occurrence = dataHelper.createScheduleOccurrence(schedule);
        MarkAttendanceRequest request = new MarkAttendanceRequest(List.of(1), AttendanceStatus.PRESENT);
        String url = String.format("/scheduleOccurrence/%s/mark", occurrence.getId());

        given()
                .headers(headers)
                .body(request)
        .when()
                .post(url)
        .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
