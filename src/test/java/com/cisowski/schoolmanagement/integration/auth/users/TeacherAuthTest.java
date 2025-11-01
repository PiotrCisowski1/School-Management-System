package com.cisowski.schoolmanagement.integration.auth.users;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherCreateRequest;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherPatchRequest;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityRequest;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TimeRange;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import io.restassured.http.Headers;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TeacherAuthTest extends BaseIntegrationTest {

    @Test
    void shouldAllowAdminToCreateTeacher() {
        Headers headers = createHeadersWithRandomAdminUser();
        TeacherCreateRequest request = dataHelper.createTeacherCreateRequest(null);

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("teachers")
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("firstName", equalTo(request.getFirstName()));
    }

    @Test
    void shouldNotAllowOtherUsersToCreateTeacher() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        TeacherCreateRequest request = dataHelper.createTeacherCreateRequest(null);

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("teachers")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToPatchTeacher() {
        Headers headers = createHeadersWithRandomAdminUser();
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherPatchRequest request = new TeacherPatchRequest();
        request.setFirstName("test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("teachers/" + teacher.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("firstName", equalTo(request.getFirstName()));
    }

    @Test
    void shouldNotAllowOtherUsersToPatchTeacher() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherPatchRequest request = new TeacherPatchRequest();
        request.setFirstName("test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("teachers/" + teacher.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToDeleteTeacher() {
        Headers headers = createHeadersWithRandomAdminUser();
        TeacherEntity teacher = dataHelper.createTeacher(null);

        given()
                .headers(headers)
        .when()
                .delete("teachers/" + teacher.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotAllowOtherUsersToDeleteTeacher() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        TeacherEntity teacher = dataHelper.createTeacher(null);

        given()
                .headers(headers)
        .when()
                .delete("teachers/" + teacher.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetTeacherById() {
        Headers headers = createHeadersWithRandomAdminUser();
        TeacherEntity teacher = dataHelper.createTeacher(null);

        given()
                .headers(headers)
        .when()
                .get("teachers/" + teacher.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(teacher.getId()));
    }

    @Test
    void shouldAllowTeacherToGetTeacherByIdIfSelfAccess() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("teachers/" + teacher.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(teacher.getId()));
    }

    @Test
    void shouldNotAllowRandomTeacherToGetAnotherTeacherById() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherEntity anotherTeacher = dataHelper.createTeacher(null);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("teachers/" + anotherTeacher.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowStudentToGetTeacherByIdIfHeadTeacherOfStudentsYearbook() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(null,teacher);
        StudentEntity student = dataHelper.createStudent(yearbook,null);
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get("teachers/" + teacher.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(teacher.getId()));
    }

    @Test
    void shouldAllowStudentToGetTeacherByIdIfTeachingStudentsSubject() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        TeacherEntity yearbookHeadTeacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject),yearbookHeadTeacher);
        StudentEntity student = dataHelper.createStudent(yearbook,null);
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get("teachers/" + teacher.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(teacher.getId()));
    }

    @Test
    void shouldNotAllowRandomStudentToGetTeacherById() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherEntity anotherTeacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(null,anotherTeacher);
        StudentEntity student = dataHelper.createStudent(yearbook,null);
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get("teachers/" + teacher.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowParentToGetTeacherByIdIfChildsHeadTeacher() {
        ParentEntity parent = dataHelper.createRandomParent();
        TeacherEntity teacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(null, teacher);
        StudentEntity student = dataHelper.createStudent(yearbook,Collections.singletonList(parent));
        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get("teachers/" + teacher.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(teacher.getId()));
    }

    @Test
    void shouldAllowParentToGetTeacherByIdIfTeachingStudentsSubject() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        TeacherEntity yearbookHeadTeacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject),yearbookHeadTeacher);
        ParentEntity parent = dataHelper.createRandomParent();
        StudentEntity student = dataHelper.createStudent(yearbook,Collections.singletonList(parent));
        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get("teachers/" + teacher.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(teacher.getId()));
    }

    @Test
    void shouldNotAllowRandomParentToGetTeacherById() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        ParentEntity parent = dataHelper.createRandomParent();
        TeacherEntity anotherTeacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(null,anotherTeacher);
        StudentEntity student = dataHelper.createStudent(yearbook, Collections.singletonList(parent));
        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get("teachers/" + teacher.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToGetAllTeachers() {
        Headers headers = createHeadersWithRandomAdminUser();
        TeacherEntity teacher = dataHelper.createTeacher(null);

        given()
                .headers(headers)
        .when()
                .get("teachers")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1));
    }

    @Test
    void shouldNotAllowOtherUsersToGetAllTeachers() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        TeacherEntity teacher = dataHelper.createTeacher(null);

        given()
                .headers(headers)
        .when()
                .get("teachers")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToCreateTeacherAvailability() {
        Headers headers = createHeadersWithRandomAdminUser();
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityRequest request = dataHelper.createTeacherAvailabilityRequest();

        given()
                .headers(headers)
                .body(request)
        .when()
                .post(String.format("teachers/%d/teacher-availability", teacher.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void shouldAllowTeacherToCreateTeacherAvailabilityForItself() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityRequest request = dataHelper.createTeacherAvailabilityRequest();
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
                .body(request)
        .when()
                .post(String.format("teachers/%d/teacher-availability", teacher.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void shouldNotAllowTeacherToCreateTeacherAvailabilityForOtherTeacher() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityRequest request = dataHelper.createTeacherAvailabilityRequest();
        Headers headers = buildBasicHeaders(teacher.getEmail());
        TeacherEntity anotherTeacher = dataHelper.createTeacher(null);

        given()
                .headers(headers)
                .body(request)
        .when()
                .post(String.format("teachers/%d/teacher-availability", anotherTeacher.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldNotAllowStudentParentToCreateTeacherAvailability() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityRequest request = dataHelper.createTeacherAvailabilityRequest();
        Headers headers = createHeadersForRandomUserNotAdmin(List.of("STUDENT", "PARENT"));

        given()
                .headers(headers)
                .body(request)
        .when()
                .post(String.format("teachers/%d/teacher-availability", teacher.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToDeleteTeacherAvailability() {
        Headers headers = createHeadersWithRandomAdminUser();
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);

        given()
                .headers(headers)
        .when()
                .delete(String.format("teachers/%d/teacher-availability/%d", teacher.getId(), teacherAvailabilityEntity.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldAllowTeacherToDeleteTeacherAvailabilityForItself() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .delete(String.format("teachers/%d/teacher-availability/%d", teacher.getId(), teacherAvailabilityEntity.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotAllowTeacherToDeleteTeacherAvailabilityForAnotherTeacher() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        TeacherEntity anotherTeacher = dataHelper.createTeacher(null);
        Headers headers = buildBasicHeaders(anotherTeacher.getEmail());

        given()
                .headers(headers)
        .when()
                .delete(String.format("teachers/%d/teacher-availability/%d", teacher.getId(), teacherAvailabilityEntity.getId()))
       .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldNotAllowStudentParentToDeleteTeacherAvailability() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        Headers headers = createHeadersForRandomUserNotAdmin(List.of("STUDENT", "PARENT"));

        given()
                .headers(headers)
        .when()
                .delete(String.format("teachers/%d/teacher-availability/%d", teacher.getId(), teacherAvailabilityEntity.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToGetTeacherAvailabilityById() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        Headers headers = createHeadersWithRandomAdminUser();

        given()
                .headers(headers)
        .when()
                .get(String.format("teachers/%d/teacher-availability/%d", teacher.getId(), teacherAvailabilityEntity.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldAllowTeacherToGetTeacherAvailabilityByIdForItself() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("teachers/%d/teacher-availability/%d", teacher.getId(), teacherAvailabilityEntity.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldNotAllowTeacherToGetTeacherAvailabilityByIdForAnotherTeacher() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        TeacherEntity anotherTeacher = dataHelper.createTeacher(null);
        Headers headers = buildBasicHeaders(anotherTeacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("teachers/%d/teacher-availability/%d", teacher.getId(), teacherAvailabilityEntity.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldNotAllowStudentParentToGetTeacherAvailabilityById() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        Headers headers = createHeadersForRandomUserNotAdmin(List.of("STUDENT", "PARENT"));

        given()
                .headers(headers)
        .when()
                .get(String.format("teachers/%d/teacher-availability/%d", teacher.getId(), teacherAvailabilityEntity.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToGetTeacherAvailabilityByTeacherId() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        Headers headers = createHeadersWithRandomAdminUser();

        given()
                .headers(headers)
        .when()
                .get(String.format("teachers/%d/teacher-availability", teacher.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1));
    }

    @Test
    void shouldAllowTeacherToGetTeacherAvailabilityByTeacherIdIfSelfRead() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("teachers/%d/teacher-availability", teacher.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1));
    }

    @Test
    void shouldNotAllowTeacherToGetTeacherAvailabilityByAnotherTeacherId() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        TeacherEntity anotherTeacher = dataHelper.createTeacher(null);
        Headers headers = buildBasicHeaders(anotherTeacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("teachers/%d/teacher-availability", teacher.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldNotAllowStudentParentToGetTeacherAvailabilityByTeacherId() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        Headers headers = createHeadersForRandomUserNotAdmin(List.of("STUDENT", "PARENT"));

        given()
                .headers(headers)
        .when()
                .get(String.format("teachers/%d/teacher-availability", teacher.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToGetTeacherAvailabilitiesByDayOfWeek() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        Headers headers = createHeadersWithRandomAdminUser();

        given()
                .headers(headers)
        .when()
                .get(String.format("teachers/teacher-availability/day/%d", teacherAvailabilityEntity.getDayOfWeek().getValue()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1));
    }

    @Test
    void shouldNotAllowOtherUsersToGetTeacherAvailabilitiesByDayOfWeek() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);

        given()
                .headers(headers)
        .when()
                .get(String.format("teachers/teacher-availability/day/%d", teacherAvailabilityEntity.getDayOfWeek().getValue()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetTeacherAvailabilitiesBySubjectIdAndTimeRange() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        Headers headers = createHeadersWithRandomAdminUser();
        TimeRange timeRange = new TimeRange();
        timeRange.setStartTime(teacherAvailabilityEntity.getStartTime().minusMinutes(30));
        timeRange.setEndTime(teacherAvailabilityEntity.getEndTime().plusMinutes(30));
        timeRange.setDayOfWeek(teacherAvailabilityEntity.getDayOfWeek().getValue());

        given()
                .headers(headers)
                .body(timeRange)
        .when()
                .get(String.format("teachers/subject/%d/teacher-availability", subject.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldNotAllowOtherUsersToGetTeacherAvailabilitiesBySubjectIdAndTimeRange() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        TimeRange timeRange = new TimeRange();
        timeRange.setStartTime(teacherAvailabilityEntity.getStartTime().minusMinutes(30));
        timeRange.setEndTime(teacherAvailabilityEntity.getEndTime().plusMinutes(30));
        timeRange.setDayOfWeek(teacherAvailabilityEntity.getDayOfWeek().getValue());

        given()
                .headers(headers)
                .body(timeRange)
        .when()
                .get(String.format("teachers/subject/%d/teacher-availability", subject.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
