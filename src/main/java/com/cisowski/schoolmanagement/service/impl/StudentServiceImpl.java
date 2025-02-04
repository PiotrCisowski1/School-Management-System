package com.cisowski.schoolmanagement.service.impl;

import com.cisowski.schoolmanagement.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.mapper.StudentMapper;
import com.cisowski.schoolmanagement.model.entity.Parent;
import com.cisowski.schoolmanagement.model.entity.Yearbook;
import com.cisowski.schoolmanagement.model.request.StudentPatchRequest;
import com.cisowski.schoolmanagement.model.response.AddStudentResponse;
import com.cisowski.schoolmanagement.model.request.StudentCreateRequest;
import com.cisowski.schoolmanagement.model.response.StudentDetailedResponse;
import com.cisowski.schoolmanagement.model.response.StudentSummaryResponse;
import com.cisowski.schoolmanagement.model.entity.Student;
import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.repository.ParentRepository;
import com.cisowski.schoolmanagement.repository.StudentRepository;
import com.cisowski.schoolmanagement.service.StudentService;
import com.cisowski.schoolmanagement.utility.DbLogger;
import jakarta.transaction.Transactional;
import jdk.jfr.Frequency;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final StudentMapper studentMapper;
    private final ParentRepository parentRepository;

    public StudentServiceImpl(StudentRepository repository, StudentMapper studentMapper, ParentRepository parentRepository) {
        this.repository = repository;
        this.studentMapper = studentMapper;
        this.parentRepository = parentRepository;
    }

    @Override
    @Transactional
    public AddStudentResponse addStudent(StudentCreateRequest studentDto) {
        String message = "Add Student for: " + studentDto.toString();
        DbLogger.info(message);

        Optional<Student> existingUser = repository.findByEmail(studentDto.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException(studentDto.getEmail());
        }
        Student requestStudent = studentMapper.toStudentEntity(studentDto);
        String firstPassword = this.generateNewUserPassword();
        String hashedPassword = this.hashPassword(firstPassword);
        requestStudent.setPassword(hashedPassword);
        requestStudent.setParents(fetchParentEntities(studentDto.getParentsIds()));

        //TODO: fetch yearbook - not yet implemented
        Yearbook yearbook = new Yearbook();
        yearbook.setId(studentDto.getYearbookId());
        requestStudent.setYearbook(yearbook);

        Student savedStudent = repository.save(requestStudent);

        message = "Student saved successfully Student: " + savedStudent.toString();
        DbLogger.info(message);

        AddStudentResponse response = studentMapper.toAddStudentResponse(savedStudent);
        response.setPassword(firstPassword);

        return response;
    }

    private Collection<Parent> fetchParentEntities(Collection<Integer> parentIds){
        if (parentIds == null || parentIds.isEmpty())
            return null;

        List<Parent> parents = parentRepository.findAllById(parentIds);
        if(parents.size() != parentIds.size())
            throw new SpecificationBrokenException("Some of given Parent IDs are invalid or non-existent");

        return parents;
    }

    @Override
    @Transactional
    public StudentDetailedResponse updateStudent(StudentPatchRequest studentDto, Integer studentId) {
        String message = String.format("Update Student with ID: %s, with given data: %s", studentDto.toString(), studentId);
        DbLogger.info(message);

        Optional<Student> existingStudent = repository.findById(studentId);
        if (existingStudent.isEmpty())
            throw new EntityNotFoundException(Student.class, "Email", studentDto.getEmail());

        Student existingStudentEntity = existingStudent.get();
        Student requestStudent = studentMapper.toStudentEntity(studentDto);

        //TODO: fetch Yearbook - yearbook not yet implemented
        Yearbook yearbook = new Yearbook();
        if(studentDto.getYearbookId() != null && studentDto.getYearbookId().toString().isEmpty())
            yearbook.setId(studentDto.getYearbookId());
        else
            yearbook.setId(existingStudentEntity.getYearbook().getId());
        requestStudent.setYearbook(yearbook);
        checkAndUpdateParentEntities(existingStudentEntity, studentDto);
        studentMapper.patchStudent(requestStudent, existingStudent.get());
        Student updatedStudent = repository.save(existingStudentEntity);

        message = "Student updated successfully: " + updatedStudent.toString();
        DbLogger.info(message);

        return studentMapper.toStudentResponse(updatedStudent);
    }

    private void checkAndUpdateParentEntities(Student student, StudentPatchRequest request){
        if(request.getParentIdsToAdd() != null && !request.getParentIdsToAdd().isEmpty()){
            student.getParents().addAll(fetchParentEntities(request.getParentIdsToAdd()));
        }
        if(request.getParentIdsToRemove() != null && !request.getParentIdsToRemove().isEmpty()){
            removeParentRelation(student, request.getParentIdsToRemove());
        }
    }

    private void removeParentRelation(Student student, Collection<Integer> parentIds){
        Collection<Parent> parents = fetchParentEntities(parentIds);
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

        Optional<Student> existingStudent = repository.findById(studentId);
        if (existingStudent.isEmpty())
            throw new EntityNotFoundException(Student.class, "Student ID", studentId.toString());

        repository.delete(existingStudent.get());

        message = String.format("Student with ID %s, was successfully removed", studentId);
        DbLogger.info(message);
    }

    @Override
    public List<StudentSummaryResponse> findAll() {
        String message = "Searching for all Student entities";
        DbLogger.info(message);

        Collection<Student> students = repository.findAll();

        return studentMapper.toStudentsResponse(students);
    }


    @Override
    public StudentDetailedResponse findById(Integer studentId) {
        String message = String.format("Searching for Student with ID %s", studentId);
        DbLogger.info(message);

        Optional<Student> existingStudent = repository.findById(studentId);
        if (existingStudent.isEmpty())
            throw new EntityNotFoundException(Student.class, "ID", studentId.toString());

        message = String.format("Found Student with ID %s", studentId);
        DbLogger.info(message);

        return studentMapper.toStudentResponse(existingStudent.get());
    }

}
