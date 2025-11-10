package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.integration.BasicCrudHappyPathTests;
import com.cisowski.schoolmanagement.subject.model.*;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubjectTest extends BaseIntegrationTest implements BasicCrudHappyPathTests {

    @Test
    @Override
    public void shouldCreateAndFetchEntity() {
        SubjectTypeEntity subjectType = dataHelper.createSubjectType();
        AddSubjectRequest postSubjectRequest = dataHelper.createAddSubjectRequest(subjectType);
        Integer createdSubjectId = postEntity(fullAdminHeaders, "subjects", postSubjectRequest);

        SubjectDetailedResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("/subjects/" + createdSubjectId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(SubjectDetailedResponse.class);

        assertEquals(createdSubjectId, getResponse.getId());
        assertEquals(postSubjectRequest.getName(), getResponse.getName());
        assertEquals(postSubjectRequest.getCode(), getResponse.getCode());
        assertEquals(postSubjectRequest.getDescription(), getResponse.getDescription());
        assertTrue(getResponse.getTeachers().isEmpty());
        assertTrue(getResponse.getYearbooksTakingSubject().isEmpty());
    }

    @Test
    @Override
    public void shouldFetchAllEntities() {
        List<Integer> subjectIds = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
            SubjectTypeEntity subjectType = dataHelper.createSubjectType();
            AddSubjectRequest postSubjectRequest = dataHelper.createAddSubjectRequest(subjectType);
            Integer createdSubjectId = postEntity(fullAdminHeaders, "subjects", postSubjectRequest);
            subjectIds.add(createdSubjectId);
        }

        List<SubjectSummaryResponse> getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("/subjects")
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath()
                .getList("", SubjectSummaryResponse.class);
        assertEquals(subjectIds.size(), getResponse.size());
    }

    @Test
    @Override
    public void shouldCreateUpdateAndFetchEntity() {
        SubjectTypeEntity subjectType = dataHelper.createSubjectType();
        AddSubjectRequest postSubjectRequest = dataHelper.createAddSubjectRequest(subjectType);
        Integer createdSubjectId = postEntity(fullAdminHeaders, "subjects", postSubjectRequest);

        SubjectTypeEntity patchSubjectType = dataHelper.createSubjectType();
        PatchSubjectRequest patchSubjectRequest = Instancio.create(PatchSubjectRequest.class);
        patchSubjectRequest.setSubjectTypeId(patchSubjectType.getId());
        patchSubjectRequest.setCode("test1");
        SubjectDetailedResponse patchResponse = given()
                .headers(fullAdminHeaders)
                .body(patchSubjectRequest)
        .when()
                .patch("subjects/" + createdSubjectId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(SubjectDetailedResponse.class);
        assertEquals(patchSubjectRequest.getName(), patchResponse.getName());
        assertEquals(patchSubjectRequest.getCode(), patchResponse.getCode());
        assertEquals(patchSubjectRequest.getDescription(), patchResponse.getDescription());
        assertEquals(patchSubjectType.getName(), patchResponse.getSubjectType());

        SubjectDetailedResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("subjects/" + createdSubjectId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(SubjectDetailedResponse.class);
        assertEquals(patchSubjectRequest.getName(), getResponse.getName());
        assertEquals(patchSubjectRequest.getCode(), getResponse.getCode());
        assertEquals(patchSubjectRequest.getDescription(), getResponse.getDescription());
        assertEquals(patchSubjectType.getName(), getResponse.getSubjectType());
    }

    @Test
    @Override
    public void shouldCreateDeleteAndNotFetchEntity() {
        SubjectTypeEntity subjectType = dataHelper.createSubjectType();
        AddSubjectRequest postSubjectRequest = dataHelper.createAddSubjectRequest(subjectType);
        Integer createdSubjectId = postEntity(fullAdminHeaders, "subjects", postSubjectRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("subjects/" + createdSubjectId)
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("subjects/" + createdSubjectId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldCreateAndFetchSubjectByCode() {
        SubjectTypeEntity subjectType = dataHelper.createSubjectType();
        AddSubjectRequest postSubjectRequest = dataHelper.createAddSubjectRequest(subjectType);
        Integer createdSubjectId = postEntity(fullAdminHeaders, "subjects", postSubjectRequest);

        SubjectDetailedResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("subjects/code/" + postSubjectRequest.getCode())
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(SubjectDetailedResponse.class);
        assertEquals(createdSubjectId, getResponse.getId());
        assertEquals(postSubjectRequest.getName(), getResponse.getName());
        assertEquals(postSubjectRequest.getCode(), getResponse.getCode());
        assertEquals(postSubjectRequest.getDescription(), getResponse.getDescription());
    }

    @Test
    void shouldCreateAndFetchSubjectsBySubjectType() {
        SubjectTypeEntity subjectType = dataHelper.createSubjectType();
        AddSubjectRequest postSubjectRequest = dataHelper.createAddSubjectRequest(subjectType);
        postEntity(fullAdminHeaders, "subjects", postSubjectRequest);

        List<Integer> subjectIds = new ArrayList<>();
        SubjectTypeEntity anotherSubjectType = dataHelper.createSubjectType();
        for(int i = 0; i < 10; i++) {
            AddSubjectRequest anotherSubjectRequest = dataHelper.createAddSubjectRequest(anotherSubjectType);
            Integer anotherSubjectId = postEntity(fullAdminHeaders, "subjects", anotherSubjectRequest);
            subjectIds.add(anotherSubjectId);
        }

        List<SubjectSummaryResponse> getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("subjects/type/" + anotherSubjectType.getName())
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath()
                .getList("", SubjectSummaryResponse.class);
        assertEquals(subjectIds.size(), getResponse.size());
        List<SubjectSummaryResponse> noMatchSubjects = getResponse.stream()
                .filter(subject -> !subjectIds.contains(subject.getId()))
                .toList();
        assertTrue(noMatchSubjects.isEmpty());
    }
}
