package com.cisowski.schoolmanagement.users.teacher.controller;

import com.cisowski.schoolmanagement.common.annotation.SecurityResponses;
import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherCreateRequest;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherPatchRequest;
import com.cisowski.schoolmanagement.users.teacher.model.AddTeacherResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherDetailedResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityRequest;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityResponse;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TimeRange;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherAvailabilityService;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherService;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
@Tag(name = "Teachers", description = "Administration of teaching staff, including professional profiles, subject specializations, and departmental affiliations.")
public class TeacherController {

    private final TeacherService service;
    private final TeacherAvailabilityService teacherAvailabilityService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Create teacher",
            description = "Adds new Teacher profile. Required authority level: Administrator")
    @ApiResponse(responseCode = "201", description = "Created successfully")
    @ApiResponse(responseCode = "404", description = "Subject not found")
    @ApiResponse(responseCode = "404", description = "Authority not found")
    @ApiResponse(responseCode = "409", description = "User with given email already exists")
    public ResponseEntity<AddTeacherResponse> addTeacher(@Valid @RequestBody TeacherCreateRequest dto){
        String message = "Received Teacher POST request for object: " + dto.toString();
        DbLogger.info(message);

        AddTeacherResponse savedTeacher = service.addTeacher(dto);
        return new ResponseEntity<>(savedTeacher, HttpStatus.CREATED);
    }

    @PatchMapping("/{teacherId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Update teacher",
            description = "Modifies teacher profile and subject association. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Modification successful")
    @ApiResponse(responseCode = "404", description = "Teacher not found")
    @ApiResponse(responseCode = "404", description = "Subject not found")
    public ResponseEntity<TeacherDetailedResponse> updateTeacher(@Valid @RequestBody TeacherPatchRequest dto, @PathVariable Integer teacherId){
        String message = String.format("Received Teacher PUT request for object: %s", dto.toString());
        DbLogger.info(message);

        TeacherDetailedResponse updatedTeacher = service.updateTeacher(dto, teacherId);
        return new ResponseEntity<>(updatedTeacher, HttpStatus.OK);
    }

    @DeleteMapping("/{teacherId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Delete teacher",
            description = "Remove teacher profile from the system. Required authority level: Administrator")
    @ApiResponse(responseCode = "204", description = "Removed successfully")
    @ApiResponse(responseCode = "404", description = "Not found with given ID")
    @ApiResponse(responseCode = "406", description = "Removal canceled due to teacher association with yearbook")
    public ResponseEntity deleteTeacher(@PathVariable Integer teacherId){
        String message = String.format("Received Teacher DELETE request for ID: %s", teacherId);
        DbLogger.info(message);

        service.deleteUser(teacherId);
        return new ResponseEntity(HttpStatusCode.valueOf(204));
    }

    @GetMapping("/{teacherId}")
    @RequiresPermission(resource = ResourceType.TEACHER, action = ResourceActionType.READ)
    @SecurityResponses
    @Operation(
            summary = "Find teacher with ID",
            description = "Retrieves existing teacher with given ID. Required authority level: Administrator, Teacher, Student, Parent")
    @ApiResponse(responseCode = "200", description = "Returns existing teacher")
    @ApiResponse(responseCode = "404", description = "Not found with ID")
    public ResponseEntity<TeacherDetailedResponse> getTeacher(@PathVariable Integer teacherId){
        String message = String.format("Received Teacher GET request for ID: %s", teacherId);
        DbLogger.info(message);

        TeacherDetailedResponse response = service.findById(teacherId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Find all teachers",
            description = "Retrieves all existing teachers in the system. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns existing teachers list (empty result as well)")
    public ResponseEntity<Collection<TeacherSummaryResponse>> getTeachers(){
        String message = "Received Teachers GET request";
        DbLogger.info(message);

        Collection<TeacherSummaryResponse> teacherResponses = service.findAll();
        return new ResponseEntity<>(teacherResponses, HttpStatus.OK);
    }

    @PostMapping("/{teacherId}/teacher-availability")
    @RequiresPermission(resource = ResourceType.TEACHER_AVAILABILITY, action = ResourceActionType.CREATE)
    @SecurityResponses
    @Operation(
            summary = "Create teacher's availability",
            description = "Adds teacher availability record which represents teacher's possibility to teach a lesson in particular time range. Required authority level: Administrator, Teacher")
    @ApiResponse(responseCode = "201", description = "Created successfully")
    @ApiResponse(responseCode = "404", description = "Teacher not found")
    @ApiResponse(responseCode = "406", description = "Availability already booked for given teacher")
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

    @DeleteMapping("/{teacherId}/teacher-availability/{teacherAvailabilityId}")
    @RequiresPermission(resource = ResourceType.TEACHER_AVAILABILITY, action = ResourceActionType.DELETE)
    @SecurityResponses
    @Operation(
            summary = "Delete teacher's availability",
            description = "Remove availability of given teacher. Required authority level: Administrator, Teacher")
    @ApiResponse(responseCode = "204", description = "Removed successfully")
    @ApiResponse(responseCode = "404", description = "Availability not found")
    public ResponseEntity deleteTeacherAvailability(@PathVariable Integer teacherAvailabilityId, @PathVariable Integer teacherId){
        DbLogger.info("Received TeacherAvailability DELETE request for ID: " + teacherAvailabilityId);
        teacherAvailabilityService.deleteTeacherAvailability(teacherAvailabilityId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{teacherId}/teacher-availability/{teacherAvailabilityId}")
    @RequiresPermission(resource = ResourceType.TEACHER_AVAILABILITY, action = ResourceActionType.READ)
    @SecurityResponses
    @Operation(
            summary = "Find availability with ID",
            description = "Retrieves existing teacher's availability with given ID. Required authority level: Administrator, Teacher")
    @ApiResponse(responseCode = "200", description = "Returns existing availablility")
    @ApiResponse(responseCode = "404", description = "Not found with ID")
    public ResponseEntity<TeacherAvailabilityResponse> getTeacherAvailabilityById(
            @PathVariable Integer teacherAvailabilityId, @PathVariable Integer teacherId){
        DbLogger.info("Received TeacherAvailability GET request for ID: " + teacherAvailabilityId);
        TeacherAvailabilityResponse response = teacherAvailabilityService.getTeacherAvailabilityById(teacherAvailabilityId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{teacherId}/teacher-availability")
    @RequiresPermission(resource = ResourceType.TEACHER_AVAILABILITY, action = ResourceActionType.READ)
    @SecurityResponses
    @Operation(
            summary = "Get all availabilities for Teacher",
            description = "Retrieves all existing availabilities for particular teacher. Required authority level: Administrator, Teacher")
    @ApiResponse(responseCode = "200", description = "Returns existing availabilities list (empty result as well)")
    public ResponseEntity<Collection<TeacherAvailabilityResponse>> getTeacherAvailabilityByTeacherId(@PathVariable Integer teacherId){
        DbLogger.info("Received TeacherAvailability GET request for Teacher ID: " + teacherId);
        Collection<TeacherAvailabilityResponse> response = teacherAvailabilityService.getTeacherAvailabilityByTeacherId(teacherId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/teacher-availability/day/{dayOfWeek}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Find availabilities for day",
            description = "Retrieves all existing teacher availabilities for particular day of week. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns availabilities list (empty result as well)")
    public ResponseEntity<Collection<TeacherAvailabilityResponse>> getTeacherAvailabilityByDayOfWeek(@PathVariable Integer dayOfWeek){
        DbLogger.info("Received TeacherAvailability GET request for DayOfWeek: " + dayOfWeek);
        Collection<TeacherAvailabilityResponse> response = teacherAvailabilityService.getTeacherAvailabilitiesByDayOfWeek(dayOfWeek);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/subject/{subjectId}/teacher-availability")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Find availabilities for subject in time range",
            description = "Retrieves all existing availabilities for particular subject in given time range. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns availabilities list (empty result as well)")
    public ResponseEntity<Collection<TeacherAvailabilityResponse>> getTeacherAvailabilityBySubjectAndTimeRange(
            @Valid @RequestBody TimeRange timeRange, @PathVariable Integer subjectId){
        DbLogger.info(String.format("Received TeacherAvailability GET request for Subject ID: %s and TimeRange: %s", subjectId, timeRange.toString()));
        Collection<TeacherAvailabilityResponse> response = teacherAvailabilityService.getTeacherAvailabilityByTimeRangeAndSubjectType(timeRange, subjectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
