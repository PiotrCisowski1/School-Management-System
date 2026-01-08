package com.cisowski.schoolmanagement.integration.test.auth;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.timetable.schedule.model.AddScheduleRequest;
import com.cisowski.schoolmanagement.timetable.schedule.model.PatchScheduleRequest;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleStatus;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.AddScheduleVersionRequest;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.PatchScheduleVersionRequest;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionDetailedResponse;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import io.restassured.http.Headers;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class ScheduleAuthTest extends BaseIntegrationTest {

    @Test
    void shouldAllowAdminToCreateSchedule() {
        Headers headers = createHeadersWithRandomAdminUser();
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity availabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        AddScheduleRequest scheduleRequest = dataHelper.createAddScheduleRequest(null  ,teacher,null);
        scheduleRequest.setDayOfWeek(availabilityEntity.getDayOfWeek().getValue());
        scheduleRequest.setStartTime(availabilityEntity.getStartTime());
        scheduleRequest.setEndTime(availabilityEntity.getEndTime());
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);

        given()
                .headers(headers)
                .body(scheduleRequest)
        .when()
                .post(String.format("schedules/version/%d", scheduleVersion.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void shouldNotAllowOtherUsersToCreateSchedule() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        AddScheduleRequest request = dataHelper.createAddScheduleRequest((SubjectEntity) null ,null ,null);

        given()
                .headers(headers)
                .body(request)
        .when()
                .post(String.format("schedules/version/%d", 123))
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToDeleteSchedule() {
        Headers headers = createHeadersWithRandomAdminUser();
        ScheduleEntity schedule = dataHelper.createScheduleEntity(null, null, null);

        given()
                .headers(headers)
        .when()
                .delete(String.format("schedules/%d", schedule.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotAllowOtherUserToDeleteSchedule() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);

        given()
                .headers(headers)
        .when()
                .delete(String.format("schedules/%d", 123))
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToPatchSchedule() {
        Headers headers = createHeadersWithRandomAdminUser();
        ScheduleEntity schedule = dataHelper.createScheduleEntity(null ,null ,null);
        PatchScheduleRequest request = new PatchScheduleRequest();
        request.setEndTime(schedule.getEndTime().plusHours(2));

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch(String.format("schedules/%d", schedule.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldNotAllowOtherUsersToPatchSchedule() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        PatchScheduleRequest request = new PatchScheduleRequest();

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch(String.format("schedules/%d", 123))
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetSchedule() {
        Headers headers = createHeadersWithRandomAdminUser();
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion,null,null);

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/%d/%d", scheduleVersion.getId(), schedule.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldAllowParentToGetScheduleIfChildScheduleAttendant() {
        ParentEntity parent = dataHelper.createRandomParent();
        Headers headers = buildBasicHeaders(parent.getEmail());
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        StudentEntity student = dataHelper.createStudent(yearbook, Collections.singletonList(parent));
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion,null,null);

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/%d/%d", scheduleVersion.getId(), schedule.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(schedule.getId()));
    }

    @Test
    void shouldNotAllowRandomParentToGetSchedule() {
        ParentEntity parent = dataHelper.createRandomParent();
        Headers headers = buildBasicHeaders(parent.getEmail());
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        StudentEntity student = dataHelper.createStudent(yearbook, Collections.emptyList());
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion,null,null);

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/%d/%d", scheduleVersion.getId(), schedule.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowStudentToGetScheduleIfSelfReadAndSchedulePartOfAccessedScheduleVersion() {
        YearbookEntity yearbook = dataHelper.createYearbook(null ,null);
        StudentEntity student = dataHelper.createStudent(yearbook,null);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion, null, null);
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/%d/%d", scheduleVersion.getId(), schedule.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(schedule.getId()));
    }

    @Test
    void shouldNotAllowRandomStudentToGetScheduleOfOtherStudent() {
        YearbookEntity yearbook = dataHelper.createYearbook(null ,null);
        StudentEntity student = dataHelper.createStudent(yearbook,null);
        StudentEntity accessingStudent = dataHelper.createStudent(null,null);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion, null, null);
        Headers headers = buildBasicHeaders(accessingStudent.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/%d/%d", scheduleVersion.getId(), schedule.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowTeacherToGetScheduleIfTeachingSchedule() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        Headers headers = buildBasicHeaders(teacher.getEmail());
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion, teacher, null);

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/%d/%d", scheduleVersion.getId(), schedule.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(schedule.getId()));
    }

    @Test
    void shouldAllowTeacherToGetScheduleIfSchedulesYearbookHeadTeacher() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        Headers headers = buildBasicHeaders(teacher.getEmail());
        YearbookEntity yearbook = dataHelper.createYearbook(null, teacher);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion, teacher, null);

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/%d/%d", scheduleVersion.getId(), schedule.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(schedule.getId()));
    }

    @Test
    void shouldNotAllowRandomTeacherToGetSchedule() {
        Headers headers = createHeadersWithRandomTeacherUser();
        TeacherEntity teacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(null, teacher);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion, null, null);

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/%d/%d", scheduleVersion.getId(), schedule.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToGetSchedulesByDayOfWeek() {
        Headers headers = createHeadersWithRandomAdminUser();
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion, null ,null);

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/%d/day/%d", scheduleVersion.getId(), schedule.getDayOfWeek().getValue()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldAllowParentToGetSchedulesByDayOfWeekIfChildScheduleAttendant() {
        ParentEntity parent = dataHelper.createRandomParent();
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        StudentEntity student = dataHelper.createStudent(yearbook, Collections.singletonList(parent));
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion, null ,null);
        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/%d/day/%d", scheduleVersion.getId(), schedule.getDayOfWeek().getValue()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1));
    }

    @Test
    void shouldNotAllowRandomParentToGetSchedulesByDayOfWeek() {
        ParentEntity parent = dataHelper.createRandomParent();
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        StudentEntity student = dataHelper.createStudent(yearbook, Collections.emptyList());
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion, null ,null);
        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/%d/day/%d", scheduleVersion.getId(), schedule.getDayOfWeek().getValue()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowTeacherToGetScheduleByDayOfWeekIfYearbookHeadTeacher() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(null, teacher);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion, null ,null);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/%d/day/%d", scheduleVersion.getId(), schedule.getDayOfWeek().getValue()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1));
    }

    @Test
    void shouldAllowTeacherToGetSchedulesByDayOfWeekIfTeachersSchedules() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherEntity yearbookHeadTeacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(null, yearbookHeadTeacher);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion, teacher ,null);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/%d/day/%d", scheduleVersion.getId(), schedule.getDayOfWeek().getValue()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1));
    }

    @Test
    void shouldNotAllowRandomTeacherToGetSchedulesByDayOfWeek() {
        TeacherEntity yearbookHeadTeacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(null, yearbookHeadTeacher);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion, null ,null);
        Headers headers = createHeadersWithRandomTeacherUser();

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/%d/day/%d", scheduleVersion.getId(), schedule.getDayOfWeek().getValue()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToCreateScheduleVersion() {
        Headers headers = createHeadersWithRandomAdminUser();
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        AddScheduleVersionRequest request = new AddScheduleVersionRequest();
        request.setScheduleName("test123");
        request.setActive(true);
        request.setYearbookId(yearbook.getId());

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("schedules/version")
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void shouldNotAllowOtherUsersToCreateScheduleVersion() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        AddScheduleVersionRequest request = Instancio.create(AddScheduleVersionRequest.class);

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("schedules/version")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToCloneScheduleVersion() {
        Headers headers = createHeadersWithRandomAdminUser();
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);

        given()
                .headers(headers)
        .when()
                .post(String.format("schedules/version/%d/clone", scheduleVersion.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void shouldNotAllowOtherUsersToCloneScheduleVersion() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);

        given()
                .headers(headers)
        .when()
                .post(String.format("schedules/version/%d/clone", 123))
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetScheduleVersionForYearbook() {
        Headers headers = createHeadersWithRandomAdminUser();
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/yearbook/%d", yearbook.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldNotAllowOtherUsersToGetSchduleVersionsForYearbook() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/yearbook/%d", 123))
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetScheduleVersionForId() {
        Headers headers = createHeadersWithRandomAdminUser();
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/%d", scheduleVersion.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldAllowTeacherToGetScheduleVersionByIdIfHeadTeacherOfYearbook() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(null, teacher);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/%d", scheduleVersion.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldNotAllowRandomTeacherToGetScheduleVersionById() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(null, teacher);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        Headers headers = createHeadersWithRandomTeacherUser();

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/%d", scheduleVersion.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowStudentToGetScheduleVersionByIdIfSelfRead() {
        YearbookEntity yearbook = dataHelper.createYearbook(null , null);
        StudentEntity student = dataHelper.createStudent(yearbook ,null);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/%d", scheduleVersion.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldNotAllowRandomStudentToGetScheduleVersionById() {
        YearbookEntity yearbook = dataHelper.createYearbook(null , null);
        StudentEntity student = dataHelper.createStudent(yearbook ,null);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        StudentEntity anotherStudent = dataHelper.createStudent(null ,null);
        Headers headers = buildBasicHeaders(anotherStudent.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/%d", scheduleVersion.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowParentToGetScheduleVersionByIdIfChildsScheduleVersion() {
        ParentEntity parent = dataHelper.createRandomParent();
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        StudentEntity student = dataHelper.createStudent(yearbook, Collections.singletonList(parent));
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/%d", scheduleVersion.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldNotAllowRandomParentToGetScheduleVersionById() {
        ParentEntity parent = dataHelper.createRandomParent();
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        StudentEntity student = dataHelper.createStudent(yearbook, null);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/%d", scheduleVersion.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToDeleteScheduleVersion() {
        Headers headers = createHeadersWithRandomAdminUser();
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);

        given()
                .headers(headers)
        .when()
                .delete(String.format("schedules/version/%d", scheduleVersion.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotAllowOtherUsersToDeleteScheduleVersion() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);

        given()
                .headers(headers)
        .when()
                .delete(String.format("schedules/version/%d", 123))
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToPatchScheduleVersion() {
        Headers headers = createHeadersWithRandomAdminUser();
        TeacherEntity teacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(null, teacher);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        PatchScheduleVersionRequest request = new PatchScheduleVersionRequest();
        request.setName("test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch(String.format("schedules/version/%d", scheduleVersion.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldNotAllowOtherUserToPatchScheduleVersion() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        PatchScheduleVersionRequest request = new PatchScheduleVersionRequest();

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch(String.format("schedules/version/%d", 123))
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToCancelSchedule() {
        Headers headers = createHeadersWithRandomAdminUser();
        ScheduleEntity schedule = dataHelper.createScheduleEntity(null, null, null);
        String reason = "Test reason";

        assertNotEquals(ScheduleStatus.CANCELLED, schedule.getStatus());

        given()
                .headers(headers)
                .body(reason)
        .when()
                .put(String.format("schedules/%d/CANCEL", schedule.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldAllowStudentToGetScheduleVersionByYearbook() {
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        StudentEntity student = dataHelper.createStudent(yearbook, null);
        Headers headers = buildBasicHeaders(student.getEmail());
        ScheduleVersionEntity activeScheduleVersion = dataHelper.createScheduleVersion(yearbook, true);
        ScheduleVersionEntity inactiveScheduleVersion = dataHelper.createScheduleVersion(yearbook, false);

        ScheduleVersionDetailedResponse response = given()
                .headers(headers)
        .when()
                .get(String.format("schedules/version/yearbook/%s/active", yearbook.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract().as(ScheduleVersionDetailedResponse.class);

        assertNotNull(response);
        assertTrue(response.isActive());
        assertEquals(yearbook.getId(), response.getYearbook().getId());
        assertEquals(activeScheduleVersion.getId(), response.getId());
        assertEquals(activeScheduleVersion.getName(), response.getName());
    }
}
