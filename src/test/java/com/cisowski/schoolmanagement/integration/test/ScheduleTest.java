package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.integration.BasicCrudHappyPathTests;
import com.cisowski.schoolmanagement.schedule.model.*;
import com.cisowski.schoolmanagement.schedule.model.scheduleChangelog.ScheduleChangeLogEntity;
import com.cisowski.schoolmanagement.schedule.model.scheduleChangelog.ScheduleChangeType;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class ScheduleTest extends BaseIntegrationTest implements BasicCrudHappyPathTests {

    @Test
    @Override
    public void shouldCreateAndFetchEntity() {
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityEntity availabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        AddScheduleRequest postScheduleRequest = dataHelper.createAddScheduleRequest(null, teacher, null);
        postScheduleRequest.setDayOfWeek(availabilityEntity.getDayOfWeek().getValue());
        postScheduleRequest.setStartTime(availabilityEntity.getStartTime());
        postScheduleRequest.setEndTime(availabilityEntity.getEndTime());
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
        assertEquals(postScheduleRequest.getEffectiveDate(), response.getEffectiveDate());
        assertEquals(postScheduleRequest.getExpirationDate(), response.getExpirationDate());
        assertEquals(ScheduleStatus.SCHEDULED, response.getStatus());
    }

    @Test
    @Override
    @DisplayName("shouldFetchAllEntitiesByDayOfWeek")
    public void shouldFetchAllEntities() {
        //As there is no getAll endpoint test is covering getSchedulesByDayOfWeek

        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        TeacherAvailabilityEntity availabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        DayOfWeek day = DayOfWeek.MONDAY;
        AddScheduleRequest scheduleRequest = dataHelper.createAddScheduleRequest(day, availabilityEntity.getStartTime(), availabilityEntity.getEndTime(), teacher);
        String postSchedulePath = "schedules/version/" + scheduleVersion.getId();
        postEntity(fullAdminHeaders, postSchedulePath, scheduleRequest);

        TeacherAvailabilityEntity anotherAvailability = dataHelper.createTeacherAvailabilityEntity(teacher);
        AddScheduleRequest request = dataHelper.createAddScheduleRequest(DayOfWeek.FRIDAY, anotherAvailability.getStartTime(), anotherAvailability.getEndTime(), teacher);
        postEntity(fullAdminHeaders, postSchedulePath, request);

        String getSchedulesByDayOfWeekPath = String.format("schedules/version/%d/day/%d", scheduleVersion.getId(), day.getValue());
        List<ScheduleSummaryResponse> response = given()
                .headers(fullAdminHeaders)
        .when()
                .get(getSchedulesByDayOfWeekPath)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath().getList("", ScheduleSummaryResponse.class);

        assertEquals(1, response.size());
    }

    @Test
    @Override
    public void shouldCreateUpdateAndFetchEntity() {
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        TeacherAvailabilityEntity availabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        AddScheduleRequest postScheduleRequest = dataHelper.createAddScheduleRequest(availabilityEntity.getDayOfWeek(), availabilityEntity.getStartTime(), availabilityEntity.getEndTime(), teacher);
        String postSchedulePath = "schedules/version/" + scheduleVersion.getId();
        Integer createdScheduleId = postEntity(fullAdminHeaders, postSchedulePath, postScheduleRequest);

        DayOfWeek day;
        if(postScheduleRequest.getDayOfWeek() < 6)
            day = DayOfWeek.of(postScheduleRequest.getDayOfWeek() + 1);
        else
            day = DayOfWeek.MONDAY;

        TeacherAvailabilityEntity teacherAvailabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher, day);
        PatchScheduleRequest patchScheduleRequest = dataHelper.createPatchScheduleRequest(day, teacherAvailabilityEntity.getStartTime(), teacherAvailabilityEntity.getEndTime(), teacher);
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
        assertEquals(patchScheduleRequest.getEffectiveDate(), patchResponse.getEffectiveDate());
        assertEquals(patchScheduleRequest.getExpirationDate(), patchResponse.getExpirationDate());

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
        assertEquals(patchScheduleRequest.getEffectiveDate(), getResponse.getEffectiveDate());
        assertEquals(patchScheduleRequest.getExpirationDate(), getResponse.getExpirationDate());
    }

    @Test
    @Override
    public void shouldCreateDeleteAndNotFetchEntity() {
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        TeacherAvailabilityEntity availabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        AddScheduleRequest postScheduleRequest = dataHelper.createAddScheduleRequest(availabilityEntity.getDayOfWeek(), availabilityEntity.getStartTime(), availabilityEntity.getEndTime(), teacher);
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
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value());

        ScheduleEntity schedule = dataHelper.fetchSchedule(createdScheduleId);
        List<ScheduleChangeLogEntity> changelogs = dataHelper.fetchChangelogs(schedule);

        assertFalse(CollectionUtils.isEmpty(changelogs));
        Optional<ScheduleChangeLogEntity> deleteLog = changelogs.stream()
                .filter(changelog -> changelog.getChangeType().equals(ScheduleChangeType.DELETED))
                .findFirst();
        assertTrue(deleteLog.isPresent());
        assertEquals(createdScheduleId, deleteLog.get().getSchedule().getId());
    }

    @Test
    void shouldCreateScheduleAndCheckLogExists() {
        ScheduleVersionEntity scheduleVersion = dataHelper.createScheduleVersion(null);
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        TeacherAvailabilityEntity availabilityEntity = dataHelper.createTeacherAvailabilityEntity(teacher);
        AddScheduleRequest postScheduleRequest = dataHelper.createAddScheduleRequest(availabilityEntity.getDayOfWeek(), availabilityEntity.getStartTime(), availabilityEntity.getEndTime(), teacher);
        String postSchedulePath = "schedules/version/" + scheduleVersion.getId();
        Integer createdScheduleId = postEntity(fullAdminHeaders, postSchedulePath, postScheduleRequest);

        ScheduleEntity schedule = dataHelper.fetchSchedule(createdScheduleId);
        List<ScheduleChangeLogEntity> changelogs = dataHelper.fetchChangelogs(schedule);

        assertFalse(CollectionUtils.isEmpty(changelogs));
        Optional<ScheduleChangeLogEntity> createLog = changelogs.stream()
                .filter(changelog -> changelog.getChangeType().equals(ScheduleChangeType.CREATED))
                .findFirst();
        assertTrue(createLog.isPresent());
        assertTrue(createLog.get().isAutomaticChange());
        assertTrue(createLog.get().getAffectedUsers().stream().anyMatch(user -> user.getId().equals(teacher.getId())));
    }

    @Test
    void shouldThrowWhenTryingToDeleteAlreadyDeletedSchedule() {
        ScheduleEntity schedule = dataHelper.createScheduleEntity(null, null, null);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("schedules/" + schedule.getId())
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("schedules/" + schedule.getId())
        .then()
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    void shouldNotAllowToUpdateIfStatusAlreadyDeleted() {
        ScheduleEntity schedule = dataHelper.createScheduleEntity(null, null ,null);
        PatchScheduleRequest patchScheduleRequest = new PatchScheduleRequest();

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("schedules/" + schedule.getId())
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
                .body(patchScheduleRequest)
        .when()
                .patch("schedules/" + schedule.getId())
        .then()
                .statusCode(HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    void shouldAllowToCancelScheduleAndFetchCanceledSchedule() {
        ScheduleEntity schedule = dataHelper.createScheduleEntity(null, null ,null);
        String reason = "Test reason";

        assertNotEquals(ScheduleStatus.CANCELLED, schedule.getStatus());

        given()
                .headers(fullAdminHeaders)
                .body(reason)
        .when()
                .put(String.format("schedules/%d/CANCEL", schedule.getId()))
        .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        ScheduleDetailedResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("schedules/" + schedule.getScheduleVersion().getId() + "/" + schedule.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(ScheduleDetailedResponse.class);
        assertEquals(ScheduleStatus.CANCELLED, getResponse.getStatus());
    }
}
