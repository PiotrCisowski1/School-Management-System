package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.integration.BasicCrudHappyPathTests;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityRequest;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityResponse;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TimeRange;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TeacherAvailabilityTest extends BaseIntegrationTest implements BasicCrudHappyPathTests {

    @Test
    @Override
    public void shouldCreateAndFetchEntity() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityRequest postRequest = dataHelper.createTeacherAvailabilityRequest();
        String postTeacherAvPath = String.format("teachers/%d/teacher-availability", teacher.getId());
        Integer teacherAvailabilityId = postEntity(fullAdminHeaders, postTeacherAvPath, postRequest);

        TeacherAvailabilityResponse getResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get(postTeacherAvPath + "/" + teacherAvailabilityId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(TeacherAvailabilityResponse.class);
        assertEquals(postRequest.getStartTime().toString(), getResponse.getStartTime().toString());
        assertEquals(postRequest.getEndTime().toString(), getResponse.getEndTime().toString());
        assertEquals(postRequest.isAvailable(), getResponse.isAvailable());
        assertEquals(postRequest.getNotes(), getResponse.getNotes());
        assertEquals(postRequest.getDayOfWeek(), getResponse.getDayOfWeek().getValue());
        assertEquals(teacherAvailabilityId, getResponse.getId());
    }

    @Test
    @Override
    @DisplayName("shouldFetchEntitiesByDayOfWeek")
    public void shouldFetchAllEntities() {
        //no getAll endpoint - covers getByDayOfWeek

        TeacherEntity randomTeacher = dataHelper.createTeacher(null);
        TeacherAvailabilityRequest postRandomRequest = dataHelper.createTeacherAvailabilityRequest();
        String postTeacherAvPath = String.format("teachers/%d/teacher-availability", randomTeacher.getId());
        postEntity(fullAdminHeaders, postTeacherAvPath, postRandomRequest);

        DayOfWeek day = Instancio.create(DayOfWeek.class);
        if(day.getValue() == postRandomRequest.getDayOfWeek())
            if(day != DayOfWeek.SUNDAY)
                day = DayOfWeek.of(postRandomRequest.getDayOfWeek() + 1);
            else
                day = DayOfWeek.MONDAY;

        List<Integer> teacherAvIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TeacherEntity teacher = dataHelper.createTeacher(null);
            TeacherAvailabilityRequest postRequest = dataHelper.createTeacherAvailabilityRequest();
            postRequest.setDayOfWeek(day.getValue());
            String postPath = String.format("teachers/%d/teacher-availability", teacher.getId());
            Integer teacherAvailabilityId = postEntity(fullAdminHeaders, postPath, postRequest);
            teacherAvIds.add(teacherAvailabilityId);
        }

        String getByDayOfWeekPath = "teachers/teacher-availability/day/" + day.getValue();
        given()
                .headers(fullAdminHeaders)
        .when()
                .get(getByDayOfWeekPath)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(teacherAvIds.size()));
    }

    @Test
    @Override
    public void shouldCreateUpdateAndFetchEntity() {
        //no update endpoint for TeacherAvailability
    }

    @Test
    @Override
    public void shouldCreateDeleteAndNotFetchEntity() {
        TeacherEntity teacher = dataHelper.createTeacher(null);
        TeacherAvailabilityRequest postRequest = dataHelper.createTeacherAvailabilityRequest();
        String postTeacherAvPath = String.format("teachers/%d/teacher-availability", teacher.getId());
        Integer teacherAvailabilityId = postEntity(fullAdminHeaders, postTeacherAvPath, postRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete(postTeacherAvPath + "/" + teacherAvailabilityId)
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
        .when()
                .get(postTeacherAvPath + "/" + teacherAvailabilityId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void shouldCreateAndFetchEntitiesBySubjectAndTimeRange() {
        List<Integer> tavIds = new ArrayList<>();
        SubjectEntity subject = dataHelper.createSubject();
        TimeRange timeRange = new TimeRange();
        timeRange.setDayOfWeek(Instancio.create(DayOfWeek.class).getValue());
        timeRange.setStartTime(LocalTime.of(2, 0));
        timeRange.setEndTime(LocalTime.of(12, 0));

        for(int i = 0; i < 10; i++) {
            TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
            TeacherAvailabilityRequest postRequest = dataHelper.createTeacherAvailabilityRequest();
            postRequest.setStartTime(timeRange.getStartTime().plusHours(2));
            postRequest.setEndTime(timeRange.getEndTime().minusHours(2));
            postRequest.setDayOfWeek(timeRange.getDayOfWeek());
            postRequest.setAvailable(true);
            String postTeacherAvPath = String.format("teachers/%d/teacher-availability", teacher.getId());
            Integer teacherAvailabilityId = postEntity(fullAdminHeaders, postTeacherAvPath, postRequest);
            tavIds.add(teacherAvailabilityId);
        }

        String getBySubjectAndTimeRangePath = String.format("teachers/subject/%d/teacher-availability", subject.getId());
        given()
                .headers(fullAdminHeaders)
                .body(timeRange)
        .when()
                .get(getBySubjectAndTimeRangePath)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(tavIds.size()));
    }
}
