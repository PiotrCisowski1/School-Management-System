package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.integration.BasicCrudHappyPathTests;
import com.cisowski.schoolmanagement.users.student.model.StudentCreateRequest;
import com.cisowski.schoolmanagement.users.student.model.StudentDetailedResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentPatchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StudentTest extends BaseIntegrationTest implements BasicCrudHappyPathTests {

    @Test
    @Override
    public void shouldCreateAndFetchEntity() {
        StudentCreateRequest postRequest = dataHelper.createStudentCreateRequest(null);
        Integer studentId = postEntity(fullAdminHeaders, "students", postRequest);

        StudentDetailedResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("students/" + studentId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(StudentDetailedResponse.class);
        assertEquals(studentId, getResponse.getId());
        assertEquals(postRequest.getEmail(), getResponse.getEmail());
        assertEquals(postRequest.getPhoneNumber(), getResponse.getPhoneNumber());
        assertEquals(postRequest.getFirstName(), getResponse.getFirstName());
        assertEquals(postRequest.getLastName(), getResponse.getLastName());
        assertEquals(postRequest.getBirthDate(), getResponse.getBirthDate());
        assertEquals(postRequest.getGender(), getResponse.getGender());
        assertEquals(postRequest.getYearbookId(), getResponse.getYearbook().getId());
    }

    @Test
    @Override
    public void shouldFetchAllEntities() {
        List<Integer> studentIds = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            StudentCreateRequest postRequest = dataHelper.createStudentCreateRequest(null);
            Integer studentId = postEntity(fullAdminHeaders, "students", postRequest);
            studentIds.add(studentId);
        }

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("students")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(studentIds.size()));
    }

    @Test
    @Override
    public void shouldCreateUpdateAndFetchEntity() {
        StudentCreateRequest postRequest = dataHelper.createStudentCreateRequest(null);
        Integer studentId = postEntity(fullAdminHeaders, "students", postRequest);

        StudentPatchRequest patchRequest = dataHelper.createStudentPatchRequest(null);
        StudentDetailedResponse patchResponse = given()
                .headers(fullAdminHeaders)
                .body(patchRequest)
        .when()
                .patch("students/" + studentId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(StudentDetailedResponse.class);
        assertEquals(studentId, patchResponse.getId());
        assertEquals(patchRequest.getEmail(), patchResponse.getEmail());
        assertEquals(patchRequest.getPhoneNumber(), patchResponse.getPhoneNumber());
        assertEquals(patchRequest.getFirstName(), patchResponse.getFirstName());
        assertEquals(patchRequest.getLastName(), patchResponse.getLastName());
        assertEquals(patchRequest.getBirthDate(), patchResponse.getBirthDate());
        assertEquals(patchRequest.getGender(), patchResponse.getGender());
        assertEquals(patchRequest.getYearbookId(), patchResponse.getYearbook().getId());

        StudentDetailedResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("students/" + studentId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(StudentDetailedResponse.class);
        assertEquals(studentId, patchResponse.getId());
        assertEquals(patchRequest.getEmail(), getResponse.getEmail());
        assertEquals(patchRequest.getPhoneNumber(), getResponse.getPhoneNumber());
        assertEquals(patchRequest.getFirstName(), getResponse.getFirstName());
        assertEquals(patchRequest.getLastName(), getResponse.getLastName());
        assertEquals(patchRequest.getBirthDate(), getResponse.getBirthDate());
        assertEquals(patchRequest.getGender(), getResponse.getGender());
        assertEquals(patchRequest.getYearbookId(), getResponse.getYearbook().getId());
    }

    @Test
    @Override
    public void shouldCreateDeleteAndNotFetchEntity() {
        StudentCreateRequest postRequest = dataHelper.createStudentCreateRequest(null);
        Integer studentId = postEntity(fullAdminHeaders, "students", postRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("students/" + studentId)
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("students/" + studentId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
