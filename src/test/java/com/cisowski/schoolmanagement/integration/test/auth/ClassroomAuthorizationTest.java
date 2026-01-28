package com.cisowski.schoolmanagement.integration.test.auth;


import com.cisowski.schoolmanagement.classroom.model.*;
import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import io.restassured.http.Headers;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ClassroomAuthorizationTest extends BaseIntegrationTest {


    @Test
    void shouldAllowAdminToAddEquipment() {
        EquipmentRequest request = Instancio.create(EquipmentRequest.class);
        Headers headers = createHeadersWithRandomAdminUser();

        given()
                .headers(headers)
                .body(request)
        .when()
                .post("/classrooms/equipments")
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue());
    }

    @Test
    void shouldNotAllowOtherUserToAddEq() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        given()
                .headers(headers)
                .body(new EquipmentRequest())
        .when()
                .post("/classrooms/equipments")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToDeleteEquipment() {
        Equipment eq = dataHelper.createRandomEq();
        Headers headers = createHeadersWithRandomAdminUser();

        given()
                .headers(headers)
        .when()
                .delete("/classrooms/equipments/" + eq.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotAllowOtherUserToDeleteEq() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        given()
                .headers(headers)
        .when()
                .delete("/classrooms/equipments/" + 123)
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetAllEqs() {
        Headers headers = createHeadersWithRandomAdminUser();
        List<Equipment> eqs = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            eqs.add(dataHelper.createRandomEq());
        }

        given()
                .headers(headers)
        .when()
                .get("/classrooms/equipments")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(eqs.size()));
    }

    @Test
    void shouldNotAllowOtherUserToGetAllEqs() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        given()
                .headers(headers)
        .when()
                .get("/classrooms/equipments")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetEqById() {
        Headers headers = createHeadersWithRandomAdminUser();
        Equipment eq = dataHelper.createRandomEq();

        given()
                .headers(headers)
        .when()
                .get("/classrooms/equipments/" + eq.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(eq.getId()));
    }

    @Test
    void shouldNotAllowOtherUserToGetEqById() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        given()
                .headers(headers)
        .when()
                .get("/classrooms/equipments/" + 123)
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToCreateClassroom() {
        Headers headers = createHeadersWithRandomAdminUser();

        ClassroomRequest request = Instancio.create(ClassroomRequest.class);
        request.setEquipments(null);

        given()
                .body(request)
                .headers(headers)
        .when()
                .post("/classrooms")
        .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("name", equalTo(request.getName()));
    }

    @Test
    void shouldNotAllowOtherUserToCreateClassroom() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        ClassroomRequest request = Instancio.create(ClassroomRequest.class);
        request.setEquipments(null);

        given()
                .body(request)
                .headers(headers)
        .when()
                .post("/classrooms")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToDeleteClassroom() {
        Headers headers = createHeadersWithRandomAdminUser();
        ClassroomEntity classroomToDel = dataHelper.createClassroom();

        given()
                .headers(headers)
        .when()
                .delete("/classrooms/" + classroomToDel.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldNotAllowOtherUserToDeleteClassroom() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        given()
                .headers(headers)
        .when()
                .delete("/classrooms/" + 123)
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToGetClassroom() {
        Headers headers = createHeadersWithRandomAdminUser();
        ClassroomEntity classroom = dataHelper.createClassroom();
        testGetClassroomForUser(headers, classroom);
    }

    @Test
    void shouldAllowTeacherToGetClassroom() {
        TeacherEntity teacher = dataHelper.createTeacher(Collections.emptyList());
        Headers headers = buildBasicHeaders(teacher.getEmail());
        SubjectEntity subject = dataHelper.createSubject();
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), null);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        ClassroomEntity classroom = dataHelper.createClassroom(scheduleVersion, teacher, subject);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(scheduleVersion, teacher, subject, classroom);
        testGetClassroomForUser(headers, classroom);
    }

    @Test
    void shouldNotAllowTeacherToGetClassroomIfNotTeaching() {
        TeacherEntity teacher = dataHelper.createTeacher(Collections.emptyList());
        TeacherEntity anotherTeacher = dataHelper.createTeacher(Collections.emptyList());
        Headers headers = buildBasicHeaders(teacher.getEmail());
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);
        SubjectEntity subject = dataHelper.createSubject();
        ClassroomEntity classroom = dataHelper.createClassroom(scheduleVersion, anotherTeacher, subject);
        testShouldNotGetClassroomForUser(headers, classroom);
    }

    @Test
    void shouldAllowStudentToGetClassroom() {
        SubjectEntity subject = dataHelper.createSubject();
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.singletonList(subject), null);
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(yearbook);
        StudentEntity student = dataHelper.createStudent(yearbook, Collections.emptyList());
        TeacherEntity teacher = dataHelper.createTeacher(Collections.emptyList());
        ClassroomEntity classroom = dataHelper.createClassroom(scheduleVersion, teacher, subject);
        Headers headers = buildBasicHeaders(student.getEmail());
        testGetClassroomForUser(headers, classroom);
    }

    @Test
    void shouldNotAllowStudentToGetClassroomIfNotAttendingSubject() {
        YearbookEntity yearbook = dataHelper.createYearbook(Collections.emptyList(), null);
        StudentEntity student = dataHelper.createStudent(yearbook, Collections.emptyList());
        ClassroomEntity classroom = dataHelper.createClassroom();
        Headers headers = buildBasicHeaders(student.getEmail());
        testShouldNotGetClassroomForUser(headers, classroom);
    }

    private void testGetClassroomForUser(Headers headers, ClassroomEntity classroom) {
        given()
                .headers(headers)
        .when()
                .get("/classrooms/"  + classroom.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(classroom.getId()));
    }

    private void testShouldNotGetClassroomForUser(Headers headers, ClassroomEntity classroom) {
        given()
                .headers(headers)
        .when()
                .get("/classrooms/"  + classroom.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldAllowAdminToGetAllClassrooms() {
        Headers headers = createHeadersWithRandomAdminUser();
        List<ClassroomEntity> classrooms = new ArrayList<>();
        for(int i = 0; i < 5; i++)
            classrooms.add(dataHelper.createClassroom());

        given()
                .headers(headers)
        .when()
                .get("/classrooms")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(classrooms.size()));
    }

    @Test
    void shouldNotAllowOtherUserToGetAllClassrooms() {
        Headers headers = createHeadersForRandomUserNotAdmin(Collections.emptyList());

        given()
                .headers(headers)
        .when()
                .get("/classrooms")
        .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldAllowAdminToPatchClassroom() {
        Headers headers = createHeadersWithRandomAdminUser();
        ClassroomEntity classroom = dataHelper.createClassroom();
        PatchClassroomRequest request = new PatchClassroomRequest();
        request.setName("Test123");

        given()
                .headers(headers)
                .body(request)
        .when()
                .patch("/classrooms/" + classroom.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(request.getName()));
    }
}
