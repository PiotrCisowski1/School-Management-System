package com.cisowski.schoolmanagement.service.impl;

import com.cisowski.schoolmanagement.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.mapper.TeacherMapper;
import com.cisowski.schoolmanagement.model.request.TeacherCreateRequest;
import com.cisowski.schoolmanagement.model.request.TeacherPatchRequest;
import com.cisowski.schoolmanagement.model.response.AddTeacherResponse;
import com.cisowski.schoolmanagement.model.response.TeacherDetailedResponse;
import com.cisowski.schoolmanagement.model.response.TeacherSummaryResponse;
import com.cisowski.schoolmanagement.model.entity.Teacher;
import com.cisowski.schoolmanagement.repository.TeacherRepository;
import com.cisowski.schoolmanagement.service.TeacherService;
import com.cisowski.schoolmanagement.utility.DbLogger;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository repository;
    private final TeacherMapper mapper;

    public TeacherServiceImpl(TeacherRepository repository, TeacherMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    @Override
    public AddTeacherResponse addTeacher(TeacherCreateRequest dto) {
        String message = "Add Teacher for: " + dto.toString();
        DbLogger.info(message);

        Optional<Teacher> existingUser = repository.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }
        Teacher requestTeacher = mapper.toTeacherEntity(dto);

        //TODO: fetch Subjects - not implemented yet
        String firstPassword = this.generateNewUserPassword();
        String hashedPassword = this.hashPassword(firstPassword);
        requestTeacher.setPassword(hashedPassword);

        Teacher savedTeacher = repository.save(requestTeacher);

        message = "Teacher saved successfully: " + savedTeacher.toString();
        DbLogger.info(message);

        AddTeacherResponse response = mapper.toAddTeacherResponse(savedTeacher);
        response.setPassword(firstPassword);

        return response;
    }

    @Transactional
    @Override
    public TeacherDetailedResponse updateTeacher(TeacherPatchRequest dto) {
        String message = "Update Teacher for: " + dto.toString();
        DbLogger.info(message);

        Optional<Teacher> existingTeacher = repository.findByEmail(dto.getEmail());
        if (existingTeacher.isEmpty())
            throw new EntityNotFoundException(Teacher.class, "Email", dto.getEmail());
        Teacher requestTeacher = mapper.toTeacherEntity(dto);

        //TODO: fetch Subjects - not implemented yet
        requestTeacher.setId(existingTeacher.get().getId());
        requestTeacher.setPassword(existingTeacher.get().getPassword());
        Teacher updatedTeacher = repository.save(requestTeacher);

        message = "Teacher updated successfully: " + updatedTeacher.toString();
        DbLogger.info(message);

        return mapper.toTeacherResponse(updatedTeacher);
    }

    @Override
    public List<TeacherSummaryResponse> findAll() {
        String message = "Searching for all Teacher entities";
        DbLogger.info(message);

        Collection<Teacher> teachers = repository.findAll();

        return mapper.toTeachersResponse(teachers);
    }

    @Override
    public TeacherDetailedResponse findById(Integer teacherId) {
        String message = String.format("Searching for Teacher with ID %s", teacherId);
        DbLogger.info(message);

        Optional<Teacher> existingTeacher = repository.findById(teacherId);
        if (existingTeacher.isEmpty())
            throw new EntityNotFoundException(Teacher.class, "ID", teacherId.toString());

        message = String.format("Found Teacher with ID %s", teacherId);
        DbLogger.info(message);

        return mapper.toTeacherResponse(existingTeacher.get());
    }

    @Transactional
    @Override
    public void deleteUser(Integer userId) {
        String message = String.format("Deleting Teacher with ID %s", userId);
        DbLogger.info(message);

        Optional<Teacher> existingTeacher = repository.findById(userId);
        if (existingTeacher.isEmpty())
            throw new EntityNotFoundException(Teacher.class, "ID", userId.toString());

        existingTeacher.ifPresent(repository::delete);

        message = String.format("Teacher with ID %s, was successfully removed", userId);
        DbLogger.info(message);
    }
}
