package com.cisowski.schoolmanagement.users.student.controller;

import com.cisowski.schoolmanagement.common.annotation.SecurityResponses;
import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.users.student.model.StudentCreateRequest;
import com.cisowski.schoolmanagement.users.student.model.StudentPatchRequest;
import com.cisowski.schoolmanagement.users.student.model.AddStudentResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentDetailedResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentSummaryResponse;
import com.cisowski.schoolmanagement.users.student.service.StudentService;
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
@RequestMapping("/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Comprehensive student data management, including enrollment details, class assignments, and personal profiles.")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Create student",
            description = "Adds new student profile to the system. Required authority level: Administrator")
    @ApiResponse(responseCode = "201", description = "Created successfully")
    @ApiResponse(responseCode = "406", description = "Authority with given ID not found")
    @ApiResponse(responseCode = "409", description = "User with given email already exists")
    public ResponseEntity<AddStudentResponse> addStudent(@Valid @RequestBody StudentCreateRequest student){
        String message = "Received Student POST request for object: " + student.toString();
        DbLogger.info(message);

        AddStudentResponse savedStudent = studentService.addStudent(student);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    @PatchMapping("/{studentId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Update student",
            description = "Modify existing student profile. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Modification successful")
    @ApiResponse(responseCode = "404", description = "Student not found")
    @ApiResponse(responseCode = "404", description = "Modified yearbook not found not found")
    public ResponseEntity<StudentDetailedResponse> updateStudent(@Valid @RequestBody StudentPatchRequest request, @PathVariable Integer studentId){
        String message = String.format("Received Student PUT request for object: %s", request.toString());
        DbLogger.info(message);

        StudentDetailedResponse updatedStudent = studentService.updateStudent(request, studentId);
        return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
    }

    @DeleteMapping("/{studentId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Delete student",
            description = "Removes student profile from the system. Required authority level: Administrator")
    @ApiResponse(responseCode = "204", description = "Removed successfully")
    @ApiResponse(responseCode = "404", description = "Not found with ID")
    public ResponseEntity deleteStudent(@PathVariable Integer studentId){
        String message = String.format("Received Student DELETE request for ID: %s", studentId);
        DbLogger.info(message);

        studentService.deleteUser(studentId);
        return new ResponseEntity(HttpStatusCode.valueOf(204));
    }

    @RequiresPermission(resource = ResourceType.STUDENT, action = ResourceActionType.READ)
    @GetMapping("/{studentId}")
    @SecurityResponses
    @Operation(
            summary = "Find student with ID",
            description = "Retrieves existing student with given ID. Required authority level: Administrator, Teacher, Student, Parent")
    @ApiResponse(responseCode = "200", description = "Returns existing student")
    @ApiResponse(responseCode = "404", description = "Not found with ID")
    public ResponseEntity<StudentDetailedResponse> getStudent(@PathVariable Integer studentId){
        String message = String.format("Received Student GET request for ID: %s", studentId);
        DbLogger.info(message);

        StudentDetailedResponse student = studentService.findById(studentId);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Find all students",
            description = "Retrieves all existing students in the system. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns existing students list (empty result as well)")
    public ResponseEntity<Collection<StudentSummaryResponse>> getStudents(){
        String message = "Received Students GET request";
        DbLogger.info(message);

        Collection<StudentSummaryResponse> students = studentService.findAll();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }
}
