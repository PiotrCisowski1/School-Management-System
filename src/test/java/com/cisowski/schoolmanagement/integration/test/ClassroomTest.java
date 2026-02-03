package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.classroom.model.*;
import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.integration.BasicCrudHappyPathTests;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceAbsenceByScheduleResponse;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleChangelog.ScheduleChangeLogEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TimeRange;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClassroomTest extends BaseIntegrationTest implements BasicCrudHappyPathTests {

    @Test
    @Override
    public void shouldCreateAndFetchEntity() {
        EquipmentRequest eqRequest = Instancio.create(EquipmentRequest.class);
        String postEqPath = "classrooms/equipments";
        Integer eqId = postEntity(fullAdminHeaders, postEqPath, eqRequest);
        EquipmentQuantity equipmentQuantity = new EquipmentQuantity();
        equipmentQuantity.setEquipmentId(eqId);
        equipmentQuantity.setQuantity(3);

        ClassroomRequest classroomRequest = dataHelper.createClassroomRequest(Collections.singletonList(equipmentQuantity));
        String postClassroomPath = "classrooms";
        Integer classroomId = postEntity(fullAdminHeaders, postClassroomPath, classroomRequest);


        given()
                .headers(fullAdminHeaders)
        .when()
                .get("/classrooms/" + classroomId)
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(classroomRequest.getName()))
                .body("capacity", equalTo(classroomRequest.getCapacity()))
                .body("notes", equalTo(classroomRequest.getNotes()))
                .body("equipments.size()", equalTo(1))
                .body("equipments.get(0).equipment.id", equalTo(classroomRequest.getEquipments().iterator().next().getEquipmentId()))
                .body("id", equalTo(classroomId));

    }

    @Test
    @Override
    public void shouldFetchAllEntities() {
        List<Integer> classroomIds = new ArrayList<>();
        for(int i = 0; i < 7; i++) {
            ClassroomRequest classroomRequest = dataHelper.createClassroomRequest(Collections.emptyList());
            String postClassroomPath = "classrooms";
            Integer classroomId = postEntity(fullAdminHeaders, postClassroomPath, classroomRequest);
            classroomIds.add(classroomId);
        }

        List<ClassroomSummaryResponse> response = given()
                .headers(fullAdminHeaders)
        .when()
                .get("/classrooms")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath().getList("", ClassroomSummaryResponse.class);

        assertEquals(response.size(), classroomIds.size());
        List<Integer> responseIds = response.stream().map(ClassroomSummaryResponse::getId).toList();
        assertTrue(responseIds.containsAll(classroomIds));
    }

    @Test
    @Override
    public void shouldCreateUpdateAndFetchEntity() {
        EquipmentRequest eqRequest = Instancio.create(EquipmentRequest.class);
        String postEqPath = "classrooms/equipments";
        Integer eqId = postEntity(fullAdminHeaders, postEqPath, eqRequest);
        EquipmentQuantity equipmentQuantity = new EquipmentQuantity();
        equipmentQuantity.setEquipmentId(eqId);
        equipmentQuantity.setQuantity(3);

        ClassroomRequest classroomRequest = dataHelper.createClassroomRequest(Collections.singletonList(equipmentQuantity));
        String postClassroomPath = "classrooms";
        Integer classroomId = postEntity(fullAdminHeaders, postClassroomPath, classroomRequest);

        EquipmentRequest eqRequest2 = Instancio.create(EquipmentRequest.class);
        String postEqPath2 = "classrooms/equipments";
        Integer eqIdToAdd = postEntity(fullAdminHeaders, postEqPath2, eqRequest2);

        EquipmentQuantity eqToAdd = new EquipmentQuantity(eqIdToAdd, 5);
        EquipmentQuantity eqToRemove = new EquipmentQuantity(eqId, 0);
        PatchClassroomRequest updateRequest = dataHelper.createClassroomPatchRequest(Collections.singletonList(eqToAdd), Collections.singletonList(eqToRemove));

        ClassroomDetailedResponse response = given()
                .headers(fullAdminHeaders)
                .body(updateRequest)
        .when()
                .patch("/classrooms/" + classroomId)
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ClassroomDetailedResponse.class);

        assertEquals(classroomId, response.getId());
        assertEquals(updateRequest.getName(), response.getName());
        assertEquals(updateRequest.getCapacity(), response.getCapacity());
        assertEquals(updateRequest.getNotes(), response.getNotes());
        assertTrue(response.getEquipments().stream().anyMatch(eq -> eq.getEquipment().getId().equals(eqIdToAdd)));
        assertTrue(response.getEquipments().stream().noneMatch(eq -> eq.getEquipment().getId().equals(eqId)));
    }

    @Test
    @Override
    public void shouldCreateDeleteAndNotFetchEntity() {
        ClassroomRequest classroomRequest = dataHelper.createClassroomRequest(Collections.emptyList());
        String postClassroomPath = "classrooms";
        Integer classroomId = postEntity(fullAdminHeaders, postClassroomPath, classroomRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("classrooms/" + classroomId)
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("classrooms/" + classroomId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldGetAvailableClassrooms() {
        ClassroomEntity availableClassroom1 = dataHelper.createClassroom();
        ClassroomEntity availableClassroom2 = dataHelper.createClassroom();
        ClassroomEntity unavailableClassroom = dataHelper.createClassroom();
        ScheduleEntity schedule = dataHelper.createScheduleEntity(null, null, null, unavailableClassroom);

        GetClassroomAtRequest getClassroomAtRequest = new GetClassroomAtRequest();
        TimeRange timeRange = new TimeRange();
        timeRange.setStartTime(schedule.getStartTime());
        timeRange.setEndTime(schedule.getEndTime());
        timeRange.setDayOfWeek(schedule.getDayOfWeek().getValue());
        getClassroomAtRequest.setTimeRange(timeRange);
        getClassroomAtRequest.setStartDate(schedule.getEffectiveDate());
        getClassroomAtRequest.setEndDate(schedule.getExpirationDate());

        List<ClassroomSummaryResponse> response = given()
                .headers(fullAdminHeaders)
                .body(getClassroomAtRequest)
        .when()
                .get("/classrooms/available")
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList("", ClassroomSummaryResponse.class);

        List<Integer> classroomIds = response.stream()
                .map(ClassroomSummaryResponse::getId)
                .toList();
        assertTrue(classroomIds.containsAll(List.of(availableClassroom1.getId(), availableClassroom2.getId())));
    }

    @Test
    void shouldGetOnlyAvailableClassroomForGivenYearbook() {
        YearbookEntity yearbook = dataHelper.createYearbook(null, null);
        int capacity = 15;
        for (int i = 0; i < capacity; i++){
            dataHelper.createStudent(yearbook, null);
        }
        ClassroomRequest availableClassroomRequest = dataHelper.createClassroomRequest(null, 15);
        ClassroomRequest unavailableClassroomRequest = dataHelper.createClassroomRequest(null, 10);
        ClassroomRequest unavailableClassroomRequest2 = dataHelper.createClassroomRequest(null, 12);
        Integer availableClassroomId = postEntity(fullAdminHeaders, "/classrooms", availableClassroomRequest);
        Integer unavailableClassroomId1 = postEntity(fullAdminHeaders, "/classrooms", unavailableClassroomRequest);
        Integer unavailableClassroomId2 = postEntity(fullAdminHeaders, "/classrooms", unavailableClassroomRequest2);

        ClassroomEntity classroom = dataHelper.fetchClassroom(unavailableClassroomId1);
        ScheduleEntity schedule = dataHelper.createScheduleEntity(null, null, null, classroom);

        GetClassroomAtRequest getClassroomAtRequest = new GetClassroomAtRequest();
        TimeRange timeRange = new TimeRange();
        timeRange.setStartTime(schedule.getStartTime());
        timeRange.setEndTime(schedule.getEndTime());
        timeRange.setDayOfWeek(schedule.getDayOfWeek().getValue());
        getClassroomAtRequest.setTimeRange(timeRange);
        getClassroomAtRequest.setStartDate(schedule.getEffectiveDate());
        getClassroomAtRequest.setEndDate(schedule.getExpirationDate());

        List<ClassroomSummaryResponse> response = given()
                .headers(fullAdminHeaders)
                .body(getClassroomAtRequest)
        .when()
                .get("/classrooms/available?yearbookId=" + yearbook.getId())
        .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList("", ClassroomSummaryResponse.class);

        assertEquals(availableClassroomId, response.get(0).getId());
        assertEquals(1, response.size());
    }
}
