package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.integration.BasicCrudHappyPathTests;
import com.cisowski.schoolmanagement.users.parent.model.ParentCreateRequest;
import com.cisowski.schoolmanagement.users.parent.model.ParentDetailedResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentPatchRequest;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.patch;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParentTest extends BaseIntegrationTest implements BasicCrudHappyPathTests {

    @Test
    @Override
    public void shouldCreateAndFetchEntity() {
        StudentEntity student = dataHelper.createStudent(null, null);
        ParentCreateRequest postRequest = dataHelper.createParentCreateRequest(Collections.singletonList(student.getId()));
        Integer parentId = postEntity(fullAdminHeaders, "parents", postRequest);

        ParentDetailedResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("parents/" + parentId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ParentDetailedResponse.class);
        assertEquals(parentId, getResponse.getId());
        assertEquals(postRequest.getEmail(), getResponse.getEmail());
        assertEquals(postRequest.getPhoneNumber(), getResponse.getPhoneNumber());
        assertEquals(postRequest.getFirstName(), getResponse.getFirstName());
        assertEquals(postRequest.getLastName(), getResponse.getLastName());
        assertEquals(postRequest.getBirthDate(), getResponse.getBirthDate());
        assertEquals(postRequest.getGender(), getResponse.getGender());
        assertEquals(getResponse.getChildren().iterator().next().getId(), student.getId());

    }

    @Test
    @Override
    public void shouldFetchAllEntities() {
        List<Integer> parentIds = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            StudentEntity student = dataHelper.createStudent(null, null);
            ParentCreateRequest postRequest = dataHelper.createParentCreateRequest(Collections.singletonList(student.getId()));
            Integer parentId = postEntity(fullAdminHeaders, "parents", postRequest);
            parentIds.add(parentId);
        }

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("parents")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(parentIds.size()));
    }

    @Test
    @Override
    public void shouldCreateUpdateAndFetchEntity() {
        StudentEntity postStudent = dataHelper.createStudent(null, null);
        ParentCreateRequest postRequest = dataHelper.createParentCreateRequest(Collections.singletonList(postStudent.getId()));
        Integer parentId = postEntity(fullAdminHeaders, "parents", postRequest);

        StudentEntity patchStudent = dataHelper.createStudent(null, null);
        ParentPatchRequest patchRequest = dataHelper.createParentPatchRequest(Collections.singletonList(patchStudent), Collections.singletonList(postStudent));

        ParentDetailedResponse patchResponse = given()
                .headers(fullAdminHeaders)
                .body(patchRequest)
        .when()
                .patch("parents/" + parentId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ParentDetailedResponse.class);
        assertEquals(parentId, patchResponse.getId());
        assertEquals(patchRequest.getEmail(), patchResponse.getEmail());
        assertEquals(patchRequest.getPhoneNumber(), patchResponse.getPhoneNumber());
        assertEquals(patchRequest.getFirstName(), patchResponse.getFirstName());
        assertEquals(patchRequest.getLastName(), patchResponse.getLastName());
        assertEquals(patchRequest.getBirthDate(), patchResponse.getBirthDate());
        assertEquals(patchRequest.getGender(), patchResponse.getGender());
        assertEquals(patchRequest.getChildrenIdsToAdd().iterator().next(), patchStudent.getId());
        assertEquals(patchRequest.getChildrenIdsToRemove().iterator().next(), postStudent.getId());
        assertEquals(1, patchResponse.getChildren().size());

        ParentDetailedResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("parents/" + parentId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ParentDetailedResponse.class);
        assertEquals(parentId, patchResponse.getId());
        assertEquals(patchRequest.getEmail(), getResponse.getEmail());
        assertEquals(patchRequest.getPhoneNumber(), getResponse.getPhoneNumber());
        assertEquals(patchRequest.getFirstName(), getResponse.getFirstName());
        assertEquals(patchRequest.getLastName(), getResponse.getLastName());
        assertEquals(patchRequest.getBirthDate(), getResponse.getBirthDate());
        assertEquals(patchRequest.getGender(), getResponse.getGender());
        assertEquals(patchRequest.getChildrenIdsToAdd().iterator().next(), getResponse.getChildren().iterator().next().getId());
        assertEquals(1, getResponse.getChildren().size());
    }

    @Test
    @Override
    public void shouldCreateDeleteAndNotFetchEntity() {
        StudentEntity postStudent = dataHelper.createStudent(null, null);
        ParentCreateRequest postRequest = dataHelper.createParentCreateRequest(Collections.singletonList(postStudent.getId()));
        Integer parentId = postEntity(fullAdminHeaders, "parents", postRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("parents/" + parentId)
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("parents/" + parentId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
