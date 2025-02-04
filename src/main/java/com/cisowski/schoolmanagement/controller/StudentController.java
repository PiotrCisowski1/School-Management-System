package com.cisowski.schoolmanagement.controller;

import com.cisowski.schoolmanagement.model.request.StudentCreateRequest;
import com.cisowski.schoolmanagement.model.request.StudentPatchRequest;
import com.cisowski.schoolmanagement.model.response.AddStudentResponse;
import com.cisowski.schoolmanagement.model.response.StudentDetailedResponse;
import com.cisowski.schoolmanagement.model.response.StudentSummaryResponse;
import com.cisowski.schoolmanagement.service.StudentService;
import com.cisowski.schoolmanagement.utility.DbLogger;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService service) {
        this.studentService = service;
    }

    @PostMapping
    public ResponseEntity<AddStudentResponse> addStudent(@Valid @RequestBody StudentCreateRequest student){
        String message = "Received Student POST request for object: " + student.toString();
        DbLogger.info(message);

        AddStudentResponse savedStudent = studentService.addStudent(student);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    @PatchMapping("/{studentId}")
    public ResponseEntity<StudentDetailedResponse> updateStudent(@Valid @RequestBody StudentPatchRequest request, @PathVariable Integer studentId){
        String message = String.format("Received Student PUT request for object: %s", request.toString());
        DbLogger.info(message);

        StudentDetailedResponse updatedStudent = studentService.updateStudent(request, studentId);
        return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity deleteStudent(@PathVariable Integer studentId){
        String message = String.format("Received Student DELETE request for ID: %s", studentId);
        DbLogger.info(message);

        studentService.deleteUser(studentId);
        return new ResponseEntity(HttpStatusCode.valueOf(204));
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentDetailedResponse> getStudent(@PathVariable Integer studentId){
        String message = String.format("Received Student GET request for ID: %s", studentId);
        DbLogger.info(message);

        StudentDetailedResponse student = studentService.findById(studentId);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<StudentSummaryResponse>> getStudents(){
        String message = "Received Students GET request";
        DbLogger.info(message);

        Collection<StudentSummaryResponse> students = studentService.findAll();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }
}
