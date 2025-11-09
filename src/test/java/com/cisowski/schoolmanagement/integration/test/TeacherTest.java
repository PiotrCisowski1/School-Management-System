package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.integration.BasicCrudHappyPathTests;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherCreateRequest;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherDetailedResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherPatchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TeacherTest extends BaseIntegrationTest implements BasicCrudHappyPathTests {

    @Test
    @Override
    public void shouldCreateAndFetchEntity() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherCreateRequest postRequest = dataHelper.createTeacherCreateRequest(Collections.singletonList(subject));
        Integer teacherId = postEntity(fullAdminHeaders, "teachers", postRequest);

        TeacherDetailedResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("teachers/" + teacherId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(TeacherDetailedResponse.class);
        assertEquals(teacherId, getResponse.getId());
        assertEquals(postRequest.getEmail(), getResponse.getEmail());
        assertEquals(postRequest.getPhoneNumber(), getResponse.getPhoneNumber());
        assertEquals(postRequest.getFirstName(), getResponse.getFirstName());
        assertEquals(postRequest.getLastName(), getResponse.getLastName());
        assertEquals(postRequest.getBirthDate(), getResponse.getBirthDate());
        assertEquals(postRequest.getGender(), getResponse.getGender());
        assertEquals(postRequest.getTeachingSubjectsIds().size(), getResponse.getTeachingSubjects().size());
        assertEquals(postRequest.getTeachingSubjectsIds().iterator().next(), getResponse.getTeachingSubjects().iterator().next().getId());
    }

    @Test
    @Override
    public void shouldFetchAllEntities() {
        List<Integer> teacherIds = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            TeacherCreateRequest postRequest = dataHelper.createTeacherCreateRequest(null);
            Integer teacherId = postEntity(fullAdminHeaders, "teachers", postRequest);
            teacherIds.add(teacherId);
        }

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("teachers")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(teacherIds.size()));
    }

    @Test
    @Override
    public void shouldCreateUpdateAndFetchEntity() {
        SubjectEntity postSubject = dataHelper.createSubject();
        TeacherCreateRequest postRequest = dataHelper.createTeacherCreateRequest(Collections.singletonList(postSubject));
        Integer teacherId = postEntity(fullAdminHeaders, "teachers", postRequest);

        SubjectEntity patchSubject = dataHelper.createSubject();
        TeacherPatchRequest patchRequest = dataHelper.createTeacherPatchRequest(Collections.singletonList(patchSubject), Collections.singletonList(postSubject));

        TeacherDetailedResponse patchResponse = given()
                .headers(fullAdminHeaders)
                .body(patchRequest)
        .when()
                .patch("teachers/" + teacherId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(TeacherDetailedResponse.class);
        assertEquals(teacherId, patchResponse.getId());
        assertEquals(patchRequest.getEmail(), patchResponse.getEmail());
        assertEquals(patchRequest.getPhoneNumber(), patchResponse.getPhoneNumber());
        assertEquals(patchRequest.getFirstName(), patchResponse.getFirstName());
        assertEquals(patchRequest.getLastName(), patchResponse.getLastName());
        assertEquals(patchRequest.getBirthDate(), patchResponse.getBirthDate());
        assertEquals(patchRequest.getGender(), patchResponse.getGender());
        assertEquals(patchRequest.getTeachingSubjectsIdsToAdd().iterator().next(), patchResponse.getTeachingSubjects().iterator().next().getId());
        assertTrue(patchResponse.getTeachingSubjects().stream().noneMatch(subject -> subject.getId().equals(postSubject.getId())));

        TeacherDetailedResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("teachers/" + teacherId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(TeacherDetailedResponse.class);
        assertEquals(teacherId, getResponse.getId());
        assertEquals(patchRequest.getEmail(), getResponse.getEmail());
        assertEquals(patchRequest.getPhoneNumber(), getResponse.getPhoneNumber());
        assertEquals(patchRequest.getFirstName(), getResponse.getFirstName());
        assertEquals(patchRequest.getLastName(), getResponse.getLastName());
        assertEquals(patchRequest.getBirthDate(), getResponse.getBirthDate());
        assertEquals(patchRequest.getGender(), getResponse.getGender());
        assertEquals(patchRequest.getTeachingSubjectsIdsToAdd().iterator().next(), getResponse.getTeachingSubjects().iterator().next().getId());
        assertTrue(getResponse.getTeachingSubjects().stream().noneMatch(subject -> subject.getId().equals(postSubject.getId())));
    }

    @Test
    @Override
    public void shouldCreateDeleteAndNotFetchEntity() {
        TeacherCreateRequest postRequest = dataHelper.createTeacherCreateRequest(null);
        Integer teacherId = postEntity(fullAdminHeaders, "teachers", postRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("teachers/" + teacherId)
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("teachers/" + teacherId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
