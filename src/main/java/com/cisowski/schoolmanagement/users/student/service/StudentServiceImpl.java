package com.cisowski.schoolmanagement.users.student.service;

import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.common.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.users.student.mapper.StudentMapper;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentPatchRequest;
import com.cisowski.schoolmanagement.users.student.model.AddStudentResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentCreateRequest;
import com.cisowski.schoolmanagement.users.student.model.StudentDetailedResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentSummaryResponse;
import com.cisowski.schoolmanagement.users.parent.repository.ParentRepository;
import com.cisowski.schoolmanagement.users.student.repository.StudentRepository;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.yearbook.repository.YearbookRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
@AllArgsConstructor
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final StudentMapper studentMapper;
    private final ParentRepository parentRepository;
    private final YearbookRepository yearbookRepository;

    @Override
    @Transactional
    public AddStudentResponse addStudent(StudentCreateRequest studentDto) {
        String message = "Add Student for: " + studentDto.toString();
        DbLogger.info(message);

        Optional<StudentEntity> existingUser = repository.findByEmail(studentDto.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException(studentDto.getEmail());
        }
        StudentEntity requestStudent = studentMapper.toStudentEntity(studentDto);
        String firstPassword = this.generateNewUserPassword();
        String hashedPassword = this.hashPassword(firstPassword);
        requestStudent.setPassword(hashedPassword);
        requestStudent.setParents(fetchParentEntities(studentDto.getParentsIds()));
        requestStudent.setYearbook(fetchYearbookEntity(studentDto.getYearbookId()));

        StudentEntity savedStudent = repository.save(requestStudent);

        message = "Student saved successfully Student: " + savedStudent.toString();
        DbLogger.info(message);

        AddStudentResponse response = studentMapper.toAddStudentResponse(savedStudent);
        response.setPassword(firstPassword);

        return response;
    }

    private YearbookEntity fetchYearbookEntity(Integer yearbookId){
        if(yearbookId == null)
            return null;
        Optional<YearbookEntity> yearbook = yearbookRepository.findById(yearbookId);
        if(yearbook.isEmpty())
            throw new SpecificationBrokenException(String.format("Given Yearbook ID does not exist: (%s)", yearbookId));
        return yearbook.get();
    }

    private List<ParentEntity> fetchParentEntities(Collection<Integer> parentIds){
        if (parentIds == null || parentIds.isEmpty())
            return Collections.emptyList();

        List<ParentEntity> parents = parentRepository.findAllById(parentIds);
        if(parents.size() != parentIds.size())
            throw new SpecificationBrokenException("Some of given Parent IDs are invalid or non-existent");

        return parents;
    }

    @Override
    @Transactional
    public StudentDetailedResponse updateStudent(StudentPatchRequest studentDto, Integer studentId) {
        String message = String.format("Update Student with ID: %s, with given data: %s", studentDto.toString(), studentId);
        DbLogger.info(message);

        Optional<StudentEntity> existingStudent = repository.findById(studentId);
        if (existingStudent.isEmpty())
            throw new EntityNotFoundException(StudentEntity.class, "Email", studentDto.getEmail());

        StudentEntity existingStudentEntity = existingStudent.get();
        StudentEntity requestStudent = studentMapper.toStudentEntity(studentDto);

        checkAndUpdateParentEntities(existingStudentEntity, studentDto);
        requestStudent.setYearbook(fetchYearbookEntity(studentDto.getYearbookId()));
        studentMapper.patchStudent(requestStudent, existingStudent.get());
        StudentEntity updatedStudent = repository.save(existingStudentEntity);

        message = "Student updated successfully: " + updatedStudent.toString();
        DbLogger.info(message);

        return studentMapper.toStudentResponse(updatedStudent);
    }

    private void checkAndUpdateParentEntities(StudentEntity student, StudentPatchRequest request){
        if(request.getParentIdsToAdd() != null && !request.getParentIdsToAdd().isEmpty()){
            List<ParentEntity> parents = new ArrayList<>(fetchParentEntities(request.getParentIdsToAdd()));
            if(student.getParents() != null)
                parents.addAll(student.getParents());
            student.setParents(parents);
        }
        if(request.getParentIdsToRemove() != null && !request.getParentIdsToRemove().isEmpty()){
            removeParentRelation(student, request.getParentIdsToRemove());
        }
    }

    private void removeParentRelation(StudentEntity student, Collection<Integer> parentIds){
        Collection<ParentEntity> parents = fetchParentEntities(parentIds);
        parents.forEach(parent -> {
            if(!student.getParents().contains(parent))
                throw new SpecificationBrokenException(String.format(
                        "Parent with ID: %s, not associated with Student with ID: %s",
                        parent.getId(),
                        student.getId()));
            student.getParents().remove(parent);
        });
    }

    @Override
    @Transactional
    public void deleteUser(Integer studentId) {
        String message = String.format("Deleting Student with ID %s", studentId);
        DbLogger.info(message);

        Optional<StudentEntity> existingStudent = repository.findById(studentId);
        if (existingStudent.isEmpty())
            throw new EntityNotFoundException(StudentEntity.class, "Student ID", studentId.toString());

        repository.delete(existingStudent.get());

        message = String.format("Student with ID %s, was successfully removed", studentId);
        DbLogger.info(message);
    }

    @Override
    public List<StudentSummaryResponse> findAll() {
        String message = "Searching for all Student entities";
        DbLogger.info(message);

        Collection<StudentEntity> students = repository.findAll();

        return studentMapper.toStudentsResponse(students);
    }


    @Override
    public StudentDetailedResponse findById(Integer studentId) {
        String message = String.format("Searching for Student with ID %s", studentId);
        DbLogger.info(message);

        Optional<StudentEntity> existingStudent = repository.findById(studentId);
        if (existingStudent.isEmpty())
            throw new EntityNotFoundException(StudentEntity.class, "ID", studentId.toString());

        message = String.format("Found Student with ID %s", studentId);
        DbLogger.info(message);

        return studentMapper.toStudentResponse(existingStudent.get());
    }

}
