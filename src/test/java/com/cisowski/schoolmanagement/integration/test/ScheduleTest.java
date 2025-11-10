package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.integration.BasicCrudHappyPathTests;
import com.cisowski.schoolmanagement.schedule.model.AddScheduleRequest;
import com.cisowski.schoolmanagement.schedule.model.PatchScheduleRequest;
import com.cisowski.schoolmanagement.schedule.model.ScheduleDetailedResponse;
import com.cisowski.schoolmanagement.schedule.model.ScheduleSummaryResponse;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScheduleTest extends BaseIntegrationTest implements BasicCrudHappyPathTests {

    @Test
    @Override
    public void shouldCreateAndFetchEntity() {
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);
        AddScheduleRequest postScheduleRequest = dataHelper.createAddScheduleRequest((SubjectEntity) null, null, null);
        String postSchedulePath = "schedules/version/" + scheduleVersion.getId();
        Integer createdScheduleId = postEntity(fullAdminHeaders, postSchedulePath, postScheduleRequest);

        ScheduleDetailedResponse response = given()
                .headers(fullAdminHeaders)
        .when()
                .get("schedules/" + scheduleVersion.getId() + "/" + createdScheduleId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ScheduleDetailedResponse.class);

        assertEquals(createdScheduleId, response.getId());
        assertEquals(postScheduleRequest.getSubjectId(), response.getSubject().getId());
        assertEquals(postScheduleRequest.getTeacherId(), response.getTeacher().getId());
        assertEquals(postScheduleRequest.getClassroomId(), response.getClassroom().getId());
        assertEquals(postScheduleRequest.getDayOfWeek(), response.getDayOfWeek().getValue());
        assertEquals(postScheduleRequest.getStartTime(), response.getStartTime());
        assertEquals(postScheduleRequest.getEndTime(), response.getEndTime());
        assertEquals(postScheduleRequest.getRecurrenceType(), response.getRecurrenceType());
    }

    @Test
    @Override
    @DisplayName("shouldFetchAllEntitiesByDayOfWeek")
    public void shouldFetchAllEntities() {
        //As there is no getAll endpoint test is covering getSchedulesByDayOfWeek

        List<Integer> scheduleIds = new ArrayList<>();
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);
        DayOfWeek dayOfWeek = DayOfWeek.FRIDAY;
        AddScheduleRequest scheduleRequest = dataHelper.createAddScheduleRequest(dayOfWeek, null, null);
        String postSchedulePath = "schedules/version/" + scheduleVersion.getId();
        postEntity(fullAdminHeaders, postSchedulePath, scheduleRequest);

        DayOfWeek anotherDay = DayOfWeek.MONDAY;
        LocalTime startTime = Instancio.gen().temporal().localTime()
                .range(LocalTime.of(1, 0), LocalTime.of(5, 0))
                .get();
        LocalTime endTime = startTime.plusHours(1);
        for(int i = 0; i < 5; i++){
            AddScheduleRequest postScheduleRequest = dataHelper.createAddScheduleRequest(anotherDay, startTime, endTime);
            Integer createdScheduleId = postEntity(fullAdminHeaders, postSchedulePath, postScheduleRequest);
            scheduleIds.add(createdScheduleId);
            startTime = startTime.plusHours(1);
            endTime = endTime.plusHours(1);
        }

        String getSchedulesByDayOfWeekPath = String.format("schedules/version/%d/day/%d", scheduleVersion.getId(), anotherDay.getValue());
        List<ScheduleSummaryResponse> response = given()
                .headers(fullAdminHeaders)
        .when()
                .get(getSchedulesByDayOfWeekPath)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath().getList("", ScheduleSummaryResponse.class);

        assertEquals(scheduleIds.size(), response.size());
        List<ScheduleSummaryResponse> noMatchSchedules = response.stream()
                .filter(schedule -> !scheduleIds.contains(schedule.getId()))
                .toList();
        assertTrue(noMatchSchedules.isEmpty());
    }

    @Test
    @Override
    public void shouldCreateUpdateAndFetchEntity() {
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);
        AddScheduleRequest postScheduleRequest = dataHelper.createAddScheduleRequest((SubjectEntity) null, null, null);
        String postSchedulePath = "schedules/version/" + scheduleVersion.getId();
        Integer createdScheduleId = postEntity(fullAdminHeaders, postSchedulePath, postScheduleRequest);

        DayOfWeek day = DayOfWeek.of(postScheduleRequest.getDayOfWeek() + 1);
        if(postScheduleRequest.getDayOfWeek() == 6)
            day = DayOfWeek.MONDAY;

        PatchScheduleRequest patchScheduleRequest = dataHelper.createPatchScheduleRequest(day, null, null);
        ScheduleDetailedResponse patchResponse = given()
                .headers(fullAdminHeaders)
                .body(patchScheduleRequest)
        .when()
                .patch("schedules/" + createdScheduleId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ScheduleDetailedResponse.class);

        assertEquals(createdScheduleId, patchResponse.getId());
        assertEquals(patchScheduleRequest.getClassroomId(), patchResponse.getClassroom().getId());
        assertEquals(patchScheduleRequest.getTeacherId(), patchResponse.getTeacher().getId());
        assertEquals(patchScheduleRequest.getClassroomId(), patchResponse.getClassroom().getId());
        assertEquals(patchScheduleRequest.getDayOfWeek(), patchResponse.getDayOfWeek().getValue());
        assertEquals(patchScheduleRequest.getStartTime(), patchResponse.getStartTime());
        assertEquals(patchScheduleRequest.getEndTime(), patchResponse.getEndTime());
        assertEquals(patchScheduleRequest.getRecurrenceType(), patchResponse.getRecurrenceType());

        ScheduleDetailedResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("schedules/" + scheduleVersion.getId() + "/" + createdScheduleId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ScheduleDetailedResponse.class);

        assertEquals(createdScheduleId, patchResponse.getId());
        assertEquals(patchScheduleRequest.getClassroomId(), getResponse.getClassroom().getId());
        assertEquals(patchScheduleRequest.getTeacherId(), getResponse.getTeacher().getId());
        assertEquals(patchScheduleRequest.getClassroomId(), getResponse.getClassroom().getId());
        assertEquals(patchScheduleRequest.getDayOfWeek(), getResponse.getDayOfWeek().getValue());
        assertEquals(patchScheduleRequest.getStartTime(), getResponse.getStartTime());
        assertEquals(patchScheduleRequest.getEndTime(), getResponse.getEndTime());
        assertEquals(patchScheduleRequest.getRecurrenceType(), getResponse.getRecurrenceType());
    }

    @Test
    @Override
    public void shouldCreateDeleteAndNotFetchEntity() {
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);
        AddScheduleRequest postScheduleRequest = dataHelper.createAddScheduleRequest((SubjectEntity) null, null, null);
        String postSchedulePath = "schedules/version/" + scheduleVersion.getId();
        Integer createdScheduleId = postEntity(fullAdminHeaders, postSchedulePath, postScheduleRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("schedules/" + createdScheduleId)
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("schedules/" + scheduleVersion.getId() + "/" + createdScheduleId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
