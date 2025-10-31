package com.cisowski.schoolmanagement.unit.mappers;

import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.yearbook.mapper.YearbookMapper;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import com.cisowski.schoolmanagement.users.common.model.AddressRequest;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherCreateRequest;
import com.cisowski.schoolmanagement.users.teacher.model.AddTeacherResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherDetailedResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.common.model.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;

class TeacherMapperTest {

    @InjectMocks
    private final TeacherMapper mapper = Mappers.getMapper(TeacherMapper.class);

    @BeforeEach
    public void setUp(){
        YearbookMapper yearbookMapper = Mappers.getMapper(YearbookMapper.class);
        ReflectionTestUtils.setField(mapper, "yearbookMapper", yearbookMapper);
    }

    @Test
    void shouldMapTeacherDtoToTeacherEntity() {
        // Arrange
        TeacherCreateRequest teacherDto = new TeacherCreateRequest();
        teacherDto.setEmail("teacher@example.com");
        teacherDto.setFirstName("John");
        teacherDto.setLastName("Doe");
        teacherDto.setPhoneNumber("123456789");
        teacherDto.setBirthDate(new Date());
        teacherDto.setGender(Gender.MALE);
        teacherDto.setTeachingSubjectsIds(new HashSet<>(Arrays.asList(1, 2, 3)));

        AddressRequest addressDto = new AddressRequest();
        addressDto.setCity("City");
        addressDto.setStreet("Street");
        addressDto.setZipCode("12345");
        teacherDto.setAddress(addressDto);

        // Act
        TeacherEntity teacher = mapper.toTeacherEntity(teacherDto);

        // Assert
        assertThat(teacher.getEmail()).isEqualTo(teacherDto.getEmail());
        assertThat(teacher.getFirstName()).isEqualTo(teacherDto.getFirstName());
        assertThat(teacher.getLastName()).isEqualTo(teacherDto.getLastName());
        assertThat(teacher.getPhoneNumber()).isEqualTo(teacherDto.getPhoneNumber());
    }

    @Test
    void shouldMapTeacherToTeacherDetailedResponse() {
        // Arrange
        TeacherEntity teacher = new TeacherEntity();
        teacher.setId(1);
        teacher.setEmail("teacher@example.com");
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setPhoneNumber("123456789");
        teacher.setBirthDate(new Date());
        teacher.setGender(Gender.MALE);

        SubjectEntity subject1 = new SubjectEntity();
        subject1.setId(1);
        subject1.setName("Math");

        SubjectEntity subject2 = new SubjectEntity();
        subject2.setId(2);
        subject2.setName("Physics");

        teacher.setTeachingSubjects(new HashSet<>(Arrays.asList(subject1, subject2)));

        YearbookEntity leadingYearbook = new YearbookEntity();
        leadingYearbook.setId(1);
        leadingYearbook.setSymbol("Yearbook 2025");
        teacher.setLeadingYearbook(leadingYearbook);

        // Act
        TeacherDetailedResponse response = mapper.toTeacherResponse(teacher);

        // Assert
        assertThat(response.getId()).isEqualTo(teacher.getId());
        assertThat(response.getEmail()).isEqualTo(teacher.getEmail());
        assertThat(response.getFirstName()).isEqualTo(teacher.getFirstName());
        assertThat(response.getLastName()).isEqualTo(teacher.getLastName());
        assertThat(response.getTeachingSubjects()).hasSize(2);
        assertThat(response.getLeadingYearbook().getId()).isEqualTo(leadingYearbook.getId());
    }

    @Test
    void shouldMapTeacherToAddTeacherResponse() {
        // Arrange
        TeacherEntity teacher = new TeacherEntity();
        teacher.setId(1);
        teacher.setEmail("teacher@example.com");
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setPhoneNumber("123456789");
        teacher.setBirthDate(new Date());
        teacher.setGender(Gender.MALE);

        // Act
        AddTeacherResponse response = mapper.toAddTeacherResponse(teacher);

        // Assert
        assertThat(response.getId()).isEqualTo(teacher.getId());
        assertThat(response.getEmail()).isEqualTo(teacher.getEmail());
        assertThat(response.getFirstName()).isEqualTo(teacher.getFirstName());
        assertThat(response.getLastName()).isEqualTo(teacher.getLastName());
    }

    @Test
    void shouldMapCollectionOfTeachersToTeacherSummaryResponses() {
        // Arrange
        TeacherEntity teacher1 = new TeacherEntity();
        teacher1.setEmail("teacher1@example.com");
        teacher1.setFirstName("John");
        teacher1.setLastName("Doe");
        teacher1.setBirthDate(new Date());
        teacher1.setPhoneNumber("123456789");

        TeacherEntity teacher2 = new TeacherEntity();
        teacher2.setEmail("teacher2@example.com");
        teacher2.setFirstName("Jane");
        teacher2.setLastName("Smith");
        teacher2.setBirthDate(new Date());
        teacher2.setPhoneNumber("987654321");

        Collection<TeacherEntity> teachers = Arrays.asList(teacher1, teacher2);

        // Act
        List<TeacherSummaryResponse> responses = mapper.toTeachersResponse(teachers);

        // Assert
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getEmail()).isEqualTo(teacher1.getEmail());
        assertThat(responses.get(1).getEmail()).isEqualTo(teacher2.getEmail());
    }
}

