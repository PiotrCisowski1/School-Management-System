package com.cisowski.schoolmanagement.users.teacher.service;

import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.common.exception.type.EmailAlreadyExistsException;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherCreateRequest;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherPatchRequest;
import com.cisowski.schoolmanagement.users.teacher.model.AddTeacherResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherDetailedResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import com.cisowski.schoolmanagement.users.teacher.repository.TeacherRepository;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
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

        Optional<TeacherEntity> existingUser = repository.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }
        TeacherEntity requestTeacher = mapper.toTeacherEntity(dto);

        //TODO: fetch Subjects - not implemented yet
        String firstPassword = this.generateNewUserPassword();
        String hashedPassword = this.hashPassword(firstPassword);
        requestTeacher.setPassword(hashedPassword);

        TeacherEntity savedTeacher = repository.save(requestTeacher);

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

        Optional<TeacherEntity> existingTeacher = repository.findByEmail(dto.getEmail());
        if (existingTeacher.isEmpty())
            throw new EntityNotFoundException(TeacherEntity.class, "Email", dto.getEmail());
        TeacherEntity requestTeacher = mapper.toTeacherEntity(dto);

        //TODO: fetch Subjects - not implemented yet
        requestTeacher.setId(existingTeacher.get().getId());
        requestTeacher.setPassword(existingTeacher.get().getPassword());
        TeacherEntity updatedTeacher = repository.save(requestTeacher);

        message = "Teacher updated successfully: " + updatedTeacher.toString();
        DbLogger.info(message);

        return mapper.toTeacherResponse(updatedTeacher);
    }

    @Override
    public List<TeacherSummaryResponse> findAll() {
        String message = "Searching for all Teacher entities";
        DbLogger.info(message);

        Collection<TeacherEntity> teachers = repository.findAll();

        return mapper.toTeachersResponse(teachers);
    }

    @Override
    public TeacherDetailedResponse findById(Integer teacherId) {
        String message = String.format("Searching for Teacher with ID %s", teacherId);
        DbLogger.info(message);

        Optional<TeacherEntity> existingTeacher = repository.findById(teacherId);
        if (existingTeacher.isEmpty())
            throw new EntityNotFoundException(TeacherEntity.class, "ID", teacherId.toString());

        message = String.format("Found Teacher with ID %s", teacherId);
        DbLogger.info(message);

        return mapper.toTeacherResponse(existingTeacher.get());
    }

    @Transactional
    @Override
    public void deleteUser(Integer userId) {
        String message = String.format("Deleting Teacher with ID %s", userId);
        DbLogger.info(message);

        Optional<TeacherEntity> existingTeacher = repository.findById(userId);
        if (existingTeacher.isEmpty())
            throw new EntityNotFoundException(TeacherEntity.class, "ID", userId.toString());

        existingTeacher.ifPresent(repository::delete);

        message = String.format("Teacher with ID %s, was successfully removed", userId);
        DbLogger.info(message);
    }
}
