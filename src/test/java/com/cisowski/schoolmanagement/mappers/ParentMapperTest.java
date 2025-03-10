package com.cisowski.schoolmanagement.mappers;

import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.mapper.StudentMapper;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.parent.mapper.ParentMapper;
import com.cisowski.schoolmanagement.users.common.model.AddressRequest;
import com.cisowski.schoolmanagement.users.parent.model.ParentCreateRequest;
import com.cisowski.schoolmanagement.users.common.model.AddressEntity;
import com.cisowski.schoolmanagement.users.common.model.Gender;
import com.cisowski.schoolmanagement.users.parent.model.AddParentResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentDetailedResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentSummaryResponse;
import com.cisowski.schoolmanagement.yearbook.mapper.YearbookMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class ParentMapperTest {

    @InjectMocks
    private final ParentMapper mapper = Mappers.getMapper(ParentMapper.class);

    @BeforeEach
    public void setUp(){
        StudentMapper studentMapper = Mappers.getMapper(StudentMapper.class);
        ReflectionTestUtils.setField(mapper, "studentMapper", studentMapper);
    }

    @Test
    void shouldMapParentDtoToParentEntity() {
        // Arrange
        ParentCreateRequest parentDto = new ParentCreateRequest();
        parentDto.setEmail("parent@example.com");
        parentDto.setFirstName("John");
        parentDto.setLastName("Doe");
        parentDto.setPhoneNumber("123456789");
        parentDto.setBirthDate(new Date());
        parentDto.setGender(Gender.MALE);
        parentDto.setChildrenIds(Arrays.asList(1, 2, 3));

        AddressRequest addressDto = new AddressRequest();
        addressDto.setCity("City");
        addressDto.setStreet("Street");
        addressDto.setZipCode("12345");
        parentDto.setAddress(addressDto);

        // Act
        ParentEntity parent = mapper.toParentEntity(parentDto);

        // Assert
        assertThat(parent.getEmail()).isEqualTo(parentDto.getEmail());
        assertThat(parent.getFirstName()).isEqualTo(parentDto.getFirstName());
        assertThat(parent.getLastName()).isEqualTo(parentDto.getLastName());
        assertThat(parent.getPhoneNumber()).isEqualTo(parentDto.getPhoneNumber());
    }

    @Test
    void shouldMapParentToParentDetailedResponse() {
        // Arrange
        ParentEntity parent = new ParentEntity();
        parent.setId(1);
        parent.setEmail("parent@example.com");
        parent.setFirstName("John");
        parent.setLastName("Doe");
        parent.setPhoneNumber("123456789");
        parent.setBirthDate(new Date());
        parent.setGender(Gender.MALE);

        AddressEntity address = new AddressEntity();
        address.setCity("City");
        address.setStreet("Street");
        address.setZipCode("12345");
        parent.setAddress(address);

        StudentEntity child1 = new StudentEntity();
        child1.setId(1);
        child1.setFirstName("Child1");

        StudentEntity child2 = new StudentEntity();
        child2.setId(2);
        child2.setFirstName("Child2");

        parent.setChildren(Arrays.asList(child1, child2));

        // Act
        ParentDetailedResponse response = mapper.toParentDetailedResponse(parent);

        // Assert
        assertThat(response.getId()).isEqualTo(parent.getId());
        assertThat(response.getEmail()).isEqualTo(parent.getEmail());
        assertThat(response.getFirstName()).isEqualTo(parent.getFirstName());
        assertThat(response.getLastName()).isEqualTo(parent.getLastName());
        assertThat(response.getPhoneNumber()).isEqualTo(parent.getPhoneNumber());
        assertThat(response.getChildren()).hasSize(2);
    }

    @Test
    void shouldMapParentToAddParentResponse() {
        // Arrange
        ParentEntity parent = new ParentEntity();
        parent.setId(1);
        parent.setEmail("parent@example.com");
        parent.setFirstName("John");
        parent.setLastName("Doe");
        parent.setPhoneNumber("123456789");
        parent.setBirthDate(new Date());
        parent.setGender(Gender.MALE);

        // Act
        AddParentResponse response = mapper.toAddParentResponse(parent);

        // Assert
        assertThat(response.getId()).isEqualTo(parent.getId());
        assertThat(response.getEmail()).isEqualTo(parent.getEmail());
        assertThat(response.getFirstName()).isEqualTo(parent.getFirstName());
        assertThat(response.getLastName()).isEqualTo(parent.getLastName());
        assertThat(response.getPhoneNumber()).isEqualTo(parent.getPhoneNumber());
    }

    @Test
    void shouldMapCollectionOfParentsToParentSummaryResponses() {
        // Arrange
        ParentEntity parent1 = new ParentEntity();
        parent1.setEmail("parent1@example.com");
        parent1.setFirstName("John");
        parent1.setLastName("Doe");
        parent1.setBirthDate(new Date());
        parent1.setPhoneNumber("123456789");

        ParentEntity parent2 = new ParentEntity();
        parent2.setEmail("parent2@example.com");
        parent2.setFirstName("Jane");
        parent2.setLastName("Smith");
        parent2.setBirthDate(new Date());
        parent2.setPhoneNumber("987654321");

        Collection<ParentEntity> parents = Arrays.asList(parent1, parent2);

        // Act
        List<ParentSummaryResponse> responses = mapper.toParentsResponse(parents);

        // Assert
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getEmail()).isEqualTo(parent1.getEmail());
        assertThat(responses.get(1).getEmail()).isEqualTo(parent2.getEmail());
    }
}
