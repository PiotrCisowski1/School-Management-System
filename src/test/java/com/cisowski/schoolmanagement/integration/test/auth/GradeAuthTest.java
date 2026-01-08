package com.cisowski.schoolmanagement.integration.test.auth;

import com.cisowski.schoolmanagement.grade.model.grade.AddGradeRequest;
import com.cisowski.schoolmanagement.grade.model.grade.GradeEntity;
import com.cisowski.schoolmanagement.grade.model.grade.PatchGradeRequest;
import com.cisowski.schoolmanagement.grade.model.gradeScale.*;
import com.cisowski.schoolmanagement.grade.model.gradeType.AddGradeTypeRequest;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import com.cisowski.schoolmanagement.grade.model.gradeType.PatchGradeTypeRequest;
import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import io.restassured.http.Headers;
import jakarta.transaction.Transactional;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GradeAuthTest extends BaseIntegrationTest {

    @Test
    void shouldAllowAdminToAddGradeType() {
        AddGradeTypeRequest request = Instancio.create(AddGradeTypeRequest.class);
        Headers headers = createHeadersWithRandomAdminUser();

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("/grades/types")
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("gradeScope", equalTo(request.getGradeScope()));

    }

    @Test
    void shouldNotAllowOtherUserToAddGradeType() {
        AddGradeTypeRequest request = Instancio.create(AddGradeTypeRequest.class);
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("/grades/types")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToPatchGradeType() {
        Headers headers = createHeadersWithRandomAdminUser();
        GradeTypeEntity gradeType = dataHelper.createGradeType();
        PatchGradeTypeRequest request = Instancio.create(PatchGradeTypeRequest.class);

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("/grades/types/" + gradeType.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("gradeScope", equalTo(request.getGradeScope()));
    }

    @Test
    void shouldNotAllowOtherUserToPatchGradeType() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());
        PatchGradeTypeRequest request = Instancio.create(PatchGradeTypeRequest.class);

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("/grades/types/" + 123)
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToDeleteGradeType() {
        GradeTypeEntity gradeType = dataHelper.createGradeType();
        Headers headers = createHeadersWithRandomAdminUser();

        given()
                .headers(headers)
        .when()
                .delete("/grades/types/" + gradeType.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotAllowOtherUserToDeleteGradeType() {
        GradeTypeEntity gradeType = dataHelper.createGradeType();
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        given()
                .headers(headers)
        .when()
                .delete("/grades/types/" + gradeType.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetGradeType() {
        GradeTypeEntity gradeType = dataHelper.createGradeType();
        Headers headers = createHeadersWithRandomAdminUser();

        given()
                .headers(headers)
        .when()
                .get("/grades/types/" + gradeType.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(gradeType.getId().intValue()));
    }

    @Test
    void shouldNotAllowOtherUserToGetGradeType() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        given()
                .headers(headers)
        .when()
                .get("/grades/types/" + 123)
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetGradeTypes() {
        Headers headers = createHeadersWithRandomAdminUser();
        List<GradeTypeEntity> grades = dataHelper.createGradeTypes();

        given()
                .headers(headers)
        .when()
                .get("/grades/types")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(grades.size()));
    }

    @Test
    void shouldNotAllowOtherUserToGetGradeTypes() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        given()
                .headers(headers)
        .when()
                .get("/grades/types")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToCreateGradeScale() {
        Headers headers = createHeadersWithRandomAdminUser();
        GradeScaleRequest request = Instancio.create(GradeScaleRequest.class);
        request.setIsActive(true);

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("/grades/scales")
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(request.getName()));
    }

    @Test
    void shouldNotAllowOtherUserToCreateGradeScale() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());
        GradeScaleRequest request = Instancio.create(GradeScaleRequest.class);

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("/grades/scales")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToPatchGradeScale() {
        Headers headers = createHeadersWithRandomAdminUser();
        PatchGradeScaleRequest request = Instancio.create(PatchGradeScaleRequest.class);
        request.setIsActive(true);
        GradeScaleEntity gradeScale = dataHelper.createGradeScale(true);

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("/grades/scales/" + gradeScale.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(request.getName()));
    }

    @Test
    void shouldNotAllowOtherUserToPatchGradeScale() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());
        PatchGradeScaleRequest request = Instancio.create(PatchGradeScaleRequest.class);

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("/grades/scales/" + 123)
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToDeleteGradeScale() {
        Headers headers = createHeadersWithRandomAdminUser();
        GradeScaleEntity gradeScale = dataHelper.createGradeScale(true);
        GradeScaleEntity anotherGradeScale = dataHelper.createGradeScale(false);

        given()
                .headers(headers)
        .when()
                .delete("/grades/scales/" + anotherGradeScale.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotAllowOtherUserToDeleteGradeScale() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        given()
                .headers(headers)
        .when()
                .delete("/grades/scales/" + 123)
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToDeleteGradeValue() {
        Headers headers = createHeadersWithRandomAdminUser();
        GradeScaleEntity gradeScale = dataHelper.createGradeScale(true);
        GradeValueEntity gradeValue = dataHelper.createGradeValue(gradeScale);

        given()
                .headers(headers)
        .when()
                .delete("grades/scales/" + gradeScale.getId() + "/values/" + gradeValue.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotAllowOtherUserToDeleteGradeValue() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        given()
                .headers(headers)
        .when()
                .delete("grades/scales/" + 123 + "/values/" + 456)
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToCreateGradeValue() {
        Headers headers = createHeadersWithRandomAdminUser();
        GradeScaleEntity gradeScale = dataHelper.createGradeScale(true);
        GradeValueDto gradeValueDto = Instancio.create(GradeValueDto.class);

        given()
                .headers(headers)
                .body(gradeValueDto)
        .when()
                .post("grades/scales/" + gradeScale.getId() + "/values")
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("displayValue", equalTo(gradeValueDto.getDisplayValue()));
    }

    @Test
    void shouldNotAllowOtherUserToCreateGradeValue() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());
        GradeValueDto gradeValueDto = Instancio.create(GradeValueDto.class);

        given()
                .headers(headers)
                .body(gradeValueDto)
        .when()
                .post("grades/scales/" + 123 + "/values")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetScaleById() {
        GradeScaleEntity gradeScale = dataHelper.createGradeScale(true);
        Headers headers = createHeadersWithRandomAdminUser();

        given()
                .headers(headers)
        .when()
                .get("grades/scales/" + gradeScale.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(gradeScale.getId().intValue()));
    }

    @Test
    void shouldNotAllowOtherUserToGetScaleById() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        given()
                .headers(headers)
        .when()
                .get("grades/scales/" + 123)
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetAllGradeScales() {
        Headers headers = createHeadersWithRandomAdminUser();
        List<GradeScaleEntity> gradeScaleEntities = new ArrayList<>();
        gradeScaleEntities.add(dataHelper.createGradeScale(true));
        for(int i = 0; i < 5; i++)
            gradeScaleEntities.add(dataHelper.createGradeScale(false));

        given()
                .headers(headers)
        .when()
                .get("grades/scales")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(gradeScaleEntities.size()));
    }

    @Test
    void shouldNotAllowOtherUserToGetAllGradeScales() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        given()
                .headers(headers)
        .when()
                .get("grades/scales")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetActiveGradeScale() {
        Headers headers = createHeadersWithRandomAdminUser();
        GradeScaleEntity scale = dataHelper.createGradeScale(true);

        given()
                .headers(headers)
        .when()
                .get("grades/scales/active")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(scale.getId().intValue()));
    }

    @Test
    void shouldNotAllowOtherUserToGetActiveGradeScale() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        given()
                .headers(headers)
        .when()
                .get("grades/scales/active")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToCreateGrade() {
        Headers headers = createHeadersWithRandomAdminUser();
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), null);
        StudentEntity student = dataHelper.createStudent(yearbook, null);
        AddGradeRequest gradeRequest = dataHelper.createAddGradeRequest(teacher, subject, student, null);

        given()
                .headers(headers)
                .body(gradeRequest)
        .when()
                .post("grades")
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("comments", equalTo(gradeRequest.getComments()));
    }

    @Test
    void shouldAllowTeacherToCreateGrade() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), null);
        StudentEntity student = dataHelper.createStudent(yearbook, null);
        Headers headers = buildBasicHeaders(teacher.getEmail());
        AddGradeRequest request = dataHelper.createAddGradeRequest(teacher, subject, student, null);

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("grades")
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("comments", equalTo(request.getComments()));
    }

    @Test
    void shouldNotAllowParentStudentToCreateGrade() {
        List<String> userTypes = List.of("PARENT", "STUDENT");
        Headers headers = createHeadersForRandomUserNotAdmin(userTypes);
        AddGradeRequest request = dataHelper.createAddGradeRequest(null, null, null, null);

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("grades")
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToPatchGrade() {
        Headers headers = createHeadersWithRandomAdminUser();
        GradeEntity grade = dataHelper.createGradeEntity(null, null, false);
        PatchGradeRequest request = new PatchGradeRequest();
        request.setComments("com123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("comments", equalTo(request.getComments()));
    }

    @Test
    void shouldAllowTeacherToPatchGrade() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        Headers headers = buildBasicHeaders(teacher.getEmail());
        GradeEntity grade = dataHelper.createGradeEntity(teacher, subject, true);
        PatchGradeRequest request = new PatchGradeRequest();
        request.setComments("com123");
        request.setSubjectId(subject.getId());

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("comments", equalTo(request.getComments()));
    }

    @Test
    void shouldNotAllowTeacherToPatchGradeIfNotGradeOwner() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        Headers headers = buildBasicHeaders(teacher.getEmail());
        GradeEntity grade = dataHelper.createGradeEntity(teacher, subject, true);
        PatchGradeRequest request = new PatchGradeRequest();

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldNotAllowTeacherToPatchGradeIfNotTeachingSubject() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(null);
        Headers headers = buildBasicHeaders(teacher.getEmail());
        GradeEntity grade = dataHelper.createGradeEntity(teacher, subject, true);
        PatchGradeRequest request = new PatchGradeRequest();

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldNotAllowStudentParentToPatchGrade() {
        List<String> userTypes = List.of("STUDENT", "PARENT");
        Headers headers = createHeadersForRandomUserNotAdmin(userTypes);
        PatchGradeRequest request = new PatchGradeRequest();

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("grades/" + 123)
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToDeleteGrade() {
        Headers headers = createHeadersWithRandomAdminUser();
        GradeEntity grade = dataHelper.createGradeEntity(null, null, false);

        given()
                .headers(headers)
        .when()
                .delete("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldAllowTeacherToDeleteGradeIfGradeOwnerAndSubjectTeacher() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        Headers headers = buildBasicHeaders(teacher.getEmail());
        GradeEntity grade = dataHelper.createGradeEntity(teacher, subject, true);

        given()
                .headers(headers)
        .when()
                .delete("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotAllowTeacherToDelGradeIfNotGradeOwner() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        Headers headers = buildBasicHeaders(teacher.getEmail());
        GradeEntity grade = dataHelper.createGradeEntity(teacher, subject, false);

        given()
                .headers(headers)
        .when()
                .delete("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldNotAllowTeacherToDelGradeIfNotSubjectTeacher() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(null);
        Headers headers = buildBasicHeaders(teacher.getEmail());
        GradeEntity grade = dataHelper.createGradeEntity(teacher, subject, true);

        given()
                .headers(headers)
        .when()
                .delete("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldNotAllowStudentParentToDelGrade() {
        List<String> userTypes = List.of("STUDENT", "PARENT");
        Headers headers = createHeadersForRandomUserNotAdmin(userTypes);

        given()
                .headers(headers)
        .when()
                .delete("grades/" + 123)
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToGetGradeById() {
        Headers headers = createHeadersWithRandomAdminUser();
        GradeEntity grade = dataHelper.createGradeEntity(null , null, false);

        given()
                .headers(headers)
        .when()
                .get("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldAllowTeacherToGetGradeByIdIfHeadTeacherOfStudent() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), teacher);
        StudentEntity student = dataHelper.createStudent(yearbook, null);
        GradeEntity grade = dataHelper.createGradeEntity(student, subject, null);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldAllowTeacherToGetGradeByIdIfGradeOwner() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.emptyList());
        GradeEntity grade = dataHelper.createGradeEntity(teacher, subject, true);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldNotAllowRandomTeacherToGetGradeById() {
        TeacherEntity teacher = dataHelper.createTeacher(Collections.emptyList());
        GradeEntity grade = dataHelper.createGradeEntity((StudentEntity) null);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowStudentToGetGradeByIdIfGradeOwner() {
        StudentEntity student = dataHelper.createStudent(null, null);
        GradeEntity grade = dataHelper.createGradeEntity(student);
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("comments", equalTo(grade.getComments()));
    }

    @Test
    void shouldNotAllowRandomStudentToGetGrade() {
        StudentEntity student = dataHelper.createStudent(null, null);
        Headers headers = buildBasicHeaders(student.getEmail());
        GradeEntity grade = dataHelper.createGradeEntity((StudentEntity) null);

        given()
                .headers(headers)
        .when()
                .get("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowParentToGetGradeByIdIfChildIsGradeOwner() {
        ParentEntity parent = dataHelper.createRandomParent();
        StudentEntity student = dataHelper.createStudent(null, Collections.singletonList(parent));
        Headers headers = buildBasicHeaders(parent.getEmail());
        GradeEntity grade = dataHelper.createGradeEntity(student);

        given()
                .headers(headers)
        .when()
                .get("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("comments", equalTo(grade.getComments()));
    }

    @Test
    void shouldNotAllowParentToGetGradeByIdIfChildNotGradeOwner() {
        ParentEntity parent = dataHelper.createRandomParent();
        StudentEntity student = dataHelper.createStudent(null, Collections.singletonList(parent));
        Headers headers = buildBasicHeaders(parent.getEmail());
        GradeEntity grade = dataHelper.createGradeEntity((StudentEntity) null);

        given()
                .headers(headers)
        .when()
                .get("grades/" + grade.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToGetGradesByStudentId() {
        StudentEntity student = dataHelper.createStudent(null, Collections.emptyList());
        List<GradeEntity> grades = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            grades.add(dataHelper.createGradeEntity(student));
        }
        Headers headers = createHeadersWithRandomAdminUser();

        given()
                .headers(headers)
        .when()
                .get("grades/student/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(grades.size()));
    }

    @Test
    void shouldAllowParentToGetGradesByStudentIdIfChild() {
        ParentEntity parent = dataHelper.createRandomParent();
        StudentEntity student = dataHelper.createStudent(null, Collections.singletonList(parent));
        List<GradeEntity> grades = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            grades.add(dataHelper.createGradeEntity(student));
        }
        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get("grades/student/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(grades.size()));
    }

    @Test
    void shouldNotAllowParentToGetGradesByStudentIdIfNotChild() {
        ParentEntity parent = dataHelper.createRandomParent();
        StudentEntity student = dataHelper.createStudent(null, null);

        Headers headers = buildBasicHeaders(parent.getEmail());

        given()
                .headers(headers)
        .when()
                .get("grades/student/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowStudentToGetGradeBySelfId() {
        StudentEntity student = dataHelper.createStudent(null, Collections.emptyList());
        List<GradeEntity> grades = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            grades.add(dataHelper.createGradeEntity(student));
        }
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get("grades/student/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(grades.size()));
    }

    @Test
    void shouldNotAllowStudentToGetGradeByStudentIdIfOtherStudentIdRequested() {
        StudentEntity student = dataHelper.createStudent(null, Collections.emptyList());
        StudentEntity otherStudent = dataHelper.createStudent(null, Collections.emptyList());
        List<GradeEntity> grades = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            grades.add(dataHelper.createGradeEntity(otherStudent));
        }
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get("grades/student/" + otherStudent.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowTeacherToGetGradeByStudentIdIfHeadTeacherOfStudent() {
        TeacherEntity teacher = dataHelper.createTeacher(Collections.emptyList());
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.emptyList(), teacher);
        StudentEntity student = dataHelper.createStudent(yearbook, Collections.emptyList());
        List<GradeEntity> grades = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            grades.add(dataHelper.createGradeEntity(student));
        }
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get("grades/student/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(grades.size()));
    }

    @Test
    @DisplayName("GET Grade by Student - should not allow if not head teacher of Student")
    void shouldNotAllowRandomTeacherToGetGradeByStudentIdIfNotHeadTeacherOfStudent() {
        TeacherEntity teacher = dataHelper.createTeacher(Collections.emptyList());
        YearbookEntity yearbook = dataHelper.createYearbook(null, teacher);
        Headers headers = createHeadersWithRandomTeacherUser();
        StudentEntity student = dataHelper.createStudent(yearbook, Collections.emptyList());
        GradeEntity grade = dataHelper.createGradeEntity(student);

        given()
                .headers(headers)
        .when()
                .get("grades/student/" + student.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToGetGradeByStudentAndSubjectId() {
        Headers headers = createHeadersWithRandomAdminUser();
        SubjectEntity subject = dataHelper.createSubject();
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), null);
        StudentEntity student = dataHelper.createStudent(yearbook, null);
        GradeEntity grade = dataHelper.createGradeEntity(student, subject, null);
        for(int i = 0; i < 4; i++)
            dataHelper.createGradeEntity((StudentEntity) null);

        given()
                .headers(headers)
        .when()
                .get(String.format("grades/student/%d/subject/%d", student.getId(), subject.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1))
                .body("get(0).id", equalTo(grade.getId().intValue()));
    }

    @Test
    @DisplayName("GET Grade by Student and Subject - should allow if Teacher teaches Subject")
    void shouldAllowTeacherToGetGradeForStudentBySubjectIfTeacherTeachSubject() {
        SubjectEntity subject = dataHelper.createSubject();
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), null);
        StudentEntity student = dataHelper.createStudent(yearbook, null);
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        GradeEntity grade = dataHelper.createGradeEntity(student, subject, teacher);
        for(int i = 0; i < 4; i++)
            dataHelper.createGradeEntity((StudentEntity) null);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("grades/student/%d/subject/%d", student.getId(), subject.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1))
                .body("get(0).id", equalTo(grade.getId().intValue()));
    }

    @Test
    @DisplayName("GET Grade by Student and Subject - should allow if Teacher is head Teacher of Student")
    void shouldAllowTecherToGetGradeForStudentBySubjectIfHeadTeacherOfStudent() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.emptyList());
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), teacher);
        StudentEntity student = dataHelper.createStudent(yearbook, null);
        GradeEntity grade = dataHelper.createGradeEntity(student, subject, teacher);
        for(int i = 0; i < 4; i++)
            dataHelper.createGradeEntity((StudentEntity) null);
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("grades/student/%d/subject/%d", student.getId(), subject.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1))
                .body("get(0).id", equalTo(grade.getId().intValue()));
    }

    @Test
    void shouldAllowStudentToGetGradeIfSelfGradeAndSubjectAttendant() {
        SubjectEntity subject = dataHelper.createSubject();
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), null);
        StudentEntity student = dataHelper.createStudent(yearbook,null);
        Headers headers = buildBasicHeaders(student.getEmail());
        GradeEntity grade = dataHelper.createGradeEntity(student, subject ,null);
        GradeEntity anotherGrade = dataHelper.createGradeEntity((StudentEntity) null);

        given()
                .headers(headers)
        .when()
                .get(String.format("grades/student/%d/subject/%d", student.getId(), subject.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1))
                .body("get(0).id", equalTo(grade.getId().intValue()));
    }

    @Test
    void shouldNotAllowStudentToGetGradeIfNotSelfGrade() {
        SubjectEntity subject = dataHelper.createSubject();
        StudentEntity student = dataHelper.createStudent(null,null);
        StudentEntity anotherStudent = dataHelper.createStudent(null, null);
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("grades/student/%d/subject/%d", anotherStudent.getId(), subject.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldNotAllowStudentToGetGradeIfSelfGradeButNotSubjectAttendant() {
        SubjectEntity subject = dataHelper.createSubject();
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        StudentEntity student = dataHelper.createStudent(yearbook,null);
        Headers headers = buildBasicHeaders(student.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("grades/student/%d/subject/%d", student.getId(), subject.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToGetGradesBySubject() {
        Headers headers = createHeadersWithRandomAdminUser();
        SubjectEntity subject = dataHelper.createSubject();
        int gradeCount = 0;
        for(int i = 0; i < 5; i++){
            dataHelper.createGradeEntity(null, subject, false);
            gradeCount++;
        }

        given()
                .headers(headers)
        .when()
                .get(String.format("grades/subject/%d", subject.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(gradeCount));
    }

    @Test
    void shouldAllowTeacherToGetGradesBySubjectIfTeachingSubject() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        Headers headers = buildBasicHeaders(teacher.getEmail());
        int gradeCount = 0;
        for(int i = 0; i < 5; i++){
            dataHelper.createGradeEntity(teacher, subject, true);
            gradeCount++;
        }

        given()
                .headers(headers)
        .when()
                .get(String.format("grades/subject/%d", subject.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(gradeCount));
    }

    @Test
    void shouldNotAllowTeacherToGetGradesBySubjectIfNotTeachingSubject() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.emptyList());
        Headers headers = buildBasicHeaders(teacher.getEmail());

        given()
                .headers(headers)
        .when()
                .get(String.format("grades/subject/%d", subject.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldNotAllowStudentParentToGetGradesBySubject() {
        List<String> userTypes = List.of("PARENT", "STUDENT");
        Headers headers = createHeadersForRandomUserNotAdmin(userTypes);
        SubjectEntity subject = dataHelper.createSubject();

        given()
                .headers(headers)
        .when()
                .get(String.format("grades/subject/%d", subject.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToGetGradesByGradeType() {
        Headers headers = createHeadersWithRandomAdminUser();
        GradeTypeEntity gradeType = dataHelper.createGradeType();
        int gradeCount = 0;
        for(int i = 0; i < 5; i++){
            dataHelper.createGradeEntity(gradeType);
            gradeCount++;
        }

        given()
                .headers(headers)
        .when()
                .get(String.format("grades/type/%d", gradeType.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(gradeCount));
    }
}