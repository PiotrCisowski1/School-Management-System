package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.grade.model.gradeType.AddGradeTypeRequest;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import com.cisowski.schoolmanagement.grade.model.gradeType.PatchGradeTypeRequest;
import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.integration.BasicCrudHappyPathTests;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GradeTypeTest extends BaseIntegrationTest implements BasicCrudHappyPathTests {

    @Test
    @Override
    public void shouldCreateAndFetchEntity() {
        String postGradeTypePath = "grades/types";
        AddGradeTypeRequest request = Instancio.create(AddGradeTypeRequest.class);
        Integer gradeTypeId = postEntity(fullAdminHeaders, postGradeTypePath, request);

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("grades/types/" + gradeTypeId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(gradeTypeId))
                .body("gradeScope", equalTo(request.getGradeScope()))
                .body("weight", equalTo(request.getWeight().floatValue()));
    }

    @Test
    @Override
    public void shouldFetchAllEntities() {
        List<Integer> gradeTypeIds = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            String postGradeTypePath = "grades/types";
            AddGradeTypeRequest request = Instancio.create(AddGradeTypeRequest.class);
            Integer gradeTypeId = postEntity(fullAdminHeaders, postGradeTypePath, request);
            gradeTypeIds.add(gradeTypeId);
        }

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("grades/types")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(gradeTypeIds.size()));
    }

    @Test
    @Override
    public void shouldCreateUpdateAndFetchEntity() {
        String postGradeTypePath = "grades/types";
        AddGradeTypeRequest postRequest = Instancio.create(AddGradeTypeRequest.class);
        Integer gradeTypeId = postEntity(fullAdminHeaders, postGradeTypePath, postRequest);

        PatchGradeTypeRequest patchRequest = Instancio.create(PatchGradeTypeRequest.class);

        given()
                .headers(fullAdminHeaders)
                .body(patchRequest)
        .when()
                .patch("grades/types/" + gradeTypeId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(gradeTypeId))
                .body("gradeScope", equalTo(patchRequest.getGradeScope()))
                .body("weight", equalTo(patchRequest.getWeight().floatValue()));

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("grades/types/" + gradeTypeId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(gradeTypeId))
                .body("gradeScope", equalTo(patchRequest.getGradeScope()))
                .body("weight", equalTo(patchRequest.getWeight().floatValue()));
    }

    @Test
    @Override
    public void shouldCreateDeleteAndNotFetchEntity() {
        String postGradeTypePath = "grades/types";
        AddGradeTypeRequest postRequest = Instancio.create(AddGradeTypeRequest.class);
        Integer gradeTypeId = postEntity(fullAdminHeaders, postGradeTypePath, postRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("grades/types/" + gradeTypeId)
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("grades/types/" + gradeTypeId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldThrowEx_whenGradeScopeNotUnique_onUpdate() {
        GradeTypeEntity existingGradeType = dataHelper.createGradeType();
        GradeTypeEntity updatedGradeType = dataHelper.createGradeType();

        PatchGradeTypeRequest patchRequest = new PatchGradeTypeRequest();
        patchRequest.setGradeScope(existingGradeType.getGradeScope());

        given()
                .headers(fullAdminHeaders)
                .body(patchRequest)
        .when()
                .patch("grades/types/" + updatedGradeType.getId())
        .then()
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value());
    }
}
