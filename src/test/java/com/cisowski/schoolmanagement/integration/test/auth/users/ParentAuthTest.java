package com.cisowski.schoolmanagement.integration.test.auth.users;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.users.parent.model.ParentCreateRequest;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentPatchRequest;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import io.restassured.http.Headers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ParentAuthTest extends BaseIntegrationTest {

    @Test
    void shouldAllowAdminToCreateParent() {
        Headers headers = createHeadersWithRandomAdminUser();
        StudentEntity student = dataHelper.createStudent(null,null);
        ParentCreateRequest request = dataHelper.createParentCreateRequest(Collections.singletonList(student.getId()));

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("parents")
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void shouldNotAllowOtherUsersToCreateParent() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        StudentEntity student = dataHelper.createStudent(null,null);
        ParentCreateRequest request = dataHelper.createParentCreateRequest(Collections.singletonList(student.getId()));

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("parents")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToPatchParent() {
        Headers headers = createHeadersWithRandomAdminUser();
        ParentEntity parent = dataHelper.createRandomParent();
        ParentPatchRequest request = new ParentPatchRequest();
        request.setFirstName("test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("parents/" + parent.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("firstName", equalTo(request.getFirstName()));
    }

    @Test
    void shouldNotAllowOtherUsersToPatchParent() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        ParentEntity parent = dataHelper.createRandomParent();
        ParentPatchRequest request = new ParentPatchRequest();
        request.setFirstName("test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("parents/" + parent.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToDeleteParent() {
        Headers headers = createHeadersWithRandomAdminUser();
        ParentEntity parent = dataHelper.createRandomParent();

        given()
                .headers(headers)
        .when()
                .delete("parents/" + parent.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotAllowOtherUsersToDeleteParent() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        ParentEntity parent = dataHelper.createRandomParent();

        given()
                .headers(headers)
        .when()
                .delete("parents/" + parent.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetParentById() {
        Headers headers = createHeadersWithRandomAdminUser();
        ParentEntity parent = dataHelper.createRandomParent();

        given()
                .headers(headers)
        .when()
                .get("parents/" + parent.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(parent.getId()));
    }

    @Test
    void shouldAllowTeacherToGetParentByIdIfChildsHeadTeacher() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        Headers headers = buildBasicHeaders(teacher.getEmail());
        ParentEntity parent = dataHelper.createRandomParent();
        YearbookEntity yearbook = dataHelper.createYearbook(null, teacher);
        StudentEntity student = dataHelper.createStudent(yearbook, Collections.singletonList(parent));

        given()
                .headers(headers)
        .when()
                .get("parents/" + parent.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(parent.getId()));
    }

    @Test
    void shouldNotAllowRandomTeacherToGetParentById() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        Headers headers = buildBasicHeaders(teacher.getEmail());
        ParentEntity parent = dataHelper.createRandomParent();
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        StudentEntity student = dataHelper.createStudent(yearbook, Collections.singletonList(parent));

        given()
                .headers(headers)
        .when()
                .get("parents/" + parent.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowStudentToGetParentByIdIfActualParent() {
        ParentEntity parent = dataHelper.createRandomParent();
        StudentEntity student = dataHelper.createStudent(null, Collections.singletonList(parent));
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get("parents/" + parent.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(parent.getId()));
    }

    @Test
    void shouldNotAllowRandomStudentToGetParentById() {
        StudentEntity student = dataHelper.createStudent(null, null);
        Headers headers = buildBasicHeaders(student.getEmail());
        ParentEntity parent = dataHelper.createRandomParent();

        given()
                .headers(headers)
        .when()
                .get("parents/" + parent.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowParentToGetParentByIdIfSelfRead() {
        ParentEntity parent = dataHelper.createRandomParent();
        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get("parents/" + parent.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(parent.getId()));
    }

    @Test
    void shouldNotAllowRandomParentToGetParentById() {
        ParentEntity parent = dataHelper.createRandomParent();
        Headers headers = buildBasicHeaders(parent.getEmail());
        ParentEntity anotherParent = dataHelper.createRandomParent();

        given()
                .headers(headers)
        .when()
                .get("parents/" + anotherParent.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToGetAllParents() {
        Headers headers = createHeadersWithRandomAdminUser();
        ParentEntity parent = dataHelper.createRandomParent();

        given()
                .headers(headers)
        .when()
                .get("parents")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1));
    }

    @Test
    void shouldNotAllowOtherUsersToGetAllParents() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        ParentEntity parent = dataHelper.createRandomParent();

        given()
                .headers(headers)
        .when()
                .get("parents")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
