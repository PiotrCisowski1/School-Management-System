package com.cisowski.schoolmanagement.users.teacher.controller;

import com.cisowski.schoolmanagement.users.teacher.model.TeacherCreateRequest;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherPatchRequest;
import com.cisowski.schoolmanagement.users.teacher.model.AddTeacherResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherDetailedResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherService;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService service;

    public TeacherController(TeacherService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AddTeacherResponse> addTeacher(@Valid @RequestBody TeacherCreateRequest dto){
        String message = "Received Teacher POST request for object: " + dto.toString();
        DbLogger.info(message);

        AddTeacherResponse savedTeacher = service.addTeacher(dto);
        return new ResponseEntity<>(savedTeacher, HttpStatus.CREATED);
    }

    @PatchMapping("/{teacherId}")
    public ResponseEntity<TeacherDetailedResponse> updateTeacher(@Valid @RequestBody TeacherPatchRequest dto, @PathVariable Integer teacherId){
        String message = String.format("Received Teacher PUT request for object: %s", dto.toString());
        DbLogger.info(message);

        TeacherDetailedResponse updatedTeacher = service.updateTeacher(dto, teacherId);
        return new ResponseEntity<>(updatedTeacher, HttpStatus.OK);
    }

    @DeleteMapping("/{teacherId}")
    public ResponseEntity deleteTeacher(@PathVariable Integer teacherId){
        String message = String.format("Received Teacher DELETE request for ID: %s", teacherId);
        DbLogger.info(message);

        service.deleteUser(teacherId);
        return new ResponseEntity(HttpStatusCode.valueOf(204));
    }

    @GetMapping("/{teacherId}")
    public ResponseEntity<TeacherDetailedResponse> getTeacher(@PathVariable Integer teacherId){
        String message = String.format("Received Teacher GET request for ID: %s", teacherId);
        DbLogger.info(message);

        TeacherDetailedResponse response = service.findById(teacherId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<TeacherSummaryResponse>> getTeachers(){
        String message = "Received Teachers GET request";
        DbLogger.info(message);

        Collection<TeacherSummaryResponse> teacherResponses = service.findAll();
        return new ResponseEntity<>(teacherResponses, HttpStatus.OK);
    }
}
