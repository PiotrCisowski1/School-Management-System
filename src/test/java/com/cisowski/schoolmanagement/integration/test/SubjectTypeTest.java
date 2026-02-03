package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.integration.BasicCrudHappyPathTests;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class SubjectTypeTest extends BaseIntegrationTest implements BasicCrudHappyPathTests {

    @Test
    @Override
    public void shouldCreateAndFetchEntity() {
        SubjectTypeRequest postRequest = new SubjectTypeRequest();
        postRequest.setName("test123");
        Integer subjectTypeId = postEntity(fullAdminHeaders, "subjects/subjectTypes", postRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("/subjects/subjectTypes/" + subjectTypeId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(postRequest.getName()));
    }

    @Test
    @Override
    public void shouldFetchAllEntities() {
        List<Integer> subjectTypeIds = new ArrayList<>();

        for(int i = 0; i < 10; i++){
            SubjectTypeRequest postRequest = new SubjectTypeRequest();
            postRequest.setName("test" + i);
            Integer subjectTypeId = postEntity(fullAdminHeaders, "subjects/subjectTypes", postRequest);
            subjectTypeIds.add(subjectTypeId);
        }

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("/subjects/subjectTypes")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(subjectTypeIds.size()));
    }

    @Test
    @Override
    public void shouldCreateUpdateAndFetchEntity() {
        SubjectTypeRequest postRequest = new SubjectTypeRequest();
        postRequest.setName("test123");
        Integer subjectTypeId = postEntity(fullAdminHeaders, "subjects/subjectTypes", postRequest);

        SubjectTypeRequest patchRequest = new SubjectTypeRequest();
        patchRequest.setName("xyz321");

        given()
                .headers(fullAdminHeaders)
                .body(patchRequest)
        .when()
                .patch("/subjects/subjectTypes/" + subjectTypeId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(patchRequest.getName()));

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("/subjects/subjectTypes/" + subjectTypeId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(patchRequest.getName()));
    }

    @Test
    @Override
    public void shouldCreateDeleteAndNotFetchEntity() {
        SubjectTypeRequest postRequest = new SubjectTypeRequest();
        postRequest.setName("test123");
        Integer subjectTypeId = postEntity(fullAdminHeaders, "subjects/subjectTypes", postRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("/subjects/subjectTypes/" + subjectTypeId)
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("/subjects/subjectTypes/" + subjectTypeId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldThrowEx_whenNameNotUnique_onUpdate() {
        SubjectTypeEntity existingType = dataHelper.createSubjectType();
        SubjectTypeEntity updatedType = dataHelper.createSubjectType();

        SubjectTypeRequest patchRequest = new SubjectTypeRequest();
        patchRequest.setName(existingType.getName());

        given()
                .headers(fullAdminHeaders)
                .body(patchRequest)
        .when()
                .patch("/subjects/subjectTypes/" + updatedType.getId())
        .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }
}
