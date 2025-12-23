package com.cisowski.schoolmanagement.integration.test.auth.users;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentCreateRequest;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentPatchRequest;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import io.restassured.http.Headers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class StudentAuthTest extends BaseIntegrationTest {

    @Test
    void shouldAllowAdminToCreateStudent() {
        Headers headers = createHeadersWithRandomAdminUser();
        StudentCreateRequest request = dataHelper.createStudentCreateRequest(null);

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("students")
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void shouldNotAllowOtherUsersToCreateStudent() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        StudentCreateRequest request = dataHelper.createStudentCreateRequest(null);

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("students")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToPatchStudent() {
        Headers headers = createHeadersWithRandomAdminUser();
        StudentEntity student = dataHelper.createStudent(null,null);
        StudentPatchRequest request = new StudentPatchRequest();
        request.setLastName("test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("students/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("lastName", equalTo(request.getLastName()));
    }

    @Test
    void shouldNotAllowOtherUsersToPatchStudent() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        StudentEntity student = dataHelper.createStudent(null,null);
        StudentPatchRequest request = new StudentPatchRequest();
        request.setLastName("test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("students/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToDeleteStudent() {
        Headers headers = createHeadersWithRandomAdminUser();
        StudentEntity student = dataHelper.createStudent(null,null);

        given()
                .headers(headers)
        .when()
                .delete("students/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotAllowOtherUsersToDeleteStudent() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);
        StudentEntity student = dataHelper.createStudent(null,null);

        given()
                .headers(headers)
        .when()
                .delete("students/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetStudentById() {
        Headers headers = createHeadersWithRandomAdminUser();
        StudentEntity student = dataHelper.createStudent(null,null);

        given()
                .headers(headers)
        .when()
                .get("students/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(student.getId()));
    }

    @Test
    void shouldAllowStudentToGetStudentByIdIfSelfAccess() {
        StudentEntity student = dataHelper.createStudent(null,null);
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get("students/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(student.getId()));
    }

    @Test
    void shouldNotAllowRandomStudentToGetStudentById() {
        StudentEntity student = dataHelper.createStudent(null,null);
        StudentEntity anotherStudent = dataHelper.createStudent(null, null);
        Headers headers = buildBasicHeaders(anotherStudent.getEmail());

        given()
                .headers(headers)
        .when()
                .get("students/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowParentToGetStudentByIdIfChild() {
        ParentEntity parent = dataHelper.createRandomParent();
        StudentEntity student = dataHelper.createStudent(null, Collections.singletonList(parent));
        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get("students/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(student.getId()));
    }

    @Test
    void shouldNotAllowRandomParentToGetStudentById() {
        ParentEntity parent = dataHelper.createRandomParent();
        StudentEntity student = dataHelper.createStudent(null, Collections.emptyList());
        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get("students/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowTeacherToGetStudentByIdIfTeachingSubjectThatStudentAttend() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), null);
        StudentEntity student = dataHelper.createStudent(yearbook, null);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("students/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(student.getId()));
    }

    @Test
    void shouldAllowTeacherToGetStudentByIdIfHeadTeacherOfStudentsYearbook() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        YearbookEntity yearbook = dataHelper.createYearbook(null, teacher);
        StudentEntity student = dataHelper.createStudent(yearbook, null);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("students/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(student.getId()));
    }

    @Test
    void shouldNotAllowRandomTeacherToGetStudentById() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        StudentEntity student = dataHelper.createStudent(null, null);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("students/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToGetAllStudents() {
        Headers headers = createHeadersWithRandomAdminUser();
        StudentEntity student = dataHelper.createStudent(null, null);

        given()
                .headers(headers)
        .when()
                .get("students")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1));
    }

    @Test
    void shouldNotAllowOtherUsersToGetAllStudents() {
        Headers headers = createHeadersForRandomUserNotAdmin(userTypesOtherThanAdmin);

        given()
                .headers(headers)
        .when()
                .get("students")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

}
