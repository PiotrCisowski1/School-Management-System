package com.cisowski.schoolmanagement.users.parent.service;

import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.common.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.users.parent.mapper.ParentMapper;
import com.cisowski.schoolmanagement.users.parent.model.ParentPatchRequest;
import com.cisowski.schoolmanagement.users.parent.model.AddParentResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentCreateRequest;
import com.cisowski.schoolmanagement.users.parent.model.ParentDetailedResponse;
import com.cisowski.schoolmanagement.users.parent.model.ParentSummaryResponse;
import com.cisowski.schoolmanagement.users.parent.repository.ParentRepository;
import com.cisowski.schoolmanagement.users.student.repository.StudentRepository;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
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

        Optional<ParentEntity> existingUser = parentRepository.findByEmail(parentDto.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException(parentDto.getEmail());
        }
        ParentEntity requestParent = parentMapper.toParentEntity(parentDto);
        String firstPassword = this.generateNewUserPassword();
        String hashedPassword = this.hashPassword(firstPassword);
        requestParent.setPassword(hashedPassword);
        fetchChildrenEntities(requestParent, parentDto.getChildrenIds());

        ParentEntity savedParent = parentRepository.save(requestParent);

        message = "Parent saved successfully: " + savedParent.toString();
        DbLogger.info(message);

        AddParentResponse response = parentMapper.toAddParentResponse(savedParent);
        response.setPassword(firstPassword);

        return response;
    }

    private void fetchChildrenEntities(ParentEntity parent, Collection<Integer> childrenIds) {
        if (childrenIds == null || childrenIds.isEmpty())
            throw new SpecificationBrokenException("Cannot create Parent without children selected");

        List<StudentEntity> children = fetchStudentEntities(childrenIds);

        children.forEach(child -> child.getParents().add(parent));
        parent.getChildren().addAll(children);
    }

    private List<StudentEntity> fetchStudentEntities(Collection<Integer> studentIds){
        DbLogger.info("Fetching children for Parent: " + studentIds);
        List<StudentEntity> students = studentRepository.findAllById(studentIds.stream().toList());
        if(students.size() != studentIds.size())
            throw new SpecificationBrokenException("Some of given children IDs are invalid or non-existent");
        return students;
    }

    @Transactional
    @Override
    public ParentDetailedResponse updateParent(ParentPatchRequest parentDto, Integer parentId) {
        String message = String.format("Update Parent with ID %s, with given data: %s", parentId, parentDto.toString());
        DbLogger.info(message);

        Optional<ParentEntity> existingParent = parentRepository.findById(parentId);
        if (existingParent.isEmpty())
            throw new EntityNotFoundException(ParentEntity.class, "ID", parentId.toString());

        ParentEntity existingParentEntity = existingParent.get();
        checkAndUpdateChildren(existingParentEntity, parentDto);
        ParentEntity requestParent = parentMapper.toParentEntity(parentDto);
        parentMapper.patchParentEntity(requestParent, existingParentEntity);

        ParentEntity updatedParent = parentRepository.save(existingParentEntity);

        message = "Parent updated successfully: " + updatedParent.toString();
        DbLogger.info(message);

        return parentMapper.toParentDetailedResponse(updatedParent);
    }

    private void checkAndUpdateChildren(ParentEntity existingParent, ParentPatchRequest parentPatchRequest){
        if(parentPatchRequest.getChildrenIdsToAdd() != null && !parentPatchRequest.getChildrenIdsToAdd().isEmpty()){
            updateChildren(existingParent, parentPatchRequest.getChildrenIdsToAdd());
        }
        if(parentPatchRequest.getChildrenIdsToRemove() != null && !parentPatchRequest.getChildrenIdsToRemove().isEmpty()){
            removeChildrenRelation(existingParent, parentPatchRequest.getChildrenIdsToRemove());
        }
    }

    private void updateChildren(ParentEntity parent, Collection<Integer> childrenIds){
        List<StudentEntity> children = fetchStudentEntities(childrenIds);
        children.forEach(child -> {
            if(!parent.getChildren().contains(child)){
                parent.getChildren().add(child);
                child.getParents().add(parent);
            }
        });
    }

    private void removeChildrenRelation(ParentEntity parent, Collection<Integer> childrenIds){
        List<StudentEntity> children = fetchStudentEntities(childrenIds);
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

        Collection<ParentEntity> parents = parentRepository.findAll();

        return parentMapper.toParentsResponse(parents);
    }

    @Override
    public ParentDetailedResponse findById(Integer parentId) {
        String message = String.format("Searching for Parent with ID %s", parentId);
        DbLogger.info(message);

        Optional<ParentEntity> existingParent = parentRepository.findById(parentId);
        if (existingParent.isEmpty())
            throw new EntityNotFoundException(ParentEntity.class, "ID", parentId.toString());

        message = String.format("Found Parent with ID %s", parentId);
        DbLogger.info(message);

        return parentMapper.toParentDetailedResponse(existingParent.get());
    }

    @Transactional
    @Override
    public void deleteUser(Integer userId) {
        String message = String.format("Deleting Parent with ID %s", userId);
        DbLogger.info(message);

        Optional<ParentEntity> existingParent = parentRepository.findById(userId);
        if (existingParent.isEmpty())
            throw new EntityNotFoundException(ParentEntity.class, "Parent ID", userId.toString());

        removeChildParentRelation(existingParent.get(), existingParent.get().getChildren());
        parentRepository.delete(existingParent.get());

        message = String.format("Parent with ID %s, was successfully removed", userId);
        DbLogger.info(message);
    }

    private void removeChildParentRelation(ParentEntity parent, Collection<StudentEntity> children){
        children.forEach(child -> child.getParents().remove(parent));
    }
    @Override
    public List<ParentEntity> fetchParentEntities(Collection<Integer> parentIds){
        if (parentIds == null || parentIds.isEmpty())
            return Collections.emptyList();

        List<ParentEntity> parents = parentRepository.findAllById(parentIds);
        if(parents.size() != parentIds.size())
            throw new SpecificationBrokenException("Some of given Parent IDs are invalid or non-existent");

        return parents;
    }

    @Override
    public ParentEntity fetchParentEntity(Integer parentId) {
        DbLogger.info("Searching for ParentEntity with ID: " + parentId);
        ParentEntity parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException(ParentEntity.class, "ID", parentId.toString()));
        DbLogger.info("Found ParentEntity: " + parent.toString());
        return parent;
    }
}
