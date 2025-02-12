package com.cisowski.schoolmanagement.service.impl;

import com.cisowski.schoolmanagement.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.mapper.ParentMapper;
import com.cisowski.schoolmanagement.model.entity.Student;
import com.cisowski.schoolmanagement.model.request.ParentPatchRequest;
import com.cisowski.schoolmanagement.model.response.AddParentResponse;
import com.cisowski.schoolmanagement.model.request.ParentCreateRequest;
import com.cisowski.schoolmanagement.model.response.ParentDetailedResponse;
import com.cisowski.schoolmanagement.model.response.ParentSummaryResponse;
import com.cisowski.schoolmanagement.model.entity.Parent;
import com.cisowski.schoolmanagement.repository.ParentRepository;
import com.cisowski.schoolmanagement.repository.StudentRepository;
import com.cisowski.schoolmanagement.service.ParentService;
import com.cisowski.schoolmanagement.utility.DbLogger;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ParentServiceImpl implements ParentService {

    private final ParentRepository parentRepository;
    private final ParentMapper parentMapper;
    private final StudentRepository studentRepository;

    public ParentServiceImpl(
            ParentRepository parentRepository,
            ParentMapper parentMapper,
            StudentRepository studentRepository) {
        this.parentRepository = parentRepository;
        this.parentMapper = parentMapper;
        this.studentRepository = studentRepository;
    }

    @Transactional
    @Override
    public AddParentResponse addParent(ParentCreateRequest parentDto) {
        String message = "Add Parent for: " + parentDto.toString();
        DbLogger.info(message);

        Optional<Parent> existingUser = parentRepository.findByEmail(parentDto.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException(parentDto.getEmail());
        }
        Parent requestParent = parentMapper.toParentEntity(parentDto);
        String firstPassword = this.generateNewUserPassword();
        String hashedPassword = this.hashPassword(firstPassword);
        requestParent.setPassword(hashedPassword);
        fetchChildrenEntities(requestParent, parentDto.getChildrenIds());

        Parent savedParent = parentRepository.save(requestParent);

        message = "Parent saved successfully: " + savedParent.toString();
        DbLogger.info(message);

        AddParentResponse response = parentMapper.toAddParentResponse(savedParent);
        response.setPassword(firstPassword);

        return response;
    }

    private void fetchChildrenEntities(Parent parent, Collection<Integer> childrenIds) {
        if (childrenIds == null || childrenIds.isEmpty())
            throw new SpecificationBrokenException("Cannot create Parent without children selected");

        List<Student> children = fetchStudentEntities(childrenIds);

        children.forEach(child -> {
            child.getParents().add(parent);
        });
        parent.getChildren().addAll(children);
    }

    private List<Student> fetchStudentEntities(Collection<Integer> studentIds){
        DbLogger.info("Fetching children for Parent: " + studentIds);
        List<Student> students = studentRepository.findAllById(studentIds.stream().toList());
        if(students.size() != studentIds.size())
            throw new SpecificationBrokenException("Some of given children IDs are invalid or non-existent");
        return students;
    }

    @Transactional
    @Override
    public ParentDetailedResponse updateParent(ParentPatchRequest parentDto, Integer parentId) {
        String message = String.format("Update Parent with ID %s, with given data: %s", parentId, parentDto.toString());
        DbLogger.info(message);

        Optional<Parent> existingParent = parentRepository.findById(parentId);
        if (existingParent.isEmpty())
            throw new EntityNotFoundException(Parent.class, "ID", parentId.toString());

        Parent existingParentEntity = existingParent.get();
        checkAndUpdateChildren(existingParentEntity, parentDto);
        Parent requestParent = parentMapper.toParentEntity(parentDto);
        parentMapper.patchParentEntity(requestParent, existingParentEntity);

        Parent updatedParent = parentRepository.save(existingParentEntity);

        message = "Parent updated successfully: " + updatedParent.toString();
        DbLogger.info(message);

        return parentMapper.toParentDetailedResponse(updatedParent);
    }

    private void checkAndUpdateChildren(Parent existingParent, ParentPatchRequest parentPatchRequest){
        if(parentPatchRequest.getChildrenIdsToAdd() != null && !parentPatchRequest.getChildrenIdsToAdd().isEmpty()){
            updateChildren(existingParent, parentPatchRequest.getChildrenIdsToAdd());
        }
        if(parentPatchRequest.getChildrenIdsToRemove() != null && !parentPatchRequest.getChildrenIdsToRemove().isEmpty()){
            removeChildrenRelation(existingParent, parentPatchRequest.getChildrenIdsToRemove());
        }
    }

    private void updateChildren(Parent parent, Collection<Integer> childrenIds){
        List<Student> children = fetchStudentEntities(childrenIds);
        children.forEach(child -> {
            if(!parent.getChildren().contains(child)){
                parent.getChildren().add(child);
                child.getParents().add(parent);
            }
        });
    }

    private void removeChildrenRelation(Parent parent, Collection<Integer> childrenIds){
        List<Student> children = fetchStudentEntities(childrenIds);
        children.forEach(child -> {
            if(!parent.getChildren().contains(child))
                throw new SpecificationBrokenException(String.format(
                        "Student with ID: %s, not associated with Parent with ID: %s",
                        child.getId(),
                        parent.getId()));

            child.getParents().remove(parent);
            parent.getChildren().remove(child);
        });
    }

    @Override
    public List<ParentSummaryResponse> findAll() {
        String message = "Searching for all Parent entities";
        DbLogger.info(message);

        Collection<Parent> parents = parentRepository.findAll();

        return parentMapper.toParentsResponse(parents);
    }

    @Override
    public ParentDetailedResponse findById(Integer parentId) {
        String message = String.format("Searching for Parent with ID %s", parentId);
        DbLogger.info(message);

        Optional<Parent> existingParent = parentRepository.findById(parentId);
        if (existingParent.isEmpty())
            throw new EntityNotFoundException(Parent.class, "ID", parentId.toString());

        message = String.format("Found Parent with ID %s", parentId);
        DbLogger.info(message);

        return parentMapper.toParentDetailedResponse(existingParent.get());
    }

    @Transactional
    @Override
    public void deleteUser(Integer userId) {
        String message = String.format("Deleting Parent with ID %s", userId);
        DbLogger.info(message);

        Optional<Parent> existingParent = parentRepository.findById(userId);
        if (existingParent.isEmpty())
            throw new EntityNotFoundException(Parent.class, "Parent ID", userId.toString());

        removeChildParentRelation(existingParent.get(), existingParent.get().getChildren());
        parentRepository.delete(existingParent.get());

        message = String.format("Parent with ID %s, was successfully removed", userId);
        DbLogger.info(message);
    }

    private void removeChildParentRelation(Parent parent, Collection<Student> children){
        children.forEach(child -> child.getParents().remove(parent));
    }
}
