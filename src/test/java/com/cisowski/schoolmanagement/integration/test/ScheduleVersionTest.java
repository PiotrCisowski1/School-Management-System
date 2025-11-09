package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.integration.BasicCrudHappyPathTests;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.AddScheduleVersionRequest;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.PatchScheduleVersionRequest;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionDetailedResponse;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionSummaryResponse;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class ScheduleVersionTest extends BaseIntegrationTest implements BasicCrudHappyPathTests {

    @Test
    @Override
    public void shouldCreateAndFetchEntity() {
        AddScheduleVersionRequest scheduleVersionRequest = dataHelper.createAddScheduleVersionRequest(null, true);
        Integer createdScheduleVersionId = postEntity(fullAdminHeaders, "schedules/version", scheduleVersionRequest);

        ScheduleVersionDetailedResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("schedules/version/" + createdScheduleVersionId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ScheduleVersionDetailedResponse.class);

        assertEquals(createdScheduleVersionId, getResponse.getId());
        assertEquals(scheduleVersionRequest.getScheduleName(), getResponse.getName());
        assertEquals(scheduleVersionRequest.isActive(), getResponse.isActive());
        assertEquals(scheduleVersionRequest.getYearbookId(), getResponse.getYearbook().getId());
        assertTrue(CollectionUtils.isEmpty(getResponse.getSchedules()));
    }


    @Test
    @Override
    @DisplayName("shouldCreateAndFetchScheduleVersionsByYearbook")
    public void shouldFetchAllEntities() {
        //as no getAllScheduleVersions endpoint exists, test covers getScheduleVersionsByYearbook

        AddScheduleVersionRequest anotherScheduleVersion = dataHelper.createAddScheduleVersionRequest(null, false);
        postEntity(fullAdminHeaders, "schedules/version", anotherScheduleVersion);

        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        List<Integer> scheduleVersionIds = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            AddScheduleVersionRequest scheduleVersionRequest = dataHelper.createAddScheduleVersionRequest(yearbook, false);
            Integer createdScheduleVersionId = postEntity(fullAdminHeaders, "schedules/version", scheduleVersionRequest);
            scheduleVersionIds.add(createdScheduleVersionId);
        }

        List<ScheduleVersionSummaryResponse> response = given()
                .headers(fullAdminHeaders)
                .when()
                .get("schedules/version/yearbook/" + yearbook.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath()
                .getList("", ScheduleVersionSummaryResponse.class);
        assertEquals(scheduleVersionIds.size(), response.size());
        List<ScheduleVersionSummaryResponse> noMatchResponse = response.stream()
                .filter(version -> !scheduleVersionIds.contains(version.getId()))
                .toList();
        assertTrue(noMatchResponse.isEmpty());
    }


    @Test
    @Override
    public void shouldCreateUpdateAndFetchEntity() {
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        AddScheduleVersionRequest scheduleVersionRequest = dataHelper.createAddScheduleVersionRequest(yearbook, false);
        Integer createdScheduleVersionId = postEntity(fullAdminHeaders, "schedules/version", scheduleVersionRequest);

        AddScheduleVersionRequest anotherScheduleVersionRequest = dataHelper.createAddScheduleVersionRequest(yearbook, true);
        postEntity(fullAdminHeaders, "schedules/version", scheduleVersionRequest);

        YearbookEntity anotherYearbook = dataHelper.createYearbook(null, null);
        PatchScheduleVersionRequest patchRequest = dataHelper.createPatchScheduleVersionRequest(anotherYearbook, true);

        ScheduleVersionDetailedResponse patchResponse = given()
                .headers(fullAdminHeaders)
                .body(patchRequest)
        .when()
                .patch("schedules/version/" + createdScheduleVersionId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ScheduleVersionDetailedResponse.class);
        assertEquals(createdScheduleVersionId, patchResponse.getId());
        assertEquals(patchRequest.getName(), patchResponse.getName());
        assertEquals(patchRequest.isActive(), patchResponse.isActive());
        assertEquals(patchRequest.getYearbookId(), patchResponse.getYearbook().getId());
        assertTrue(CollectionUtils.isEmpty(patchResponse.getSchedules()));

        ScheduleVersionDetailedResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("schedules/version/" + createdScheduleVersionId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ScheduleVersionDetailedResponse.class);
        assertEquals(createdScheduleVersionId, getResponse.getId());
        assertEquals(patchRequest.getName(), getResponse.getName());
        assertEquals(patchRequest.isActive(), getResponse.isActive());
        assertEquals(patchRequest.getYearbookId(), getResponse.getYearbook().getId());
        assertTrue(CollectionUtils.isEmpty(getResponse.getSchedules()));
    }


    @Test
    @Override
    public void shouldCreateDeleteAndNotFetchEntity() {
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        AddScheduleVersionRequest scheduleVersionRequest = dataHelper.createAddScheduleVersionRequest(yearbook, false);
        Integer createdScheduleVersionId = postEntity(fullAdminHeaders, "schedules/version", scheduleVersionRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("schedules/version/" + createdScheduleVersionId)
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("schedules/version/" + createdScheduleVersionId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldCreateAndCloneScheduleVersion() {
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        AddScheduleVersionRequest scheduleVersionRequest = dataHelper.createAddScheduleVersionRequest(yearbook, false);
        Integer createdScheduleVersionId = postEntity(fullAdminHeaders, "schedules/version", scheduleVersionRequest);

        String clonePath = String.format("schedules/version/%d/clone", createdScheduleVersionId);
        ScheduleVersionDetailedResponse cloneResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .post(clonePath)
        .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(ScheduleVersionDetailedResponse.class);
        assertEquals(scheduleVersionRequest.getYearbookId(), cloneResponse.getYearbook().getId());
        assertFalse(cloneResponse.isActive());
    }
}
