package com.cisowski.schoolmanagement.integration.auth;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.subject.model.*;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import io.restassured.http.Headers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class SubjectAuthTest extends BaseIntegrationTest {

    @Test
    void shouldAllowAdminToCreateSubject() {
        Headers headers = createHeadersWithRandomAdminUser();
        SubjectTypeEntity subjectType = dataHelper.createSubjectType();
        AddSubjectRequest subjectRequest = dataHelper.createAddSubjectRequest(subjectType);

        given()
                .headers(headers)
                .body(subjectRequest)
        .when()
                .post("subjects")
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void shouldNotAllowOtherUsersToCreateSubject() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        SubjectTypeEntity subjectType = dataHelper.createSubjectType();
        AddSubjectRequest subjectRequest = dataHelper.createAddSubjectRequest(subjectType);

        given()
                .headers(headers)
                .body(subjectRequest)
        .when()
                .post("subjects")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToPatchSubject() {
        Headers headers = createHeadersWithRandomAdminUser();
        SubjectEntity subject = dataHelper.createSubject();
        PatchSubjectRequest request = new PatchSubjectRequest();
        request.setName("test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("subjects/" + subject.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(request.getName()));
    }

    @Test
    void shouldNotAllowOtherUsersToPatchSubject() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        PatchSubjectRequest request = new PatchSubjectRequest();
        request.setName("test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("subjects/" + 123)
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetAllSubjects() {
        Headers headers = createHeadersWithRandomAdminUser();
        SubjectEntity subject = dataHelper.createSubject();

        given()
                .headers(headers)
        .when()
                .get("subjects")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1));
    }

    @Test
    void shouldNotAllowOtherUsersToGetAllSubjects() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);

        given()
                .headers(headers)
        .when()
                .get("subjects")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetSubjectById() {
        Headers headers = createHeadersWithRandomAdminUser();
        SubjectEntity subject = dataHelper.createSubject();

        given()
                .headers(headers)
        .when()
                .get("subjects/" + subject.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(subject.getId()));
    }

    @Test
    void shouldAllowParentToGetSubjectByIdIfChildIsSubjectAttendant() {
        ParentEntity parent = dataHelper.createRandomParent();
        SubjectEntity subject = dataHelper.createSubject();
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), null);
        StudentEntity student = dataHelper.createStudent(yearbook, Collections.singletonList(parent));
        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get("subjects/" + subject.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(subject.getId()));
    }

    @Test
    void shouldNotAllowRandomParentToGetSubjectById() {
        ParentEntity parent = dataHelper.createRandomParent();
        SubjectEntity subject = dataHelper.createSubject();
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), null);
        StudentEntity student = dataHelper.createStudent(null, null);
        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get("subjects/" + subject.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowStudentToGetSubjectByIdIfSubjectAttendant() {
        SubjectEntity subject = dataHelper.createSubject();
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), null);
        StudentEntity student = dataHelper.createStudent(yearbook ,null);
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get("subjects/" + subject.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(subject.getId()));
    }

    @Test
    void shuldNotAllowRandomStudentToGetSubjectById() {
        SubjectEntity subject = dataHelper.createSubject();
        StudentEntity student = dataHelper.createStudent(null ,null);
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get("subjects/" + subject.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowTeacherToGetSubjectByIdIfIsTeachingSubject() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("subjects/" + subject.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(subject.getId()));
    }

    @Test
    void shouldAllowTeacherToGetSubjectByIdIfHeadTeacherOfYearbookTakingSubject() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.emptyList());
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), teacher);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("subjects/" + subject.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(subject.getId()));
    }

    @Test
    void shouldNotAllowRandomTeacherToGetSubjectById() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(null);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("subjects/" + subject.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToGetSubjectByCode() {
        Headers headers = createHeadersWithRandomAdminUser();
        SubjectEntity subject = dataHelper.createSubject();

        given()
                .headers(headers)
        .when()
                .get("subjects/code/" + subject.getCode())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(subject.getId()));
    }

    @Test
    void shouldNotAllowStudentParentToGetSubjectByCode() {
        Headers headers = createHeadersForRandomUserNotAdmin(List.of("PARENT", "STUDENT"));
        SubjectEntity subject = dataHelper.createSubject();

        given()
                .headers(headers)
                .when()
                .get("subjects/code/" + subject.getCode())
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowTeacherToGetSubjectByCodeIfTeachingSubject() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("subjects/code/" + subject.getCode())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(subject.getId()));
    }

    @Test
    void shouldAllowTeacherToGetSubjectByCodeIfTeachingSubjectIfHeadTeacherOfYearbookTakingSubject() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.emptyList());
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), teacher);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("subjects/code/" + subject.getCode())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(subject.getId()));
    }

    @Test
    void shouldAllowAdminToGetSubjectsByType() {
        Headers headers = createHeadersWithRandomAdminUser();
        SubjectEntity subject = dataHelper.createSubject();
        SubjectTypeRequest request = new SubjectTypeRequest();
        request.setName(subject.getSubjectType().getName());

        given()
                .headers(headers)
                .body(request)
        .when()
                .get("subjects/subjectTypes")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1));
    }

    @Test
    void shouldNotAllowOtherUsersToGetSubjectsByType() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        SubjectEntity subject = dataHelper.createSubject();
        SubjectTypeRequest request = new SubjectTypeRequest();
        request.setName(subject.getSubjectType().getName());

        given()
                .headers(headers)
                .body(request)
        .when()
                .get("subjects/subjectTypes")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToDeleteSubject() {
        Headers headers = createHeadersWithRandomAdminUser();
        SubjectEntity subject = dataHelper.createSubject();

        given()
                .headers(headers)
        .when()
                .delete("subjects/" + subject.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotAllowOtherUsersToDeleteSubject() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        SubjectEntity subject = dataHelper.createSubject();

        given()
                .headers(headers)
        .when()
                .delete("subjects/" + subject.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToCreateSubjectType() {
        Headers headers = createHeadersWithRandomAdminUser();
        SubjectTypeRequest request = new SubjectTypeRequest();
        request.setName("test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("subjects/subjectTypes")
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(request.getName()));
    }

    @Test
    void shouldNotAllowOtherUsersToCreateSubjectType() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        SubjectTypeRequest request = new SubjectTypeRequest();
        request.setName("test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("subjects/subjectTypes")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToPatchSubjectType() {
        Headers headers = createHeadersWithRandomAdminUser();
        SubjectTypeEntity subjectType = dataHelper.createSubjectType();
        SubjectTypeRequest request = new SubjectTypeRequest();
        request.setName("test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("subjects/subjectTypes/" + subjectType.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(request.getName()));
    }

    @Test
    void shouldNotAllowOtherUsersToPatchSubjectType() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        SubjectTypeEntity subjectType = dataHelper.createSubjectType();
        SubjectTypeRequest request = new SubjectTypeRequest();
        request.setName("test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("subjects/subjectTypes/" + subjectType.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetSubjectType() {
        Headers headers = createHeadersWithRandomAdminUser();
        SubjectTypeEntity subjectType = dataHelper.createSubjectType();

        given()
                .headers(headers)
        .when()
                .get("subjects/subjectTypes/" + subjectType.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(subjectType.getId()));
    }

    @Test
    void shouldNotAllowOtherUsersToGetSubjectType() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        SubjectTypeEntity subjectType = dataHelper.createSubjectType();

        given()
                .headers(headers)
        .when()
                .get("subjects/subjectTypes/" + subjectType.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetAllSubjectTypes() {
        Headers headers = createHeadersWithRandomAdminUser();
        SubjectTypeEntity subjectType = dataHelper.createSubjectType();

        given()
                .headers(headers)
        .when()
                .get("subjects/subjectTypes")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1));
    }

    @Test
    void shouldNotAllowOtherUsersToGetAllSubjectTypes() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        SubjectTypeEntity subjectType = dataHelper.createSubjectType();

        given()
                .headers(headers)
        .when()
                .get("subjects/subjectTypes")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToDeleteSubjectType() {
        Headers headers = createHeadersWithRandomAdminUser();
        SubjectTypeEntity subjectType = dataHelper.createSubjectType();

        given()
                .headers(headers)
        .when()
                .delete("subjects/subjectTypes/" + subjectType.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotAllowOtherUsersToDeleteSubjectType() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        SubjectTypeEntity subjectType = dataHelper.createSubjectType();

        given()
                .headers(headers)
        .when()
                .delete("subjects/subjectTypes/" + subjectType.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
