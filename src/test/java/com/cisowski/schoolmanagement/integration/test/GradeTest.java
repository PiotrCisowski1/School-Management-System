package com.cisowski.schoolmanagement.integration.test;

import com.cisowski.schoolmanagement.grade.model.grade.AddGradeRequest;
import com.cisowski.schoolmanagement.grade.model.grade.GradeDetailedResponse;
import com.cisowski.schoolmanagement.grade.model.grade.GradeSummaryResponse;
import com.cisowski.schoolmanagement.grade.model.grade.PatchGradeRequest;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeScaleRequest;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeScaleResponse;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeValueDto;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeValueResponse;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import com.cisowski.schoolmanagement.integration.BaseIntegrationTest;
import com.cisowski.schoolmanagement.integration.BasicCrudHappyPathTests;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GradeTest extends BaseIntegrationTest implements BasicCrudHappyPathTests {

    @Test
    @Override
    public void shouldCreateAndFetchEntity() {
        AddGradeRequest postGradeRequest = dataHelper.createAddGradeRequest(null, null, null, null);
        Integer gradeId = postEntity(fullAdminHeaders, "grades", postGradeRequest);

        GradeDetailedResponse gradeResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("/grades/" + gradeId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(GradeDetailedResponse.class);

        assertEquals(gradeId.longValue(), gradeResponse.getId());
        assertEquals(postGradeRequest.getSubjectId(), gradeResponse.getSubject().getId());
        assertEquals(postGradeRequest.getStudentId(), gradeResponse.getStudent().getId());
        assertEquals(postGradeRequest.getTeacherId(), gradeResponse.getTeacher().getId());
        assertEquals(postGradeRequest.getGradeTypeId(), gradeResponse.getGradeType().getId());
        assertEquals(postGradeRequest.getGradeValueId(), gradeResponse.getGradeValue().getId());
        assertEquals(postGradeRequest.getComments(), gradeResponse.getComments());
    }

    @Override
    public void shouldFetchAllEntities() {
        // no endpoint that list all Grades
    }

    @Test
    @Override
    public void shouldCreateUpdateAndFetchEntity() {
        SubjectEntity subject = dataHelper.createSubject();
        TeacherEntity teacher = dataHelper.createTeacher(Collections.singletonList(subject));
        AddGradeRequest postGradeRequest = dataHelper.createAddGradeRequest(teacher, subject, null, null);
        Integer gradeId = postEntity(fullAdminHeaders, "grades", postGradeRequest);

        PatchGradeRequest patchGradeRequest = dataHelper.createPatchGradeRequest(subject);
        GradeDetailedResponse detailedResponseAfterPatch = given()
                .headers(fullAdminHeaders)
                .body(patchGradeRequest)
        .when()
                .patch("grades/" + gradeId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(GradeDetailedResponse.class);

        assertEquals(gradeId.longValue(), detailedResponseAfterPatch.getId());
        assertEquals(subject.getId(), detailedResponseAfterPatch.getSubject().getId());
        assertEquals(patchGradeRequest.getStudentId(), detailedResponseAfterPatch.getStudent().getId());
        assertEquals(patchGradeRequest.getGradeTypeId(), detailedResponseAfterPatch.getGradeType().getId());
        assertEquals(patchGradeRequest.getGradeValueId(), detailedResponseAfterPatch.getGradeValue().getId());
        assertEquals(patchGradeRequest.getComments(), detailedResponseAfterPatch.getComments());

        GradeDetailedResponse detailedResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("grades/" + gradeId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(GradeDetailedResponse.class);

        assertEquals(gradeId.longValue(), detailedResponse.getId());
        assertEquals(subject.getId(), detailedResponse.getSubject().getId());
        assertEquals(patchGradeRequest.getStudentId(), detailedResponse.getStudent().getId());
        assertEquals(patchGradeRequest.getGradeTypeId(), detailedResponse.getGradeType().getId());
        assertEquals(patchGradeRequest.getGradeValueId(), detailedResponse.getGradeValue().getId());
        assertEquals(patchGradeRequest.getComments(), detailedResponse.getComments());
    }

    @Test
    @Override
    public void shouldCreateDeleteAndNotFetchEntity() {
        AddGradeRequest postGradeRequest = dataHelper.createAddGradeRequest(null, null, null, null);
        Integer gradeId = postEntity(fullAdminHeaders, "grades", postGradeRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete("grades/" + gradeId)
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .headers(fullAdminHeaders)
        .when()
                .get("grades/" + gradeId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldCreateGradesAndFetchByStudentId() {
        List<Long> firstStudentGradeIds = new ArrayList<>();
        StudentEntity firstStudent = dataHelper.createStudent(null, null);
        for(int i = 0; i < 5; i++) {
            AddGradeRequest postGradeRequest = dataHelper.createAddGradeRequest(null, null, firstStudent, null);
            Integer gradeId = postEntity(fullAdminHeaders, "grades", postGradeRequest);
            firstStudentGradeIds.add(gradeId.longValue());
        }

        StudentEntity secondStudent = dataHelper.createStudent(null, null);
        for(int i = 0; i < 3; i++) {
            AddGradeRequest postGradeRequest = dataHelper.createAddGradeRequest(null, null, secondStudent, null);
            postEntity(fullAdminHeaders, "grades", postGradeRequest);
        }

        List<GradeSummaryResponse> response = given()
                .headers(fullAdminHeaders)
        .when()
                .get("grades/student/" + firstStudent.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath().getList("", GradeSummaryResponse.class);

        assertEquals(firstStudentGradeIds.size(), response.size());
        List<GradeSummaryResponse> noMatchingGrades = response.stream()
                .filter(grade -> !firstStudentGradeIds.contains(grade.getId()))
                .toList();
        assertTrue(noMatchingGrades.isEmpty());
    }

    @Test
    void shouldCreateGradesAndFetchByStudentIdAndSubjectId() {
        List<Long> firstSubjectGradeIds = new ArrayList<>();
        SubjectEntity firstSubject = dataHelper.createSubject();
        StudentEntity student = dataHelper.createStudent(null, null);
        for(int i = 0; i < 5; i++) {
            AddGradeRequest postGradeRequest = dataHelper.createAddGradeRequest(null, firstSubject, student, null);
            Integer gradeId = postEntity(fullAdminHeaders, "grades", postGradeRequest);
            firstSubjectGradeIds.add(gradeId.longValue());
        }
        SubjectEntity secondSubject = dataHelper.createSubject();
        for(int i = 0; i < 3; i++) {
            AddGradeRequest postGradeRequest = dataHelper.createAddGradeRequest(null, secondSubject, student, null);
            postEntity(fullAdminHeaders, "grades", postGradeRequest);
        }

        String getGradesByStudentAndSubjectPath = String.format("grades/student/%d/subject/%d", student.getId(), firstSubject.getId());
        List<GradeSummaryResponse> response = given()
                .headers(fullAdminHeaders)
                .when()
                .get(getGradesByStudentAndSubjectPath)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath().getList("", GradeSummaryResponse.class);

        assertEquals(firstSubjectGradeIds.size(), response.size());
        List<GradeSummaryResponse> noMatchingGrades = response.stream()
                .filter(grade -> !firstSubjectGradeIds.contains(grade.getId()))
                .toList();
        assertTrue(noMatchingGrades.isEmpty());
    }

    @Test
    void shouldCreateGradesAndFetchBySubjectId() {
        List<Long> firstSubjectGradeIds = new ArrayList<>();
        SubjectEntity firstSubject = dataHelper.createSubject();
        for(int i = 0; i < 5; i++) {
            AddGradeRequest postGradeRequest = dataHelper.createAddGradeRequest(null, firstSubject, null, null);
            Integer gradeId = postEntity(fullAdminHeaders, "grades", postGradeRequest);
            firstSubjectGradeIds.add(gradeId.longValue());
        }
        SubjectEntity secondSubject = dataHelper.createSubject();
        for(int i = 0; i < 3; i++) {
            AddGradeRequest postGradeRequest = dataHelper.createAddGradeRequest(null, secondSubject, null, null);
            postEntity(fullAdminHeaders, "grades", postGradeRequest);
        }

        String getGradesByStudentAndSubjectPath = String.format("grades/subject/%d", firstSubject.getId());
        List<GradeSummaryResponse> response = given()
                .headers(fullAdminHeaders)
        .when()
                .get(getGradesByStudentAndSubjectPath)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath().getList("", GradeSummaryResponse.class);

        assertEquals(firstSubjectGradeIds.size(), response.size());
        List<GradeSummaryResponse> noMatchingGrades = response.stream()
                .filter(grade -> !firstSubjectGradeIds.contains(grade.getId()))
                .toList();
        assertTrue(noMatchingGrades.isEmpty());
    }

    @Test
    void shouldCreateGradesAndFetchByGradeType() {
        List<Long> firstGradeTypeGradeIds = new ArrayList<>();
        GradeTypeEntity firstGradeType = dataHelper.createGradeType();
        for(int i = 0; i < 5; i++) {
            AddGradeRequest postGradeRequest = dataHelper.createAddGradeRequest(null, null, null, firstGradeType);
            Integer gradeId = postEntity(fullAdminHeaders, "grades", postGradeRequest);
            firstGradeTypeGradeIds.add(gradeId.longValue());
        }
        GradeTypeEntity secondGradeType = dataHelper.createGradeType();
        for(int i = 0; i < 3; i++) {
            AddGradeRequest postGradeRequest = dataHelper.createAddGradeRequest(null, null, null, secondGradeType);
            postEntity(fullAdminHeaders, "grades", postGradeRequest);
        }

        String getGradesByStudentAndSubjectPath = String.format("grades/type/%d", firstGradeType.getId());
        List<GradeSummaryResponse> response = given()
                .headers(fullAdminHeaders)
        .when()
                .get(getGradesByStudentAndSubjectPath)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath().getList("", GradeSummaryResponse.class);

        assertEquals(firstGradeTypeGradeIds.size(), response.size());
        List<GradeSummaryResponse> noMatchingGrades = response.stream()
                .filter(grade -> !firstGradeTypeGradeIds.contains(grade.getId()))
                .toList();
        assertTrue(noMatchingGrades.isEmpty());
    }

    @Test
    void shouldCreateGradeValueInGradeScale() {
        GradeScaleRequest postScaleRequest = Instancio.of(GradeScaleRequest.class)
                .set(field(GradeScaleRequest::getIsActive), true)
                .create();
        Integer gradeScaleId = postEntity(fullAdminHeaders, "grades/scales", postScaleRequest);

        GradeValueDto gradeValueRequest = Instancio.create(GradeValueDto.class);
        String postGradeValuePath = String.format("grades/scales/%d/values", gradeScaleId);
        postEntity(fullAdminHeaders, postGradeValuePath, gradeValueRequest);
    }

    @Test
    void shouldCreateDeleteAndNotFetchGradeValueInGradeScale() {
        GradeScaleRequest postScaleRequest = Instancio.of(GradeScaleRequest.class)
                .set(field(GradeScaleRequest::getIsActive), true)
                .create();
        Integer gradeScaleId = postEntity(fullAdminHeaders, "grades/scales", postScaleRequest);

        GradeValueDto gradeValueRequest = Instancio.create(GradeValueDto.class);
        String postGradeValuePath = String.format("grades/scales/%d/values", gradeScaleId);
        Integer gradeValueId = postEntity(fullAdminHeaders, postGradeValuePath, gradeValueRequest);

        given()
                .headers(fullAdminHeaders)
        .when()
                .delete(postGradeValuePath + "/" + gradeValueId)
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        GradeScaleResponse scaleResponse = given()
                .headers(fullAdminHeaders)
        .when()
                .get("/grades/scales/" + gradeScaleId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(GradeScaleResponse.class);

        Optional<GradeValueResponse> deletedGV = scaleResponse.getGradeValues().stream()
                .filter(gv -> gv.getId().equals(gradeValueId.longValue()))
                .findFirst();
        assertTrue(deletedGV.isEmpty());
    }
}
