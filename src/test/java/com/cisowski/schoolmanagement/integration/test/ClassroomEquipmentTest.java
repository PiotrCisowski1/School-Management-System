package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.classroom.model.*;
import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.integration.BasicCrudHappyPathTests;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ClassroomEquipmentTest extends BaseIntegrationTest implements BasicCrudHappyPathTests {

    @Test
    @Override
    public void shouldCreateAndFetchEntity() {
        EquipmentRequest request = Instancio.create(EquipmentRequest.class);

        String postEqPath = "classrooms/equipments";
        Integer eqId = postEntity(fullAdminHeaders, postEqPath, request);

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("/classrooms/equipments/" + eqId)
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(request.getName()))
                .body("id", equalTo(eqId));
    }

    @Test
    @Override
    public void shouldFetchAllEntities() {
        List<Integer> eqIds = new ArrayList<>();
        String postEqPath = "classrooms/equipments";

        for(int i = 0; i < 7; i++) {
            EquipmentRequest request = Instancio.create(EquipmentRequest.class);
            Integer eqId = postEntity(fullAdminHeaders, postEqPath, request);
            eqIds.add(eqId);
        }

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("/classrooms/equipments")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(eqIds.size()));
    }

    @Override
    public void shouldCreateUpdateAndFetchEntity() {
        // no patch endpoint for equipment
    }

    @Test
    @Override
    public void shouldCreateDeleteAndNotFetchEntity() {
        EquipmentRequest request = Instancio.create(EquipmentRequest.class);

        String postEqPath = "classrooms/equipments";
        Integer eqId = postEntity(fullAdminHeaders, postEqPath, request);

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("/classrooms/equipments/" + eqId)
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(request.getName()))
                .body("id", equalTo(eqId));

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("/classrooms/equipments/" + eqId)
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("/classrooms/equipments/" + eqId)
        .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void shouldNotDeleteEqWhenStillInUse() {
        EquipmentRequest request = Instancio.create(EquipmentRequest.class);

        String postEqPath = "classrooms/equipments";
        Integer eqId = postEntity(fullAdminHeaders, postEqPath, request);

        EquipmentQuantity equipmentQuantity = new EquipmentQuantity();
        equipmentQuantity.setEquipmentId(eqId);
        equipmentQuantity.setQuantity(3);
        ClassroomRequest classroomRequest = dataHelper.createClassroomRequest(Collections.singletonList(equipmentQuantity));
        String postClassroomPath = "classrooms";
        Integer classroomId = postEntity(fullAdminHeaders, postClassroomPath, classroomRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("/classrooms/equipments/" + eqId)
        .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value());
    }
}
