package com.cisowski.schoolmanagement.mappers;

import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.model.entity.YearbookEntity;
import com.cisowski.schoolmanagement.users.student.mapper.StudentMapper;
import com.cisowski.schoolmanagement.users.common.model.AddressRequest;
import com.cisowski.schoolmanagement.users.student.model.StudentCreateRequest;
import com.cisowski.schoolmanagement.users.common.model.Gender;
import com.cisowski.schoolmanagement.users.student.model.AddStudentResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentDetailedResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentSummaryResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class StudentMapperTest {

    @InjectMocks
    private final StudentMapper mapper = Mappers.getMapper(StudentMapper.class);

    @Test
    void shouldMapStudentDtoToStudentEntity() {
        // Arrange
        StudentCreateRequest studentDto = new StudentCreateRequest();
        studentDto.setEmail("student@example.com");
        studentDto.setFirstName("Alice");
        studentDto.setLastName("Smith");
        studentDto.setPhoneNumber("123456789");
        studentDto.setBirthDate(new Date());
        studentDto.setGender(Gender.FEMALE);
        studentDto.setYearbookId(1);
        studentDto.setParentsIds(Arrays.asList(1, 2, 3));

        AddressRequest addressDto = new AddressRequest();
        addressDto.setCity("City");
        addressDto.setStreet("Street");
        addressDto.setZipCode("12345");
        studentDto.setAddress(addressDto);

        // Act
        StudentEntity student = mapper.toStudentEntity(studentDto);

        // Assert
        assertThat(student.getEmail()).isEqualTo(studentDto.getEmail());
        assertThat(student.getFirstName()).isEqualTo(studentDto.getFirstName());
        assertThat(student.getLastName()).isEqualTo(studentDto.getLastName());
        assertThat(student.getPhoneNumber()).isEqualTo(studentDto.getPhoneNumber());
    }

    @Test
    void shouldMapStudentToStudentDetailedResponse() {
        // Arrange
        StudentEntity student = new StudentEntity();
        student.setId(1);
        student.setEmail("student@example.com");
        student.setFirstName("Alice");
        student.setLastName("Smith");
        student.setPhoneNumber("123456789");
        student.setBirthDate(new Date());
        student.setGender(Gender.FEMALE);

        YearbookEntity yearbook = new YearbookEntity();
        yearbook.setId(1);
        yearbook.setSymbol("Yearbook 2025");
        student.setYearbook(yearbook);

        ParentEntity parent1 = new ParentEntity();
        parent1.setId(1);
        parent1.setFirstName("John");

        ParentEntity parent2 = new ParentEntity();
        parent2.setId(2);
        parent2.setFirstName("Jane");

        student.setParents(Arrays.asList(parent1, parent2));

        // Act
        StudentDetailedResponse response = mapper.toStudentResponse(student);

        // Assert
        assertThat(response.getId()).isEqualTo(student.getId());
        assertThat(response.getEmail()).isEqualTo(student.getEmail());
        assertThat(response.getFirstName()).isEqualTo(student.getFirstName());
        assertThat(response.getLastName()).isEqualTo(student.getLastName());
        assertThat(response.getYearbook().getId()).isEqualTo(student.getYearbook().getId());
        assertThat(response.getParents()).hasSize(2);
    }

    @Test
    void shouldMapStudentToAddStudentResponse() {
        // Arrange
        StudentEntity student = new StudentEntity();
        student.setId(1);
        student.setEmail("student@example.com");
        student.setFirstName("Alice");
        student.setLastName("Smith");
        student.setPhoneNumber("123456789");
        student.setBirthDate(new Date());
        student.setGender(Gender.FEMALE);

        // Act
        AddStudentResponse response = mapper.toAddStudentResponse(student);

        // Assert
        assertThat(response.getId()).isEqualTo(student.getId());
        assertThat(response.getEmail()).isEqualTo(student.getEmail());
        assertThat(response.getFirstName()).isEqualTo(student.getFirstName());
        assertThat(response.getLastName()).isEqualTo(student.getLastName());
    }

    @Test
    void shouldMapCollectionOfStudentsToStudentSummaryResponses() {
        // Arrange
        StudentEntity student1 = new StudentEntity();
        student1.setEmail("student1@example.com");
        student1.setFirstName("Alice");
        student1.setLastName("Smith");
        student1.setBirthDate(new Date());
        student1.setPhoneNumber("123456789");

        StudentEntity student2 = new StudentEntity();
        student2.setEmail("student2@example.com");
        student2.setFirstName("Bob");
        student2.setLastName("Jones");
        student2.setBirthDate(new Date());
        student2.setPhoneNumber("987654321");

        Collection<StudentEntity> students = Arrays.asList(student1, student2);

        // Act
        List<StudentSummaryResponse> responses = mapper.toStudentsResponse(students);

        // Assert
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getEmail()).isEqualTo(student1.getEmail());
        assertThat(responses.get(1).getEmail()).isEqualTo(student2.getEmail());
    }
}

