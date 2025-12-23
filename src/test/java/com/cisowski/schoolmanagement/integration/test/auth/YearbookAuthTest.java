package com.cisowski.schoolmanagement.integration.test.auth;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.yearbook.model.AddYearbookRequest;
import com.cisowski.schoolmanagement.yearbook.model.PatchYearbookRequest;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import io.restassured.http.Headers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class YearbookAuthTest extends BaseIntegrationTest {

    @Test
    void shouldAllowAdminToCreateYearbook() {
        Headers headers = createHeadersWithRandomAdminUser();
        AddYearbookRequest request = dataHelper.createAddYearbookRequest(null, null);

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("yearbooks")
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("symbol", equalTo(request.getSymbol()));
    }

    @Test
    void shouldNotAllowOtherUsersToCreateYearbook() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        AddYearbookRequest request = dataHelper.createAddYearbookRequest(null, null);

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("yearbooks")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToPatchYearbook() {
        Headers headers = createHeadersWithRandomAdminUser();
        YearbookEntity yearbook = dataHelper.createYearbook(null ,null);
        PatchYearbookRequest request = new PatchYearbookRequest();
        request.setSymbol("test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("yearbooks/" + yearbook.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("symbol", equalTo(request.getSymbol()));
    }

    @Test
    void shouldNotAllowOtherUsersToPatchYearbook() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        YearbookEntity yearbook = dataHelper.createYearbook(null ,null);
        PatchYearbookRequest request = new PatchYearbookRequest();
        request.setSymbol("test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("yearbooks/" + yearbook.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToDeleteYearbook() {
        Headers headers = createHeadersWithRandomAdminUser();
        YearbookEntity yearbook = dataHelper.createYearbook(null ,null);

        given()
                .headers(headers)
        .when()
                .delete("yearbooks/" + yearbook.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotAllowOtherUsersToDeleteYearbook() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        YearbookEntity yearbook = dataHelper.createYearbook(null ,null);

        given()
                .headers(headers)
        .when()
                .delete("yearbooks/" + yearbook.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetYearbookById() {
        Headers headers = createHeadersWithRandomAdminUser();
        YearbookEntity yearbook = dataHelper.createYearbook(null ,null);

        given()
                .headers(headers)
        .when()
                .get("yearbooks/" + yearbook.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(yearbook.getId()));
    }

    @Test
    void shouldAllowTeacherToGetYearbookByIdIfYearbooksHeadTeacher() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(null ,teacher);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("yearbooks/" + yearbook.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(yearbook.getId()));
    }

    @Test
    void shouldAllowTeacherToGetYearbookByIdIfSubjectAssociatedWithTeacherAndYearbook() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        TeacherEntity randomYearbookHeadTeacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), randomYearbookHeadTeacher);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("yearbooks/" + yearbook.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(yearbook.getId()));
    }

    @Test
    void shouldNotAllowRandomTeacherToGetYearbookById() {
        TeacherEntity yearbookHeadTeacher = dataHelper.createTeacher(null);
        Headers randomTeacherHeaders = createHeadersWithRandomTeacherUser();
        YearbookEntity yearbook = dataHelper.createYearbook(null,yearbookHeadTeacher);

        given()
                .headers(randomTeacherHeaders)
        .when()
                .get("yearbooks/" + yearbook.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowStudentToGetYearbookByIdIfSelfRead() {
        YearbookEntity yearbook = dataHelper.createYearbook(null,null);
        StudentEntity student = dataHelper.createStudent(yearbook, null);
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get("yearbooks/" + yearbook.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(yearbook.getId()));
    }

    @Test
    void shouldNotAllowRandomStudentToGetYearbookById() {
        YearbookEntity yearbook = dataHelper.createYearbook(null,null);
        StudentEntity student = dataHelper.createStudent(null, null);
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get("yearbooks/" + yearbook.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowParentToGetYearbookByIdIfChildsYearbook() {
        YearbookEntity yearbook = dataHelper.createYearbook(null,null);
        ParentEntity parent = dataHelper.createRandomParent();
        StudentEntity student = dataHelper.createStudent(yearbook, Collections.singletonList(parent));
        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get("yearbooks/" + yearbook.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(yearbook.getId()));
    }

    @Test
    void shouldNotAllowRandomParentToGetYearbookById() {
        YearbookEntity yearbook = dataHelper.createYearbook(null,null);
        ParentEntity parent = dataHelper.createRandomParent();
        StudentEntity student = dataHelper.createStudent(yearbook, null);
        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get("yearbooks/" + yearbook.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToGetAllYearbooks() {
        Headers headers = createHeadersWithRandomAdminUser();
        YearbookEntity yearbook = dataHelper.createYearbook(null,null);

        given()
                .headers(headers)
        .when()
                .get("yearbooks")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1));
    }

    @Test
    void shouldNotAllowOtherUsersToGetAllYearbooks() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        YearbookEntity yearbook = dataHelper.createYearbook(null,null);

        given()
                .headers(headers)
        .when()
                .get("yearbooks")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
