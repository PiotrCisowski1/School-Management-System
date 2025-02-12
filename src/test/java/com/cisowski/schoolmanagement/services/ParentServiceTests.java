package com.cisowski.schoolmanagement.services;

import com.cisowski.schoolmanagement.users.parent.mapper.ParentMapperImpl;
import com.cisowski.schoolmanagement.users.parent.model.ParentCreateRequest;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentPatchRequest;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.common.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.users.parent.mapper.ParentMapper;
import com.cisowski.schoolmanagement.users.parent.model.AddParentResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentDetailedResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentSummaryResponse;
import com.cisowski.schoolmanagement.users.parent.repository.ParentRepository;
import com.cisowski.schoolmanagement.users.student.repository.StudentRepository;
import com.cisowski.schoolmanagement.users.parent.service.ParentServiceImpl;
import com.cisowski.schoolmanagement.common.utility.PasswordGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
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
    private StudentRepository studentRepository;
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
        List<StudentEntity> children = Instancio.ofList(StudentEntity.class).size(3).create();
        List<Integer> childrenIds = children.stream().map(StudentEntity::getId).toList();
        dto.setChildrenIds(childrenIds);
        ParentEntity parent = parentMapper.toParentEntity(dto);
        parent.setPassword(generatedPassword);
        parent.setChildren(children);
        parent.setId(1);

        AddParentResponse mockResponse = parentMapper.toAddParentResponse(parent);
        mockResponse.setId(1);
        mockResponse.setPassword("ThePass123");

        when(repository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(mockedMapper.toParentEntity(dto)).thenReturn(parent);
        when(studentRepository.findAllById(any())).thenReturn(children);
        when(repository.save(any())).thenReturn(parent);
        when(mockedMapper.toAddParentResponse(any())).thenReturn(mockResponse);

        AddParentResponse result = service.addParent(dto);

        assertNotNull(result);
        assertNotNull(result.getPassword());
        assertNotNull(result.getId());
        assertEquals(dto.getEmail(), result.getEmail());
        assertEquals(dto.getFirstName(), result.getFirstName());
        assertEquals(dto.getLastName(), result.getLastName());
        assertEquals(dto.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(dto.getBirthDate(), result.getBirthDate());
        assertEquals(dto.getGender(), result.getGender());
        assertIterableEquals(dto.getAuthority(), result.getAuthority());
        assertEquals(childrenIds.size(), result.getChildren().size());
        verify(repository, times(1)).findByEmail(dto.getEmail());
        verify(repository, times(1)).save(any());
        verify(mockedMapper,times(1)).toParentEntity(dto);
        verify(mockedMapper,times(1)).toAddParentResponse(any());
    }

    @Test
    @DisplayName("addParent should throw EmailAlreadyExistsException")
    public void addParent_throwsEmailAlreadyExistsEx() {
        ParentCreateRequest dto = Instancio.create(ParentCreateRequest.class);
        ParentEntity existingUser = new ParentEntity();

        when(repository.findByEmail(any())).thenReturn(Optional.of(existingUser));

        assertThrows(EmailAlreadyExistsException.class, () ->
                service.addParent(dto));
    }

    @Test
    @DisplayName("addParent throws SpecificationBrokenException - no children selected")
    public void addParent_throwsSpecBrokenEx(){
        ParentCreateRequest dto = Instancio.create(ParentCreateRequest.class);
        dto.setChildrenIds(null);
        ParentEntity parent = parentMapper.toParentEntity(dto);

        when(repository.findByEmail(any())).thenReturn(Optional.empty());
        when(mockedMapper.toParentEntity(dto)).thenReturn(parent);

        SpecificationBrokenException exception = assertThrows(
                SpecificationBrokenException.class,
                () -> service.addParent(dto));
        assertTrue(exception.getMessage().contains("without children"));
    }

    @Test
    @DisplayName("addParent throws SpecificationBrokenException - not every childrenId exists")
    public void addParent_throwsSpecBrokenEx2(){
        ParentCreateRequest dto = Instancio.create(ParentCreateRequest.class);
        List<Integer> childrenIds = Instancio.ofList(Integer.class).size(3).create();
        dto.setChildrenIds(childrenIds);
        List<StudentEntity> children = Instancio.ofList(StudentEntity.class).size(1).create();
        ParentEntity parent = parentMapper.toParentEntity(dto);

        when(repository.findByEmail(any())).thenReturn(Optional.empty());
        when(mockedMapper.toParentEntity(dto)).thenReturn(parent);
        when(studentRepository.findAllById(any())).thenReturn(children);

        SpecificationBrokenException thrown = assertThrows(
                SpecificationBrokenException.class,
                () -> service.addParent(dto));
        assertTrue(thrown.getMessage().contains("children IDs are invalid"));
    }

    @Test
    @DisplayName("updateParent successful - returns ParentResponse")
    public void updateParent_successful() {
        ParentPatchRequest dto = Instancio.create(ParentPatchRequest.class);
        List<StudentEntity> children = Instancio.ofList(StudentEntity.class).size(1).create();
        children.get(0).setId(1);
        StudentEntity studentToRemove = Instancio.create(StudentEntity.class);
        children.add(studentToRemove);
        List<Integer> childrenIdsToRemove = Collections.singletonList(studentToRemove.getId());
        List<Integer> childrenIdsToAdd = Arrays.asList(3,4);
        List<StudentEntity> childrenToAdd = Instancio.ofList(StudentEntity.class).size(2).create();
        childrenToAdd.get(0).setId(3);
        childrenToAdd.get(1).setId(4);
        List<StudentEntity> childrenToRemove = Collections.singletonList(studentToRemove);
        childrenToRemove.get(0).setId(1);
        dto.setChildrenIdsToAdd(childrenIdsToAdd);
        dto.setChildrenIdsToRemove(childrenIdsToRemove);
        ParentEntity parent = parentMapper.toParentEntity(dto);
        parent.setId(1);
        children.addAll(childrenToAdd);
        parent.setChildren(children);
        ParentDetailedResponse response = parentMapper.toParentDetailedResponse(parent);

        when(repository.findById(any())).thenReturn(Optional.of(parent));
        when(repository.save(any())).thenReturn(parent);
        when(mockedMapper.toParentDetailedResponse(any())).thenReturn(response);
        when(mockedMapper.toParentEntity(dto)).thenReturn(parent);
        when(studentRepository.findAllById(any())).thenReturn(childrenToAdd,childrenToRemove);
        doNothing().when(mockedMapper).patchParentEntity(any(), any());

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
        Integer expectedChildrenSize = children.size() + childrenToAdd.size() - childrenToRemove.size();
        assertEquals(expectedChildrenSize, result.getChildren().size());
        verify(repository, times(1)).findById(any());
        verify(mockedMapper, times(1)).toParentEntity(dto);
        verify(mockedMapper, times(1)).patchParentEntity(any(), any());
        verify(mockedMapper, times(1)).toParentDetailedResponse(any());
        verify(repository, times(1)).save(any());
        verify(studentRepository, times(2)).findAllById(any());
    }

    @Test
    @DisplayName("updateParent should throw EntityNotFoundException")
    public void updateParent_throwsEntityNotFoundEx() {
        ParentPatchRequest dto = Instancio.create(ParentPatchRequest.class);

        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                service.updateParent(dto, 1));
    }

    @Test
    @DisplayName("updateParent throws SpecificationBrokenException - child to remove does not exist")
    public void updateParent_throwsSpecBrokenEx(){
        ParentPatchRequest dto = Instancio.create(ParentPatchRequest.class);
        List<Integer> childrenIds = Instancio.ofList(Integer.class).size(3).create();
        dto.setChildrenIdsToAdd(null);
        dto.setChildrenIdsToRemove(childrenIds);
        List<StudentEntity> children = Instancio.ofList(StudentEntity.class).size(3).create();
        ParentEntity parent = parentMapper.toParentEntity(dto);

        when(repository.findById(any())).thenReturn(Optional.of(parent));
        when(studentRepository.findAllById(any())).thenReturn(children);

        SpecificationBrokenException exception = assertThrows(
                SpecificationBrokenException.class,
                () -> service.updateParent(dto, 1));
        assertTrue(exception.getMessage().contains("not associated with Parent"));
    }

    @Test
    @DisplayName("updateParent throws SpecificationBrokenException - not every childrenId exists")
    public void updateParent_throwsSpecBrokenEx2(){
        ParentPatchRequest dto = Instancio.create(ParentPatchRequest.class);
        List<Integer> childrenIds = Instancio.ofList(Integer.class).size(3).create();
        dto.setChildrenIdsToAdd(childrenIds);
        List<StudentEntity> children = Instancio.ofList(StudentEntity.class).size(1).create();
        ParentEntity parent = parentMapper.toParentEntity(dto);

        when(repository.findById(any())).thenReturn(Optional.of(parent));
        when(studentRepository.findAllById(any())).thenReturn(children);

        SpecificationBrokenException thrown = assertThrows(
                SpecificationBrokenException.class,
                () -> service.updateParent(dto, 1));
        assertTrue(thrown.getMessage().contains("children IDs are invalid"));
    }

    @Test
    @DisplayName("deleteParent should invoke delete method in repo")
    public void deleteParent_successful() {
        Integer userId = 1;
        ParentEntity parent = new ParentEntity();
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
        List<ParentEntity> parents = Instancio.createList(ParentEntity.class);
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
        ParentEntity existingParent = Instancio.create(ParentEntity.class);
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

