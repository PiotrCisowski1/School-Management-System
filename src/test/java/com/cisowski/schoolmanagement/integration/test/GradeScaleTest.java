package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeScaleRequest;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeScaleSummaryResponse;
import com.cisowski.schoolmanagement.grade.model.gradeScale.PatchGradeScaleRequest;
import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.integration.BasicCrudHappyPathTests;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GradeScaleTest extends BaseIntegrationTest implements BasicCrudHappyPathTests {

    @Test
    @Override
    public void shouldCreateAndFetchEntity() {
        GradeScaleRequest postScaleRequest = Instancio.of(GradeScaleRequest.class)
                .set(field(GradeScaleRequest::getIsActive), true)
                .create();
        Integer gradeScaleId = postEntity(fullAdminHeaders, "grades/scales", postScaleRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("/grades/scales/" + gradeScaleId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(gradeScaleId))
                .body("name", equalTo(postScaleRequest.getName()))
                .body("description", equalTo(postScaleRequest.getDescription()))
                .body("isActive", equalTo(postScaleRequest.getIsActive()))
                .body("isHide", equalTo(false))
                .body("gradeValues.size()", equalTo(postScaleRequest.getGradeValues().size()));
    }

    @Test
    @Override
    public void shouldFetchAllEntities() {
        List<Long> scaleIds = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            GradeScaleRequest postScaleRequest = Instancio.create(GradeScaleRequest.class);
            if(i == 0)
                postScaleRequest.setIsActive(true);
            Integer gradeScaleId = postEntity(fullAdminHeaders, "grades/scales", postScaleRequest);
            scaleIds.add(gradeScaleId.longValue());
        }

        List<GradeScaleSummaryResponse> response = given()
                .headers(fullAdminHeaders)
        .when()
                .get("/grades/scales")
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath().getList("", GradeScaleSummaryResponse.class);

        assertEquals(scaleIds.size(), response.size());
        List<Long> responseIds = response.stream().map(GradeScaleSummaryResponse::getId).toList();
        assertTrue(responseIds.containsAll(scaleIds));
    }

    @Test
    @Override
    public void shouldCreateUpdateAndFetchEntity() {
        GradeScaleRequest postScaleRequest = Instancio.of(GradeScaleRequest.class)
                .set(field(GradeScaleRequest::getIsActive), true)
                .create();
        Integer gradeScaleId = postEntity(fullAdminHeaders, "grades/scales", postScaleRequest);

        PatchGradeScaleRequest patchRequest = Instancio.of(PatchGradeScaleRequest.class)
                .set(field(PatchGradeScaleRequest::getIsActive), true)
                .create();

        given()
                .headers(fullAdminHeaders)
                .body(patchRequest)
        .when()
                .patch("/grades/scales/" + gradeScaleId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(patchRequest.getName()))
                .body("description", equalTo(patchRequest.getDescription()))
                .body("isActive", equalTo(patchRequest.getIsActive()));

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("/grades/scales/" + gradeScaleId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(patchRequest.getName()))
                .body("description", equalTo(patchRequest.getDescription()))
                .body("isActive", equalTo(patchRequest.getIsActive()));
    }

    @Test
    @Override
    public void shouldCreateDeleteAndNotFetchEntity() {
        GradeScaleRequest postScaleRequest = Instancio.of(GradeScaleRequest.class)
                .set(field(GradeScaleRequest::getIsActive), true)
                .create();
        Integer gradeScaleId = postEntity(fullAdminHeaders, "grades/scales", postScaleRequest);

        GradeScaleRequest anotherPostScaleRequest = Instancio.create(GradeScaleRequest.class);
        postEntity(fullAdminHeaders, "grades/scales", anotherPostScaleRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("/grades/scales/" + gradeScaleId)
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("/grades/scales/" + gradeScaleId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldFetchActiveGradeScale() {
        GradeScaleRequest postActiveGradeScale = Instancio.of(GradeScaleRequest.class)
                .set(field(GradeScaleRequest::getIsActive), true)
                .create();
        Integer activeGradeScaleId = postEntity(fullAdminHeaders, "grades/scales", postActiveGradeScale);

        GradeScaleRequest postInactiveGradeScale = Instancio.of(GradeScaleRequest.class)
                .set(field(GradeScaleRequest::getIsActive), false)
                .create();
        Integer inactiveGradeScaleId = postEntity(fullAdminHeaders, "grades/scales", postInactiveGradeScale);

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("/grades/scales/active")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(activeGradeScaleId))
                .body("name", equalTo(postActiveGradeScale.getName()))
                .body("description", equalTo(postActiveGradeScale.getDescription()));
    }
}
