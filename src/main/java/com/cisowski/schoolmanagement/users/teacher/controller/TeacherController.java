package com.cisowski.schoolmanagement.users.teacher.controller;

import com.cisowski.schoolmanagement.users.teacher.model.TeacherCreateRequest;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherPatchRequest;
import com.cisowski.schoolmanagement.users.teacher.model.AddTeacherResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherDetailedResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityRequest;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityResponse;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherAvailabilityService;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherService;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService service;
    private final TeacherAvailabilityService teacherAvailabilityService;

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

    @PostMapping("/{teacherId}/teacher-availability")
    public ResponseEntity<TeacherAvailabilityResponse> addTeacherAvailability(
            @RequestBody @Valid TeacherAvailabilityRequest request,
            @PathVariable Integer teacherId){
        DbLogger.info(String.format(
                "Received TeacherAvailability POST request for Teacher ID: %s and request: %s",
                teacherId,
                request.toString()));
        TeacherAvailabilityResponse response = teacherAvailabilityService.addTeacherAvailability(request, teacherId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/teacher-availability/{teacherAvailabilityId}")
    public ResponseEntity deleteTeacherAvailability(@PathVariable Integer teacherAvailabilityId){
        DbLogger.info("Received TeacherAvailability DELETE request for ID: " + teacherAvailabilityId);
        teacherAvailabilityService.deleteTeacherAvailability(teacherAvailabilityId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/teacher-availability/{teacherAvailabilityId}")
    public ResponseEntity<TeacherAvailabilityResponse> getTeacherAvailabilityById(@PathVariable Integer teacherAvailabilityId){
        DbLogger.info("Received TeacherAvailability GET request for ID: " + teacherAvailabilityId);
        TeacherAvailabilityResponse response = teacherAvailabilityService.getTeacherAvailabilityById(teacherAvailabilityId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{teacherId}/teacher-availability")
    public ResponseEntity<TeacherAvailabilityResponse> getTeacherAvailabilityByTeacherId(@PathVariable Integer teacherId){
        DbLogger.info("Received TeacherAvailability GET request for Teacher ID: " + teacherId);
        TeacherAvailabilityResponse response = teacherAvailabilityService.getTeacherAvailabilityByTeacherId(teacherId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/teacher-availability/{dayOfWeek}")
    public ResponseEntity<Collection<TeacherAvailabilityResponse>> getTeacherAvailabilityByDayOfWeek(@PathVariable Integer dayOfWeek){
        DbLogger.info("Received TeacherAvailability GET request for DayOfWeek: " + dayOfWeek);
        Collection<TeacherAvailabilityResponse> response = teacherAvailabilityService.getTeacherAvailabilitiesByDayOfWeek(dayOfWeek);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
