package com.cisowski.schoolmanagement.services;

import com.cisowski.schoolmanagement.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.mapper.ParentMapper;
import com.cisowski.schoolmanagement.mapper.ParentMapperImpl;
import com.cisowski.schoolmanagement.model.request.*;
import com.cisowski.schoolmanagement.model.response.AddParentResponse;
import com.cisowski.schoolmanagement.model.response.ParentDetailedResponse;
import com.cisowski.schoolmanagement.model.response.ParentSummaryResponse;
import com.cisowski.schoolmanagement.model.entity.Parent;
import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.repository.ParentRepository;
import com.cisowski.schoolmanagement.service.impl.ParentServiceImpl;
import com.cisowski.schoolmanagement.utility.PasswordGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
public class ParentServiceTests {

    @Mock
    private ParentRepository repository;
    @Mock
    private ParentMapperImpl mockedMapper;
    private final ParentMapper parentMapper = Mappers.getMapper(ParentMapper.class);
    @InjectMocks
    private ParentServiceImpl service;
    private final String generatedPassword = PasswordGenerator.generatePassword();



    @Test
    @DisplayName("add Parent successful - returns full ParentResponse")
    public void addParent_successful() {
        ParentCreateRequest dto = Instancio.create(ParentCreateRequest.class);
        Parent parent = parentMapper.toParentEntity(dto);
        parent.setPassword(generatedPassword);
        parent.setId(1);
        AddParentResponse response = parentMapper.toAddParentResponse(parent);

        when(repository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(mockedMapper.toParentEntity(dto)).thenReturn(parent);
        when(repository.save(any())).thenReturn(parent);
        when(mockedMapper.toAddParentResponse(any())).thenReturn(response);

        AddParentResponse result = service.addParent(dto);

        assertNotNull(result);
        assertNotNull(result.getPassword());
        assertEquals(response, result);
        assertEquals(response.getId(), result.getId());
        assertEquals(response.getEmail(), result.getEmail());
        assertEquals(response.getFirstName(), result.getFirstName());
        assertEquals(response.getLastName(), result.getLastName());
        assertEquals(response.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(response.getBirthDate(), result.getBirthDate());
        assertEquals(response.getGender(), result.getGender());
        assertIterableEquals(response.getAuthority(), result.getAuthority());
        assertIterableEquals(response.getChildren(), result.getChildren());
    }

    @Test
    @DisplayName("addParent should throw EmailAlreadyExistsException")
    public void addParent_throwsEmailAlreadyExistsEx() {
        ParentCreateRequest dto = Instancio.create(ParentCreateRequest.class);
        User existingUser = new User();

        when(repository.findByEmail(any())).thenReturn(Optional.of(existingUser));

        assertThrows(EmailAlreadyExistsException.class, () ->
                service.addParent(dto));
    }

    @Test
    @DisplayName("updateParent successful - returns ParentResponse")
    public void updateParent_successful() {
        ParentPatchRequest dto = Instancio.create(ParentPatchRequest.class);
        User user = new User();
        user.setPassword("strongPass123");
        Parent parent = parentMapper.toParentEntity(dto);
        ParentDetailedResponse response = parentMapper.toParentDetailedResponse(parent);

        when(repository.findByEmail(any())).thenReturn(Optional.of(user));
        when(repository.save(any())).thenReturn(parent);
        when(mockedMapper.toParentDetailedResponse(any())).thenReturn(response);
        when(mockedMapper.toParentEntity(dto)).thenReturn(parent);

        ParentDetailedResponse result = service.updateParent(dto, 1);

        assertNotNull(result);
        assertEquals(response, result);
        assertEquals(response.getId(), result.getId());
        assertEquals(response.getEmail(), result.getEmail());
        assertEquals(response.getFirstName(), result.getFirstName());
        assertEquals(response.getLastName(), result.getLastName());
        assertEquals(response.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(response.getBirthDate(), result.getBirthDate());
        assertEquals(response.getGender(), result.getGender());
        assertIterableEquals(response.getAuthority(), result.getAuthority());
        assertIterableEquals(response.getChildren(), result.getChildren());
    }

    @Test
    @DisplayName("updateParent should throw EntityNotFoundException")
    public void updateParent_throwsEntityNotFoundEx() {
        ParentPatchRequest dto = Instancio.create(ParentPatchRequest.class);

        when(repository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                service.updateParent(dto, 1));
    }

    @Test
    @DisplayName("deleteParent should invoke delete method in repo")
    public void deleteParent_successful() {
        Integer userId = 1;
        Parent parent = new Parent();
        parent.setId(userId);

        when(repository.findById(userId)).thenReturn(Optional.of(parent));

        service.deleteUser(userId);

        verify(repository, times(1)).findById(userId);
        verify(repository).delete(parent);
    }

    @Test
    @DisplayName("deleteParent should throw EntityNotFoundException")
    public void deleteParent_throwsEntityNotFoundEx() {
        Integer userId = 1;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                service.deleteUser(userId));
    }

    @Test
    @DisplayName("findAll successful - should return Collection<ParentResponse>")
    public void findAll_successful() {
        List<Parent> parents = Instancio.createList(Parent.class);
        List<ParentSummaryResponse> responses = parentMapper.toParentsResponse(parents);

        when(repository.findAll()).thenReturn(parents);
        when(mockedMapper.toParentsResponse(any())).thenReturn(responses);

        List<ParentSummaryResponse> result = service.findAll();

        verify(repository, times(1)).findAll();
        assertNotNull(result);
        assertIterableEquals(responses, result);
    }

    @Test
    @DisplayName("findById successful - should return ParentResponse")
    public void findById_successful() {
        Integer userId = 1;
        Parent existingParent = Instancio.create(Parent.class);
        ParentDetailedResponse response = parentMapper.toAddParentResponse(existingParent);

        when(repository.findById(userId)).thenReturn(Optional.of(existingParent));
        when(mockedMapper.toParentDetailedResponse(existingParent)).thenReturn(response);

        ParentDetailedResponse result = service.findById(userId);

        verify(repository, times(1)).findById(userId);
        verify(mockedMapper, times(1)).toParentDetailedResponse(existingParent);
        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    @DisplayName("findById - should throw EntityNotFoundEx")
    public void findById_throwsEntityNotFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                service.findById(1));
    }

}

