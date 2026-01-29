package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.integration.BasicCrudHappyPathTests;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.yearbook.model.AddYearbookRequest;
import com.cisowski.schoolmanagement.yearbook.model.PatchYearbookRequest;
import com.cisowski.schoolmanagement.yearbook.model.YearbookDetailedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class YearbookTest extends BaseIntegrationTest implements BasicCrudHappyPathTests {

    @Test
    @Override
    public void shouldCreateAndFetchEntity() {
        AddYearbookRequest postRequest = dataHelper.createAddYearbookRequest(null, null);
        Integer yearbookId = postEntity(fullAdminHeaders, "yearbooks", postRequest);

        YearbookDetailedResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("yearbooks/" + yearbookId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(YearbookDetailedResponse.class);
        assertEquals(yearbookId, getResponse.getId());
        assertEquals(postRequest.getHeadTeacherId(), getResponse.getHeadTeacher().getId());
        assertEquals(postRequest.getSymbol(), getResponse.getSymbol());
        assertEquals(postRequest.getStartingYear().getYear(), getResponse.getStartingYear().getYear());
        assertEquals(postRequest.getGraduationYear().getYear(), getResponse.getGraduationYear().getYear());
        assertEquals(postRequest.getMainCourseSubjectsIds().size(), getResponse.getMainCourseSubjects().size());
    }

    @Test
    @Override
    public void shouldFetchAllEntities() {
        List<Integer> yearbookIds = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            AddYearbookRequest postRequest = dataHelper.createAddYearbookRequest(null, null);
            Integer yearbookId = postEntity(fullAdminHeaders, "yearbooks", postRequest);
            yearbookIds.add(yearbookId);
        }

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("yearbooks")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(yearbookIds.size()));
    }

    @Test
    @Override
    public void shouldCreateUpdateAndFetchEntity() {
        SubjectEntity postSubject = dataHelper.createSubject();
        AddYearbookRequest postRequest = dataHelper.createAddYearbookRequest(null, Collections.singletonList(postSubject));
        Integer yearbookId = postEntity(fullAdminHeaders, "yearbooks", postRequest);

        SubjectEntity patchSubject = dataHelper.createSubject();
        PatchYearbookRequest patchYearbookRequest = dataHelper.createPatchYearbookRequest(null, Collections.singletonList(patchSubject), Collections.singletonList(postSubject));

        YearbookDetailedResponse patchResponse = given()
                .headers(fullAdminHeaders)
                .body(patchYearbookRequest)
        .when()
                .patch("yearbooks/" + yearbookId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(YearbookDetailedResponse.class);
        assertEquals(yearbookId, patchResponse.getId());
        assertEquals(patchYearbookRequest.getHeadTeacherId(), patchResponse.getHeadTeacher().getId());
        assertEquals(patchYearbookRequest.getSymbol(), patchResponse.getSymbol());
        assertEquals(patchYearbookRequest.getStartingYear().getYear(), patchResponse.getStartingYear().getYear());
        assertEquals(patchYearbookRequest.getTargetGraduationYear().getYear(), patchResponse.getGraduationYear().getYear());
        assertEquals(patchSubject.getId(), patchResponse.getMainCourseSubjects().iterator().next().getId());
        assertFalse(patchResponse.getMainCourseSubjects().stream().anyMatch(subject -> subject.getId().equals(postSubject.getId())));

        YearbookDetailedResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("yearbooks/" + yearbookId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(YearbookDetailedResponse.class);
        assertEquals(yearbookId, getResponse.getId());
        assertEquals(patchYearbookRequest.getHeadTeacherId(), getResponse.getHeadTeacher().getId());
        assertEquals(patchYearbookRequest.getSymbol(), getResponse.getSymbol());
        assertEquals(patchYearbookRequest.getStartingYear().getYear(), getResponse.getStartingYear().getYear());
        assertEquals(patchYearbookRequest.getTargetGraduationYear().getYear(), getResponse.getGraduationYear().getYear());
        assertEquals(patchSubject.getId(), getResponse.getMainCourseSubjects().iterator().next().getId());
        assertFalse(getResponse.getMainCourseSubjects().stream().anyMatch(subject -> subject.getId().equals(postSubject.getId())));
    }

    @Test
    @Override
    public void shouldCreateDeleteAndNotFetchEntity() {
        AddYearbookRequest postRequest = dataHelper.createAddYearbookRequest(null, null);
        Integer yearbookId = postEntity(fullAdminHeaders, "yearbooks", postRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("yearbooks/" + yearbookId)
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("yearbooks/" + yearbookId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldThrowExOnUpdate_whenSymbolNotUnique() {
        AddYearbookRequest postRequest = dataHelper.createAddYearbookRequest(null, null);
        Integer yearbookId = postEntity(fullAdminHeaders, "yearbooks", postRequest);
        PatchYearbookRequest patchRequest = new PatchYearbookRequest();
        patchRequest.setSymbol(postRequest.getSymbol());

        given()
                .headers(fullAdminHeaders)
                .body(patchRequest)
        .when()
                .patch("yearbooks/" + yearbookId)
        .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldThrowExOnUpdate_whenHeadTeacherNotUnique() {
        AddYearbookRequest postRequest = dataHelper.createAddYearbookRequest(null, null);
        Integer yearbookId = postEntity(fullAdminHeaders, "yearbooks", postRequest);
        PatchYearbookRequest patchRequest = new PatchYearbookRequest();
        patchRequest.setHeadTeacherId(postRequest.getHeadTeacherId());

        given()
                .headers(fullAdminHeaders)
                .body(patchRequest)
        .when()
                .patch("yearbooks/" + yearbookId)
        .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }
}
