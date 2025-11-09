package com.cisowski.schoolmanagement.users.student.service;

import com.cisowski.schoolmanagement.users.parent.service.ParentService;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.common.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.users.student.mapper.StudentMapper;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentPatchRequest;
import com.cisowski.schoolmanagement.users.student.model.AddStudentResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentCreateRequest;
import com.cisowski.schoolmanagement.users.student.model.StudentDetailedResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentSummaryResponse;
import com.cisowski.schoolmanagement.users.student.repository.StudentRepository;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import com.cisowski.schoolmanagement.yearbook.service.YearbookService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
@AllArgsConstructor
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final StudentMapper studentMapper;
    private final ParentService parentService;
    private final YearbookService yearbookService;

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
        requestStudent.setParents(parentService.fetchParentEntities(studentDto.getParentsIds()));
        requestStudent.setYearbook(yearbookService.fetchYearbookEntity(studentDto.getYearbookId()));

        StudentEntity savedStudent = repository.save(requestStudent);

        message = "Student saved successfully Student: " + savedStudent.toString();
        DbLogger.info(message);

        AddStudentResponse response = studentMapper.toAddStudentResponse(savedStudent);
        response.setPassword(firstPassword);

        return response;
    }

    @Override
    @Transactional
    public StudentDetailedResponse updateStudent(StudentPatchRequest studentDto, Integer studentId) {
        String message = String.format("Update Student with ID: %s, with given data: %s", studentDto.toString(), studentId);
        DbLogger.info(message);

        StudentEntity existingStudentEntity = fetchStudent(studentId);

        StudentEntity requestStudent = studentMapper.toStudentEntity(studentDto);

        checkAndUpdateParentEntities(existingStudentEntity, studentDto);
        YearbookEntity yearbookUpdate = yearbookService.fetchYearbookEntity(studentDto.getYearbookId());
        studentMapper.patchStudent(requestStudent, existingStudentEntity);
        if(yearbookUpdate != null)
            existingStudentEntity.setYearbook(yearbookUpdate);
        StudentEntity updatedStudent = repository.save(existingStudentEntity);

        message = "Student updated successfully: " + updatedStudent.toString();
        DbLogger.info(message);

        return studentMapper.toStudentResponse(updatedStudent);
    }

    private void checkAndUpdateParentEntities(StudentEntity student, StudentPatchRequest request){
        if(request.getParentIdsToAdd() != null && !request.getParentIdsToAdd().isEmpty()){
            List<ParentEntity> parents = new ArrayList<>(parentService.fetchParentEntities(request.getParentIdsToAdd()));
            if(student.getParents() != null)
                parents.addAll(student.getParents());
            student.setParents(parents);
        }
        if(request.getParentIdsToRemove() != null && !request.getParentIdsToRemove().isEmpty()){
            removeParentRelation(student, request.getParentIdsToRemove());
        }
    }

    private void removeParentRelation(StudentEntity student, Collection<Integer> parentIds){
        Collection<ParentEntity> parents = parentService.fetchParentEntities(parentIds);
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

        StudentEntity existingStudent = fetchStudent(studentId);

        repository.delete(existingStudent);

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
        StudentEntity existingStudent = fetchStudent(studentId);

        DbLogger.info(String.format("Found Student with ID %s", studentId));

        return studentMapper.toStudentResponse(existingStudent);
    }

    @Override
    public StudentEntity fetchStudent(Integer studentId) {
        DbLogger.info("Searching for Student with ID: " + studentId);
        Optional<StudentEntity> existingStudent = repository.findById(studentId);
        if (existingStudent.isEmpty())
            throw new EntityNotFoundException(StudentEntity.class, "ID", String.valueOf(studentId));
        return existingStudent.get();
    }
}
