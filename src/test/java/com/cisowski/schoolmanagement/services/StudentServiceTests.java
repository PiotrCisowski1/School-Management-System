package com.cisowski.schoolmanagement.services;

import com.cisowski.schoolmanagement.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.mapper.StudentMapper;
import com.cisowski.schoolmanagement.mapper.StudentMapperImpl;
import com.cisowski.schoolmanagement.model.request.StudentCreateRequest;
import com.cisowski.schoolmanagement.model.request.StudentPatchRequest;
import com.cisowski.schoolmanagement.model.response.AddStudentResponse;
import com.cisowski.schoolmanagement.model.response.StudentDetailedResponse;
import com.cisowski.schoolmanagement.model.response.StudentSummaryResponse;
import com.cisowski.schoolmanagement.model.entity.Student;
import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.repository.StudentRepository;
import com.cisowski.schoolmanagement.service.impl.StudentServiceImpl;
import com.cisowski.schoolmanagement.utility.PasswordGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTests {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StudentMapperImpl mockedStudentMapper;
    private StudentMapper studentMapper = Mappers.getMapper(StudentMapper.class);
    @InjectMocks
    private StudentServiceImpl studentService;
    private final String generatedPassword = PasswordGenerator.generatePassword();

    @Test
    @DisplayName("add Student successful - returns full StudentResponse")
    public void addStudent_successful() {
        StudentCreateRequest studentDto = Instancio.create(StudentCreateRequest.class);
        Student student = studentMapper.toStudentEntity(studentDto);
        student.setPassword(generatedPassword);
        student.setId(1);
        AddStudentResponse response = studentMapper.toAddStudentResponse(student);

        when(studentRepository.findByEmail(studentDto.getEmail())).thenReturn(Optional.empty());
        when(mockedStudentMapper.toStudentEntity(studentDto)).thenReturn(student);
        when(studentRepository.save(any())).thenReturn(student);
        when(mockedStudentMapper.toAddStudentResponse(any())).thenReturn(response);

        AddStudentResponse result = studentService.addStudent(studentDto);

        assertNotNull(result);
        assertNotNull(result.getPassword());
        assertEquals(response.getId(), result.getId());
        assertEquals(response.getEmail(), result.getEmail());
        assertEquals(response.getFirstName(), result.getFirstName());
        assertEquals(response.getLastName(), result.getLastName());
        assertEquals(response.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(response.getBirthDate(), result.getBirthDate());
        assertEquals(response.getGender(), result.getGender());
        assertIterableEquals(response.getAuthority(), result.getAuthority());
        assertIterableEquals(response.getParents(), result.getParents());
        assertEquals(response.getYearbook(), result.getYearbook());
    }

    @Test
    @DisplayName("addStudent should throw EmailAlreadyExistsException")
    public void addStudent_throwsEmailAlreadyExistsEx() {
        StudentCreateRequest studentDto = Instancio.create(StudentCreateRequest.class);
        User existingUser = new User();

        when(studentRepository.findByEmail(any())).thenReturn(Optional.of(existingUser));

        assertThrows(EmailAlreadyExistsException.class, () ->
                studentService.addStudent(studentDto));
    }

    @Test
    @DisplayName("updateStudent successful - returns StudentResponse")
    public void updateStudent_successful() {
        StudentPatchRequest studentDto = Instancio.create(StudentPatchRequest.class);
        User user = new User();
        user.setPassword("strongPass123");
        Student student = studentMapper.toStudentEntity(studentDto);
        StudentDetailedResponse studentResponse = studentMapper.toStudentResponse(student);

        when(studentRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(mockedStudentMapper.toStudentEntity(studentDto)).thenReturn(student);
        when(studentRepository.save(any())).thenReturn(student);
        when(mockedStudentMapper.toStudentResponse(any())).thenReturn(studentResponse);

        StudentDetailedResponse result = studentService.updateStudent(studentDto, 1);

        assertNotNull(result);
        assertEquals(studentResponse, result);
        assertEquals(studentResponse.getId(), result.getId());
        assertEquals(studentResponse.getEmail(), result.getEmail());
        assertEquals(studentResponse.getFirstName(), result.getFirstName());
        assertEquals(studentResponse.getLastName(), result.getLastName());
        assertEquals(studentResponse.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(studentResponse.getBirthDate(), result.getBirthDate());
        assertEquals(studentResponse.getGender(), result.getGender());
        assertIterableEquals(studentResponse.getAuthority(), result.getAuthority());
        assertIterableEquals(studentResponse.getParents(), result.getParents());
        assertEquals(studentResponse.getYearbook(), result.getYearbook());
    }

    @Test
    @DisplayName("updateStudent should throw EntityNotFoundException")
    public void updateStudent_throwsEntityNotFoundEx() {
        StudentPatchRequest studentDto = Instancio.create(StudentPatchRequest.class);

        when(studentRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                studentService.updateStudent(studentDto, 1));
    }

    @Test
    @DisplayName("deleteStudent should invoke delete method in repo")
    public void deleteStudent_successful() {
        Integer studentId = 1;
        Student student = new Student();
        student.setId(studentId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        studentService.deleteUser(studentId);

        verify(studentRepository, times(1)).findById(studentId);
        verify(studentRepository).delete(student);
    }

    @Test
    @DisplayName("deleteStudent should throw EntityNotFoundException")
    public void deleteStudent_throwsEntityNotFoundEx() {
        Integer studentId = 1;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                studentService.deleteUser(studentId));
    }

    @Test
    @DisplayName("findAll successful - should return Collection<StudentResponse>")
    public void findAll_successful() {
        List<Student> students = Instancio.createList(Student.class);
        List<StudentSummaryResponse> responses = studentMapper.toStudentsResponse(students);

        when(studentRepository.findAll()).thenReturn(students);
        when(mockedStudentMapper.toStudentsResponse(any())).thenReturn(responses);

        List<StudentSummaryResponse> result = studentService.findAll();

        verify(studentRepository, times(1)).findAll();
        verify(mockedStudentMapper, times(1)).toStudentsResponse(students);
        assertNotNull(result);
        assertIterableEquals(responses, result);
    }

    @Test
    @DisplayName("findById successful - should return StudentResponse")
    public void findById_successful() {
        Integer studentId = 1;
        Student existingStudent = Instancio.create(Student.class);
        StudentDetailedResponse studentResponse = studentMapper.toStudentResponse(existingStudent);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        when(mockedStudentMapper.toStudentResponse(existingStudent)).thenReturn(studentResponse);

        StudentDetailedResponse result = studentService.findById(studentId);

        verify(studentRepository, times(1)).findById(studentId);
        verify(mockedStudentMapper, times(1)).toStudentResponse(existingStudent);
        assertNotNull(result);
        assertEquals(studentResponse, result);
    }

    @Test
    @DisplayName("findById - should throw EntityNotFoundEx")
    public void findById_throwsEntityNotFound(){
        when(studentRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                studentService.findById(1));
    }

}
