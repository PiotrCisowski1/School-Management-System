package com.cisowski.schoolmanagement.users.teacher.service;

import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.subject.service.SubjectService;
import com.cisowski.schoolmanagement.users.common.service.AuthorityService;
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
import com.cisowski.schoolmanagement.yearbook.repository.YearbookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository repository;
    private final TeacherMapper mapper;
    private final SubjectService subjectService;
    private final YearbookRepository yearbookRepository;
    private final AuthorityService authorityService;

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

        requestTeacher.setTeachingSubjects(subjectService.fetchSubjects(dto.getTeachingSubjectsIds()));
        String firstPassword = this.generateNewUserPassword();
        String hashedPassword = this.hashPassword(firstPassword);
        requestTeacher.setPassword(hashedPassword);
        authorityService.resolveUserAuthorities(requestTeacher, dto.getAuthority());

        TeacherEntity savedTeacher = repository.save(requestTeacher);

        message = "Teacher saved successfully: " + savedTeacher.toString();
        DbLogger.info(message);

        AddTeacherResponse response = mapper.toAddTeacherResponse(savedTeacher);
        response.setPassword(firstPassword);

        return response;
    }

    @Transactional
    @Override
    public TeacherDetailedResponse updateTeacher(TeacherPatchRequest dto, Integer teacherId) {
        String message = String.format("Update Teacher with ID %s for request: %s", teacherId, dto.toString());
        DbLogger.info(message);

        Optional<TeacherEntity> existingTeacher = repository.findById(teacherId);
        if (existingTeacher.isEmpty())
            throw new EntityNotFoundException(TeacherEntity.class, "ID", teacherId.toString());
        TeacherEntity requestTeacher = mapper.toTeacherEntity(dto);

        updateSubjects(requestTeacher, dto.getTeachingSubjectsIdsToAdd(), dto.getTeachingSubjectsIdsToRemove());
        mapper.patchTeacher(existingTeacher.get(), requestTeacher);
        TeacherEntity updatedTeacher = repository.save(existingTeacher.get());

        message = "Teacher updated successfully: " + updatedTeacher.toString();
        DbLogger.info(message);

        return mapper.toTeacherResponse(updatedTeacher);
    }

    private void updateSubjects(TeacherEntity teacher, Collection<Integer> subjectsToAdd, Collection<Integer> subjectToRemove){
        if(!CollectionUtils.isEmpty(subjectsToAdd))
            addSubjectsForTeacher(teacher,subjectsToAdd);
        if(!CollectionUtils.isEmpty(subjectToRemove))
            removeSubjectsForTeacher(teacher, subjectToRemove);
    }

    private void addSubjectsForTeacher(TeacherEntity teacher, Collection<Integer> subjectsToAdd){
        Collection<SubjectEntity> subjects = subjectService.fetchSubjects(subjectsToAdd);
        teacher.setTeachingSubjects(subjects);
    }

    private void removeSubjectsForTeacher(TeacherEntity teacher, Collection<Integer> subjectsToRemove){
        Collection<SubjectEntity> subjectEntities = subjectService.fetchSubjects(subjectsToRemove);
        try{
            teacher.getTeachingSubjects().removeAll(subjectEntities);
        }catch (Exception exception){
            throw new SpecificationBrokenException(String.format("Some of given Subjects are not correlated with Teacher with ID: %s", teacher.getId()));
        }
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
        checkIfTeacherAssociatedWithYearbook(existingTeacher.get());

        existingTeacher.ifPresent(repository::delete);

        message = String.format("Teacher with ID %s, was successfully removed", userId);
        DbLogger.info(message);
    }

    private void checkIfTeacherAssociatedWithYearbook(TeacherEntity teacher){
        DbLogger.info(String.format("Checking if Teacher with ID %s is still associated with any Yearbook before deleting", teacher.getId()));
        if(yearbookRepository.existsByHeadTeacher(teacher))
            throw new SpecificationBrokenException(String.format("Cannot delete Teacher with ID %s, who is still head Teacher of Yearbook", teacher.getId()));
    }

    @Override
    public TeacherEntity fetchTeacher(Integer teacherId){
        Optional<TeacherEntity> teacher = repository.findById(teacherId);
        if(teacher.isEmpty())
            throw new EntityNotFoundException(TeacherEntity.class, "ID", teacherId.toString());
        return teacher.get();
    }

    @Override
    public List<SubjectEntity> fetchTeacherSubjects(Integer teacherId) {
        DbLogger.info("Searching for Subjects of Teacher with ID: " + teacherId);
        List<SubjectEntity> subjects = repository.findByTeacherId(teacherId);
        DbLogger.info(String.format("Found %s Subjects for Teacher with ID %s", subjects.size(), teacherId));
        return subjects;
    }
}
